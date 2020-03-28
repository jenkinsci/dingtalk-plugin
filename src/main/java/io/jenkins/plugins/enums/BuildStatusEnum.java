package io.jenkins.plugins.enums;

import io.jenkins.plugins.Messages;
import lombok.Getter;

/**
 * @author liuwei
 * @date 2020-03-28 22:48
 * @desc 构建状态
 */
@Getter
public enum BuildStatusEnum {

  /**
   * 开始构建
   */
  START("\uD83D\uDE09", Messages.BuildStatusType_start()),

  /**
   * 已取消
   */
  ABORTED("\uD83D\uDE1C", Messages.BuildStatusType_aborted()),

  /**
   * 失败
   */
  FAILURE("\uD83D\uDE2D", Messages.BuildStatusType_failure()),

  /**
   * 成功
   */
  SUCCESS("\uD83D\uDE18", Messages.BuildStatusType_success()),

  /**
   * 构建不稳定
   */
  UNSTABLE("\uD83D\uDE2A", Messages.BuildStatusType_unstable()),

  /**
   * 未构建
   */
  NOT_BUILT("\uD83D\uDE34", Messages.BuildStatusType_not_built()),

  /**
   * 未知
   */
  UNKNOWN("\uD83D\uDE35", Messages.BuildStatusType_unknown());

  private String icon;

  private String label;

  BuildStatusEnum(String icon, String label) {
    this.icon = icon;
    this.label = label;
  }
}
