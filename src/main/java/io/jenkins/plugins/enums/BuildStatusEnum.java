package io.jenkins.plugins.enums;

import io.jenkins.plugins.Messages;
import io.jenkins.plugins.tools.AntdColor;
import lombok.Getter;

/**
 * 构建状态
 *
 * @author liuwei
 * @date 2020-03-28 22:48
 */
@Getter
public enum BuildStatusEnum {

  /**
   * 开始
   */
  START(Messages.BuildStatusType_start(), AntdColor.GEEK_BLUE.toString()),

  /**
   * 失败
   */
  FAILURE(Messages.BuildStatusType_failure(), AntdColor.RED.toString()),

  /**
   * 成功
   */
  SUCCESS(Messages.BuildStatusType_success(), AntdColor.GREEN.toString()),

  /**
   * 取消
   */
  ABORTED(Messages.BuildStatusType_aborted(), AntdColor.CYAN.toString()),

  /**
   * 不稳定
   */
  UNSTABLE(Messages.BuildStatusType_unstable(), AntdColor.CYAN.toString()),

  /**
   * 未构建
   */
  NOT_BUILT(Messages.BuildStatusType_not_built(), AntdColor.CYAN.toString()),

  /**
   * 未知
   */
  UNKNOWN(Messages.BuildStatusType_unknown(), AntdColor.PURPLE.toString());


  private String label;


  private String color;

  BuildStatusEnum(String label, String color) {
    this.label = label;
    this.color = color;
  }
}
