package de.einsjustin.premid.listener;

import de.einsjustin.premid.PreMiDAddon;
import de.einsjustin.premid.api.PreMiDActivity;
import de.einsjustin.premid.api.event.PreMiDActivityChangeEvent;
import de.einsjustin.premid.api.event.PreMiDActivityReceivedEvent;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.playerinfo.PlayerInfoRemoveEvent;
import net.labymod.api.event.client.world.WorldLeaveEvent;
import net.labymod.api.event.labymod.labyconnect.session.LabyConnectBroadcastEvent;
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
    this.activities.put(this.addon.labyAPI().getUniqueId(), activity);

    // TODO: send activity to LabyConnect broadcast
  }

  @Subscribe
  public void onPreMiDActivityReceived(PreMiDActivityReceivedEvent event) {
    UUID uuid = event.uuid();
    PreMiDActivity activity = event.activity();
    this.activities.put(uuid, activity);
  }

  @Subscribe
  public void on(LabyConnectBroadcastEvent event) {
    // TODO: handle broadcast from other player
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
