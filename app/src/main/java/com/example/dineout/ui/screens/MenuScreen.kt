package com.example.dineout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.dineout.R
import com.example.dineout.data.MenuItem
import com.example.dineout.data.Restaurant
import com.example.dineout.ui.theme.GreekBlue
import com.example.dineout.ui.theme.GreekWhite
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    restaurant: Restaurant,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    isFavorite: Boolean,
    onCartClick: () -> Unit,
    cartItems: Map<MenuItem, Int>,
    cartItemCount: Int,
    onUpdateCart: (MenuItem, Int) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(restaurant.menu.firstOrNull()?.id ?: "") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = restaurant.name,
                        color = GreekWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) 
                },
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
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) stringResource(R.string.remove_from_favorites) else stringResource(R.string.add_to_favorites),
                            tint = if (isFavorite) Color.Red else GreekWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreekBlue
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCartClick,
                containerColor = GreekBlue,
                contentColor = GreekWhite
            ) {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = Color.Red
                            ) {
                                Text(
                                    text = cartItemCount.toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = stringResource(R.string.view_cart)
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Restaurant image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            
            // Category tabs
            ScrollableTabRow(
                selectedTabIndex = restaurant.menu.indexOfFirst { it.id == selectedCategory }.takeIf { it >= 0 } ?: 0,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = { 
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                },
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[restaurant.menu.indexOfFirst { it.id == selectedCategory }.takeIf { it >= 0 } ?: 0]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                restaurant.menu.forEach { category ->
                    Tab(
                        selected = selectedCategory == category.id,
                        onClick = { selectedCategory = category.id },
                        text = { 
                            Text(
                                text = category.name,
                                fontWeight = if (selectedCategory == category.id) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        },
                        selectedContentColor = GreekBlue,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            // Menu items
            val selectedCategoryItems = restaurant.menu
                .find { it.id == selectedCategory }
                ?.items ?: emptyList()
            
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 76.dp) // Add padding for the FAB
            ) {
                // Category description
                restaurant.menu.find { it.id == selectedCategory }?.description?.let { description ->
                    item {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(vertical = 8.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                }
                
                items(selectedCategoryItems) { item ->
                    MenuScreenItemCard(
                        item = item,
                        quantity = cartItems[item] ?: 0,
                        onAddToCart = { 
                            val currentQuantity = cartItems[item] ?: 0
                            onUpdateCart(item, currentQuantity + 1)
                        },
                        onRemoveFromCart = {
                            val currentQuantity = cartItems[item] ?: 0
                            if (currentQuantity > 0) {
                                onUpdateCart(item, currentQuantity - 1)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MenuScreenItemCard(
    item: MenuItem,
    quantity: Int = 0,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Image
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.name.take(2).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Middle: Text content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (item.isVegetarian) {
                    Text(
                        text = stringResource(R.string.vegetarian),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Green,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "€${String.format("%.2f", item.price)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = GreekBlue
                )
            }
            
            // Right: Add/Remove buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (quantity > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = GreekBlue.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = onRemoveFromCart,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = stringResource(R.string.decrease_quantity),
                                tint = GreekBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        
                        IconButton(
                            onClick = onAddToCart,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.increase_quantity),
                                tint = GreekBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onAddToCart,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreekBlue,
                            contentColor = GreekWhite
                        ),
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_to_cart),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
} 