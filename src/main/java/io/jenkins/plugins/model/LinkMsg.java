package io.jenkins.plugins.model;

import java.util.Set;
import lombok.Builder;
import lombok.Data;

/**
 * @author liuwei
 * @date 2020/3/27 17:01
 * @desc 链接消息
 */
@Data
@Builder
public class LinkMsg {

  /**
   * 点击消息跳转的URL
   */
  private String messageUrl;

  /**
   * 图片URL
   */
  private String picUrl;
}
