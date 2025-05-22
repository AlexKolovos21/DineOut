package com.example.dineout.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuItem implements Parcelable {
    private String id;
    private String name;
    private String description;
    private double price;
    private boolean isVegetarian;
    private boolean isSpicy;
    private int calories;
    private int preparationTime;
    private String restaurantId;
    private String restaurantName;
    private double restaurantLatitude;
    private double restaurantLongitude;
    private String restaurantAddress;
    private String restaurantCuisine;
    private double restaurantRating;
    private String restaurantPhone;
    private String restaurantDescription;
    private String restaurantPriceRange;

    public MenuItem(String id, String name, String description, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.isVegetarian = false;
        this.isSpicy = false;
        this.calories = 0;
        this.preparationTime = 0;
    }

    public MenuItem(String id, String name, String description, double price, boolean isVegetarian,
                   boolean isSpicy, int calories, int preparationTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.isVegetarian = isVegetarian;
        this.isSpicy = isSpicy;
        this.calories = calories;
        this.preparationTime = preparationTime;
    }

    protected MenuItem(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        isVegetarian = in.readByte() != 0;
        isSpicy = in.readByte() != 0;
        calories = in.readInt();
        preparationTime = in.readInt();
        restaurantId = in.readString();
        restaurantName = in.readString();
        restaurantLatitude = in.readDouble();
        restaurantLongitude = in.readDouble();
        restaurantAddress = in.readString();
        restaurantCuisine = in.readString();
        restaurantRating = in.readDouble();
        restaurantPhone = in.readString();
        restaurantDescription = in.readString();
        restaurantPriceRange = in.readString();
    }

    public static final Creator<MenuItem> CREATOR = new Creator<MenuItem>() {
        @Override
        public MenuItem createFromParcel(Parcel in) {
            return new MenuItem(in);
        }

        @Override
        public MenuItem[] newArray(int size) {
            return new MenuItem[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public boolean isSpicy() {
        return isSpicy;
    }

    public int getCalories() {
        return calories;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setRestaurantInfo(Restaurant restaurant) {
        this.restaurantId = restaurant.getId();
        this.restaurantName = restaurant.getName();
        this.restaurantLatitude = restaurant.getLatitude();
        this.restaurantLongitude = restaurant.getLongitude();
        this.restaurantAddress = restaurant.getAddress();
        this.restaurantCuisine = restaurant.getCuisine();
        this.restaurantRating = restaurant.getRating();
        this.restaurantPhone = restaurant.getPhone();
        this.restaurantDescription = restaurant.getDescription();
        this.restaurantPriceRange = restaurant.getPriceRange();
    }

    public double getRestaurantLatitude() {
        return restaurantLatitude;
    }

    public double getRestaurantLongitude() {
        return restaurantLongitude;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public String getRestaurantCuisine() {
        return restaurantCuisine;
    }

    public double getRestaurantRating() {
        return restaurantRating;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public String getRestaurantDescription() {
        return restaurantDescription;
    }

    public String getRestaurantPriceRange() {
        return restaurantPriceRange;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeByte((byte) (isVegetarian ? 1 : 0));
        dest.writeByte((byte) (isSpicy ? 1 : 0));
        dest.writeInt(calories);
        dest.writeInt(preparationTime);
        dest.writeString(restaurantId);
        dest.writeString(restaurantName);
        dest.writeDouble(restaurantLatitude);
        dest.writeDouble(restaurantLongitude);
        dest.writeString(restaurantAddress);
        dest.writeString(restaurantCuisine);
        dest.writeDouble(restaurantRating);
        dest.writeString(restaurantPhone);
        dest.writeString(restaurantDescription);
        dest.writeString(restaurantPriceRange);
    }
} 