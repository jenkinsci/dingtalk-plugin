package io.jenkins.plugins.model;

import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author liuwei
 * @date 2020/3/27 17:02
 * @desc markdown 消息
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class MarkdownMsg extends BaseMsg {

}
