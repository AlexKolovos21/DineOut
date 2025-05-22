package com.example.dineout.utils;

import com.example.dineout.data.MenuItem;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MenuItemMapTypeAdapter implements JsonSerializer<Map<MenuItem, Integer>>, JsonDeserializer<Map<MenuItem, Integer>> {
    @Override
    public JsonElement serialize(Map<MenuItem, Integer> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray result = new JsonArray();
        for (Map.Entry<MenuItem, Integer> entry : src.entrySet()) {
            JsonObject entryObject = new JsonObject();
            MenuItem item = entry.getKey();
            
            JsonObject itemJson = new JsonObject();
            itemJson.addProperty("id", item.getId());
            itemJson.addProperty("name", item.getName());
            itemJson.addProperty("description", item.getDescription());
            itemJson.addProperty("price", item.getPrice());
            itemJson.addProperty("isVegetarian", item.isVegetarian());
            itemJson.addProperty("isSpicy", item.isSpicy());
            itemJson.addProperty("calories", item.getCalories());
            itemJson.addProperty("preparationTime", item.getPreparationTime());
            itemJson.addProperty("restaurantId", item.getRestaurantId());
            itemJson.addProperty("restaurantName", item.getRestaurantName());
            
            entryObject.add("item", itemJson);
            entryObject.addProperty("quantity", entry.getValue());
            result.add(entryObject);
        }
        return result;
    }

    @Override
    public Map<MenuItem, Integer> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<MenuItem, Integer> result = new HashMap<>();
        
        try {
            // Try to parse as the new array format first
            if (json.isJsonArray()) {
                JsonArray jsonArray = json.getAsJsonArray();
                for (JsonElement element : jsonArray) {
                    JsonObject entryObject = element.getAsJsonObject();
                    JsonObject itemJson = entryObject.getAsJsonObject("item");
                    
                    MenuItem item = new MenuItem(
                        itemJson.get("id").getAsString(),
                        itemJson.get("name").getAsString(),
                        itemJson.get("description").getAsString(),
                        itemJson.get("price").getAsDouble(),
                        itemJson.get("isVegetarian").getAsBoolean(),
                        itemJson.get("isSpicy").getAsBoolean(),
                        itemJson.get("calories").getAsInt(),
                        itemJson.get("preparationTime").getAsInt()
                    );
                    item.setRestaurantId(itemJson.get("restaurantId").getAsString());
                    item.setRestaurantName(itemJson.get("restaurantName").getAsString());
                    
                    int quantity = entryObject.get("quantity").getAsInt();
                    result.put(item, quantity);
                }
            } else if (json.isJsonObject()) {
                // Handle the old object format
                JsonObject jsonObject = json.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    // Skip entries that don't have a valid MenuItem string representation
                    if (!entry.getKey().startsWith("com.example.dineout.data.MenuItem@")) {
                        continue;
                    }
                    
                    // Create a default MenuItem with the quantity
                    MenuItem item = new MenuItem(
                        "legacy",
                        "Legacy Item",
                        "This item was saved in the old format",
                        0.0,
                        false,
                        false,
                        0,
                        0
                    );
                    result.put(item, entry.getValue().getAsInt());
                }
            }
        } catch (Exception e) {
            // If any error occurs during deserialization, return an empty map
            return new HashMap<>();
        }
        
        return result;
    }
} 