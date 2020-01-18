package io.jenkins.plugins.enums;

import lombok.Getter;

@Getter
@SuppressWarnings("all")
public enum BuildStatusType {

  ABORTED("grey.gif", "已取消"),
  DOING("blue_anime.gif", "构建中"),
  SUCCESS("green.gif", "成功"),
  FAILURE("red.gif", "失败"),
  UNKNOW("yellow.gif", "未知");

  private String icon;

  private String label;

  private static final String IMAGE_SERVER = "http://q346bl12y.bkt.clouddn.com/";


  BuildStatusType(String icon, String label) {
    this.icon = IMAGE_SERVER + icon;
    this.label = label;
  }
}
