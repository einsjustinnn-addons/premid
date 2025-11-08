package de.einsjustin.premid.util;

import de.einsjustin.premid.api.PreMiDActivity.ActiveActivity;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.client.resources.texture.GameImage;
import net.labymod.api.client.resources.texture.SimpleTexture;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Utils {

  private static final Map<String, Icon> iconCache = new HashMap<>();

  public static Icon getIcon(ActiveActivity activeActivity) {

    String state = activeActivity.getState() !=  null ? activeActivity.getState() : "placeholder_state";
    String details = activeActivity.getDetails() !=  null ? activeActivity.getDetails() : "placeholder_details";
    String path = state.toLowerCase().replaceAll(" ", "_") + "-" + details.toLowerCase().replaceAll(" ", "_");

    Icon cachedIcon = iconCache.get(path);
    if (cachedIcon != null) {
      return cachedIcon;
    }

    ResourceLocation resourceLocation = getResourceLocationForActivity(path);

    BufferedImage bufferedImage = ImageLoader.loadImage(activeActivity.getAssets().getLargeImage());
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
    return Laby.references().resourceLocationFactory().create("premid", "test/" + path);
  }

}
