package com.example.dineout.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MenuItemMapTypeAdapter implements JsonSerializer<Map<MenuItem, Integer>>, JsonDeserializer<Map<MenuItem, Integer>> {
    @Override
    public JsonElement serialize(Map<MenuItem, Integer> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<MenuItem, Integer> entry : src.entrySet()) {
            jsonObject.addProperty(entry.getKey().getId(), entry.getValue());
        }
        return jsonObject;
    }

    @Override
    public Map<MenuItem, Integer> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<MenuItem, Integer> map = new HashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String itemId = entry.getKey();
            int quantity = entry.getValue().getAsInt();
            MenuItem item = new MenuItem(itemId, "Temporary Item", "Temporary Description", 0.0);
            map.put(item, quantity);
        }
        return map;
    }
} 