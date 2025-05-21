package com.example.dineout.ui.screens;

import androidx.compose.foundation.layout.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.dp;
import com.example.dineout.data.Restaurant;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.compose.*;
import java.util.Set;

public class MapScreen {
    public static final MapScreen INSTANCE = new MapScreen();

    public void apply(Runnable onBackClick, Function1<Restaurant, Unit> onRestaurantClick,
                     Set<String> favoriteRestaurants) {
        Box box = new Box(
            Modifier.Companion.fillMaxSize(),
            null,
            null
        );

        // Map
        GoogleMap map = new GoogleMap(
            Modifier.Companion.fillMaxSize(),
            CameraPositionState.Companion.create(
                new CameraPosition(
                    new LatLng(37.7749, -122.4194), // San Francisco
                    12f,
                    0f,
                    0f
                )
            ),
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
            null,
            null
        );

        // Add markers for each restaurant
        for (Restaurant restaurant : Restaurant.getSampleRestaurants()) {
            MarkerState markerState = new MarkerState(restaurant.getLocation());
            Marker marker = new Marker(
                markerState,
                restaurant.getName(),
                restaurant.getDescription(),
                null,
                null,
                null,
                null,
                () -> {
                    onRestaurantClick.invoke(restaurant);
                    return Unit.INSTANCE;
                }
            );
            map.add(marker);
        }

        box.add(map);

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
            "Nearby Restaurants",
            null,
            MaterialTheme.INSTANCE.getTypography().getHeadlineMedium(),
            MaterialTheme.INSTANCE.getColorScheme().getOnSurface(),
            null,
            null,
            null,
            null,
            null,
            null
        );
        topBar.add(title);

        // Empty space to balance the back button
        Box spacer = new Box(Modifier.Companion.size(48.dp));
        topBar.add(spacer);

        box.add(topBar);

        // Restaurant list at the bottom
        LazyRow restaurantList = new LazyRow(
            Modifier.Companion.fillMaxWidth()
                .align(Alignment.Companion.getBottomCenter())
                .padding(16.dp),
            null,
            null,
            null,
            null,
            null
        );

        for (Restaurant restaurant : Restaurant.getSampleRestaurants()) {
            Card restaurantCard = new Card(
                Modifier.Companion.width(300.dp).padding(end = 16.dp),
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
                    Modifier.Companion.fillMaxWidth().height(150.dp),
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

        box.add(restaurantList);
    }
} 