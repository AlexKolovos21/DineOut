package com.example.dineout.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dineout.R
import com.example.dineout.data.MenuItem
import com.example.dineout.data.Order
import com.example.dineout.data.Restaurant
import com.example.dineout.utils.QRCodeGenerator
import androidx.compose.foundation.Image
import androidx.activity.compose.BackHandler
import java.util.UUID
import androidx.compose.ui.graphics.toArgb
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.dineout.ui.theme.GreekBlue
import kotlin.random.Random
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    
    // Calculate total price
    val total = cartItems.entries.sumOf { (item, quantity) -> item.price * quantity }
    
    // Apply BackHandler to prevent back navigation during order confirmation
    BackHandler(enabled = orderPlaced) {
        // Do nothing, effectively blocking back navigation when order is confirmed
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
            // Empty cart state
            EmptyCartView(
                onBackClick = onBackClick,
                modifier = Modifier.padding(padding)
            )
        } else {
            // Normal cart view with items
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
                        items = cartItems.map { (item, quantity) -> item to quantity }.toMap(),
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
                cartItems = cartItems,
                total = total,
                onCheckout = onCheckout,
                deliveryAddress = deliveryAddress,
                paymentMethod = paymentMethod
            )
        }
    }
}

@Composable
fun CartItemCard(
    item: MenuItem,
    quantity: Int,
    onUpdateQuantity: (MenuItem, Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp)
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "€${item.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onUpdateQuantity(item, quantity - 1) },
                    enabled = quantity > 0
                ) {
                    if (quantity > 1) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = stringResource(R.string.decrease_quantity)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.remove_item)
                        )
                    }
                }
                
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                
                IconButton(onClick = { onUpdateQuantity(item, quantity + 1) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.increase_quantity)
                    )
                }
            }
        }
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
        
        // Payment methods
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = paymentMethod == "Cash",
                onClick = { onPaymentMethodChange("Cash") }
            )
            Text(
                text = "Cash on Delivery",
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
                text = "Credit/Debit Card",
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Find your address",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search for your address") },
            placeholder = { Text("Enter street, number, city") },
            trailingIcon = {
                IconButton(onClick = {
                    // This would trigger address lookup in real implementation
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Find address"
                    )
                }
            }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Add a find my location button
        Button(
            onClick = {
                // This would trigger location permission and retrieval in real implementation
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = GreekBlue,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Use my current location",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Use my current location")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Sample suggested addresses
        Text(
            text = "Suggested addresses",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { onAddressChange("28 Ermou St, Athens 10563") },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "28 Ermou St",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Athens 10563",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { onAddressChange("15 Adrianou St, Athens 10555") },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "15 Adrianou St",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Athens 10555",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun OrderSummarySection(
    cartItems: Map<MenuItem, Int>,
    total: Double,
    onCheckout: () -> Unit,
    deliveryAddress: String,
    paymentMethod: String
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
                    text = "€${total.toFixed(2)}",
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
            
            Divider(
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
                    text = "€${(total + 3.99).toFixed(2)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth(),
                enabled = deliveryAddress.isNotBlank() && cartItems.isNotEmpty()
            ) {
                Text(text = stringResource(R.string.place_order))
            }
        }
    }
}

@Composable
fun OrderConfirmationScreen(
    order: Order,
    onMainMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(100.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = stringResource(R.string.thank_you_for_your_order),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.order_id_format, order.id.take(8)),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.order_date, formatDate(order.date)),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Generate QR code for order
        val qrCodeBitmap = QRCodeGenerator.generateQRCode(order.id)
        qrCodeBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = stringResource(R.string.order_qr_code),
                modifier = Modifier.size(200.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = stringResource(R.string.scan_to_track),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onMainMenuClick,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = stringResource(R.string.return_to_main_menu))
        }
    }
}

// Update the price formatter to use Euro format
private fun Double.toFixed(decimals: Int): String {
    return "€%.${decimals}f".format(this)
}

// Helper function to format date
private fun formatDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("dd MMMM yyyy, HH:mm", java.util.Locale.getDefault())
    return formatter.format(date)
}