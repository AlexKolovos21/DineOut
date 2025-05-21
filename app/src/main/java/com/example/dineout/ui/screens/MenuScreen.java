package com.example.dineout.ui.screens;

import androidx.compose.foundation.layout.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.unit.dp;
import com.example.dineout.data.*;
import java.util.Map;

public class MenuScreen {
    public static final MenuScreen INSTANCE = new MenuScreen();

    public void apply(Restaurant restaurant, Runnable onBackClick, Runnable onFavoriteClick,
                     boolean isFavorite, Runnable onCartClick, Map<MenuItem, Integer> cartItems,
                     int cartItemCount, Function2<MenuItem, Integer, Unit> onUpdateCart) {
        Column column = new Column(
            Modifier.Companion.fillMaxSize(),
            null,
            null,
            null
        );

        // Top bar
        Row topBar = new Row(
            Modifier.Companion.fillMaxWidth().padding(16.dp),
            Arrangement.Companion.getSpaceBetween(),
            Alignment.Companion.getCenterVertically(),
            null
        );

        IconButton backButton = new IconButton(onBackClick);
        backButton.setIcon(Icons.Filled.ArrowBack);
        topBar.add(backButton);

        Text title = new Text(
            "Menu",
            null,
            MaterialTheme.INSTANCE.getTypography().getHeadlineMedium(),
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
        topBar.add(title);

        Row actions = new Row(
            null,
            null,
            Alignment.Companion.getCenterVertically(),
            null
        );

        IconButton favoriteButton = new IconButton(onFavoriteClick);
        favoriteButton.setIcon(isFavorite ? Icons.Filled.Favorite : Icons.Filled.FavoriteBorder);
        actions.add(favoriteButton);

        BadgedBox cartBadge = new BadgedBox(
            cartItemCount > 0 ? new Badge(String.valueOf(cartItemCount)) : null,
            null,
            null
        );

        IconButton cartButton = new IconButton(onCartClick);
        cartButton.setIcon(Icons.Filled.ShoppingCart);
        cartBadge.add(cartButton);
        actions.add(cartBadge);

        topBar.add(actions);
        column.add(topBar);

        // Menu categories
        LazyColumn menuList = new LazyColumn(
            Modifier.Companion.weight(1f).padding(horizontal = 16.dp),
            null,
            null,
            null,
            null,
            null
        );

        for (MenuCategory category : restaurant.getMenu()) {
            // Category header
            Text categoryName = new Text(
                category.getName(),
                null,
                MaterialTheme.INSTANCE.getTypography().getHeadlineSmall(),
                null,
                null,
                null,
                null,
                null,
                null,
                null
            );
            menuList.add(categoryName);

            if (category.getDescription() != null && !category.getDescription().isEmpty()) {
                Text categoryDescription = new Text(
                    category.getDescription(),
                    null,
                    MaterialTheme.INSTANCE.getTypography().getBodyMedium(),
                    MaterialTheme.INSTANCE.getColorScheme().getOnSurfaceVariant(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                );
                menuList.add(categoryDescription);
            }

            Spacer spacer1 = new Spacer(Modifier.Companion.height(8.dp));
            menuList.add(spacer1);

            // Menu items
            for (MenuItem item : category.getItems()) {
                Card itemCard = new Card(
                    Modifier.Companion.fillMaxWidth().padding(vertical = 8.dp),
                    null,
                    null,
                    null,
                    null
                );

                Row itemRow = new Row(
                    Modifier.Companion.padding(16.dp),
                    null,
                    Alignment.Companion.getCenterVertically(),
                    null
                );

                // Item image
                if (item.getImageUrl() != null) {
                    AsyncImage image = new AsyncImage(
                        item.getImageUrl(),
                        item.getName(),
                        Modifier.Companion.size(80.dp),
                        null,
                        ContentScale.Companion.getCrop()
                    );
                    itemRow.add(image);

                    Spacer spacer2 = new Spacer(Modifier.Companion.width(16.dp));
                    itemRow.add(spacer2);
                }

                // Item details
                Column itemDetails = new Column(
                    Modifier.Companion.weight(1f),
                    null,
                    null,
                    null
                );

                Row nameRow = new Row(
                    Modifier.Companion.fillMaxWidth(),
                    Arrangement.Companion.getSpaceBetween(),
                    Alignment.Companion.getCenterVertically(),
                    null
                );

                Text itemName = new Text(
                    item.getName(),
                    null,
                    MaterialTheme.INSTANCE.getTypography().getTitleMedium(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                );
                nameRow.add(itemName);

                Text itemPrice = new Text(
                    String.format("$%.2f", item.getPrice()),
                    null,
                    MaterialTheme.INSTANCE.getTypography().getTitleMedium(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                );
                nameRow.add(itemPrice);

                itemDetails.add(nameRow);

                if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                    Text itemDescription = new Text(
                        item.getDescription(),
                        null,
                        MaterialTheme.INSTANCE.getTypography().getBodyMedium(),
                        MaterialTheme.INSTANCE.getColorScheme().getOnSurfaceVariant(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                    );
                    itemDetails.add(itemDescription);
                }

                Row tagsRow = new Row(
                    Modifier.Companion.fillMaxWidth(),
                    null,
                    Alignment.Companion.getCenterVertically(),
                    null
                );

                if (item.isVegetarian()) {
                    AssistChip vegChip = new AssistChip(
                        "Vegetarian",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                    );
                    tagsRow.add(vegChip);
                }

                if (item.isSpicy()) {
                    AssistChip spicyChip = new AssistChip(
                        "Spicy",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                    );
                    tagsRow.add(spicyChip);
                }

                if (!item.getAllergens().isEmpty()) {
                    AssistChip allergensChip = new AssistChip(
                        "Contains allergens",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                    );
                    tagsRow.add(allergensChip);
                }

                itemDetails.add(tagsRow);
                itemRow.add(itemDetails);

                // Quantity controls
                Row quantityControls = new Row(
                    null,
                    null,
                    Alignment.Companion.getCenterVertically(),
                    null
                );

                int quantity = cartItems.getOrDefault(item, 0);

                if (quantity > 0) {
                    IconButton decreaseButton = new IconButton(() -> onUpdateCart.invoke(item, quantity - 1));
                    decreaseButton.setIcon(Icons.Filled.Remove);
                    quantityControls.add(decreaseButton);

                    Text quantityText = new Text(
                        String.valueOf(quantity),
                        null,
                        MaterialTheme.INSTANCE.getTypography().getTitleMedium(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                    );
                    quantityControls.add(quantityText);
                }

                IconButton increaseButton = new IconButton(() -> onUpdateCart.invoke(item, quantity + 1));
                increaseButton.setIcon(quantity > 0 ? Icons.Filled.Add : Icons.Filled.AddShoppingCart);
                quantityControls.add(increaseButton);

                itemRow.add(quantityControls);
                itemCard.add(itemRow);
                menuList.add(itemCard);
            }

            Spacer spacer3 = new Spacer(Modifier.Companion.height(16.dp));
            menuList.add(spacer3);
        }

        column.add(menuList);
    }
} 