package de.einsjustin.premid.listener;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import de.einsjustin.premid.PreMiDAddon;
import de.einsjustin.premid.api.PreMiDActivity;
import de.einsjustin.premid.api.event.PreMiDActivityChangeEvent;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.playerinfo.PlayerInfoRemoveEvent;
import net.labymod.api.event.client.world.WorldLeaveEvent;
import net.labymod.api.event.labymod.labyconnect.session.LabyConnectBroadcastEvent;
import net.labymod.api.event.labymod.labyconnect.session.LabyConnectBroadcastEvent.Action;
import net.labymod.api.labyconnect.LabyConnectSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityHandler {

  private final Map<UUID, PreMiDActivity> activities = new HashMap<>();

  private final PreMiDAddon addon;

  public ActivityHandler(PreMiDAddon addon) {
    this.addon = addon;
  }

  @Subscribe
  public void onPreMiDActivityChange(PreMiDActivityChangeEvent event) {
    PreMiDActivity activity = event.activity();
    if (activity == null) return;
    this.activities.put(this.addon.labyAPI().getUniqueId(), activity);

    // TODO: send activity to LabyConnect broadcast
    sendBroadcast(activity);
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
    // TODO: handle broadcast from other player

    System.out.println("payload received");

    if (event.action() != Action.RECEIVE) {
      return;
    }

    if (!event.getKey().equals("premid-activity")) {
      return;
    }

    UUID sender = event.getSender();
    PreMiDActivity activity = new Gson().fromJson(event.getPayload(), PreMiDActivity.class);

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

  public boolean hasActivity(UUID uuid) {
    return this.activities.containsKey(uuid);
  }

  public PreMiDActivity getActivity(UUID uuid) {
    return this.activities.get(uuid);
  }
}
