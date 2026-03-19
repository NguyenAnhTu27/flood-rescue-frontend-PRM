package com.floodrescue.mobile.data.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.math.BigDecimal;

import retrofit2.Response;

public abstract class JsonRepositorySupport {

    protected JsonObject readObject(JsonElement element) {
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
    }

    protected JsonArray readArray(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull() || !object.get(key).isJsonArray()) {
            return null;
        }
        return object.getAsJsonArray(key);
    }

    protected String readString(JsonObject object, String key, String fallback) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return fallback;
        }
        try {
            String value = object.get(key).getAsString();
            return value == null || value.trim().isEmpty() ? fallback : value.trim();
        } catch (Exception ignored) {
            return fallback;
        }
    }

    protected long readLong(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return 0L;
        }
        try {
            return object.get(key).getAsLong();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    protected int readInt(JsonObject object, String key, int fallback) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return fallback;
        }
        try {
            return object.get(key).getAsInt();
        } catch (Exception ignored) {
            return fallback;
        }
    }

    protected Integer readIntegerObject(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return null;
        }
        try {
            return object.get(key).getAsInt();
        } catch (Exception ignored) {
            return null;
        }
    }

    protected Double readDoubleObject(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return null;
        }
        try {
            return object.get(key).getAsDouble();
        } catch (Exception ignored) {
            return null;
        }
    }

    protected boolean readBoolean(JsonObject object, String key, boolean fallback) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return fallback;
        }
        try {
            return object.get(key).getAsBoolean();
        } catch (Exception ignored) {
            return fallback;
        }
    }

    protected String parseApiMessage(Response<?> response, String fallback) {
        try {
            if (response != null && response.errorBody() != null) {
                String raw = response.errorBody().string();
                JSONObject json = new JSONObject(raw);
                if (json.has("message")) {
                    return json.getString("message");
                }
            }
        } catch (Exception ignored) {
            // Fallback below.
        }
        return fallback;
    }

    protected String decimalToString(JsonObject object, String key, String fallback) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return fallback;
        }
        try {
            BigDecimal value = object.get(key).getAsBigDecimal();
            return value.stripTrailingZeros().toPlainString();
        } catch (Exception ignored) {
            try {
                return object.get(key).getAsString();
            } catch (Exception inner) {
                return fallback;
            }
        }
    }
}
