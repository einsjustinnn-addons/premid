package de.einsjustin.premid.snapshot;

import de.einsjustin.premid.PreMiDAddon;
import de.einsjustin.premid.api.PreMiDActivity;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.laby3d.renderer.snapshot.AbstractLabySnapshot;
import net.labymod.api.laby3d.renderer.snapshot.Extras;

public class PreMiDUserSnapshot extends AbstractLabySnapshot {

  private final PreMiDActivity activity;

  public PreMiDUserSnapshot(Extras extras, Player player, PreMiDAddon addon) {
    super(extras);
    this.activity = addon.getController().getActivity(player.getUniqueId());
  }

  public PreMiDActivity getActivity() {
    return activity;
  }
}
