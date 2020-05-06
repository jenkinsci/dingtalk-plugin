package io.jenkins.plugins.tools;

import hudson.model.TaskListener;
import io.jenkins.plugins.model.ButtonModel;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * 通用方法合集
 *
 * @author liuwei
 * @date 2020/3/28 16:04
 */
public class Utils {

  /**
   * 字符串分隔符
   */
  public static final String DELIMITER = "\n";

  /**
   * markdown 染色
   *
   * @param content 内容
   * @param color   颜色
   * @return 带颜色的内容
   */
  public static String dye(String content, String color) {
    return "<font color=" + color + ">" + content + "</font>";
  }

  /**
   * markdown 数组转字符串
   *
   * @param list 数组
   * @return 字符串
   */
  public static String join(Iterable<? extends CharSequence> list) {
    if (list == null) {
      return "";
    }
    return String.join(DELIMITER, list);
  }

  /**
   * 字符串转数组
   *
   * @param str 字符串
   * @return 数组
   */
  public static String[] split(String str) {
    return str.split(DELIMITER);
  }

}
