package io.jenkins.plugins.model;

import io.jenkins.plugins.enums.BuildStatusEnum;
import io.jenkins.plugins.tools.Utils;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liuwei
 */
@Data
@Builder
public class BuildJobModel {

  private String projectName;

  private String projectUrl;

  private String jobName;

  private String jobUrl;

  private BuildStatusEnum statusType;

  private String duration;

  private String executorName;

  private String executorMobile;

  private String content;

  private String change;

  public String toMarkdown() {

    return Stream.of(
            String.format("# [%s](%s)", projectName, projectUrl),
            "---",
            String.format("- 任务：[%s](%s)", jobName, jobUrl),
            String.format("- 状态：%s",
                Utils.dye(
                    statusType.getLabel(),
                    statusType.getColor()
                )
            ),
            String.format("- 持续时间：%s", duration),
            String.format("- 执行人：%s", executorName),
            StringUtils.isEmpty(change) ? "" : String.format("- 变更：%s", change),
            content)
        .filter(StringUtils::isNotEmpty)
        .collect(Collectors.joining("\n"));
  }
}
