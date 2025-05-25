package com.example.dineout.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.dineout.R
import com.example.dineout.data.Restaurant
import com.example.dineout.data.sampleRestaurants
import com.example.dineout.ui.theme.GreekBlue
import com.example.dineout.ui.theme.GreekWhite
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRestaurantClick: (Restaurant) -> Unit,
    onMapClick: () -> Unit,
    onHistoryClick: () -> Unit,
    favoriteRestaurants: Set<String>
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DineOut", color = GreekWhite) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreekBlue
                ),
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = stringResource(R.string.order_history),
                            tint = GreekWhite
                        )
                    }
                    IconButton(onClick = onMapClick) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = stringResource(R.string.view_map),
                            tint = GreekWhite
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(GreekWhite)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(stringResource(R.string.search_restaurants)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_restaurants)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreekBlue,
                    unfocusedBorderColor = GreekBlue.copy(alpha = 0.5f)
                )
            )

            // Restaurant List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sampleRestaurants) { restaurant ->
                    RestaurantCard(
                        restaurant = restaurant,
                        onClick = { onRestaurantClick(restaurant) },
                        isFavorite = restaurant.id in favoriteRestaurants
                    )
                }
            }
        }
    }
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit,
    isFavorite: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Restaurant Icon/Initial
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GreekBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = restaurant.name.take(2).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = GreekBlue
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Restaurant Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = restaurant.cuisine,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = stringResource(R.string.rating),
                        tint = Color.Yellow,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = String.format("%.1f", restaurant.rating),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    if (restaurant.isOpen) {
                        Text(
                            text = "• Open",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Green,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
            
            // Favorite Icon
            if (isFavorite) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = stringResource(R.string.favorite),
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
} 