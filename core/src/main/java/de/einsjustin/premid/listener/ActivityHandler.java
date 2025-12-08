package de.einsjustin.premid.listener;

import com.google.gson.JsonElement;
import de.einsjustin.premid.PreMiDAddon;
import de.einsjustin.premid.api.PreMiDActivity;
import de.einsjustin.premid.api.PreMiDActivity.ActiveActivity;
import de.einsjustin.premid.api.event.PreMiDActivityChangeEvent;
import de.einsjustin.premid.util.ImageLoader;
import de.einsjustin.premid.util.JsonUtil;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.playerinfo.PlayerInfoRemoveEvent;
import net.labymod.api.event.client.world.WorldLeaveEvent;
import net.labymod.api.event.labymod.labyconnect.session.LabyConnectBroadcastEvent;
import net.labymod.api.event.labymod.labyconnect.session.LabyConnectBroadcastEvent.Action;
import net.labymod.api.labyconnect.LabyConnectSession;

@SuppressWarnings("unused")
public class ActivityHandler {

  private final PreMiDAddon addon;
  private final Map<UUID, PreMiDActivity> activities = new ConcurrentHashMap<>();
  private PreMiDActivity previousActivity;

  public ActivityHandler(PreMiDAddon addon) {
    this.addon = addon;
  }

  @Subscribe
  public void onPreMiDActivityChange(PreMiDActivityChangeEvent event) {
    PreMiDActivity activity = event.activity();

    if (activity.getActiveActivity() == null) {
      this.activities.remove(this.addon.labyAPI().getUniqueId());
      this.sendBroadcast(activity);
      return;
    }

    this.formatActivity(activity);

    this.activities.put(this.addon.labyAPI().getUniqueId(), activity);

    this.sendBroadcast(activity);
  }

  @Subscribe
  public void onLabyConnectBroadcast(LabyConnectBroadcastEvent event) {

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
    PreMiDActivity activity = JsonUtil.fromJson(event.getPayload(), PreMiDActivity.class);

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
    ImageLoader.clearIconCache();
  }

  public PreMiDActivity getActivity(UUID uuid) {
    return this.activities.get(uuid);
  }

  private void sendBroadcast(PreMiDActivity activity) {
    if (this.previousActivity != null && this.previousActivity.equals(activity)) {
      return;
    }
    if (!this.addon.configuration().shareActivity().get() || !this.addon.configuration().enabled().get()) {
      return;
    }
    LabyConnectSession session = this.addon.labyAPI().labyConnect().getSession();
    if (session == null || !session.isAuthenticated()) {
      return;
    }
    JsonElement json = JsonUtil.toJsonTree(activity);
    session.sendBroadcastPayload("premid-activity", json);
  }

  private void formatActivity(PreMiDActivity activity) {
    ActiveActivity activeActivity = activity.getActiveActivity();

    if (activeActivity.getAssets().getLargeImage() != null) {
      String shortImageUrl = ImageLoader.getShortImageUrl(activeActivity);
      activeActivity.getAssets().setLargeImage(shortImageUrl);
    }

    String name = activeActivity.getName();
    if (name != null && name.length() > 32) {
      activeActivity.setName(name.substring(0, 29) + "...");
    }
    String details = activeActivity.getDetails();
    if (details != null && details.length() > 32) {
      activeActivity.setDetails(details.substring(0, 29) + "...");
    }
    String state = activeActivity.getState();
    if (state != null && state.length() > 32) {
      activeActivity.setState(state.substring(0, 29) + "...");
    }
  }
}
