package de.einsjustin.premid.api;

import com.google.gson.annotations.SerializedName;

public class PreMiDActivity {

  @SerializedName("active_activity")
  private ActiveActivity activeActivity;

  public ActiveActivity getActiveActivity() {
    return activeActivity;
  }

  @Override
  public String toString() {
    return "PreMiDActivity{" +
        "activeActivity=" + activeActivity +
        '}';
  }

  public class ActiveActivity {
    private String name;
    private int type;
    private String details;
    private String state;
    private Assets assets;

    public String getName() {
      return name;
    }

    public Assets getAssets() {
      return assets;
    }

    public String getState() {
      return state;
    }

    public String getDetails() {
      return details;
    }

    public int getType() {
      return type;
    }

    @Override
    public String toString() {
      return "ActiveActivity{" +
          "name='" + name + '\'' +
          ", type=" + type +
          ", details='" + details + '\'' +
          ", state='" + state + '\'' +
          ", assets=" + assets +
          '}';
    }
  }

  public class Assets {
    @SerializedName("large_image")
    private String largeImage;

    public void setLargeImage(String largeImage) {
      this.largeImage = largeImage;
    }

    public String getLargeImage() {
      return largeImage;
    }

    @Override
    public String toString() {
      return "Assets{" +
          "largeImage='" + largeImage + '\'' +
          '}';
    }
  }

  public enum ActivityType {
    PLAYING(0),
    STREAMING(1),
    LISTENING(2),
    WATCHING(3),
    COMPETING(5),
    UNKNOWN(-1);

    private final int id;

    ActivityType(int id) {
      this.id = id;
    }

    public String getNiceName() {
      return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }

    public static ActivityType fromId(int id) {
      for (ActivityType type : ActivityType.values()) {
        if (type.id == id) {
          return type;
        }
      }
      return UNKNOWN;
    }
  }
}
