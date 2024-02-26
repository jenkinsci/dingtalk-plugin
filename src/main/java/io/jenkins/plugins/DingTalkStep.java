package io.jenkins.plugins;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.enums.BtnLayoutEnum;
import io.jenkins.plugins.enums.MsgTypeEnum;
import io.jenkins.plugins.model.ButtonModel;
import io.jenkins.plugins.model.MessageModel;
import io.jenkins.plugins.service.DingTalkService;
import io.jenkins.plugins.tools.DingTalkUtils;
import io.jenkins.plugins.tools.Logger;
import io.jenkins.plugins.tools.Utils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jenkins.model.Jenkins;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * 支持 pipeline 中使用
 *
 * <p>* 不要使用 @Data 注解，spotbugs 会报错 *
 *
 * <p>* Redundant nullcheck of this$title, which is known to be non-null in *
 * io.jenkins.plugins.model.MessageModel.equals(Object)
 *
 * @author liuwei
 */
@Getter
@Setter
@SuppressWarnings("unused")
public class DingTalkStep extends Step {

  /**
   * 机器人 id
   */
  private String robot;

  private MsgTypeEnum type;

  private Set<String> at;

  private boolean atAll;

  private String title;

  private List<String> text;

  private String messageUrl;

  private String picUrl;

  private String singleTitle;

  private String singleUrl;

  private List<ButtonModel> btns;

  private BtnLayoutEnum btnLayout;

  private boolean hideAvatar;

  private String rootPath = Jenkins.get().getRootUrl();

  @DataBoundConstructor
  public DingTalkStep(String robot) {
    this.robot = robot;
  }

  @DataBoundSetter
  public void setType(MsgTypeEnum type) {
    if (type == null) {
      type = MsgTypeEnum.MARKDOWN;
    }
    this.type = type;
  }

  @DataBoundSetter
  public void setAt(List<String> at) {
    if (!(at == null || at.isEmpty())) {
      this.at = new HashSet<>(at);
    }
  }

  @DataBoundSetter
  public void setAtAll(boolean atAll) {
    this.atAll = atAll;
  }

  @DataBoundSetter
  public void setTitle(String title) {
    this.title = title;
  }

  @DataBoundSetter
  public void setText(List<String> text) {
    this.text = text;
  }

  @DataBoundSetter
  public void setMessageUrl(String messageUrl) {
    this.messageUrl = messageUrl;
  }

  @DataBoundSetter
  public void setPicUrl(String picUrl) {
    this.picUrl = picUrl;
  }

  @DataBoundSetter
  public void setSingleTitle(String singleTitle) {
    this.singleTitle = singleTitle;
  }

  @DataBoundSetter
  public void setSingleUrl(String singleUrl) {
    this.singleUrl = singleUrl;
  }

  @DataBoundSetter
  public void setBtns(List<ButtonModel> btns) {
    this.btns = btns;
  }

  @DataBoundSetter
  public void setBtnLayout(BtnLayoutEnum btnLayout) {
    this.btnLayout = btnLayout;
  }

  @DataBoundSetter
  public void setHideAvatar(boolean hideAvatar) {
    this.hideAvatar = hideAvatar;
  }

  /**
   * 获取按钮排列方向
   *
   * @return 水平或则垂直
   */
  public String getBtnLayout() {
    return BtnLayoutEnum.V.equals(btnLayout) ? "0" : "1";
  }

  public String isHideAvatar() {
    return hideAvatar ? "1" : "0";
  }

  public String send(Run<?, ?> run, EnvVars envVars, TaskListener listener) {
    boolean defaultBtns =
        MsgTypeEnum.ACTION_CARD.equals(type)
            && StringUtils.isEmpty(singleTitle)
            && (btns == null || btns.isEmpty());

    if (defaultBtns) {
      String jobUrl = rootPath + run.getUrl();
      this.btns = Utils.createDefaultBtns(jobUrl);
    } else if (btns != null) {
      btns.forEach(
          item -> {
            item.setTitle(envVars.expand(item.getTitle()));
            item.setActionUrl(envVars.expand(item.getActionUrl()));
          });
    }

    if (at != null) {
      String atStr = envVars.expand(Utils.join(at));
      this.at = new HashSet<>(Arrays.asList(Utils.split(atStr)));
    }

    MessageModel message = MessageModel.builder()
        .type(type)
        .atMobiles(at)
        .atAll(atAll)
        .title(envVars.expand(title))
        .text(envVars.expand(Utils.join(text)))
        .messageUrl(envVars.expand(messageUrl))
        .picUrl(envVars.expand(picUrl))
        .singleTitle(envVars.expand(singleTitle))
        .singleUrl(envVars.expand(singleUrl))
        .btns(btns)
        .btnOrientation(getBtnLayout())
        .hideAvatar(isHideAvatar())
        .build();

    DingTalkUtils.log(listener, "当前机器人信息，%s",
        Utils.toJson(DingTalkGlobalConfig.getRobot(robot)));
    DingTalkUtils.log(listener, "发送的消息详情，%s", Utils.toJson(message));

    return DingTalkService.getInstance().send(envVars.expand(robot), message);
  }

  @Override
  public StepExecution start(StepContext context) throws Exception {
    return new DingTalkStepExecution(this, context);
  }

  private static class DingTalkStepExecution extends StepExecution {

    private static final long serialVersionUID = 1L;
    private final transient DingTalkStep step;

    private DingTalkStepExecution(DingTalkStep step, StepContext context) {
      super(context);
      this.step = step;
    }


    @Override
    public boolean start() throws Exception {
      StepContext context = this.getContext();
      Run<?, ?> run = context.get(Run.class);
      EnvVars envVars = context.get(EnvVars.class);
      TaskListener listener = context.get(TaskListener.class);
      try {
        String result = this.step.send(run, envVars, listener);
        if (StringUtils.isEmpty(result)) {
          context.onSuccess(result);
        } else {
          context.onFailure(new Throwable(Logger.format(result)));
        }
        return true;
      } catch (Exception e) {
        context.onFailure(e);
        return false;
      }
    }
  }

  @Extension
  public static class DescriptorImpl extends StepDescriptor {

    @Override
    public Set<? extends Class<?>> getRequiredContext() {
      return new HashSet<Class<?>>() {{
        add(Run.class);
        add(TaskListener.class);
      }};
    }

    @Override
    public String getFunctionName() {
      return "dingtalk";
    }

    @NonNull
    @Override
    public String getDisplayName() {
      return "Send DingTalk message";
    }
  }
}
