package de.einsjustin.premid;

import de.einsjustin.premid.bridge.PreMiDServer;
import de.einsjustin.premid.listener.ActivityHandler;
import de.einsjustin.premid.nametag.PreMiDTag;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.entity.player.tag.PositionType;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class PreMiDAddon extends LabyAddon<PreMiDConfiguration> {

  private static PreMiDServer preMiDServer;
  private ActivityHandler controller;

  @Override
  protected void enable() {
    this.registerSettingCategory();

    preMiDServer = new PreMiDServer(this);

    try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
      executor.execute(() -> {
        try {
          preMiDServer.run();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }

    this.controller = new ActivityHandler(this);
    this.registerListener(this.controller);

    this.labyAPI().tagRegistry().register("premid_tag", PositionType.BELOW_NAME, new PreMiDTag(this));
  }

  @Override
  protected Class<PreMiDConfiguration> configurationClass() {
    return PreMiDConfiguration.class;
  }

  public ActivityHandler getController() {
    return this.controller;
  }

  public static String getURL() {
    return preMiDServer.getURL();
  }
}
