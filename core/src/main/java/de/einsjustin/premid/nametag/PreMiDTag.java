package de.einsjustin.premid.nametag;

import de.einsjustin.premid.api.PreMiDActivity;
import de.einsjustin.premid.api.PreMiDActivity.ActiveActivity;
import de.einsjustin.premid.api.PreMiDActivity.ActivityType;
import de.einsjustin.premid.snapshot.PreMiDExtraKeys;
import de.einsjustin.premid.snapshot.PreMiDUserSnapshot;
import de.einsjustin.premid.util.ImageLoader;
import java.util.ArrayList;
import java.util.List;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.tag.tags.ComponentNameTag;
import net.labymod.api.client.gfx.pipeline.renderer.text.FontFlags;
import net.labymod.api.client.gui.HorizontalAlignment;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.render.matrix.Stack;
import net.labymod.api.client.render.state.entity.AvatarSnapshot;
import net.labymod.api.client.render.state.entity.EntitySnapshot;
import net.labymod.api.laby3d.pipeline.RenderStates;
import net.labymod.api.laby3d.pipeline.material.GuiMaterial;
import net.labymod.api.laby3d.render.queue.CustomGeometryRenderer;
import net.labymod.api.laby3d.render.queue.SubmissionCollector;
import net.labymod.api.laby3d.render.queue.submissions.IconSubmission.DisplayMode;
import net.labymod.api.loader.MinecraftVersions;
import net.labymod.laby3d.api.vertex.VertexConsumer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class PreMiDTag extends ComponentNameTag {

  private static final boolean INVERSE_DEPTH = MinecraftVersions.V1_20_6.orOlder();
  private static final float BACKGROUND_DEPTH = -0.03F;

  private HorizontalAlignment alignment = HorizontalAlignment.CENTER;

  private Icon icon;
  private List<Component> components;

  @Override
  protected @NotNull List<Component> buildComponents(EntitySnapshot snapshot) {
    if (!(snapshot instanceof AvatarSnapshot avatarSnapshot) || avatarSnapshot.isDiscrete()) {
      return super.buildComponents(snapshot);
    }

    if (!(this.snapshot.has(PreMiDExtraKeys.PREMID_USER))) {
      return super.buildComponents(snapshot);
    }

    PreMiDUserSnapshot preMiDUserSnapshot = this.snapshot.get(PreMiDExtraKeys.PREMID_USER);
    PreMiDActivity activity = preMiDUserSnapshot.getActivity();
    if (activity == null) {
      return super.buildComponents(snapshot);
    }
    ActiveActivity activeActivity = activity.getActiveActivity();

    if (activeActivity == null) {
      return super.buildComponents(snapshot);
    }

    if (activeActivity.getAssets().getLargeImage() != null) {
      this.icon = ImageLoader.getIcon(activeActivity);
    }

    this.components = new ArrayList<>();

    this.alignment = this.icon == null ? HorizontalAlignment.CENTER : HorizontalAlignment.LEFT;

    ActivityType type = ActivityType.fromId(activeActivity.getType());
    String niceName = type.getName();

    String name = activeActivity.getName();
    if (name != null) {
      this.components.add(Component.text(niceName + " " +  name));
    } else {
      this.components.add(Component.text(niceName));
    }

    String details = activeActivity.getDetails();
    if (details != null) {
      this.components.add(Component.text(details));
    }

    String state = activeActivity.getState();
    if (state != null) {
      this.components.add(Component.text(state));
    }

    return this.components;
  }

  @Override
  public void render(Stack stack, SubmissionCollector submissionCollector,
      EntitySnapshot snapshot) {

    float size = this.getHeight();
    float backgroundWidth = this.getWidth();

    int backgroundArgb = Laby.labyAPI()
        .minecraft()
        .options()
        .getBackgroundColorWithOpacity(DEFAULT_BACKGROUND_COLOR);
    submissionCollector.submitCustomGeometry(
        stack,
        GuiMaterial.builder(RenderStates.GUI).build(),
        new ColoredRectangle(
            -3.0F, -1.0F,
            backgroundWidth + 1.0F, size + 1.0F,
            INVERSE_DEPTH ? -BACKGROUND_DEPTH : BACKGROUND_DEPTH,
            backgroundArgb
        )
    );

    super.render(stack, submissionCollector, snapshot);

    if (this.icon != null) {
      submissionCollector.submitIcon(
          stack,
          this.icon,
          DisplayMode.NORMAL,
          -2, 0,
          size, size,
          -1
      );
    }
  }

  @Override
  protected void submitText(Stack stack, SubmissionCollector submissionCollector,
      EntitySnapshot snapshot, Component component, float xOffset, float yOffset) {

    if (this.alignment == HorizontalAlignment.LEFT) {
      xOffset = this.getHeight() + 1.0F;
    } else {
      xOffset = (this.getWidth() - this.fontRenderer.getWidth(component)) / 2.0F;
    }

    if (this.components.size() != 3) {
      yOffset += (this.fontRenderer.getLineHeight() + this.components.size()) / this.components.size();
    }

    submissionCollector.order(3).submitComponent(
        stack,
        component,
        xOffset,
        yOffset,
        DEFAULT_TEXT_COLOR,
        snapshot.lightCoords(),
        this.getBackgroundColor(snapshot),
        FontFlags.DISPLAY_MODE_NORMAL
    );
  }

  @Override
  public float getScale() {
    return 0.5F;
  }

  @Override
  protected int getBackgroundColor(EntitySnapshot snapshot) {
    return 0;
  }

  @Override
  public float getWidth() {
    return super.getWidth() + (this.icon != null ? this.getHeight() : 0) + 2.0F;
  }

  @Override
  public float getHeight() {
    return this.fontRenderer.getLineHeight() * 3;
  }

  @Override
  public boolean isDiscrete(EntitySnapshot snapshot) {
    return true;
  }

  record ColoredRectangle(float left, float top, float right, float bottom, float depth, int argb) implements CustomGeometryRenderer {

    @Override
      public void render(Matrix4f pose, VertexConsumer consumer) {
        consumer.addVertex(pose, this.left, this.top, this.depth).setBlankUv().setColor(this.argb);
        consumer.addVertex(pose, this.left, this.bottom, this.depth).setBlankUv().setColor(this.argb);
        consumer.addVertex(pose, this.right, this.bottom, this.depth).setBlankUv()
            .setColor(this.argb);
        consumer.addVertex(pose, this.right, this.top, this.depth).setBlankUv().setColor(this.argb);
      }
    }
}
