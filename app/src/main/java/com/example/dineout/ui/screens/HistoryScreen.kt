package com.example.dineout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dineout.R
import com.example.dineout.data.Order
import com.example.dineout.utils.QRCodeGenerator
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    orders: List<Order>,
    onBackClick: () -> Unit,
    onOrderClick: (Order) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order History") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        if (orders.isEmpty()) {
            EmptyHistoryScreen()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                items(orders) { order ->
                    OrderHistoryItem(
                        order = order,
                        onClick = { onOrderClick(order) }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Orders Yet",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your order history will appear here once you make your first order",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryItem(
    order: Order,
    onClick: () -> Unit
) {
    val qrCodeBitmap = remember(order.id) {
        QRCodeGenerator.generateQRCode(order.id, 128)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // QR Code
            androidx.compose.foundation.Image(
                bitmap = qrCodeBitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Order Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = order.restaurantName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "€${String.format("%.2f", order.total)}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = order.getFormattedDate(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = order.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = when(order.status) {
                            Order.STATUS_DELIVERED -> MaterialTheme.colorScheme.primary
                            Order.STATUS_CANCELLED -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.secondary
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    order: Order,
    onBackClick: () -> Unit
) {
    val qrCodeBitmap = remember(order.id) {
        QRCodeGenerator.generateQRCode(order.id, 256)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // QR Code Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.foundation.Image(
                        bitmap = qrCodeBitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Order #${order.id.takeLast(8).uppercase()}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // Order Info
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    InfoRow("Status", order.status)
                    InfoRow("Date", order.getFormattedDate())
                    InfoRow("Restaurant", order.restaurantName)
                    InfoRow("Delivery Address", order.deliveryAddress)
                    InfoRow("Payment Method", order.paymentMethod)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Items List
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Items",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    order.items.forEach { (item, quantity) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.name} × $quantity",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "€${String.format("%.2f", item.price * quantity)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "€${String.format("%.2f", order.total)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
} 