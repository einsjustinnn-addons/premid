package de.einsjustin.premid.util;

import de.einsjustin.premid.api.PreMiDActivity.ActiveActivity;
import net.labymod.api.client.gui.icon.Icon;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Util {

  public static final Map<String, Icon> iconCache = new HashMap<>();

  public static Icon getIcon(ActiveActivity activeActivity) {

    String largeImage = activeActivity.getAssets().getLargeImage();
    String path = Base64.getEncoder().encodeToString(largeImage.getBytes(StandardCharsets.UTF_8)).replace("=", "").toLowerCase();

    Icon cachedIcon = iconCache.get(path);
    if (cachedIcon != null) {
      return cachedIcon;
    }

    Icon icon = ImageLoader.getIcon(largeImage);
    iconCache.put(path, icon);
    return icon;
  }
}
