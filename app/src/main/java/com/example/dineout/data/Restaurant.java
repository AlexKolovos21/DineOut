package com.example.dineout.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;
import java.util.*;

public class Restaurant implements Parcelable {
    private final String id;
    private final String name;
    private final String cuisine;
    private final double rating;
    private final String imageUrl;
    private final LatLng location;
    private final String address;
    private final String phone;
    private final String description;
    private final String priceRange;
    private final Map<String, String> openingHours;
    private final List<String> features;
    private final List<MenuCategory> menu;
    private final boolean isOpen;
    private final boolean isFavorite;
    private final Double distance;
    private final String website;
    private final String email;
    private final Map<String, String> socialMedia;
    private final List<String> paymentMethods;
    private final boolean reservations;
    private final boolean delivery;
    private final boolean takeout;
    private final boolean outdoorSeating;
    private final boolean parking;
    private final boolean wifi;
    private final boolean accessibility;
    private final long lastUpdated;

    public Restaurant(String id, String name, String cuisine, double rating, String imageUrl,
                     LatLng location, String address, String phone, String description,
                     String priceRange, Map<String, String> openingHours, List<String> features,
                     List<MenuCategory> menu, boolean isOpen, boolean isFavorite, Double distance,
                     String website, String email, Map<String, String> socialMedia,
                     List<String> paymentMethods, boolean reservations, boolean delivery,
                     boolean takeout, boolean outdoorSeating, boolean parking, boolean wifi,
                     boolean accessibility, long lastUpdated) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.location = location;
        this.address = address;
        this.phone = phone;
        this.description = description;
        this.priceRange = priceRange;
        this.openingHours = new HashMap<>(openingHours);
        this.features = new ArrayList<>(features);
        this.menu = new ArrayList<>(menu);
        this.isOpen = isOpen;
        this.isFavorite = isFavorite;
        this.distance = distance;
        this.website = website;
        this.email = email;
        this.socialMedia = new HashMap<>(socialMedia);
        this.paymentMethods = new ArrayList<>(paymentMethods);
        this.reservations = reservations;
        this.delivery = delivery;
        this.takeout = takeout;
        this.outdoorSeating = outdoorSeating;
        this.parking = parking;
        this.wifi = wifi;
        this.accessibility = accessibility;
        this.lastUpdated = lastUpdated;
    }

    protected Restaurant(Parcel in) {
        id = in.readString();
        name = in.readString();
        cuisine = in.readString();
        rating = in.readDouble();
        imageUrl = in.readString();
        location = in.readParcelable(LatLng.class.getClassLoader());
        address = in.readString();
        phone = in.readString();
        description = in.readString();
        priceRange = in.readString();
        openingHours = new HashMap<>();
        in.readMap(openingHours, String.class.getClassLoader());
        features = new ArrayList<>();
        in.readStringList(features);
        menu = new ArrayList<>();
        in.readList(menu, MenuCategory.class.getClassLoader());
        isOpen = in.readByte() != 0;
        isFavorite = in.readByte() != 0;
        distance = in.readDouble();
        website = in.readString();
        email = in.readString();
        socialMedia = new HashMap<>();
        in.readMap(socialMedia, String.class.getClassLoader());
        paymentMethods = new ArrayList<>();
        in.readStringList(paymentMethods);
        reservations = in.readByte() != 0;
        delivery = in.readByte() != 0;
        takeout = in.readByte() != 0;
        outdoorSeating = in.readByte() != 0;
        parking = in.readByte() != 0;
        wifi = in.readByte() != 0;
        accessibility = in.readByte() != 0;
        lastUpdated = in.readLong();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(cuisine);
        dest.writeDouble(rating);
        dest.writeString(imageUrl);
        dest.writeParcelable(location, flags);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(description);
        dest.writeString(priceRange);
        dest.writeMap(openingHours);
        dest.writeStringList(features);
        dest.writeList(menu);
        dest.writeByte((byte) (isOpen ? 1 : 0));
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeDouble(distance != null ? distance : -1);
        dest.writeString(website);
        dest.writeString(email);
        dest.writeMap(socialMedia);
        dest.writeStringList(paymentMethods);
        dest.writeByte((byte) (reservations ? 1 : 0));
        dest.writeByte((byte) (delivery ? 1 : 0));
        dest.writeByte((byte) (takeout ? 1 : 0));
        dest.writeByte((byte) (outdoorSeating ? 1 : 0));
        dest.writeByte((byte) (parking ? 1 : 0));
        dest.writeByte((byte) (wifi ? 1 : 0));
        dest.writeByte((byte) (accessibility ? 1 : 0));
        dest.writeLong(lastUpdated);
    }

    public boolean isOpenNow() {
        if (!isOpen) return false;
        
        Calendar now = Calendar.getInstance();
        int currentDay = now.get(Calendar.DAY_OF_WEEK);
        int currentTime = now.get(Calendar.HOUR_OF_DAY) * 100 + 
                         now.get(Calendar.MINUTE);
        
        List<String> dayNames = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", 
                                           "Thursday", "Friday", "Saturday");
        String currentDayName = dayNames.get(currentDay - 1);
        
        String hours = openingHours.get(currentDayName);
        if (hours == null) return false;
        
        String[] times = hours.split("-");
        String openTime = times[0].trim();
        String closeTime = times[1].trim();
        
        String[] openHourParts = openTime.split(":");
        String[] closeHourParts = closeTime.split(":");
        
        int openTimeMinutes = Integer.parseInt(openHourParts[0]) * 100 + 
                            Integer.parseInt(openHourParts[1]);
        int closeTimeMinutes = Integer.parseInt(closeHourParts[0]) * 100 + 
                             Integer.parseInt(closeHourParts[1]);
        
        return currentTime >= openTimeMinutes && currentTime <= closeTimeMinutes;
    }
    
    public double getAveragePrice() {
        return menu.stream()
            .flatMap(category -> category.getItems().stream())
            .mapToDouble(MenuItem::getPrice)
            .average()
            .orElse(0.0);
    }
    
    public List<MenuItem> getVegetarianOptions() {
        return menu.stream()
            .flatMap(category -> category.getItems().stream())
            .filter(MenuItem::isVegetarian)
            .collect(Collectors.toList());
    }
    
    public MenuCategory getMenuByCategory(String categoryId) {
        return menu.stream()
            .filter(category -> category.getId().equals(categoryId))
            .findFirst()
            .orElse(null);
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCuisine() { return cuisine; }
    public double getRating() { return rating; }
    public String getImageUrl() { return imageUrl; }
    public LatLng getLocation() { return location; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getDescription() { return description; }
    public String getPriceRange() { return priceRange; }
    public Map<String, String> getOpeningHours() { return new HashMap<>(openingHours); }
    public List<String> getFeatures() { return new ArrayList<>(features); }
    public List<MenuCategory> getMenu() { return new ArrayList<>(menu); }
    public boolean isOpen() { return isOpen; }
    public boolean isFavorite() { return isFavorite; }
    public Double getDistance() { return distance; }
    public String getWebsite() { return website; }
    public String getEmail() { return email; }
    public Map<String, String> getSocialMedia() { return new HashMap<>(socialMedia); }
    public List<String> getPaymentMethods() { return new ArrayList<>(paymentMethods); }
    public boolean hasReservations() { return reservations; }
    public boolean hasDelivery() { return delivery; }
    public boolean hasTakeout() { return takeout; }
    public boolean hasOutdoorSeating() { return outdoorSeating; }
    public boolean hasParking() { return parking; }
    public boolean hasWifi() { return wifi; }
    public boolean hasAccessibility() { return accessibility; }
    public long getLastUpdated() { return lastUpdated; }
} 