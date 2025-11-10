package de.einsjustin.premid.snapshot;

import de.einsjustin.premid.PreMiDAddon;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.laby3d.renderer.snapshot.Extras;
import net.labymod.api.laby3d.renderer.snapshot.LabySnapshotFactory;
import net.labymod.api.service.annotation.AutoService;

@SuppressWarnings("unused")
@AutoService(LabySnapshotFactory.class)
public class PreMiDSnapshotFactory extends LabySnapshotFactory<Player, PreMiDUserSnapshot> {

  private final PreMiDAddon addon;

  public PreMiDSnapshotFactory(PreMiDAddon addon) {
    super(PreMiDExtraKeys.PREMID_USER);
    this.addon = addon;
  }

  @Override
  protected PreMiDUserSnapshot create(Player player, Extras extras) {
    return new PreMiDUserSnapshot(extras, player, this.addon);
  }
}
