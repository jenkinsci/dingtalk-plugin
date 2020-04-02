package io.jenkins.plugins;

import hudson.Extension;
import hudson.model.Cause.UserIdCause;
import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.User;
import hudson.model.listeners.RunListener;
import io.jenkins.plugins.enums.BuildStatusEnum;
import io.jenkins.plugins.enums.MsgTypeEnum;
import io.jenkins.plugins.enums.NoticeOccasionEnum;
import io.jenkins.plugins.model.BuildJobModel;
import io.jenkins.plugins.model.ButtonModel;
import io.jenkins.plugins.model.MessageModel;
import io.jenkins.plugins.service.impl.DingTalkServiceImpl;
import io.jenkins.plugins.tools.Utils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import jenkins.model.Jenkins;

/**
 * 所有项目触发
 *
 * @author liuwei
 * @date 2019/12/28 15:31
 */
@SuppressWarnings("unused")
@Extension
public class DingTalkRunListener extends RunListener<Run<?, ?>> {

  private DingTalkServiceImpl service = new DingTalkServiceImpl();

  private DingTalkGlobalConfig globalConfig = DingTalkGlobalConfig.getInstance();

  private final String rootPath = Jenkins.get().getRootUrl();

  public void send(Run<?, ?> run, TaskListener listener, BuildStatusEnum statusType) {
    Job<?, ?> job = run.getParent();
    UserIdCause userIdCause = run.getCause(UserIdCause.class);
    String userId = userIdCause == null ? null : userIdCause.getUserId();
    // 执行人信息
    User user = null;
    String executorName = null;
    String executorMobile = null;
    // 执行人不存在
    if (userIdCause != null && userIdCause.getUserId() != null) {
      user = user = User.getById(userIdCause.getUserId(), false);
    }
    if (user != null) {
      executorName = user.getDisplayName();
      executorMobile = user.getProperty(DingTalkUserProperty.class).getMobile();
    } else {
      executorName = run.getCauses()
          .stream()
          .map(
              item -> item.getShortDescription().replace(
                  "Started by remote host",
                  "Host"
              )
          )
          .collect(Collectors.joining());
    }

    // 项目信息
    String projectName = job.getFullDisplayName();
    String projectUrl = job.getAbsoluteUrl();

    // 构建信息
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String jobName = run.getDisplayName();
    String jobUrl = rootPath + run.getUrl();
    String duration = run.getDurationString();
    List<ButtonModel> btns = Utils.createDefaultBtns(jobUrl);
    List<String> result = new ArrayList<>();
    DingTalkJobProperty property = job.getProperty(DingTalkJobProperty.class);
    List<DingTalkNotifierConfig> notifierConfigs = property.getCheckedNotifierConfigs();
    for (DingTalkNotifierConfig item : notifierConfigs) {
      String robotId = item.getRobotId();
      Set<String> atMobiles = item.getAtMobiles();
      String text = BuildJobModel.builder()
          .projectName(projectName)
          .projectUrl(projectUrl)
          .jobName(jobName)
          .jobUrl(jobUrl)
          .statusType(statusType)
          .duration(duration)
          .executorName(executorName)
          .executorMobile(executorMobile)
          .build()
          .toMarkdown();
      MessageModel message = MessageModel.builder()
          .type(MsgTypeEnum.ACTION_CARD)
          .atMobiles(atMobiles)
          .text(text)
          .btns(btns)
          .build();
      String msg = service.send(robotId, message);
      if (msg != null) {
        result.add(msg);
      }
    }
    if (listener != null && !result.isEmpty()) {
      result.forEach(msg -> Utils.log(listener, msg));
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
