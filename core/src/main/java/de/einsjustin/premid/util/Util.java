package de.einsjustin.premid.util;

import de.einsjustin.premid.api.PreMiDActivity.ActiveActivity;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.client.resources.texture.GameImage;
import net.labymod.api.client.resources.texture.SimpleTexture;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Util {

  public static final Map<String, Icon> iconCache = new HashMap<>();

  @SuppressWarnings("UnstableApiUsage")
  public static Icon getIcon(ActiveActivity activeActivity) {

    String largeImage = activeActivity.getAssets().getLargeImage();
    String path = Base64.getEncoder().encodeToString(largeImage.getBytes(StandardCharsets.UTF_8)).replace("=", "").toLowerCase();

    Icon cachedIcon = iconCache.get(path);
    if (cachedIcon != null) {
      return cachedIcon;
    }

    ResourceLocation resourceLocation = getResourceLocationForActivity(path);

    BufferedImage bufferedImage = ImageLoader.loadImage(largeImage);
    if (bufferedImage != null) {
      GameImage image = Laby.references().gameImageProvider().getImage(bufferedImage);
      SimpleTexture texture = SimpleTexture.simple(resourceLocation, image);
      texture.bindTo(texture::upload);
    }

    Icon icon = Icon.texture(resourceLocation);
    iconCache.put(path, icon);
    return icon;
  }

  private static ResourceLocation getResourceLocationForActivity(String path) {
    return Laby.references().resourceLocationFactory().create("premid", "icons/" + path);
  }

}
