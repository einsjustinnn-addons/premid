package de.einsjustin.premid.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.util.io.web.request.Request;
import net.labymod.api.util.io.web.request.Request.Method;
import java.io.*;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

public class ImageLoader {

  public static Icon getIcon(String input) {
    try {
      if (isBase64Image(input)) {
        String urlFromBase64 = getUrlFromBase64(input);
        return Icon.url(urlFromBase64);
      } else if (isUrl(input)) {
        return Icon.url(input);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private static boolean isUrl(String input) {
    try {
      URI.create(input);
      return input.startsWith("http://") || input.startsWith("https://");
    } catch (Exception e) {
      return false;
    }
  }

  private static boolean isBase64Image(String input) {
    return input != null && input.matches("^data:image/(png|jpeg|jpg|gif);base64,.*");
  }

  public static String getUrlFromBase64(String base64String) throws IOException {

    if (!isBase64Image(base64String)) {
      return base64String;
    }

    AtomicReference<String> url = new AtomicReference<>();

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("imagebase64", base64String);
    Request.ofString()
        .url("https://premid.jxtn.de/upload")
        .method(Method.POST)
        .json(jsonObject)
        .handleErrorStream()
        .execute(stringResponse -> {
          if (stringResponse.hasException()) {
            return;
          }
          if (stringResponse.getStatusCode() != 200) {
            return;
          }
          JsonObject asJsonObject = JsonParser.parseString(stringResponse.get()).getAsJsonObject();
          url.set(asJsonObject.get("shortlink").getAsString());
        });

    return url.get();
  }
}
