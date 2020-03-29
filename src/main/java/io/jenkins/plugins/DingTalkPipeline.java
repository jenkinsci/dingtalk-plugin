package io.jenkins.plugins;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import io.jenkins.plugins.enums.MsgTypeEnum;
import io.jenkins.plugins.model.ButtonModel;
import io.jenkins.plugins.model.MessageModel;
import io.jenkins.plugins.service.impl.DingTalkServiceImpl;
import io.jenkins.plugins.tools.Utils;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * @author liuwei
 * @date 2020/3/27 16:36
 * @desc 支持 pipeline 中使用
 */

@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("unused")
public class DingTalkPipeline extends Builder implements SimpleBuildStep {

  /**
   * 机器人 id
   */
  private String robot;

  private MsgTypeEnum type;

  private Set<String> at;

  private boolean atAll;

  private String title;

  private String text;

  private String messageUrl;

  private String picUrl;

  private String singleTitle;

  private String singleUrl;

  private List<ButtonModel> btns;

  private String btnOrientation;

  private String hideAvatar;

  private String rootPath = Jenkins.get().getRootUrl();

  private DingTalkServiceImpl service = new DingTalkServiceImpl();

  @DataBoundConstructor
  public DingTalkPipeline(String robot, String text) {
    this.robot = robot;
    this.text = text;
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
  public void setBtnOrientation(String btnOrientation) {
    this.btnOrientation = btnOrientation;
  }

  @DataBoundSetter
  public void setHideAvatar(String hideAvatar) {
    this.hideAvatar = hideAvatar;
  }

  @Override
  public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace,
      @Nonnull Launcher launcher, @Nonnull TaskListener listener)
      throws InterruptedException, IOException {
    if (
        MsgTypeEnum.ACTION_CARD.equals(type) &&
            StringUtils.isEmpty(singleTitle) &&
            (btns == null || btns.isEmpty())
    ) {
      String jobUrl = rootPath + run.getUrl();
      this.btns = Utils.createDefaultBtns(jobUrl);
    }

    System.out.println("=========================== 接收到的参数 ===========================");
    System.out.println(this.toString());

    System.out.println("=========================== 钉钉返回信息 ===========================");
    String result = service.send(
        robot,
        MessageModel.builder()
            .type(type)
            .atMobiles(at)
            .atAll(atAll)
            .title(title)
            .text(text)
            .messageUrl(messageUrl)
            .picUrl(picUrl)
            .singleTitle(singleTitle)
            .singleUrl(singleUrl)
            .btns(btns)
            .btnOrientation(btnOrientation)
            .hideAvatar(hideAvatar)
            .build()
    );
    if (!StringUtils.isEmpty(result)) {
      Utils.log(listener, result);
    }

    System.out.println(result);

  }

  @Symbol("dingTalk")
  @Extension
  public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

    @Nonnull
    @Override
    public String getDisplayName() {
      return "DingTalk";
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> t) {
      return true;
    }
  }
}