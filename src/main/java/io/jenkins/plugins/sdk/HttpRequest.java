package io.jenkins.plugins.sdk;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liuwei
 */
@Data
@Builder
public class HttpRequest {

  private static final String DEFAULT_CHARSET = Constants.CHARSET_UTF8;

  private static boolean IGNORE_SSL_CHECK = true;

  private static boolean IGNORE_HOST_CHECK = true;

  private String server;

  @Default
  private String method = Constants.METHOD_GET;

  @Default
  private String contentType = Constants.CONTENT_TYPE_APPLICATION_JSON;

  private Map<String, String> header;

  /**
   * url 参数
   */
  private Map<String, String> params;

  /**
   * body 参数
   */
  private Map<String, Object> data;

  @Default
  private int timeout = 30000;

  @Default
  private String charset = DEFAULT_CHARSET;

  private Proxy proxy;

  public HttpResponse request() throws IOException {
    String rsp;
    HttpURLConnection conn = null;
    OutputStream out = null;
    HttpResponse response = new HttpResponse();
    try {
      String query = buildQuery(params, charset);
      URL url = new URL(StringUtils.isEmpty(query) ? server : buildRequestUrl(server, query));
      byte[] body = buildBody(data, charset);
      conn = getConnection(url, method, contentType, header, proxy);
      conn.setConnectTimeout(timeout);
      conn.setReadTimeout(timeout);
      out = conn.getOutputStream();
      out.write(body);
      rsp = getResponseAsString(conn);
      response.setBody(rsp);
      Map<String, List<String>> headers = conn.getHeaderFields();
      response.setHeaders(headers);
    } finally {
      if (out != null) {
        out.close();
      }
      if (conn != null) {
        conn.disconnect();
      }
    }
    return response;
  }

  private static HttpURLConnection getConnection(
      URL url, String method, String contentType, Map<String, String> headerMap, Proxy proxy)
      throws IOException {
    HttpURLConnection conn;
    if (proxy == null) {
      conn = (HttpURLConnection) url.openConnection();
    } else {
      conn = (HttpURLConnection) url.openConnection(proxy);
    }
    if (conn instanceof HttpsURLConnection) {
      HttpsURLConnection connHttps = (HttpsURLConnection) conn;
      if (IGNORE_SSL_CHECK) {
        try {
          SSLContext ctx = SSLContext.getInstance("TLS");
          ctx.init(null, new TrustManager[]{new TrustAllTrustManager()}, new SecureRandom());
          connHttps.setSSLSocketFactory(ctx.getSocketFactory());
          connHttps.setHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
          throw new IOException(e.toString());
        }
      } else {
        if (IGNORE_HOST_CHECK) {
          connHttps.setHostnameVerifier((hostname, session) -> true);
        }
      }
    }

    conn.setRequestMethod(method);
    conn.setDoInput(true);
    conn.setDoOutput(true);
    conn.setRequestProperty("Host", url.getHost());
    conn.setRequestProperty("Content-Type", contentType);
    if (headerMap != null) {
      for (Entry<String, String> entry : headerMap.entrySet()) {
        conn.setRequestProperty(entry.getKey(), entry.getValue());
      }
    }
    return conn;
  }

  public static byte[] buildBody(Object data, String charset) throws UnsupportedEncodingException {
    if (data == null) {
      return new byte[]{};
    }
    String body;
    if (data instanceof String) {
      body = (String) data;
    } else {
      body = new Gson().toJson(data);
    }
    return body.getBytes(charset);
  }

  public static String buildQuery(Map<String, String> params, String charset) throws IOException {
    if (params == null || params.isEmpty()) {
      return null;
    }

    StringBuilder query = new StringBuilder();
    Set<Entry<String, String>> entries = params.entrySet();
    boolean hasParam = false;

    for (Entry<String, String> entry : entries) {
      String name = entry.getKey();
      String value = entry.getValue();
      // 忽略参数名或参数值为空的参数
      if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value)) {
        if (hasParam) {
          query.append("&");
        } else {
          hasParam = true;
        }
        query.append(name).append("=").append(URLEncoder.encode(value, charset));
      }
    }

    return query.toString();
  }

  public static String buildRequestUrl(String url, String... queries) {
    if (queries == null || queries.length == 0) {
      return url;
    }

    StringBuilder newUrl = new StringBuilder(url);
    boolean hasQuery = url.contains("?");
    boolean hasPrepend = url.endsWith("?") || url.endsWith("&");

    for (String query : queries) {
      if (StringUtils.isNotEmpty(query)) {
        if (!hasPrepend) {
          if (hasQuery) {
            newUrl.append("&");
          } else {
            newUrl.append("?");
            hasQuery = true;
          }
        }
        newUrl.append(query);
        hasPrepend = false;
      }
    }
    return newUrl.toString();
  }

  private static String getResponseAsString(HttpURLConnection conn) throws IOException {
    String charset = getResponseCharset(conn.getContentType());
    if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
      String contentEncoding = conn.getContentEncoding();
      if (Constants.CONTENT_ENCODING_GZIP.equalsIgnoreCase(contentEncoding)) {
        return getStreamAsString(new GZIPInputStream(conn.getInputStream()), charset);
      } else {
        return getStreamAsString(conn.getInputStream(), charset);
      }
    } else {
      // OAuth bad request always return 400 status
      if (conn.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
        InputStream error = conn.getErrorStream();
        if (error != null) {
          return getStreamAsString(error, charset);
        }
      }
      // Client Error 4xx and Server Error 5xx
      throw new IOException(conn.getResponseCode() + " " + conn.getResponseMessage());
    }
  }

  private static String getStreamAsString(InputStream stream, String charset) throws IOException {
    try {
      Reader reader = new InputStreamReader(stream, charset);
      StringBuilder response = new StringBuilder();

      final char[] buff = new char[1024];
      int read;
      while ((read = reader.read(buff)) > 0) {
        response.append(buff, 0, read);
      }

      return response.toString();
    } finally {
      if (stream != null) {
        stream.close();
      }
    }
  }

  public static String getResponseCharset(String contentType) {
    String charset = DEFAULT_CHARSET;

    if (StringUtils.isNotEmpty(contentType)) {
      String[] params = contentType.split(";");
      for (String param : params) {
        param = param.trim();
        if (param.startsWith("charset")) {
          String[] pair = param.split("=", 2);
          if (pair.length == 2) {
            if (!StringUtils.isEmpty(pair[1])) {
              charset = pair[1].trim();
            }
          }
          break;
        }
      }
    }

    return charset;
  }

  private static class TrustAllTrustManager implements X509TrustManager {

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[]{};
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
    }
  }
}
