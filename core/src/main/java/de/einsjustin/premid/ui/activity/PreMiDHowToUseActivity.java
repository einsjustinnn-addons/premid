package de.einsjustin.premid.ui.activity;

import de.einsjustin.premid.PreMiDAddon;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.activity.types.SimpleActivity;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.notification.Notification;

@Link("how-to-use.lss")
@AutoActivity
public class PreMiDHowToUseActivity extends SimpleActivity {

  private final String PREMID_URL = PreMiDAddon.getURL();

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    FlexibleContentWidget container = new FlexibleContentWidget().addId("container");
    HorizontalListWidget header = new HorizontalListWidget().addId("header");

    ComponentWidget titleWidget = ComponentWidget.i18n("premid.activity.howToUse.title").addId("title");
    header.addEntry(titleWidget);

    FlexibleContentWidget descriptionContainer = new FlexibleContentWidget().addId("description-container");
    ComponentWidget line1 = ComponentWidget.i18n("premid.activity.howToUse.steps.line1", NamedTextColor.GRAY);
    descriptionContainer.addContent(line1);

    IconWidget openSettingsIcon = new IconWidget(Icon.texture(ResourceLocation.create("premid", "textures/open_settings.png"))).addId("open-settings-image");
    descriptionContainer.addContent(openSettingsIcon);

    ComponentWidget line2 = ComponentWidget.i18n("premid.activity.howToUse.steps.line2", NamedTextColor.GRAY);
    descriptionContainer.addContent(line2);
    ComponentWidget line3 = ComponentWidget.i18n("premid.activity.howToUse.steps.line3", NamedTextColor.GRAY);
    descriptionContainer.addContent(line3);

    TextFieldWidget urlFieldWidget = new TextFieldWidget().addId("url-field");
    urlFieldWidget.setText(PREMID_URL);
    urlFieldWidget.validator(s -> false);
    urlFieldWidget.setHoverComponent(Component.translatable("premid.activity.howToUse.url.hover"));
    urlFieldWidget.setPressable(() -> {
      Laby.labyAPI().minecraft().chatExecutor().copyToClipboard(PREMID_URL);
      Notification.builder()
          .title(Component.translatable("premid.activity.howToUse.url.copied.title"))
          .text(Component.translatable("premid.activity.howToUse.url.copied.description"))
          .buildAndPush();
    });
    descriptionContainer.addContent(urlFieldWidget);

    IconWidget enableForwardingImage = new IconWidget(Icon.texture(ResourceLocation.create("premid", "textures/enable_activity_forwarding.png"))).addId("enable-forwarding-image");
    descriptionContainer.addContent(enableForwardingImage);

    FlexibleContentWidget content = new FlexibleContentWidget().addId("content");

    content.addContent(descriptionContainer);

    container.addContent(header);
    container.addContent(content);

    this.document.addChild(container);
  }
}
