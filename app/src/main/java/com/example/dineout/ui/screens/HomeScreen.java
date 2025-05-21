package com.example.dineout.ui.screens;

import androidx.compose.foundation.layout.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.unit.dp;
import com.example.dineout.data.Restaurant;
import java.util.Set;

public class HomeScreen {
    public static final HomeScreen INSTANCE = new HomeScreen();

    public void apply(Function1<Restaurant, Unit> onRestaurantClick, Runnable onMapClick,
                     Runnable onHistoryClick, Set<String> favoriteRestaurants) {
        Column column = new Column(
            Modifier.Companion.fillMaxSize().padding(16.dp),
            null,
            null,
            null
        );

        // Top bar
        Row topBar = new Row(
            Modifier.Companion.fillMaxWidth(),
            Arrangement.Companion.getSpaceBetween(),
            Alignment.Companion.getCenterVertically(),
            null
        );

        Text title = new Text(
            "DineOut",
            null,
            MaterialTheme.INSTANCE.getTypography().getHeadlineLarge(),
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

        IconButton mapButton = new IconButton(onMapClick);
        mapButton.setIcon(Icons.Filled.Map);
        actions.add(mapButton);

        IconButton historyButton = new IconButton(onHistoryClick);
        historyButton.setIcon(Icons.Filled.History);
        actions.add(historyButton);

        topBar.add(actions);
        column.add(topBar);

        // Search bar
        TextField searchField = new TextField(
            "",
            text -> {},
            Modifier.Companion.fillMaxWidth().padding(vertical = 16.dp),
            null,
            null,
            "Search restaurants...",
            null,
            null,
            false,
            null,
            null,
            null,
            null,
            null
        );
        column.add(searchField);

        // Restaurant list
        LazyColumn restaurantList = new LazyColumn(
            Modifier.Companion.weight(1f),
            null,
            null,
            null,
            null,
            null
        );

        for (Restaurant restaurant : Restaurant.getSampleRestaurants()) {
            Card restaurantCard = new Card(
                Modifier.Companion.fillMaxWidth().padding(vertical = 8.dp),
                null,
                null,
                null,
                null
            );

            Column cardContent = new Column(
                Modifier.Companion.padding(16.dp),
                null,
                null,
                null
            );

            // Restaurant image
            if (restaurant.getImageUrl() != null) {
                AsyncImage image = new AsyncImage(
                    restaurant.getImageUrl(),
                    restaurant.getName(),
                    Modifier.Companion.fillMaxWidth().height(200.dp),
                    null,
                    ContentScale.Companion.getCrop()
                );
                cardContent.add(image);
            }

            Row restaurantInfo = new Row(
                Modifier.Companion.fillMaxWidth(),
                Arrangement.Companion.getSpaceBetween(),
                Alignment.Companion.getCenterVertically(),
                null
            );

            Column textInfo = new Column(
                Modifier.Companion.weight(1f),
                null,
                null,
                null
            );

            Text restaurantName = new Text(
                restaurant.getName(),
                null,
                MaterialTheme.INSTANCE.getTypography().getTitleLarge(),
                null,
                null,
                null,
                null,
                null,
                null,
                null
            );
            textInfo.add(restaurantName);

            Text cuisineText = new Text(
                restaurant.getCuisine(),
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
            textInfo.add(cuisineText);

            restaurantInfo.add(textInfo);

            // Rating
            Row ratingRow = new Row(
                null,
                null,
                Alignment.Companion.getCenterVertically(),
                null
            );

            Icon starIcon = new Icon(
                Icons.Filled.Star,
                null,
                null,
                MaterialTheme.INSTANCE.getColorScheme().getPrimary()
            );
            ratingRow.add(starIcon);

            Text ratingText = new Text(
                String.format("%.1f", restaurant.getRating()),
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
            ratingRow.add(ratingText);

            restaurantInfo.add(ratingRow);
            cardContent.add(restaurantInfo);

            // Distance and price range
            Row detailsRow = new Row(
                Modifier.Companion.fillMaxWidth(),
                Arrangement.Companion.getSpaceBetween(),
                Alignment.Companion.getCenterVertically(),
                null
            );

            if (restaurant.getDistance() != null) {
                Text distanceText = new Text(
                    String.format("%.1f km", restaurant.getDistance()),
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
                detailsRow.add(distanceText);
            }

            Text priceText = new Text(
                restaurant.getPriceRange(),
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
            detailsRow.add(priceText);

            cardContent.add(detailsRow);
            restaurantCard.add(cardContent);
            restaurantCard.setOnClick(() -> onRestaurantClick.invoke(restaurant));
            restaurantList.add(restaurantCard);
        }

        column.add(restaurantList);
    }
} 