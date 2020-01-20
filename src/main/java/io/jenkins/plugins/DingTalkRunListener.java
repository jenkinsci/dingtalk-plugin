package io.jenkins.plugins;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Cause.UserIdCause;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.tasks.Publisher;
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
 * @author liuwei
 * @date 2019/12/28 15:31
 * @desc freeStyle 项目构建
 */
@Extension
@SuppressWarnings("unused")
public class DingTalkRunListener extends RunListener<AbstractBuild<?, ?>> {

  private DingTalkServiceImpl service = new DingTalkServiceImpl();

  private DingTalkGlobalConfig globalConfig = DingTalkGlobalConfig.getInstance();

  private final String rootPath = Jenkins.get().getRootUrl();

  public void send(AbstractBuild<?, ?> build, TaskListener listener, BuildStatusEnum statusType) {
    AbstractProject<?, ?> project = build.getProject();
    UserIdCause user = build.getCause(UserIdCause.class);

    if (user == null) {
      user = new UserIdCause();
    }

    // 项目信息
    String projectName = project.getFullDisplayName();
    String projectUrl = project.getAbsoluteUrl();

    // 构建信息
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String jobName = build.getDisplayName();
    String jobUrl = rootPath + build.getUrl();
    String duration = build.getDurationString();
    String executorName = user.getUserName();
    String executorPhone = user.getShortDescription();
    String datetime = formatter.format(build.getTimestamp().getTime());
    String changeLog = jobUrl + "/changes";
    String console = jobUrl + "/console";

    List<String> result = new ArrayList<>();

    for (Publisher publisher : project.getPublishersList()) {
      if (publisher instanceof DingTalkNotifier) {
        DingTalkNotifier notifier = (DingTalkNotifier) publisher;
        List<DingTalkNotifierConfig> notifierConfigs = notifier.getCheckedNotifierConfigs();
        notifierConfigs.forEach(notifierConfig -> {
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
    }
  }

  @Override
  public void onStarted(AbstractBuild<?, ?> build, TaskListener listener) {
    if (
        globalConfig.getNoticeOccasions().contains(
            NoticeOccasionEnum.START.name()
        )
    ) {
      this.send(build, listener, BuildStatusEnum.START);
    }
  }

  @Override
  public void onCompleted(AbstractBuild<?, ?> build, @Nonnull TaskListener listener) {
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
