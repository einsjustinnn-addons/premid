package de.einsjustin.premid.util;

import com.google.gson.JsonObject;
import de.einsjustin.premid.api.PreMiDActivity.ActiveActivity;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.util.io.web.request.Request;
import net.labymod.api.util.io.web.request.Request.Method;

public class ImageLoader {

  private static final Pattern BASE64_PATTERN = Pattern.compile("^data:image/(png|jpeg|jpg|gif);base64,.*");

  private static final Map<String, Icon> iconCache = new HashMap<>();

  public static String getShortImageUrl(ActiveActivity activeActivity) {

    String largeImage = activeActivity.getAssets().getLargeImage();

    try {
      if (isBase64Image(largeImage)) {
        return getUrlFromBase64(activeActivity);
      } else if (isUrl(largeImage)) {
        return largeImage;
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return largeImage;

  }

  public static Icon getIcon(ActiveActivity activeActivity) {

    String largeImage = activeActivity.getAssets().getLargeImage();
    String path = Base64.getEncoder().encodeToString(largeImage.getBytes(StandardCharsets.UTF_8)).replace("=", "").toLowerCase();

    Icon icon = iconCache.get(path);
    if (icon != null) {
      return icon;
    }

    String shortImageUrl = getShortImageUrl(activeActivity);
    icon = Icon.url(shortImageUrl);

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

  private static String getUrlFromBase64(ActiveActivity activeActivity) throws IOException {

    String base64String = activeActivity.getAssets().getLargeImage();

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
          JsonObject asJsonObject = JsonUtil.fromJson(stringResponse.get(), JsonObject.class);
          url.set(asJsonObject.get("shortlink").getAsString());
        });

    // fallback if my base64 shorter doesn't work
    if (url.get() == null) {
      String name = activeActivity.getName();
      return String.format("https://cdn.rcd.gg/PreMiD/websites/%s/%s/assets/logo.png", name.toUpperCase().charAt(0), name.replace(" ", "%20"));
    }

    return url.get();
  }

  public static void clearIconCache() {
    iconCache.clear();
  }
}
