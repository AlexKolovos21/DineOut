package com.example.dineout.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.dineout.R
import com.example.dineout.data.MenuItem
import com.example.dineout.data.Restaurant
import com.example.dineout.data.sampleRestaurants
import com.example.dineout.ui.theme.GreekBlue
import com.example.dineout.ui.theme.GreekWhite
import com.example.dineout.ui.theme.GreekGold
import com.google.android.gms.maps.model.LatLng
import androidx.compose.foundation.layout.ExperimentalLayoutApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(
    restaurant: Restaurant,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    isFavorite: Boolean,
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(restaurant.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) stringResource(R.string.remove_from_favorites) else stringResource(R.string.add_to_favorites)
                        )
                    }
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = stringResource(R.string.view_menu)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                RestaurantHeader(restaurant)
            }
            
            item {
                RestaurantInfo(restaurant)
            }
            
            item {
                RestaurantFeatures(restaurant)
            }
            
            // Preview of menu items
            item {
                Text(
                    text = stringResource(R.string.menu_preview),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Display a few items from each category
            restaurant.menu.take(2).forEach { category ->
                item {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                
                items(category.items.take(2)) { item ->
                    DetailMenuItemCard(item = item)
                }
            }
            
            // View full menu button
            item {
                Button(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(stringResource(R.string.view_full_menu))
                }
            }
        }
    }
}

@Composable
fun RestaurantHeader(restaurant: Restaurant) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        if (restaurant.imageUrl != null) {
            AsyncImage(
                model = restaurant.imageUrl,
                contentDescription = restaurant.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
            
            // Restaurant info overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Text(
                    text = restaurant.cuisine,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = "★ ${restaurant.rating}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Yellow
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = restaurant.cuisine,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "★ ${restaurant.rating}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun RestaurantInfo(restaurant: Restaurant) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.about),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = restaurant.description,
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.contact),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = restaurant.address,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = restaurant.phone,
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.opening_hours),
            style = MaterialTheme.typography.titleLarge
        )
        restaurant.openingHours.forEach { (day, hours) ->
            Row {
                Text(
                    text = "$day: ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = hours,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RestaurantFeatures(restaurant: Restaurant) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.features),
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FlowRow(
                maxItemsInEachRow = 3,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                restaurant.features.forEach { feature ->
                    SuggestionChip(
                        onClick = { },
                        label = { Text(feature) }
                    )
                }
            }
        }
    }
}

@Composable
fun DetailMenuItemCard(item: MenuItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$${String.format("%.2f", item.price)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (item.isVegetarian) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_vegetarian),
                    contentDescription = stringResource(R.string.vegetarian),
                    tint = Color.Green,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GreekBlue,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun getRestaurantById(id: String): Restaurant {
    return sampleRestaurants.first { it.id == id }
} 