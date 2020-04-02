package io.jenkins.plugins;

import hudson.Extension;
import io.jenkins.plugins.DingTalkRobotConfig.DingTalkRobotConfigDescriptor;
import io.jenkins.plugins.enums.NoticeOccasionEnum;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import lombok.Getter;
import lombok.ToString;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * 全局配置
 *
 * @author liuwei
 * @date 2019/12/24 10:46
 */
@Getter
@ToString
@Extension(ordinal = 100)
@SuppressWarnings("unused")
public class DingTalkGlobalConfig extends GlobalConfiguration {

  /**
   * 通知时机
   */
  private Set<String> noticeOccasions = Arrays.stream(
      NoticeOccasionEnum.values())
      .map(Enum::name)
      .collect(Collectors.toSet());

  /**
   * 机器人配置列表
   */
  private CopyOnWriteArrayList<DingTalkRobotConfig> robotConfigs = new CopyOnWriteArrayList<>();

  /**
   * 通知时机列表
   *
   * @return 通知时机
   */
  public NoticeOccasionEnum[] getNoticeOccasionTypes() {
    return NoticeOccasionEnum.values();
  }

  /**
   * `机器人` 配置页面
   *
   * @return 机器人配置页面
   */
  public DingTalkRobotConfigDescriptor getDingTalkRobotConfigDescriptor() {
    return Jenkins.get().getDescriptorByType(DingTalkRobotConfigDescriptor.class);
  }

  @DataBoundSetter
  public void setNoticeOccasions(Set<String> noticeOccasions) {
    this.noticeOccasions = noticeOccasions;
  }

  @DataBoundSetter
  public void setRobotConfigs(
      CopyOnWriteArrayList<DingTalkRobotConfig> robotConfigs) {
    this.robotConfigs = robotConfigs;
  }

  @DataBoundConstructor
  public DingTalkGlobalConfig(Set<String> noticeOccasions,
      CopyOnWriteArrayList<DingTalkRobotConfig> robotConfigs) {
    this.noticeOccasions = noticeOccasions;
    this.robotConfigs = robotConfigs;
  }

  public DingTalkGlobalConfig() {
    this.load();
  }

  @Override
  public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
//    System.out.println("============ old form data =============");
//    System.out.println(json.toString());
    Object robotConfigObj = json.get("robotConfigs");
    if (robotConfigObj == null) {
      json.put("robotConfigs", new JSONArray());
    } else {
      JSONArray robotConfigs = JSONArray.fromObject(robotConfigObj);
      robotConfigs.removeIf(item -> {
        JSONObject jsonObject = JSONObject.fromObject(item);
        String webhook = jsonObject.getString("webhook");
        return StringUtils.isEmpty(webhook);
      });
    }
//    System.out.println("============ new form data =============");
//    System.out.println(json.toString());
    req.bindJSON(this, json);
    this.save();
    return super.configure(req, json);
  }

  /**
   * 获取全局配置信息
   *
   * @return 全局配置信息
   */
  public static DingTalkGlobalConfig getInstance() {
    return GlobalConfiguration.all().get(DingTalkGlobalConfig.class);
  }
}
