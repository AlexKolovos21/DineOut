package com.example.dineout.data;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Converters {
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(new TypeToken<Map<MenuItem, Integer>>(){}.getType(), new MenuItemMapTypeAdapter())
        .create();

    @TypeConverter
    public static String fromMenuItemMap(Map<MenuItem, Integer> items) {
        if (items == null) return null;
        return gson.toJson(items);
    }

    @TypeConverter
    public static Map<MenuItem, Integer> toMenuItemMap(String value) {
        if (value == null) return null;
        Type type = new TypeToken<Map<MenuItem, Integer>>(){}.getType();
        return gson.fromJson(value, type);
    }
} 