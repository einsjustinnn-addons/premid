package de.einsjustin.premid.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Base64;

public class ImageLoader {

  public static BufferedImage loadImage(String input) {
    try {
      if (isBase64Image(input)) {
        return fromBase64(input);
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

  private static BufferedImage fromBase64(String base64String) throws IOException {
    String base64Data = base64String.substring(base64String.indexOf(",") + 1);
    byte[] imageBytes = Base64.getDecoder().decode(base64Data);
    try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes)) {
      return ImageIO.read(bis);
    }
  }
}
