package io.jenkins.plugins.model;

import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author liuwei
 * @date 2020/3/27 16:59
 * @desc 文本消息
 */

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TextMsg extends BaseMsg{

}
