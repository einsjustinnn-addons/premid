package de.einsjustin.premid.bridge;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.einsjustin.premid.PreMiDAddon;
import de.einsjustin.premid.api.PreMiDActivity;
import de.einsjustin.premid.api.event.PreMiDActivityChangeEvent;
import net.labymod.api.Laby;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class PreMiDServer {

  private final PreMiDAddon addon;
  private HttpServer server;

  public PreMiDServer(PreMiDAddon addon) {
    this.addon = addon;
  }

  public void run() throws IOException {
    server = HttpServer.create(new InetSocketAddress(5646), 0);
    server.createContext("/api/premid", new PreMiDHandler());
    server.setExecutor(null);
    server.start();
    addon.logger().info("PreMID Activity server started at http://localhost:5646/api/premid");
  }

  public void stop() {
    server.stop(0);
  }

  private static class PreMiDHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
      if (!exchange.getRequestMethod().equals("POST")) {
        exchange.sendResponseHeaders(400, 0);
        return;
      }

      try (InputStream is = exchange.getRequestBody()) {

        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        PreMiDActivity activity = new Gson().fromJson(body, PreMiDActivity.class);

        System.out.println("fireEvent: " + activity.getActiveActivity().getAssets().getLargeImage());
        Laby.fireEvent(new PreMiDActivityChangeEvent(activity));

        sendResponse(exchange);
      }
    }
  }

  private static void sendResponse(HttpExchange exchange) throws IOException {
    byte[] bytes = "OK".getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(200, 0);
    exchange.getResponseBody().write(bytes);
  }
}
