package com.example.dineout.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.dineout.R
import com.example.dineout.data.MenuItem
import com.example.dineout.data.Order
import com.example.dineout.data.Restaurant
import com.example.dineout.ui.theme.GreekBlue
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity
import android.graphics.Color as AndroidColor
import android.location.Geocoder
import android.os.Build

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    restaurant: Restaurant?,
    cartItems: Map<MenuItem, Int>,
    onBackClick: () -> Unit,
    onCheckoutClick: (Order) -> Unit,
    onUpdateQuantity: (MenuItem, Int) -> Unit
) {
    var orderPlaced by remember { mutableStateOf(false) }
    var currentOrder by remember { mutableStateOf<Order?>(null) }
    var deliveryAddress by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Credit Card") }
    
    val total = cartItems.entries.sumOf { (item, quantity) -> item.price * quantity }
    
    BackHandler(enabled = orderPlaced) {
        // Block back navigation when order is confirmed
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (orderPlaced) 
                            stringResource(R.string.order_confirmation)
                        else 
                            stringResource(R.string.your_cart),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    if (!orderPlaced) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        if (orderPlaced && currentOrder != null) {
            OrderConfirmationScreen(
                order = currentOrder!!,
                onMainMenuClick = {
                    repeat(3) { onBackClick() }
                },
                modifier = Modifier.padding(padding)
            )
        } else if (cartItems.isEmpty()) {
            EmptyCartView(
                onBackClick = onBackClick,
                modifier = Modifier.padding(padding)
            )
        } else {
            CartContent(
                restaurant = restaurant,
                cartItems = cartItems,
                total = total,
                deliveryAddress = deliveryAddress,
                onDeliveryAddressChange = { deliveryAddress = it },
                paymentMethod = paymentMethod,
                onPaymentMethodChange = { paymentMethod = it },
                onUpdateQuantity = onUpdateQuantity,
                onCheckout = {
                    val order = Order(
                        id = UUID.randomUUID().toString(),
                        restaurantId = restaurant?.id ?: "",
                        restaurantName = restaurant?.name ?: "",
                        items = cartItems,
                        total = total,
                        date = System.currentTimeMillis(),
                        status = "Confirmed",
                        deliveryAddress = deliveryAddress,
                        paymentMethod = paymentMethod
                    )
                    currentOrder = order
                    orderPlaced = true
                    onCheckoutClick(order)
                },
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun EmptyCartView(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.empty_cart),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBackClick) {
                Text(text = stringResource(R.string.continue_shopping))
            }
        }
    }
}

@Composable
fun CartContent(
    restaurant: Restaurant?,
    cartItems: Map<MenuItem, Int>,
    total: Double,
    deliveryAddress: String,
    onDeliveryAddressChange: (String) -> Unit,
    paymentMethod: String,
    onPaymentMethodChange: (String) -> Unit,
    onUpdateQuantity: (MenuItem, Int) -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            restaurant?.let {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        items(cartItems.entries.toList()) { (item, quantity) ->
            CartItemCard(
                item = item,
                quantity = quantity,
                onUpdateQuantity = onUpdateQuantity
            )
        }
        
        item {
            DeliveryInfoSection(
                deliveryAddress = deliveryAddress,
                onDeliveryAddressChange = onDeliveryAddressChange,
                paymentMethod = paymentMethod,
                onPaymentMethodChange = onPaymentMethodChange
            )
        }
        
        item {
            OrderSummarySection(
                total = total,
                onCheckout = onCheckout
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartItemCard(
    item: MenuItem,
    quantity: Int,
    onUpdateQuantity: (MenuItem, Int) -> Unit
) {
    var isRemoving by remember { mutableStateOf(false) }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                isRemoving = true
                true
            } else {
                false
            }
        }
    )
    
    LaunchedEffect(isRemoving) {
        if (isRemoving) {
            onUpdateQuantity(item, 0)
        }
    }

    AnimatedVisibility(
        visible = !isRemoving,
        exit = slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.error)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Διαγραφή",
                        tint = Color.White
                    )
                }
            },
            content = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .animateContentSize(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val infiniteTransition = rememberInfiniteTransition(label = "κλίμακα_εικόνας")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.05f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "κλίμακα"
                        )
                        
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = "Εικόνα προϊόντος",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .scale(scale),
                            contentScale = ContentScale.Crop
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "€${String.format("%.2f", item.price)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) onUpdateQuantity(item, quantity - 1) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Μείωση ποσότητας",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.animateContentSize()
                            )
                            
                            IconButton(
                                onClick = { onUpdateQuantity(item, quantity + 1) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Αύξηση ποσότητας",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun DeliveryInfoSection(
    deliveryAddress: String,
    onDeliveryAddressChange: (String) -> Unit,
    paymentMethod: String,
    onPaymentMethodChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.delivery_information),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        DeliveryAddressFinder(
            address = deliveryAddress,
            onAddressChange = onDeliveryAddressChange
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.payment_method),
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = paymentMethod == "Cash",
                onClick = { onPaymentMethodChange("Cash") }
            )
            Text(
                text = "Μετρητά κατά την παράδοση",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = paymentMethod == "Card",
                onClick = { onPaymentMethodChange("Card") }
            )
            Text(
                text = "Πιστωτική/Χρεωστική Κάρτα",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun DeliveryAddressFinder(
    address: String,
    onAddressChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLocationLoading by remember { mutableStateOf(false) }
    var showLocationError by remember { mutableStateOf(false) }
    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation(context, onAddressChange, scope)
        } else {
            showLocationError = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Βρείτε τη διεύθυνσή σας",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Αναζητήστε τη διεύθυνσή σας") },
            placeholder = { Text("Εισάγετε οδό, αριθμό, πόλη") },
            trailingIcon = {
                IconButton(onClick = {
                    // Αυτό θα ενεργοποιούσε την αναζήτηση διεύθυνσης στην πραγματική υλοποίηση
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Εύρεση διεύθυνσης"
                    )
                }
            }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Button(
            onClick = {
                when {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        getCurrentLocation(context, onAddressChange, scope)
                    }
                    else -> {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = GreekBlue,
                contentColor = Color.White
            )
        ) {
            if (isLocationLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Χρήση τρέχουσας τοποθεσίας",
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Χρήση τρέχουσας τοποθεσίας")
        }
        
        AnimatedVisibility(
            visible = showLocationError,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Text(
                text = "Απαιτείται άδεια πρόσβασης στην τοποθεσία για να χρησιμοποιήσετε αυτή τη λειτουργία",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

fun getCurrentLocation(
    context: android.content.Context,
    onAddressChange: (String) -> Unit,
    scope: kotlinx.coroutines.CoroutineScope
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val geocoder = Geocoder(context, Locale.getDefault())
    
    scope.launch {
        try {
            val locationRequest = Priority.PRIORITY_HIGH_ACCURACY
            val cancellationTokenSource = CancellationTokenSource()
            
            val location = fusedLocationClient.getCurrentLocation(
                locationRequest,
                cancellationTokenSource.token
            ).await()
            
            location?.let { loc ->
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(loc.latitude, loc.longitude, 1) { addresses ->
                            if (!addresses.isNullOrEmpty()) {
                                val address = addresses[0]
                                val addressLines = mutableListOf<String>()
                                
                                // Προσθήκη διεύθυνσης οδού αν είναι διαθέσιμη
                                address.thoroughfare?.let { addressLines.add(it) }
                                address.subThoroughfare?.let { addressLines.add(it) }
                                
                                // Προσθήκη πόλης αν είναι διαθέσιμη
                                address.locality?.let { addressLines.add(it) }
                                
                                // Προσθήκη ταχυδρομικού κώδικα αν είναι διαθέσιμος
                                address.postalCode?.let { addressLines.add(it) }
                                
                                // Προσθήκη χώρας αν είναι διαθέσιμη
                                address.countryName?.let { addressLines.add(it) }
                                
                                val formattedAddress = addressLines.joinToString(", ")
                                onAddressChange(formattedAddress)
                            } else {
                                // Επιστροφή στις συντεταγμένες αν δεν βρέθηκε διεύθυνση
                                onAddressChange("Τρέχουσα Τοποθεσία: ${loc.latitude}, ${loc.longitude}")
                            }
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0]
                            val addressLines = mutableListOf<String>()
                            
                            // Προσθήκη διεύθυνσης οδού αν είναι διαθέσιμη
                            address.thoroughfare?.let { addressLines.add(it) }
                            address.subThoroughfare?.let { addressLines.add(it) }
                            
                            // Προσθήκη πόλης αν είναι διαθέσιμη
                            address.locality?.let { addressLines.add(it) }
                            
                            // Προσθήκη ταχυδρομικού κώδικα αν είναι διαθέσιμος
                            address.postalCode?.let { addressLines.add(it) }
                            
                            // Προσθήκη χώρας αν είναι διαθέσιμη
                            address.countryName?.let { addressLines.add(it) }
                            
                            val formattedAddress = addressLines.joinToString(", ")
                            onAddressChange(formattedAddress)
                        } else {
                            // Επιστροφή στις συντεταγμένες αν δεν βρέθηκε διεύθυνση
                            onAddressChange("Τρέχουσα Τοποθεσία: ${loc.latitude}, ${loc.longitude}")
                        }
                    }
                } catch (e: Exception) {
                    // Επιστροφή στις συντεταγμένες αν αποτύχει η γεωκωδικοποίηση
                    onAddressChange("Τρέχουσα Τοποθεσία: ${loc.latitude}, ${loc.longitude}")
                }
            }
        } catch (e: Exception) {
            // Χειρισμός σφάλματος
        }
    }
}

@Composable
fun OrderSummarySection(
    total: Double,
    onCheckout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.order_summary),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.subtotal),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "€${String.format("%.2f", total)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.delivery_fee),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "€3.99",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "€${String.format("%.2f", total + 3.99)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            CheckoutButton(
                total = total,
                onCheckout = onCheckout,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CheckoutButton(
    total: Double,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Button(
        onClick = onCheckout,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = GreekBlue,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Text(
            text = "Ολοκλήρωση - €${String.format("%.2f", total)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun OrderConfirmationScreen(
    order: Order,
    onMainMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    
    // Δημιουργία QR Κώδικα
    val qrCodeBitmap = remember {
        val writer = QRCodeWriter()
        val bitMatrix: BitMatrix = writer.encode(
            "Κωδικός Παραγγελίας: ${order.id}\nΕστιατόριο: ${order.restaurantName}\nΣύνολο: €${String.format("%.2f", order.total)}",
            BarcodeFormat.QR_CODE,
            512,
            512
        )
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
            }
        }
        bitmap
    }
    
    // Κινούμενα σχέδια
    val infiniteTransition = rememberInfiniteTransition(label = "άπειρο")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "περιστροφή"
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "κλίμακα"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "διαφάνεια"
    )
    
    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Εικονίδιο επιτυχίας με κινούμενα σχέδια
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(500)
            ) + fadeIn(animationSpec = tween(500))
        ) {
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .padding(16.dp)
                    .rotate(rotation)
                    .scale(scale)
                    .alpha(alpha),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Επιτυχής παραγγελία",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
        
        // Μήνυμα ευχαριστίας
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(500, delayMillis = 200)
            ) + fadeIn(animationSpec = tween(500, delayMillis = 200))
        ) {
            Text(
                text = stringResource(R.string.thank_you_for_your_order),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.scale(scale)
            )
        }
        
        // Κάρτα λεπτομερειών παραγγελίας
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(500, delayMillis = 400)
            ) + fadeIn(animationSpec = tween(500, delayMillis = 400))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .scale(scale),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.order_id_format, order.id.take(8)),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = stringResource(R.string.order_date, formatDate(order.date)),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Εστιατόριο: ${order.restaurantName}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Σύνολο: €${String.format("%.2f", order.total)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // QR Κώδικας
                    Card(
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Image(
                            bitmap = qrCodeBitmap.asImageBitmap(),
                            contentDescription = "QR Κώδικας Παραγγελίας",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .scale(scale)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Κουμπί επιστροφής στο κεντρικό μενού
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(500, delayMillis = 800)
            ) + fadeIn(animationSpec = tween(500, delayMillis = 800))
        ) {
            Button(
                onClick = onMainMenuClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
                    .scale(scale),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.return_to_main_menu),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(date)
}