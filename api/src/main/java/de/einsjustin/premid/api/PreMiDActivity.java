package de.einsjustin.premid.api;

import com.google.gson.annotations.SerializedName;

public class PreMiDActivity {

  @SerializedName("active_activity")
  private ActiveActivity activeActivity;

  private Extension extension;

  public ActiveActivity getActiveActivity() {
    return activeActivity;
  }

  public Extension getExtension() {
    return extension;
  }

  @Override
  public String toString() {
    return "Activity{" +
        "activeActivity=" + activeActivity +
        ", extension=" + extension +
        '}';
  }

  public class Extension {

    private String version;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("api_version")
    private int apiVersion;

    public int getApiVersion() {
      return apiVersion;
    }

    public String getUserId() {
      return userId;
    }

    public String getVersion() {
      return version;
    }

    @Override
    public String toString() {
      return "Extension{" +
          "version='" + version + '\'' +
          ", userId='" + userId + '\'' +
          ", apiVersion=" + apiVersion +
          '}';
    }
  }

  public class ActiveActivity {
    private String name;
    private String service;
    private int type;
    private String details;
    private String state;
    private Timestamps timestamps;
    private Assets assets;

    public String getName() {
      return name;
    }

    public Assets getAssets() {
      return assets;
    }

    public Timestamps getTimestamps() {
      return timestamps;
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

    public String getService() {
      return service;
    }

    @Override
    public String toString() {
      return "ActiveActivity{" +
          "name='" + name + '\'' +
          ", service='" + service + '\'' +
          ", type=" + type +
          ", details='" + details + '\'' +
          ", state='" + state + '\'' +
          ", timestamps=" + timestamps +
          ", assets=" + assets +
          '}';
    }
  }

  public class Timestamps {
    private long start;
    private long end;

    public long getStart() {
      return start;
    }

    public long getEnd() {
      return end;
    }

    @Override
    public String toString() {
      return "Timestamps{" +
          "start=" + start +
          ", end=" + end +
          '}';
    }
  }

  public class Assets {
    @SerializedName("large_image")
    private String largeImage;
    @SerializedName("small_image")
    private String smallImage;

    public String getLargeImage() {
      return largeImage;
    }

    public String getSmallImage() {
      return smallImage;
    }

    @Override
    public String toString() {
      return "Assets{" +
          "largeImage='" + largeImage + '\'' +
          ", smallImage='" + smallImage + '\'' +
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

    public int getId() {
      return id;
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
