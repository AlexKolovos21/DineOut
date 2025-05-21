package com.example.dineout.ui.screens;

import androidx.compose.foundation.layout.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.unit.dp;
import com.example.dineout.data.Restaurant;

public class RestaurantDetailScreen {
    public static final RestaurantDetailScreen INSTANCE = new RestaurantDetailScreen();

    public void apply(Restaurant restaurant, Runnable onBackClick, Runnable onFavoriteClick,
                     boolean isFavorite, Runnable onMenuClick) {
        Box box = new Box(
            Modifier.Companion.fillMaxSize(),
            null,
            null
        );

        // Restaurant image
        if (restaurant.getImageUrl() != null) {
            AsyncImage image = new AsyncImage(
                restaurant.getImageUrl(),
                restaurant.getName(),
                Modifier.Companion.fillMaxWidth().height(250.dp),
                null,
                ContentScale.Companion.getCrop()
            );
            box.add(image);
        }

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

        IconButton favoriteButton = new IconButton(onFavoriteClick);
        favoriteButton.setIcon(isFavorite ? Icons.Filled.Favorite : Icons.Filled.FavoriteBorder);
        topBar.add(favoriteButton);

        box.add(topBar);

        // Restaurant details
        Column details = new Column(
            Modifier.Companion.fillMaxSize().padding(16.dp),
            null,
            null,
            null
        );

        Text name = new Text(
            restaurant.getName(),
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
        details.add(name);

        Row infoRow = new Row(
            Modifier.Companion.fillMaxWidth(),
            Arrangement.Companion.getSpaceBetween(),
            Alignment.Companion.getCenterVertically(),
            null
        );

        Text cuisine = new Text(
            restaurant.getCuisine(),
            null,
            MaterialTheme.INSTANCE.getTypography().getTitleMedium(),
            MaterialTheme.INSTANCE.getColorScheme().getOnSurfaceVariant(),
            null,
            null,
            null,
            null,
            null,
            null
        );
        infoRow.add(cuisine);

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

        Text rating = new Text(
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
        ratingRow.add(rating);

        infoRow.add(ratingRow);
        details.add(infoRow);

        Spacer spacer1 = new Spacer(Modifier.Companion.height(16.dp));
        details.add(spacer1);

        Text description = new Text(
            restaurant.getDescription(),
            null,
            MaterialTheme.INSTANCE.getTypography().getBodyLarge(),
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
        details.add(description);

        Spacer spacer2 = new Spacer(Modifier.Companion.height(16.dp));
        details.add(spacer2);

        // Features
        FlowRow features = new FlowRow(
            Modifier.Companion.fillMaxWidth(),
            null,
            null,
            null
        );

        for (String feature : restaurant.getFeatures()) {
            AssistChip chip = new AssistChip(
                feature,
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
            features.add(chip);
        }

        details.add(features);

        Spacer spacer3 = new Spacer(Modifier.Companion.height(16.dp));
        details.add(spacer3);

        // Opening hours
        Text hoursTitle = new Text(
            "Opening Hours",
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
        details.add(hoursTitle);

        for (Map.Entry<String, String> entry : restaurant.getOpeningHours().entrySet()) {
            Row hourRow = new Row(
                Modifier.Companion.fillMaxWidth(),
                Arrangement.Companion.getSpaceBetween(),
                Alignment.Companion.getCenterVertically(),
                null
            );

            Text day = new Text(
                entry.getKey(),
                null,
                MaterialTheme.INSTANCE.getTypography().getBodyLarge(),
                null,
                null,
                null,
                null,
                null,
                null,
                null
            );
            hourRow.add(day);

            Text hours = new Text(
                entry.getValue(),
                null,
                MaterialTheme.INSTANCE.getTypography().getBodyLarge(),
                null,
                null,
                null,
                null,
                null,
                null,
                null
            );
            hourRow.add(hours);

            details.add(hourRow);
        }

        Spacer spacer4 = new Spacer(Modifier.Companion.height(16.dp));
        details.add(spacer4);

        // Contact info
        Text contactTitle = new Text(
            "Contact",
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
        details.add(contactTitle);

        Text address = new Text(
            restaurant.getAddress(),
            null,
            MaterialTheme.INSTANCE.getTypography().getBodyLarge(),
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
        details.add(address);

        Text phone = new Text(
            restaurant.getPhone(),
            null,
            MaterialTheme.INSTANCE.getTypography().getBodyLarge(),
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
        details.add(phone);

        if (restaurant.getWebsite() != null) {
            Text website = new Text(
                restaurant.getWebsite(),
                null,
                MaterialTheme.INSTANCE.getTypography().getBodyLarge(),
                MaterialTheme.INSTANCE.getColorScheme().getPrimary(),
                null,
                null,
                null,
                null,
                null,
                null
            );
            details.add(website);
        }

        Spacer spacer5 = new Spacer(Modifier.Companion.weight(1f));
        details.add(spacer5);

        // View menu button
        Button menuButton = new Button(
            onMenuClick,
            Modifier.Companion.fillMaxWidth(),
            null,
            null,
            null,
            null
        );

        Text buttonText = new Text(
            "View Menu",
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
        menuButton.add(buttonText);

        details.add(menuButton);
        box.add(details);
    }
} 