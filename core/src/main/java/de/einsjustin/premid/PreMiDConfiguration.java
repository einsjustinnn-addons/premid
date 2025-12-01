package de.einsjustin.premid;

import de.einsjustin.premid.ui.activity.PreMiDHowToUseActivity;
import net.labymod.api.Laby;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.Setting;
import net.labymod.api.util.MethodOrder;

@ConfigName("settings")
public class PreMiDConfiguration extends AddonConfig {

  // TODO: add some config options

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> shareActivity = new ConfigProperty<>(true);

  @SuppressWarnings("unused")
  @MethodOrder(after = "shareActivity")
  @ButtonSetting
  public void howToUse(Setting setting) {
    Laby.labyAPI().minecraft().minecraftWindow().displayScreen(new PreMiDHowToUseActivity());
  }

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }

  public ConfigProperty<Boolean> shareActivity() {
    return this.shareActivity;
  }
}
