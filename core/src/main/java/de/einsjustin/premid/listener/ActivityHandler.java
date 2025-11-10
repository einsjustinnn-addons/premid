package de.einsjustin.premid.listener;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import de.einsjustin.premid.PreMiDAddon;
import de.einsjustin.premid.api.PreMiDActivity;
import de.einsjustin.premid.api.PreMiDActivity.ActiveActivity;
import de.einsjustin.premid.api.event.PreMiDActivityChangeEvent;
import de.einsjustin.premid.util.ImageLoader;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.playerinfo.PlayerInfoRemoveEvent;
import net.labymod.api.event.client.world.WorldLeaveEvent;
import net.labymod.api.event.labymod.labyconnect.session.LabyConnectBroadcastEvent;
import net.labymod.api.event.labymod.labyconnect.session.LabyConnectBroadcastEvent.Action;
import net.labymod.api.labyconnect.LabyConnectSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class ActivityHandler {

  private final PreMiDAddon addon;
  private final Map<UUID, PreMiDActivity> activities = new HashMap<>();

  public ActivityHandler(PreMiDAddon addon) {
    this.addon = addon;
  }

  @Subscribe
  public void onPreMiDActivityChange(PreMiDActivityChangeEvent event) {
    PreMiDActivity activity = event.activity();

    if (activity.getActiveActivity() == null) {
      this.activities.remove(this.addon.labyAPI().getUniqueId());
      sendBroadcast(activity);
      return;
    }

    formatActivity(activity);

    this.activities.put(this.addon.labyAPI().getUniqueId(), activity);

    sendBroadcast(activity);
  }

  private void formatActivity(PreMiDActivity activity) {
    ActiveActivity activeActivity = activity.getActiveActivity();
    String largeImage = activeActivity.getAssets().getLargeImage();
    String urlFromBase64;
    try {
      urlFromBase64 = ImageLoader.getUrlFromBase64(largeImage);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    activeActivity.getAssets().setLargeImage(urlFromBase64);

    if (activeActivity.getName() != null && activeActivity.getName().length() > 32) {
      activeActivity.setName(activeActivity.getName().substring(0, 29) + "...");
    }
    if (activeActivity.getDetails() != null && activeActivity.getDetails().length() > 32) {
      activeActivity.setDetails(activeActivity.getDetails().substring(0, 29) + "...");
    }
    if (activeActivity.getState() != null && activeActivity.getState().length() > 32) {
      activeActivity.setState(activeActivity.getState().substring(0, 29) + "...");
    }
  }

  private void sendBroadcast(PreMiDActivity activity) {
    LabyConnectSession session = this.addon.labyAPI().labyConnect().getSession();
    if (session == null || !session.isAuthenticated()) {
      return;
    }
    JsonElement json = new Gson().toJsonTree(activity);
    session.sendBroadcastPayload("premid-activity", json);
    System.out.println("payload sent");
  }

  @Subscribe
  public void onLabyConnectBroadcast(LabyConnectBroadcastEvent event) {

    System.out.println("payload received");

    if (event.action() != Action.RECEIVE) {
      return;
    }
    if (event.getSender() == this.addon.labyAPI().getUniqueId()) {
      return;
    }
    if (!event.getKey().equals("premid-activity")) {
      return;
    }

    UUID sender = event.getSender();
    PreMiDActivity activity = new Gson().fromJson(event.getPayload(), PreMiDActivity.class);

    if (activity.getActiveActivity() == null) {
      this.activities.remove(sender);
      return;
    }

    this.activities.put(sender, activity);
  }

  @Subscribe
  public void onPlayerInfoRemove(PlayerInfoRemoveEvent event) {
    UUID uuid = event.playerInfo().profile().getUniqueId();
    this.activities.remove(uuid);
  }

  @Subscribe
  public void onWorldLeave(WorldLeaveEvent event) {
    this.activities.clear();
  }

  public PreMiDActivity getActivity(UUID uuid) {
    return this.activities.get(uuid);
  }
}
