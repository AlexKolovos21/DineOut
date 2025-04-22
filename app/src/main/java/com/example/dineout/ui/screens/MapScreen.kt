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
    val athens = LatLng(37.9838, 23.7275) // Athens coordinates
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
    
    // Check Google Play Services availability
    val isGooglePlayServicesAvailable = remember {
        val result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        if (result != ConnectionResult.SUCCESS) {
            Log.e("MapScreen", "Google Play Services not available: ${GoogleApiAvailability.getInstance().getErrorString(result)}")
            mapError = "Google Play Services error: ${GoogleApiAvailability.getInstance().getErrorString(result)}"
            false
        } else {
            Log.d("MapScreen", "Google Play Services is available")
            true
        }
    }
    
    // Log that we're attempting to create the map
    LaunchedEffect(Unit) {
        Log.d("MapScreen", "Initializing map with API key from manifest")
        // Display toast message to help debug
        Toast.makeText(context, "Attempting to load Google Map...", Toast.LENGTH_SHORT).show()
    }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(athens, 12f)
    }
    
    // Log camera position changes
    LaunchedEffect(cameraPositionState.position) {
        Log.d("MapScreen", "Camera position updated: ${cameraPositionState.position}")
    }
    
    // Add a timeout to ensure map loading doesn't get stuck
    LaunchedEffect(key1 = Unit) {
        // Force map to be considered loaded after 5 seconds to prevent infinite loading
        kotlinx.coroutines.delay(5000)
        if (!isMapLoaded) {
            Log.w("MapScreen", "Map load timeout triggered - forcing isMapLoaded=true")
            isMapLoaded = true
            mapError = "Map may not have loaded correctly. Timeout reached."
        }
    }
    
    // Get user's location
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
                                mapError = "Couldn't get your location"
                            }
                        }.addOnFailureListener { exception ->
                            Log.e("MapScreen", "Failed to get location", exception)
                            mapError = "Error getting location: ${exception.localizedMessage}"
                        }
                    } catch (e: Exception) {
                        Log.e("MapScreen", "Exception in location retrieval", e)
                        mapError = "Location exception: ${e.localizedMessage}"
                    }
                }
            } else {
                Log.w("MapScreen", "Location permission not granted")
                mapError = "Location permission not granted"
            }
        } catch (e: Exception) {
            Log.e("MapScreen", "Exception in location code", e)
            mapError = "General error: ${e.localizedMessage}"
            e.printStackTrace()
        }
    }
    
    // Function to calculate distance between two coordinates
    fun calculateDistance(start: LatLng, end: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            start.latitude, start.longitude,
            end.latitude, end.longitude,
            results
        )
        return results[0] / 1000 // Convert to kilometers
    }
    
    // Points of interest data
    val pointsOfInterest = listOf(
        Triple("Acropolis", LatLng(37.9715, 23.7269), BitmapDescriptorFactory.HUE_YELLOW),
        Triple("Syntagma Square", LatLng(37.9750, 23.7354), BitmapDescriptorFactory.HUE_YELLOW),
        Triple("National Garden", LatLng(37.9732, 23.7378), BitmapDescriptorFactory.HUE_GREEN),
        Triple("Temple of Olympian Zeus", LatLng(37.9694, 23.7331), BitmapDescriptorFactory.HUE_YELLOW),
        Triple("National Archaeological Museum", LatLng(37.9893, 23.7320), BitmapDescriptorFactory.HUE_MAGENTA)
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
                    // Map type toggle
                    IconButton(onClick = { 
                        mapType = if (mapType == MapType.NORMAL) MapType.SATELLITE else MapType.NORMAL
                        Log.d("MapScreen", "Map type changed to: $mapType")
                    }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Toggle Map Type",
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
                            Toast.makeText(context, "User location not available", Toast.LENGTH_SHORT).show()
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
                // Replace try-catch with safe composable approach
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
                            Text("Loading map...")
                        }
                    }
                }
                
                if (isGooglePlayServicesAvailable) {
                    // Log that we're rendering the GoogleMap
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
                            mapError = null // Clear any error if map loaded successfully
                            Toast.makeText(context, "Map loaded successfully", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        // Always show markers regardless of loading state
                        // Add restaurant markers
                        sampleRestaurants
                            .filter { selectedCuisine == null || it.cuisine == selectedCuisine }
                            .forEach { restaurant ->
                                // Choose marker color based on cuisine
                                val markerColor = when (restaurant.cuisine) {
                                    "Greek" -> BitmapDescriptorFactory.HUE_AZURE
                                    "Italian" -> BitmapDescriptorFactory.HUE_RED
                                    "Japanese" -> BitmapDescriptorFactory.HUE_VIOLET
                                    else -> BitmapDescriptorFactory.HUE_ORANGE
                                }
                                
                                // Calculate distance from user if location is available
                                val distance = userLocation?.let { 
                                    calculateDistance(it, restaurant.location)
                                }
                                
                                // Create title with distance info if available
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
                        
                        // Add user location marker if available
                        userLocation?.let {
                            Marker(
                                state = MarkerState(position = it),
                                title = "Your Location",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                            )
                            
                            // Add a circle to show approximate location radius
                            Circle(
                                center = it,
                                radius = 300.0, // 300 meters radius
                                fillColor = Color(0x220000FF),
                                strokeColor = Color(0x660000FF),
                                strokeWidth = 2f
                            )
                        }
                        
                        // Add points of interest
                        pointsOfInterest.forEach { (name, position, colorHue) ->
                            Marker(
                                state = MarkerState(position = position),
                                title = name,
                                snippet = "Point of Interest",
                                icon = BitmapDescriptorFactory.defaultMarker(colorHue),
                                alpha = 0.8f
                            )
                        }
                    }
                }
                
                // Display error message if map fails to load
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
                                    text = "Map Error",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = mapError ?: "Unknown error",
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Suggestions: Check internet connection, API key, and Google Play Services.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
                
                // Add a helper message if Google Play Services not available
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
                                    text = "Google Play Services Required",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "This app requires Google Play Services to display maps. Please install or update Google Play Services and try again.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { onBackClick() }
                                ) {
                                    Text("Go Back")
                                }
                            }
                        }
                    }
                }
            }

            // Restaurant info card
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
                            // Add restaurant image
                            restaurant.imageUrl?.let { imageUrl ->
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = restaurant.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            
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
                            
                            // Display distance if available
                            userLocation?.let { userLoc ->
                                val distance = calculateDistance(userLoc, restaurant.location)
                                Text(
                                    text = "Distance: ${String.format("%.1f", distance)} km",
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
                            
                            // Display restaurant address
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
        
        // Filters dialog
        if (showFilters) {
            AlertDialog(
                onDismissRequest = { showFilters = false },
                title = { Text(stringResource(R.string.filters)) },
                text = {
                    Column {
                        Text(stringResource(R.string.select_cuisine))
                        Spacer(modifier = Modifier.height(8.dp))
                        listOf(
                            "Greek",
                            "Italian", 
                            "Japanese"
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