package de.einsjustin.premid.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.labymod.api.util.io.web.request.Request;
import net.labymod.api.util.io.web.request.Request.Method;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

public class ImageLoader {

  public static BufferedImage loadImage(String input) {
    try {
      if (isBase64Image(input)) {
        return fromUrl(getUrlFromBase64(input));
      } else if (isUrl(input)) {
        return fromUrl(input);
      } else {
        System.err.println("Ungültiges Eingabeformat: weder URL noch Base64-Bild.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private static boolean isUrl(String input) {
    try {
      new URL(input).toURI();
      return input.startsWith("http://") || input.startsWith("https://");
    } catch (Exception e) {
      return false;
    }
  }

  private static boolean isBase64Image(String input) {
    return input != null && input.matches("^data:image/(png|jpeg|jpg|gif);base64,.*");
  }

  private static BufferedImage fromUrl(String urlString) throws IOException {
    URL url = new URL(urlString);
    return ImageIO.read(url);
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
