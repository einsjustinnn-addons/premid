package de.einsjustin.premid.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.einsjustin.premid.api.PreMiDActivity.ActiveActivity;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.util.io.web.request.Request;
import net.labymod.api.util.io.web.request.Request.Method;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class ImageLoader {

  private static final Pattern BASE64_PATTERN = Pattern.compile("^data:image/(png|jpeg|jpg|gif);base64,.*");

  private static final Map<String, Icon> iconCache = new HashMap<>();

  public static Icon getIcon(ActiveActivity activeActivity) {

    String largeImage = activeActivity.getAssets().getLargeImage();
    String path = Base64.getEncoder().encodeToString(largeImage.getBytes(StandardCharsets.UTF_8)).replace("=", "").toLowerCase();

    Icon icon = iconCache.get(path);
    if (icon != null) {
      return icon;
    }

    try {
      if (isBase64Image(largeImage)) {
        String urlFromBase64 = getUrlFromBase64(largeImage);
        // System.out.println(urlFromBase64);
        activeActivity.getAssets().setLargeImage(urlFromBase64);
        icon = Icon.url(urlFromBase64);
      } else if (isUrl(largeImage)) {
        icon = Icon.url(largeImage);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    iconCache.put(path, icon);
    return icon;
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
    return input != null && BASE64_PATTERN.matcher(input).matches();
  }

  // TODO: add fallback
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

  public static void clearIconCache() {
    iconCache.clear();
  }
}
