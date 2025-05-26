package com.example.dineout.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.*
import com.example.dineout.R
import com.example.dineout.data.Restaurant
import com.example.dineout.data.sampleRestaurants
import com.example.dineout.ui.theme.GreekBlue
import com.example.dineout.ui.theme.GreekWhite
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import androidx.compose.foundation.shape.RoundedCornerShape
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.ConnectionResult
import android.util.Log
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBackClick: () -> Unit,
    onRestaurantClick: (Restaurant) -> Unit,
    favoriteRestaurants: Set<String>
) {
    val athens = LatLng(37.9838, 23.7275) // Συντεταγμένες Αθήνας
    var selectedRestaurant by remember { mutableStateOf<Restaurant?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    var selectedCuisine by remember { mutableStateOf<String?>(null) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var isMapLoaded by remember { mutableStateOf(false) }
    var mapError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    val isGooglePlayServicesAvailable = remember {
        val result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        if (result != ConnectionResult.SUCCESS) {
            Log.e("MapScreen", "Google Play Services not available: ${GoogleApiAvailability.getInstance().getErrorString(result)}")
            mapError = "Σφάλμα Google Play Services: ${GoogleApiAvailability.getInstance().getErrorString(result)}"
            false
        } else {
            Log.d("MapScreen", "Google Play Services is available")
            true
        }
    }
    
    LaunchedEffect(Unit) {
        Log.d("MapScreen", "Initializing map with API key from manifest")
        Toast.makeText(context, "Φόρτωση χάρτη...", Toast.LENGTH_SHORT).show()
    }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(athens, 12f)
    }
    
    LaunchedEffect(cameraPositionState.position) {
        Log.d("MapScreen", "Camera position updated: ${cameraPositionState.position}")
    }
    
    LaunchedEffect(key1 = Unit) {
        kotlinx.coroutines.delay(5000)
        if (!isMapLoaded) {
            Log.w("MapScreen", "Map load timeout triggered - forcing isMapLoaded=true")
            isMapLoaded = true
            mapError = "Ο χάρτης μπορεί να μην φορτώθηκε σωστά. Έληξε ο χρόνος αναμονής."
        }
    }
    
    LaunchedEffect(Unit) {
        try {
            Log.d("MapScreen", "Attempting to get user location")
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                withContext(Dispatchers.IO) {
                    try {
                        val tokenSource = CancellationTokenSource()
                        val locationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            tokenSource.token
                        )
                        
                        locationTask.addOnSuccessListener { location ->
                            location?.let {
                                Log.d("MapScreen", "Location retrieved: ${it.latitude}, ${it.longitude}")
                                userLocation = LatLng(it.latitude, it.longitude)
                                scope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(it.latitude, it.longitude),
                                            15f
                                        )
                                    )
                                }
                            } ?: run {
                                Log.e("MapScreen", "Location is null")
                                mapError = "Δεν μπορέσαμε να βρούμε την τοποθεσία σου"
                            }
                        }.addOnFailureListener { exception ->
                            Log.e("MapScreen", "Failed to get location", exception)
                            mapError = "Σφάλμα κατά την εύρεση της τοποθεσίας σου"
                        }
                    } catch (e: Exception) {
                        Log.e("MapScreen", "Exception in location retrieval", e)
                        mapError = "Σφάλμα τοποθεσίας: ${e.localizedMessage}"
                    }
                }
            } else {
                Log.w("MapScreen", "Location permission not granted")
                mapError = "Δεν δόθηκε άδεια πρόσβασης στην τοποθεσία"
            }
        } catch (e: Exception) {
            Log.e("MapScreen", "Exception in location code", e)
            mapError = "Γενικό σφάλμα: ${e.localizedMessage}"
            e.printStackTrace()
        }
    }
    
    fun calculateDistance(start: LatLng, end: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            start.latitude, start.longitude,
            end.latitude, end.longitude,
            results
        )
        return results[0] / 1000 // Μετατροπή σε χιλιόμετρα
    }
    
    val pointsOfInterest = listOf(
        Triple("Ακρόπολη", LatLng(37.9715, 23.7269), BitmapDescriptorFactory.HUE_YELLOW),
        Triple("Σύνταγμα", LatLng(37.9750, 23.7354), BitmapDescriptorFactory.HUE_YELLOW),
        Triple("Εθνικός Κήπος", LatLng(37.9732, 23.7378), BitmapDescriptorFactory.HUE_GREEN),
        Triple("Ναός του Ολυμπίου Διός", LatLng(37.9694, 23.7331), BitmapDescriptorFactory.HUE_YELLOW),
        Triple("Εθνικό Αρχαιολογικό Μουσείο", LatLng(37.9893, 23.7320), BitmapDescriptorFactory.HUE_MAGENTA)
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.restaurant_map), color = GreekWhite) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = GreekWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        mapType = if (mapType == MapType.NORMAL) MapType.SATELLITE else MapType.NORMAL
                        Log.d("MapScreen", "Map type changed to: $mapType")
                    }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Εναλλαγή τύπου χάρτη",
                            tint = GreekWhite
                        )
                    }
                    
                    IconButton(onClick = { showFilters = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = stringResource(R.string.filters),
                            tint = GreekWhite
                        )
                    }
                    IconButton(onClick = {
                        userLocation?.let { location ->
                            scope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(location, 15f)
                                )
                            }
                            Log.d("MapScreen", "Moving camera to user location")
                        } ?: run {
                            Log.w("MapScreen", "User location is null, can't move camera")
                            Toast.makeText(context, "Η τοποθεσία σου δεν είναι διαθέσιμη", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = stringResource(R.string.my_location),
                            tint = GreekWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreekBlue
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (!isMapLoaded) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(50.dp),
                                color = GreekBlue
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Φόρτωση χάρτη...")
                        }
                    }
                }
                
                if (isGooglePlayServicesAvailable) {
                    Log.d("MapScreen", "Attempting to render GoogleMap composable")
                    
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            mapType = mapType,
                            isMyLocationEnabled = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = true,
                            myLocationButtonEnabled = false,
                            compassEnabled = true,
                            mapToolbarEnabled = true
                        ),
                        onMapLoaded = {
                            Log.d("MapScreen", "Map has loaded successfully")
                            isMapLoaded = true
                            mapError = null
                            Toast.makeText(context, "Ο χάρτης φορτώθηκε επιτυχώς", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        sampleRestaurants
                            .filter { selectedCuisine == null || it.cuisine == selectedCuisine }
                            .forEach { restaurant ->
                                val markerColor = when (restaurant.cuisine) {
                                    "Greek" -> BitmapDescriptorFactory.HUE_AZURE
                                    "Italian" -> BitmapDescriptorFactory.HUE_RED
                                    "Japanese" -> BitmapDescriptorFactory.HUE_VIOLET
                                    else -> BitmapDescriptorFactory.HUE_ORANGE
                                }
                                
                                val distance = userLocation?.let { 
                                    calculateDistance(it, restaurant.location)
                                }
                                
                                val title = if (distance != null) {
                                    "${restaurant.name} (${String.format("%.1f", distance)} km)"
                                } else {
                                    restaurant.name
                                }
                                
                                Marker(
                                    state = MarkerState(position = restaurant.location),
                                    title = title,
                                    snippet = restaurant.cuisine,
                                    icon = BitmapDescriptorFactory.defaultMarker(markerColor),
                                    onClick = {
                                        selectedRestaurant = restaurant
                                        true
                                    }
                                )
                            }
                        
                        userLocation?.let {
                            Marker(
                                state = MarkerState(position = it),
                                title = "Η τοποθεσία σου",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                            )
                            
                            Circle(
                                center = it,
                                radius = 300.0,
                                fillColor = Color(0x220000FF),
                                strokeColor = Color(0x660000FF),
                                strokeWidth = 2f
                            )
                        }
                        
                        pointsOfInterest.forEach { (name, position, colorHue) ->
                            Marker(
                                state = MarkerState(position = position),
                                title = name,
                                snippet = "Σημείο ενδιαφέροντος",
                                icon = BitmapDescriptorFactory.defaultMarker(colorHue),
                                alpha = 0.8f
                            )
                        }
                    }
                }
                
                if (isMapLoaded && mapError != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.TopCenter),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.padding(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Σφάλμα Χάρτη",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = mapError ?: "Άγνωστο σφάλμα",
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Προτάσεις: Ελέγξτε τη σύνδεση στο διαδίκτυο, το API key και τα Google Play Services.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
                
                if (!isGooglePlayServicesAvailable) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Απαιτούνται Google Play Services",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Αυτή η εφαρμογή απαιτεί Google Play Services για την εμφάνιση χαρτών. Παρακαλώ εγκαταστήστε ή ενημερώστε τα Google Play Services και δοκιμάστε ξανά.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { onBackClick() }
                                ) {
                                    Text("Επιστροφή")
                                }
                            }
                        }
                    }
                }
            }

            if (isMapLoaded && selectedRestaurant != null) {
                selectedRestaurant?.let { restaurant ->
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .fillMaxWidth(),
                        onClick = { onRestaurantClick(restaurant) }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = restaurant.name,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = restaurant.cuisine,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${stringResource(R.string.rating)}: ${restaurant.rating}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            userLocation?.let { userLoc ->
                                val distance = calculateDistance(userLoc, restaurant.location)
                                Text(
                                    text = "Απόσταση: ${String.format("%.1f", distance)} km",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            if (restaurant.id in favoriteRestaurants) {
                                Text(
                                    text = stringResource(R.string.favorite),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            Text(
                                text = restaurant.address,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onRestaurantClick(restaurant) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = stringResource(R.string.view_menu))
                            }
                        }
                    }
                }
            }
        }
        
        if (showFilters) {
            AlertDialog(
                onDismissRequest = { showFilters = false },
                title = { Text(stringResource(R.string.filters)) },
                text = {
                    Column {
                        Text(stringResource(R.string.select_cuisine))
                        Spacer(modifier = Modifier.height(8.dp))
                        listOf(
                            "Ελληνική",
                            "Ιταλική", 
                            "Ιαπωνική"
                        ).forEach { cuisine ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = selectedCuisine == cuisine,
                                    onClick = { selectedCuisine = cuisine }
                                )
                                Text(
                                    text = cuisine,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedCuisine == null,
                                onClick = { selectedCuisine = null }
                            )
                            Text(
                                text = stringResource(R.string.all),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFilters = false }) {
                        Text(stringResource(id = android.R.string.ok))
                    }
                }
            )
        }
    }
} 