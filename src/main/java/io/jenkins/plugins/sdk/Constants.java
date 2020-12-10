package io.jenkins.plugins.sdk;

/**
 * @author liuwei
 * @date 2020-12-09 21:21:02
 */
public class Constants {
  /** 消息类型 */
  public static final String MSG_TYPE_TEXT = "text";

  public static final String MSG_TYPE_LINK = "link";
  public static final String MSG_TYPE_MARKDOWN = "markdown";
  public static final String MSG_TYPE_ACTION_CARD = "actionCard";
  public static final String MSG_TYPE_FEED_CARD = "feedCard";

  /** UTF-8字符集 * */
  public static final String CHARSET_UTF8 = "UTF-8";

  /** HTTP请求相关 * */
  public static final String METHOD_POST = "POST";
  public static final String METHOD_GET = "GET";

  public static final String CONTENT_TYPE_FORM_DATA = "application/x-www-form-urlencoded";
  public static final String CONTENT_TYPE_FILE_UPLOAD = "multipart/form-data";
  public static final String CONTENT_TYPE_TEXT_XML = "text/xml";
  public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
  public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
  
  /** 响应编码 */
  public static final String ACCEPT_ENCODING = "Accept-Encoding";
  public static final String CONTENT_ENCODING = "Content-Encoding";
  public static final String CONTENT_ENCODING_GZIP = "gzip";
}
