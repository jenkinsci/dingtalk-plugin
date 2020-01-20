package io.jenkins.plugins.enums;

import io.jenkins.plugins.Messages;
import lombok.Getter;

@Getter
public enum BuildStatusEnum {

  /**
   * 开始构建
   */
  START("start.gif", Messages.BuildStatusType_start()),

  /**
   * 已取消
   */
  ABORTED("aborted.gif", Messages.NoticeOccasion_aborted()),

  /**
   * 失败
   */
  FAILURE("failure.gif", Messages.BuildStatusType_failure()),

  /**
   * 成功
   */
  SUCCESS("success.gif", Messages.BuildStatusType_success()),

  /**
   * 构建不稳定
   */
  UNSTABLE("unstable.gif", Messages.BuildStatusType_unstable()),

  /**
   * 未构建
   */
  NOT_BUILT("not_built.gif", Messages.BuildStatusType_not_built()),

  /**
   * 未知
   */
  UNKNOWN("unknown.gif", Messages.BuildStatusType_unknown());

  private String icon;

  private String label;

  /**
   * TODO: 可配置
   */
  private static final String IMAGE_SERVER = "http://q346bl12y.bkt.clouddn.com/";


  BuildStatusEnum(String icon, String label) {
    this.icon = IMAGE_SERVER + icon;
    this.label = label;
  }
}
