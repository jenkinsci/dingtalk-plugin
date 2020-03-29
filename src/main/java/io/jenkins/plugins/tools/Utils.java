package io.jenkins.plugins.tools;

import hudson.model.TaskListener;
import io.jenkins.plugins.model.ButtonModel;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用方法合集
 *
 * @author liuwei
 * @date 2020/3/28 16:04
 */
public class Utils {

  /**
   * 统一输出日志
   *
   * @param listener 任务监听器
   * @param msg      消息
   */
  public static void log(TaskListener listener, String msg) {
    listener.error("钉钉机器人消息发送失败：%s", msg);
  }

  /**
   * 创建默认的按钮列表
   *
   * @param jobUrl 任务地址
   * @return 按钮列表
   */
  public static List<ButtonModel> createDefaultBtns(String jobUrl) {
    String changeLog = jobUrl + "/changes";
    String console = jobUrl + "/console";

    List<ButtonModel> btns = new ArrayList<>();
    btns.add(
        ButtonModel.of("更改记录", changeLog)
    );
    btns.add(
        ButtonModel.of("控制台", console)
    );

    return btns;
  }

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
   * @param markdown 数组
   * @return 字符串
   */
  public static String join(Iterable<? extends CharSequence>  markdown){
    return String.join("\n\r", markdown);
  }

}
