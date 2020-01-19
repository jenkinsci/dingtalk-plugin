package io.jenkins.plugins.model;

import com.dingtalk.api.request.OapiRobotSendRequest.At;
import io.jenkins.plugins.enums.BuildStatusType;
import java.util.Collections;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
@Builder
public class BuildMessage {

  private String projectName;

  private String projectUrl;

  private String jobName;

  private String jobUrl;

  private BuildStatusType statusType;

  private String duration;

  private String executorName;

  private String executorPhone;

  private String datetime;

  private String changeLog;

  private String detailUrl;


  public At getAt() {
    At at = new At();
    at.setAtMobiles(Collections.singletonList(this.executorPhone));
    return at;
  }

  public String getText() {

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
        StringUtils.isEmpty(this.executorPhone) ? this.executorName : ("@" + this.executorPhone)
    )
        + "\n";
  }
}
