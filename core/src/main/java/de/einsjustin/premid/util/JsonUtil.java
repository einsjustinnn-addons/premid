package de.einsjustin.premid.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class JsonUtil {

  private static final Gson GSON = new Gson();

  public static <T> T fromJson(String json, Class<T> clazz) {
    return GSON.fromJson(json, clazz);
  }

  public static <T> T fromJson(JsonElement json, Class<T> clazz) {
    return GSON.fromJson(json, clazz);
  }

  public static JsonElement toJsonTree(Object src) {
    return GSON.toJsonTree(src);
  }
}
