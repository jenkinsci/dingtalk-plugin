package com.liuweigl.dingtalk;

import com.liuweigl.dingtalk.enums.BuildStatusType;
import com.liuweigl.dingtalk.enums.NoticeOccasionType;
import com.liuweigl.dingtalk.model.BuildMessage;
import com.liuweigl.dingtalk.service.impl.DingTalkServiceImpl;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Cause.UserIdCause;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.tasks.Publisher;
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
public class DingTalkListener extends RunListener<AbstractBuild<?,?>> {

  private DingTalkServiceImpl service = new DingTalkServiceImpl();

  private DingTalkGlobalConfig globalConfig = DingTalkGlobalConfig.getInstance();

  private final String rootPath = Jenkins.get().getRootUrl();

  public void send(AbstractBuild<?,?> build, TaskListener listener, BuildStatusType statusType) {
    AbstractProject<?,?> project = build.getProject();
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
          BuildMessage message = BuildMessage.builder()
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
          String msg = service.send(robotId, message);
          if (msg != null) {
            result.add(msg);
          }
        });
        if (listener != null && !result.isEmpty()) {
          result.forEach(listener::error);
        }
      }
    }
  }


  @Override
  public void onStarted(AbstractBuild<?,?> build, TaskListener listener) {
    if (
        globalConfig.getNoticeOccasions().contains(
            NoticeOccasionType.START.name()
        )
    ) {
      this.send(build, listener, BuildStatusType.DOING);
    }
  }


  @Override
  public void onCompleted(AbstractBuild<?,?> build, @Nonnull TaskListener listener) {
    BuildStatusType statusType = null;
    Set<String> noticeOccasions = globalConfig.getNoticeOccasions();
    Result result = build.getResult();
    if (Result.SUCCESS.equals(result)) {

      if (noticeOccasions.contains(NoticeOccasionType.SUCCESS.name())) {
        statusType = BuildStatusType.SUCCESS;
      }

    } else if (Result.FAILURE.equals(result)) {

      if (noticeOccasions.contains(NoticeOccasionType.FAIL.name())) {
        statusType = BuildStatusType.FAILURE;
      }

    } else if (Result.ABORTED.equals(result)) {

      if (noticeOccasions.contains(NoticeOccasionType.CANCEL.name())) {
        statusType = BuildStatusType.ABORTED;
      }

    } else {
      statusType = BuildStatusType.UNKNOW;
    }

    if (statusType != null) {
      this.send(build, listener, statusType);
    }

  }

}
