package io.jenkins.plugins.model;

import com.dingtalk.api.request.OapiRobotSendRequest.At;
import io.jenkins.plugins.enums.BuildStatusEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
@Builder
public class BuildJobModel {

  private String projectName;

  private String projectUrl;

  private String jobName;

  private String jobUrl;

  private BuildStatusEnum statusType;

  private String duration;

  private String datetime;

  private String executorName;

  private String executorMobile;

  public String toMarkdown() {

    return "# "
        + "["
        + this.projectName
        + "]"
        + "("
        + this.projectUrl
        + ")"
        + "\n"
        + "---"
        + "\n"

        + "<font>"
        + "- 任务："
        + "["
        + this.jobName
        + "]"
        + "("
        + this.jobUrl
        + ")"
        + "\n"

        + "- 状态："
        + this.statusType.getLabel()
        + "![]"
        + "("
        + this.statusType.getIcon()
        + ")"
        + "\n"

        + "- 持续时间："
        + this.duration
        + "\n"

        + "- 执行时间："
        + this.datetime
        + "\n"

        + "- 执行人："
        + (
        StringUtils.isEmpty(this.executorMobile) ? this.executorName : ("@" + this.executorMobile)
    )
        + "\n"
        ;
  }
}
