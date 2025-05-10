package io.jenkins.plugins;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.User;
import hudson.util.FormValidation;
import hudson.util.FormValidation.Kind;
import hudson.util.Secret;
import io.jenkins.plugins.DingTalkSecurityPolicyConfig.DingTalkSecurityPolicyConfigDescriptor;
import io.jenkins.plugins.enums.BuildStatusEnum;
import io.jenkins.plugins.enums.MsgTypeEnum;
import io.jenkins.plugins.enums.SecurityPolicyEnum;
import io.jenkins.plugins.model.BuildJobModel;
import io.jenkins.plugins.model.MessageModel;
import io.jenkins.plugins.sdk.DingTalkSender;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import jenkins.model.Jenkins;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * 机器人配置页面
 *
 * @author liuwei
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuppressWarnings("unused")
public class DingTalkRobotConfig implements Describable<DingTalkRobotConfig> {

  private String id;

  /**
   * 名称
   */
  private String name;

  /**
   * webhook 地址
   */
  private Secret webhook;

  /**
   * 安全策略配置
   */
  private ArrayList<DingTalkSecurityPolicyConfig> securityPolicyConfigs;

  @DataBoundConstructor
  public DingTalkRobotConfig(
      String id,
      String name,
      String webhook,
      ArrayList<DingTalkSecurityPolicyConfig> securityPolicyConfigs) {
    this.id = StringUtils.isBlank(id) ? UUID.randomUUID().toString() : id;
    this.name = name;
    this.webhook = Secret.fromString(webhook);
    this.securityPolicyConfigs = securityPolicyConfigs;
  }

  public String getId() {
    if (StringUtils.isBlank(id)) {
      setId(UUID.randomUUID().toString());
    }
    return id;
  }

  public String getWebhook() {
    if (webhook == null) {
      return null;
    }
    return webhook.getPlainText();
  }

  public ArrayList<DingTalkSecurityPolicyConfig> getSecurityPolicyConfigs() {
    return Arrays.stream(SecurityPolicyEnum.values())
        .map(
            enumItem -> {
              DingTalkSecurityPolicyConfig newItem = DingTalkSecurityPolicyConfig.of(enumItem);
              if (securityPolicyConfigs != null) {
                Optional<DingTalkSecurityPolicyConfig> config =
                    securityPolicyConfigs.stream()
                        .filter(configItem -> enumItem.name().equals(configItem.getType()))
                        .findAny();
                config.ifPresent(t -> newItem.setValue(t.getValue()));
              }
              return newItem;
            })
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @Override
  public Descriptor<DingTalkRobotConfig> getDescriptor() {
    return Jenkins.get().getDescriptorByType(DingTalkRobotConfigDescriptor.class);
  }

  @Extension
  public static class DingTalkRobotConfigDescriptor extends Descriptor<DingTalkRobotConfig> {

    /**
     * 安全配置页面
     *
     * @return 安全策略配置页面
     */
    public DingTalkSecurityPolicyConfigDescriptor getDingTalkSecurityPolicyConfigDescriptor() {
      return Jenkins.get().getDescriptorByType(DingTalkSecurityPolicyConfigDescriptor.class);
    }

    /**
     * 获取默认的安全配置选项
     *
     * @return 默认的安全配置选项
     */
    public ArrayList<DingTalkSecurityPolicyConfig> getDefaultSecurityPolicyConfigs() {
      return Arrays.stream(SecurityPolicyEnum.values())
          .map(DingTalkSecurityPolicyConfig::of)
          .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * name 字段必填
     *
     * @param value name
     * @return 是否校验成功
     */
    public FormValidation doCheckName(@QueryParameter String value) {
      if (StringUtils.isBlank(value)) {
        return FormValidation.error(Messages.RobotConfigFormValidation_name());
      }
      return FormValidation.ok();
    }

    /**
     * webhook 字段必填
     *
     * @param value webhook
     * @return 是否校验成功
     */
    public FormValidation doCheckWebhook(@QueryParameter String value) {
      if (StringUtils.isBlank(value)) {
        return FormValidation.error(Messages.RobotConfigFormValidation_webhook());
      }
      return FormValidation.ok();
    }

    /**
     * 测试配置信息
     *
     * @param id                      id
     * @param name                    名称
     * @param webhook                 webhook
     * @param securityPolicyConfigStr 安全策略
     * @param proxyStr                代理
     * @return 机器人配置是否正确
     */
    public String doTest(
        @QueryParameter("id") String id,
        @QueryParameter("name") String name,
        @QueryParameter("webhook") String webhook,
        @QueryParameter("securityPolicyConfigs") String securityPolicyConfigStr,
        @QueryParameter("proxy") String proxyStr) {
      ArrayList<DingTalkSecurityPolicyConfig> securityPolicyConfigs =
          parseSecurityPolicyConfigs(securityPolicyConfigStr);
      DingTalkRobotConfig robotConfig =
          new DingTalkRobotConfig(id, name, webhook, securityPolicyConfigs);
      Proxy proxy = getProxy(proxyStr);
      DingTalkSender sender = new DingTalkSender(robotConfig, proxy);
      MessageModel msg = getMsg();
      String message = sender.sendMarkdown(msg);

      if (message == null) {
          return Messages.RobotConfigFormValidation_success();
      }
      return "Error: " + message;
    }

    private ArrayList<DingTalkSecurityPolicyConfig> parseSecurityPolicyConfigs(String param) {
      ArrayList<DingTalkSecurityPolicyConfig> securityPolicyConfigs =
          new ArrayList<>();
      JSONArray array = (JSONArray) JSONSerializer.toJSON(param);
      for (Object item : array) {
        JSONObject json = (JSONObject) item;
        securityPolicyConfigs.add(
            new DingTalkSecurityPolicyConfig(
                (String) json.get("type"), (String) json.get("value"), "")
        );
      }
      return securityPolicyConfigs;
    }

    private String getText() {
      String rootUrl = Jenkins.get().getRootUrl();
      User user = User.current();
      if (user == null) {
        user = User.getUnknown();
      }
      return BuildJobModel.builder()
          .projectName("欢迎使用钉钉机器人插件~")
          .projectUrl(rootUrl)
          .jobName("系统配置")
          .jobUrl(rootUrl + "/configure")
          .statusType(BuildStatusEnum.SUCCESS)
          .duration("-")
          .executorName(user.getDisplayName())
          .executorMobile(user.getDescription())
          .build()
          .toMarkdown();
    }

    private MessageModel getMsg() {
      return MessageModel.builder()
          .type(MsgTypeEnum.MARKDOWN)
          .title("钉钉机器人测试成功")
          .text(getText())
          .atAll(false)
          .build();
    }

    private Proxy getProxy(String param) {
      Proxy proxy = null;
      try {
        JSONObject proxyObj = (JSONObject) JSONSerializer.toJSON(param);
        DingTalkProxyConfig netProxy =
            new DingTalkProxyConfig(
                Proxy.Type.valueOf(proxyObj.getString("type")),
                proxyObj.getString("host"),
                proxyObj.getInt("port"));
        proxy = netProxy.getProxy();
      } catch (Exception ignored) {

      }
      return proxy;
    }
  }
}
