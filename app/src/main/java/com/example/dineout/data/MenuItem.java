package com.example.dineout.data;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class MenuItem implements Parcelable {
    private final String id;
    private final String name;
    private final String description;
    private final double price;
    private final String imageUrl;
    private final boolean isVegetarian;
    private final boolean isSpicy;
    private final List<String> allergens;
    private final Integer calories;
    private final Integer preparationTime;

    public MenuItem(String id, String name, String description, double price, String imageUrl,
                   boolean isVegetarian, boolean isSpicy, List<String> allergens,
                   Integer calories, Integer preparationTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isVegetarian = isVegetarian;
        this.isSpicy = isSpicy;
        this.allergens = allergens != null ? new ArrayList<>(allergens) : new ArrayList<>();
        this.calories = calories;
        this.preparationTime = preparationTime;
    }

    protected MenuItem(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        imageUrl = in.readString();
        isVegetarian = in.readByte() != 0;
        isSpicy = in.readByte() != 0;
        allergens = new ArrayList<>();
        in.readStringList(allergens);
        calories = in.readInt();
        preparationTime = in.readInt();
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
        dest.writeString(imageUrl);
        dest.writeByte((byte) (isVegetarian ? 1 : 0));
        dest.writeByte((byte) (isSpicy ? 1 : 0));
        dest.writeStringList(allergens);
        dest.writeInt(calories != null ? calories : -1);
        dest.writeInt(preparationTime != null ? preparationTime : -1);
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public boolean isVegetarian() { return isVegetarian; }
    public boolean isSpicy() { return isSpicy; }
    public List<String> getAllergens() { return new ArrayList<>(allergens); }
    public Integer getCalories() { return calories; }
    public Integer getPreparationTime() { return preparationTime; }
} 