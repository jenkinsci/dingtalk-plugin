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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import jenkins.model.Jenkins;
import lombok.extern.log4j.Log4j;

/**
 * 所有项目触发
 *
 * @author liuwei
 * @date 2019/12/28 15:31
 */
@SuppressWarnings("unused")
@Log4j
@Extension
public class DingTalkRunListener extends RunListener<Run<?, ?>> {

  private DingTalkServiceImpl service = new DingTalkServiceImpl();

  private DingTalkGlobalConfig globalConfig = DingTalkGlobalConfig.get();

  private final String rootPath = Jenkins.get().getRootUrl();

  public DingTalkRunListener() {
    log.debug("================= 钉钉监听器已生效 =================");
  }

  public void send(Run<?, ?> run, TaskListener listener, BuildStatusEnum statusType) {
    PrintStream logger = listener.getLogger();
    boolean isVerbose = globalConfig.isVerbose();
    Job<?, ?> job = run.getParent();
    UserIdCause userIdCause = run.getCause(UserIdCause.class);
    // 执行人信息
    User user = null;
    String executorName;
    String executorMobile = null;
    if (userIdCause != null && userIdCause.getUserId() != null) {
      user = User.getById(userIdCause.getUserId(), false);
    }

    if (user == null) {
      if (isVerbose) {
        logger.println("未获取到构建人信息，将尝试从构建信息中模糊匹配。");
      }
      executorName = run.getCauses()
          .stream()
          .map(
              item -> item.getShortDescription().replace(
                  "Started by remote host",
                  "Host"
              )
          )
          .collect(Collectors.joining());
    } else {
      executorName = user.getDisplayName();
      executorMobile = user.getProperty(DingTalkUserProperty.class).getMobile();
      if (isVerbose && executorMobile == null) {
        logger.println(String.format(
            "[%s] 暂未设置手机号码，请前往 %s 添加。",
            executorName,
            String.format(
                "<a href=\"%s\">%s</a>",
                user.getAbsoluteUrl() + "/configure",
                "设置页面"
            )
        ));
      }
    }

    // 项目信息
    String projectName = job.getFullDisplayName();
    String projectUrl = job.getAbsoluteUrl();

    // 构建信息
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

    if (result.isEmpty()) {
      if (isVerbose) {
        logger.println("钉钉已成功发送消息，请注意查收。");
      }
    } else {
      result.forEach(msg -> Utils.log(listener, msg));
    }
  }

  @Override
  public void onStarted(Run<?, ?> build, TaskListener listener) {
    boolean isVerbose = globalConfig.isVerbose();
    PrintStream logger = listener.getLogger();
    if (isVerbose) {
      logger.println(String.format(
          "钉钉全局配置信息：<pre>%s</pre>",
          globalConfig
      ));
    }
    if (
        globalConfig.getNoticeOccasions().contains(
            NoticeOccasionEnum.START.name()
        )
    ) {
      this.send(build, listener, BuildStatusEnum.START);
    } else if (isVerbose) {
      listener.getLogger().println("项目开始构建：无需触发钉钉");
    }
  }

  @Override
  public void onCompleted(Run<?, ?> build, @Nonnull TaskListener listener) {
    BuildStatusEnum statusType = null;
    boolean skipped = true;
    boolean isVerbose = globalConfig.isVerbose();
    Set<String> noticeOccasions = globalConfig.getNoticeOccasions();
    Result result = build.getResult();
    PrintStream logger = listener.getLogger();
    if (Result.SUCCESS.equals(result)) {

      if (noticeOccasions.contains(NoticeOccasionEnum.SUCCESS.name())) {
        skipped = false;
        statusType = BuildStatusEnum.SUCCESS;
      }

    } else if (Result.FAILURE.equals(result)) {

      if (noticeOccasions.contains(NoticeOccasionEnum.FAILURE.name())) {
        skipped = false;
        statusType = BuildStatusEnum.FAILURE;
      }

    } else if (Result.ABORTED.equals(result)) {

      if (noticeOccasions.contains(NoticeOccasionEnum.ABORTED.name())) {
        skipped = false;
        statusType = BuildStatusEnum.ABORTED;
      }

    } else if (Result.UNSTABLE.equals(result)) {

      if (noticeOccasions.contains(NoticeOccasionEnum.UNSTABLE.name())) {
        skipped = false;
        statusType = BuildStatusEnum.UNSTABLE;
      }

    } else if (Result.NOT_BUILT.equals(result)) {

      if (noticeOccasions.contains(NoticeOccasionEnum.NOT_BUILT.name())) {
        skipped = false;
        statusType = BuildStatusEnum.NOT_BUILT;
      }

    } else {
      statusType = BuildStatusEnum.UNKNOWN;
      if (isVerbose) {
        logger.println(String.format(
            "不匹配的构建结果类型：%s",
            result == null ? "null" : result
        ));
      }
    }

    if (skipped) {
      if (isVerbose) {
        logger.println("构建已结束：无需触发钉钉");
      }
      return;
    }

    this.send(build, listener, statusType);
  }

}
