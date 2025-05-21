package com.example.dineout.data;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class MenuCategory implements Parcelable {
    private final String id;
    private final String name;
    private final String description;
    private final List<MenuItem> items;

    public MenuCategory(String id, String name, String description, List<MenuItem> items) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.items = new ArrayList<>(items);
    }

    protected MenuCategory(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        items = new ArrayList<>();
        in.readList(items, MenuItem.class.getClassLoader());
    }

    public static final Creator<MenuCategory> CREATOR = new Creator<MenuCategory>() {
        @Override
        public MenuCategory createFromParcel(Parcel in) {
            return new MenuCategory(in);
        }

        @Override
        public MenuCategory[] newArray(int size) {
            return new MenuCategory[size];
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
        dest.writeList(items);
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<MenuItem> getItems() { return new ArrayList<>(items); }
} 