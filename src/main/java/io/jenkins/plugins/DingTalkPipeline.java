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
import io.jenkins.plugins.service.impl.DingTalkServiceImpl;
import java.io.IOException;
import java.util.Set;
import javax.annotation.Nonnull;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import lombok.Getter;
import lombok.ToString;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author liuwei
 * @date 2020/3/27 16:36
 * @desc 支持 pipeline 中使用
 */
@Getter
@ToString
public class DingTalkPipeline extends Builder implements SimpleBuildStep {

  /**
   * 机器人 id
   */
  private final String robot;

  /**
   * 消息类型
   */
  private MsgTypeEnum type;

  /**
   * at 的手机号码
   */
  private final Set<String> at;

  /**
   * 发送的消息
   */
  private final String text;

  /**
   * link 标题
   */
  private final String title;

  /**
   * link 点击消息跳转的 URL
   */
  private final String messageUrl;

  /**
   * link 图片 URL
   */
  private final String picUrl;

  /**
   * ActionCard 单个按钮的方案
   */
  private final String singleTitle;

  /**
   * ActionCard 单个按钮的方案
   */
  private final String singleURL;

  /**
   * ActionCard 0-按钮竖直排列，1-按钮横向排列
   */
  private final String btnOrientation;

  /**
   * ActionCard 0-正常发消息者头像，1-隐藏发消息者头像
   */
  private final String hideAvatar;

  private final String rootPath = Jenkins.get().getRootUrl();

  private DingTalkServiceImpl service = new DingTalkServiceImpl();


  public DingTalkPipeline(String robot, MsgTypeEnum type, Set<String> at, String text,
      String title, String messageUrl, String picUrl, String singleTitle, String singleURL,
      String btnOrientation, String hideAvatar,
      DingTalkServiceImpl service) {
    this.robot = robot;
    this.type = type;
    this.at = at;
    this.text = text;
    this.title = title;
    this.messageUrl = messageUrl;
    this.picUrl = picUrl;
    this.singleTitle = singleTitle;
    this.singleURL = singleURL;
    this.btnOrientation = btnOrientation;
    this.hideAvatar = hideAvatar;
    this.service = service;
  }

  @Override
  public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace,
      @Nonnull Launcher launcher, @Nonnull TaskListener listener)
      throws InterruptedException, IOException {
    String jobUrl = rootPath + run.getUrl();
    String duration = run.getDurationString();
    String changeLog = jobUrl + "/changes";
    String console = jobUrl + "/console";
    System.out.println(this.toString());
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
