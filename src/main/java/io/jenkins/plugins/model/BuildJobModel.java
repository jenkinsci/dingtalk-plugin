package io.jenkins.plugins.model;

import io.jenkins.plugins.enums.BuildStatusEnum;
import io.jenkins.plugins.tools.Utils;
import java.util.Arrays;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

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

  public String toMarkdown() {
    String owner = StringUtils.isEmpty(executorMobile) ? executorName : ("@" + executorMobile);
    String status = Utils.dye(statusType.getLabel(), statusType.getColor());
    String consoleUrl = jobUrl + "console";

    return Utils.join(
        Arrays.asList(
            String.format("# %s", projectName),
            "---",
            String.format(
                "[%s](%s) - %s ",
                jobName,
                jobUrl,
                status
            ),
            "---",
            owner,
            "---",
            String.format(
                "%s - [Console](%s)",
                duration,
                consoleUrl
            ),
            content
        )
    );
  }
}
