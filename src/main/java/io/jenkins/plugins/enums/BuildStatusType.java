package io.jenkins.plugins.enums;

import lombok.Getter;

@Getter
@SuppressWarnings("all")
public enum BuildStatusType {

  START("blue_anime.gif", "开始构建"),
  ABORTED("grey.gif", "已取消"),
  UNSTABLE("yellow.gif", "构建不稳定"),
  FAILURE("red.gif", "失败"),
  SUCCESS("green.gif", "成功"),
  NOT_BUILT("nobuilt.gif", "未构建"),
  UNKNOW("warning.gif", "未知");

  private String icon;

  private String label;

  private static final String IMAGE_SERVER = "http://q346bl12y.bkt.clouddn.com/";


  BuildStatusType(String icon, String label) {
    this.icon = IMAGE_SERVER + icon;
    this.label = label;
  }
}
