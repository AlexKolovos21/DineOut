package com.example.dineout.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dineout.R
import com.example.dineout.data.Order
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order #${order.id.takeLast(8).uppercase()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when(order.status) {
                        Order.STATUS_DELIVERED -> MaterialTheme.colorScheme.primary
                        Order.STATUS_CANCELLED -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.secondary
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = order.getFormattedDate(),
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${order.getTotalItems()} items",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$${String.format("%.2f", order.total)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
} 