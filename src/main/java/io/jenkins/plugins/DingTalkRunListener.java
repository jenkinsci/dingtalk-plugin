package io.jenkins.plugins;

import hudson.Extension;
import hudson.model.*;
import hudson.model.Cause.UserIdCause;
import hudson.model.listeners.RunListener;
import io.jenkins.plugins.enums.BuildStatusEnum;
import io.jenkins.plugins.enums.NoticeOccasionEnum;
import io.jenkins.plugins.model.BuildJobModel;
import io.jenkins.plugins.service.impl.DingTalkServiceImpl;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import jenkins.model.Jenkins;

/**
 * 监听 job 任务，使用钉钉机器人发送消息
 *
 * @author liuwei
 * @date 2019/12/28 15:31
 */

@Extension
public class DingTalkRunListener extends RunListener<Run<?, ?>> {

  private DingTalkServiceImpl service = new DingTalkServiceImpl();

  private DingTalkGlobalConfig globalConfig = DingTalkGlobalConfig.getInstance();

  private final String rootPath = Jenkins.get().getRootUrl();

  public void send(Run<?, ?> run, TaskListener listener, BuildStatusEnum statusType) {
    Job<?, ?> job = run.getParent();
    User user = User.current();

    if (user == null) {
      user = User.getUnknown();
    }

    // 项目信息
    String projectName = job.getFullDisplayName();
    String projectUrl = job.getAbsoluteUrl();
    UserIdCause cause = job.getLastBuild().getCause(Cause.UserIdCause.class);

    // 构建信息
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String jobName = run.getDisplayName();
    String jobUrl = rootPath + run.getUrl();
    String duration = run.getDurationString();
//    String executorName = user.getDisplayName();
    String executorName = cause != null ? cause.getUserName() : user.getDisplayName();
    String executorPhone = user.getProperty(DingTalkUserProperty.class).getMobile();
    String datetime = formatter.format(run.getTimestamp().getTime());
    String changeLog = jobUrl + "/changes";
    String console = jobUrl + "/console";

    List<String> result = new ArrayList<>();
    DingTalkJobProperty property = job.getProperty(DingTalkJobProperty.class);
    property.getCheckedNotifierConfigs().forEach(notifierConfig -> {
      String robotId = notifierConfig.getRobotId();
      Set<String> atMobiles = notifierConfig.getAtMobiles();
      BuildJobModel buildJobModel = BuildJobModel.builder()
          .projectName(projectName)
          .projectUrl(projectUrl)
          .jobName(jobName)
          .jobUrl(jobUrl)
          .statusType(statusType)
          .duration(duration)
          .datetime(datetime)
          .executorName(executorName)
          .executorMobile(executorPhone)
          .atMobiles(atMobiles)
          .changeLog(changeLog)
          .console(console)
          .build();
      String msg = service.send(robotId, buildJobModel);
      if (msg != null) {
        result.add(msg);
      }
    });
    if (listener != null && !result.isEmpty()) {
      result.forEach(msg -> listener.error("钉钉机器人消息发送失败：%s", msg));
    }
  }

  @Override
  public void onStarted(Run<?, ?> build, TaskListener listener) {
    if (
        globalConfig.getNoticeOccasions().contains(
            NoticeOccasionEnum.START.name()
        )
    ) {
      this.send(build, listener, BuildStatusEnum.START);
    }
  }

  @Override
  public void onCompleted(Run<?, ?> build, @Nonnull TaskListener listener) {
    BuildStatusEnum statusType = null;
    Set<String> noticeOccasions = globalConfig.getNoticeOccasions();
    Result result = build.getResult();
    if (Result.SUCCESS.equals(result)) {

      if (noticeOccasions.contains(NoticeOccasionEnum.SUCCESS.name())) {
        statusType = BuildStatusEnum.SUCCESS;
      }

    } else if (Result.FAILURE.equals(result)) {

      if (noticeOccasions.contains(NoticeOccasionEnum.FAILURE.name())) {
        statusType = BuildStatusEnum.FAILURE;
      }

    } else if (Result.ABORTED.equals(result)) {

      if (noticeOccasions.contains(NoticeOccasionEnum.ABORTED.name())) {
        statusType = BuildStatusEnum.ABORTED;
      }

    } else if (Result.UNSTABLE.equals(result)) {

      if (noticeOccasions.contains(NoticeOccasionEnum.UNSTABLE.name())) {
        statusType = BuildStatusEnum.UNSTABLE;
      }

    } else if (Result.NOT_BUILT.equals(result)) {

      if (noticeOccasions.contains(NoticeOccasionEnum.NOT_BUILT.name())) {
        statusType = BuildStatusEnum.NOT_BUILT;
      }

    } else {
      statusType = BuildStatusEnum.UNKNOWN;
    }

    if (statusType != null) {
      this.send(build, listener, statusType);
    }

  }

}
