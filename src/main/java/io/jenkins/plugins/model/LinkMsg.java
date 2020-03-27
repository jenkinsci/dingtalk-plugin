package io.jenkins.plugins.model;

import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author liuwei
 * @date 2020/3/27 17:01
 * @desc 链接消息
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class LinkMsg extends BaseMsg{

  /**
   * 点击消息跳转的URL
   */
  private String messageUrl;

  /**
   * 图片URL
   */
  private String picUrl;
}
