package io.jenkins.plugins;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A local stand-in for the DingTalk endpoint that records what the plugin actually posted.
 *
 * <p>The robot webhook is configurable in full, so pointing it here captures the expanded payload
 * without touching plugin code or needing a real token.
 */
public final class CapturingWebhook implements AutoCloseable {

  private static final byte[] OK = "{\"errcode\":0,\"errmsg\":\"ok\"}".getBytes(StandardCharsets.UTF_8);

  private final HttpServer server;
  private final List<String> bodies = new ArrayList<>();

  public CapturingWebhook() throws IOException {
    server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
    server.createContext("/robot/send", exchange -> {
      String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
      synchronized (bodies) {
        bodies.add(body);
      }
      exchange.getResponseHeaders().add("Content-Type", "application/json");
      exchange.sendResponseHeaders(200, OK.length);
      exchange.getResponseBody().write(OK);
      exchange.close();
    });
    server.start();
  }

  public String url() {
    InetSocketAddress address = server.getAddress();
    return "http://" + address.getHostString() + ":" + address.getPort()
        + "/robot/send?access_token=test";
  }

  public List<String> bodies() {
    synchronized (bodies) {
      return new ArrayList<>(bodies);
    }
  }

  /** The single captured payload, parsed. Fails loudly if the count is not exactly one. */
  public JsonObject onlyPayload() {
    List<String> captured = bodies();
    if (captured.size() != 1) {
      throw new AssertionError("expected exactly one request, captured " + captured);
    }
    return JsonParser.parseString(captured.get(0)).getAsJsonObject();
  }

  @Override
  public void close() {
    server.stop(0);
  }
}
