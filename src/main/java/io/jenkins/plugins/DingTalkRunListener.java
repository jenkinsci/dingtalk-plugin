package io.jenkins.plugins;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.Cause;
import hudson.model.Cause.RemoteCause;
import hudson.model.Cause.UpstreamCause;
import hudson.model.Cause.UserIdCause;
import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.User;
import hudson.model.listeners.RunListener;
import io.jenkins.plugins.context.PipelineEnvContext;
import io.jenkins.plugins.enums.BuildStatusEnum;
import io.jenkins.plugins.enums.MsgTypeEnum;
import io.jenkins.plugins.enums.NoticeOccasionEnum;
import io.jenkins.plugins.model.BuildJobModel;
import io.jenkins.plugins.model.ButtonModel;
import io.jenkins.plugins.model.MessageModel;
import io.jenkins.plugins.service.impl.DingTalkServiceImpl;
import io.jenkins.plugins.tools.DingTalkUtils;
import io.jenkins.plugins.tools.Logger;
import io.jenkins.plugins.tools.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jenkins.model.Jenkins;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * 所有项目触发
 *
 * @author liuwei
 */
@Log4j
@Extension
public class DingTalkRunListener extends RunListener<Run<?, ?>> {

  private final DingTalkServiceImpl service = new DingTalkServiceImpl();

  private final String rootPath = Jenkins.get().getRootUrl();

  @Override
  public void onStarted(Run<?, ?> run, TaskListener listener) {
    DingTalkGlobalConfig globalConfig = DingTalkGlobalConfig.getInstance();
    DingTalkUtils.log(listener, "全局配置信息，%s", Utils.toJson(globalConfig));
    this.send(run, listener, NoticeOccasionEnum.START);
  }

  @Override
  public void onCompleted(Run<?, ?> run, @NonNull TaskListener listener) {
    Result result = run.getResult();
    NoticeOccasionEnum noticeOccasion = getNoticeOccasion(result);
    try {
      this.send(run, listener, noticeOccasion);
    } catch (Exception e) {
      e.printStackTrace();
      DingTalkUtils.log(listener, "发送消息时报错: %s", e);
    }finally {
      // 重置环境变量
      PipelineEnvContext.reset();
    }
  }

  private NoticeOccasionEnum getNoticeOccasion(Result result) {
    if (Result.SUCCESS.equals(result)) {
      return NoticeOccasionEnum.SUCCESS;
    }
    if (Result.FAILURE.equals(result)) {
      return NoticeOccasionEnum.FAILURE;
    }
    if (Result.ABORTED.equals(result)) {
      return NoticeOccasionEnum.ABORTED;
    }
    if (Result.UNSTABLE.equals(result)) {
      return NoticeOccasionEnum.UNSTABLE;
    }
    if (Result.NOT_BUILT.equals(result)) {
      return NoticeOccasionEnum.NOT_BUILT;
    }
    return null;
  }

  private BuildStatusEnum getBuildStatus(NoticeOccasionEnum noticeOccasion) {
    switch (noticeOccasion) {
      case START:
        return BuildStatusEnum.START;
      case SUCCESS:
        return BuildStatusEnum.SUCCESS;
      case FAILURE:
        return BuildStatusEnum.FAILURE;
      case ABORTED:
        return BuildStatusEnum.ABORTED;
      case UNSTABLE:
        return BuildStatusEnum.UNSTABLE;
      case NOT_BUILT:
        return BuildStatusEnum.NOT_BUILT;
      default:
        return null;
    }
  }

  /**
   * @see <a
   * href="https://github.com/jenkinsci/build-user-vars-plugin/blob/master/src/main/java/org/jenkinsci/plugins/builduser/BuildUser.java">...</a>
   */
  private Map<String, String> getUser(Run<?, ?> run, TaskListener listener) {
    // 执行人信息
    User user = null;
    String executorName = null;
    String executorMobile = null;

    UserIdCause userIdCause = run.getCause(UserIdCause.class);
    if (userIdCause != null && userIdCause.getUserId() != null) {
      user = User.getById(userIdCause.getUserId(), false);
    }
    if (user == null) {
      RemoteCause remoteCause = run.getCause(RemoteCause.class);
      if (remoteCause != null) {
        executorName = String.format("%s %s", remoteCause.getAddr(), remoteCause.getNote());
      } else {
        UpstreamCause upstreamCause = run.getCause(UpstreamCause.class);
        if (upstreamCause != null) {
          Job<?, ?> job = Jenkins.get()
              .getItemByFullName(upstreamCause.getUpstreamProject(), Job.class);
          if (job != null) {
            Run<?, ?> upstream = job.getBuildByNumber(upstreamCause.getUpstreamBuild());
            if (upstream != null) {
              return getUser(run, listener);
            }
          }
          executorName = upstreamCause.getUpstreamProject();
        }
      }
      if (executorName == null) {
        DingTalkUtils.log(listener, "未获取到构建人信息，将尝试从构建信息中模糊匹配。");
        executorName = run.getCauses()
            .stream().map(Cause::getShortDescription)
            .collect(Collectors.joining());
      }
    } else {
      executorName = user.getDisplayName();
      executorMobile = user.getProperty(DingTalkUserProperty.class).getMobile();
      if (executorMobile == null) {
        DingTalkUtils.log(listener, "用户【%s】暂未设置手机号码，请前往 %s 添加。", executorName,
            user.getAbsoluteUrl() + "/configure");
      }
    }
    Map<String, String> result = new HashMap<>(16);
    result.put("name", executorName);
    result.put("mobile", executorMobile);
    return result;
  }

  private EnvVars getEnvVars(Run<?, ?> run, TaskListener listener) {
    EnvVars jobEnvVars;
    try {
      jobEnvVars = run.getEnvironment(listener);
    } catch (Exception e) {
      jobEnvVars = new EnvVars();
      log.error(e);
      DingTalkUtils.log(listener, "获取 job 任务的环境变量时发生异常");
      DingTalkUtils.log(listener, ExceptionUtils.getStackTrace(e));
      Thread.currentThread().interrupt();
    }
    try {
      EnvVars pipelineEnvVars = PipelineEnvContext.get();
      jobEnvVars.overrideAll(pipelineEnvVars);
    }catch (Exception e){
      log.error(e);
      DingTalkUtils.log(listener, "获取 pipeline 环境变量时发生异常");
      DingTalkUtils.log(listener, ExceptionUtils.getStackTrace(e));
    }
    return jobEnvVars;
  }

  private boolean skip(TaskListener listener, NoticeOccasionEnum noticeOccasion,
      DingTalkNotifierConfig notifierConfig) {
    String stage = noticeOccasion.name();
    Set<String> noticeOccasions = notifierConfig.getNoticeOccasions();
    if (noticeOccasions.contains(stage)) {
      return false;
    }
    DingTalkUtils.log(listener, "机器人 %s 已跳过 %s 环节", notifierConfig.getRobotName(), stage);
    return true;
  }


  private void send(Run<?, ?> run, TaskListener listener, NoticeOccasionEnum noticeOccasion) {
    Job<?, ?> job = run.getParent();
    DingTalkJobProperty property = job.getProperty(DingTalkJobProperty.class);

    if (property == null) {
      DingTalkUtils.log(listener, "当前任务未配置机器人，已跳过");
      return;
    }

    // 环境变量
    EnvVars envVars = getEnvVars(run, listener);

    // 执行人信息
    Map<String, String> user = getUser(run, listener);
    String executorName = envVars.get("EXECUTOR_NAME", user.get("name"));
    String executorMobile = envVars.get("EXECUTOR_MOBILE", user.get("mobile"));

    // 项目信息
    String projectName = job.getFullDisplayName();
    String projectUrl = job.getAbsoluteUrl();

    // 构建信息
    BuildStatusEnum statusType = getBuildStatus(noticeOccasion);
    String jobName = run.getDisplayName();
    String jobUrl = rootPath + run.getUrl();
    String duration = run.getDurationString();
    List<ButtonModel> btns = Utils.createDefaultBtns(jobUrl);
    List<String> result = new ArrayList<>();
    List<DingTalkNotifierConfig> notifierConfigs = property.getAvailableNotifierConfigs();

    for (DingTalkNotifierConfig item : notifierConfigs) {
      boolean skipped = skip(listener, noticeOccasion, item);

      if (skipped) {
        continue;
      }

      String robotId = item.getRobotId();
      String content = item.getContent();
      boolean atAll = item.isAtAll();
      Set<String> atMobiles = item.resolveAtMobiles(envVars);

      if (StringUtils.isNotEmpty(executorMobile)) {
        atMobiles.add(executorMobile);
      }

      String text = BuildJobModel.builder().projectName(projectName).projectUrl(projectUrl)
          .jobName(jobName)
          .jobUrl(jobUrl)
          .statusType(statusType)
          .duration(duration)
          .executorName(executorName)
          .executorMobile(executorMobile)
          .content(
              envVars.expand(content).replace("\\\\n", "\n")
          )
          .build()
          .toMarkdown();

      String statusLabel = statusType == null ? "unknown" : statusType.getLabel();

      MessageModel message = MessageModel.builder()
          .type(MsgTypeEnum.ACTION_CARD)
          .atAll(atAll)
          .atMobiles(atMobiles)
          .title(
              String.format("%s %s", projectName, statusLabel)
          )
          .text(text)
          .btns(btns)
          .build();

      DingTalkUtils.log(listener, "当前机器人信息，%s", Utils.toJson(item));
      DingTalkUtils.log(listener, "发送的消息详情，%s", Utils.toJson(message));

      String msg = service.send(robotId, message);

      if (msg != null) {
        result.add(msg);
      }
    }

    if (!result.isEmpty()) {
      result.forEach(msg -> Logger.error(listener, msg));
    }
  }
}
