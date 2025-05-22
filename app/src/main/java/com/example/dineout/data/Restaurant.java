package com.example.dineout.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Restaurant implements Parcelable {
    private final String id;
    private final String name;
    private final String cuisine;
    private final double rating;
    private final double latitude;
    private final double longitude;
    private final String address;
    private final String phone;
    private final String description;
    private final String priceRange;
    private List<MenuItem> menu;

    public Restaurant(String id, String name, String cuisine, double rating,
                     double latitude, double longitude, String address, String phone,
                     String description, String priceRange) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.phone = phone;
        this.description = description;
        this.priceRange = priceRange;
        this.menu = new ArrayList<>();
    }

    protected Restaurant(Parcel in) {
        id = in.readString();
        name = in.readString();
        cuisine = in.readString();
        rating = in.readDouble();
        latitude = in.readDouble();
        longitude = in.readDouble();
        address = in.readString();
        phone = in.readString();
        description = in.readString();
        priceRange = in.readString();
        menu = new ArrayList<>();
        in.readList(menu, MenuItem.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(cuisine);
        dest.writeDouble(rating);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(description);
        dest.writeString(priceRange);
        dest.writeList(menu);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCuisine() { return cuisine; }
    public double getRating() { return rating; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getDescription() { return description; }
    public String getPriceRange() { return priceRange; }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }

    public boolean isOpen() {
        // For now, return true as a placeholder
        // In a real app, this would check against opening hours
        return true;
    }

    public static List<Restaurant> getSampleRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(new Restaurant(
            "1",
            "Greek Taverna",
            "Traditional Greek cuisine",
            4.5,
            37.9715, 23.7267,
            "123 Main St",
            "+30 210 1234567",
            "Authentic Greek dishes in a cozy atmosphere",
            "€€"
        ));
        restaurants.add(new Restaurant(
            "2",
            "Seafood Paradise",
            "Fresh seafood",
            4.7,
            37.9738, 23.7275,
            "456 Ocean Ave",
            "+30 210 7654321",
            "Best seafood in town with sea view",
            "€€€"
        ));
        return restaurants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(cuisine, that.cuisine) &&
               Double.compare(that.rating, rating) == 0 &&
               Objects.equals(priceRange, that.priceRange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, cuisine, rating, priceRange);
    }

    public List<MenuItem> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuItem> menu) {
        this.menu = menu;
    }

    public double calculateDistance(double userLat, double userLng) {
        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(latitude - userLat);
        double lngDistance = Math.toRadians(longitude - userLng);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(latitude))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in kilometers
    }
} 