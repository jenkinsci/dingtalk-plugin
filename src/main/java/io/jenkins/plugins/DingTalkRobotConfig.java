package io.jenkins.plugins;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.User;
import hudson.util.FormValidation;
import hudson.util.Secret;
import io.jenkins.plugins.DingTalkSecurityPolicyConfig.DingTalkSecurityPolicyConfigDescriptor;
import io.jenkins.plugins.enums.BuildStatusEnum;
import io.jenkins.plugins.enums.MsgTypeEnum;
import io.jenkins.plugins.enums.SecurityPolicyEnum;
import io.jenkins.plugins.model.BuildJobModel;
import io.jenkins.plugins.model.MessageModel;
import io.jenkins.plugins.sdk.DingTalkSender;
import io.jenkins.plugins.tools.Constants;
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

import java.net.Proxy;
import java.util.ArrayList;
import java.util.UUID;

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
    if (securityPolicyConfigs == null) {
      return new ArrayList<>();
    }
    ArrayList<DingTalkSecurityPolicyConfig> resolved = new ArrayList<>();
    for (DingTalkSecurityPolicyConfig config : securityPolicyConfigs) {
      if (StringUtils.isBlank(config.getValue())) {
        continue;
      }
      if (config instanceof KeySecurityPolicyConfig || config instanceof SecretSecurityPolicyConfig) {
        resolved.add(config);
        continue;
      }
      if (SecurityPolicyEnum.KEY.name().equals(config.getType())) {
        resolved.add(new KeySecurityPolicyConfig(config.getValue()));
        continue;
      }
      if (SecurityPolicyEnum.SECRET.name().equals(config.getType())) {
        resolved.add(new SecretSecurityPolicyConfig(config.getValue()));
        continue;
      }
      resolved.add(config);
    }
    return resolved;
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
      return new ArrayList<>();
    }

    /**
     * 获取安全配置描述符
     *
     * @return 安全策略描述符
     */
    public ArrayList<Descriptor> getSecurityPolicyConfigsDescriptors() {
      ArrayList<Descriptor> descriptors = new ArrayList<>();
      Descriptor keyDescriptor =
          Jenkins.get().getDescriptorByType(KeySecurityPolicyConfig.DescriptorImpl.class);
      if (keyDescriptor != null) {
        descriptors.add(keyDescriptor);
      }
      Descriptor secretDescriptor =
          Jenkins.get().getDescriptorByType(SecretSecurityPolicyConfig.DescriptorImpl.class);
      if (secretDescriptor != null) {
        descriptors.add(secretDescriptor);
      }
      return descriptors;
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
      if (!value.startsWith(Constants.DINGTALK_WEBHOOK_URL_PREFIX)) {
        return FormValidation.error(Messages.RobotConfigFormValidation_webhook_invalid());
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
      // Check configuration permission
      Jenkins.get().checkPermission(DingTalkPermissions.CONFIGURE);

      if (StringUtils.isBlank(webhook)) {
          return "Error: " + Messages.RobotConfigFormValidation_webhook();
      }
      if (!webhook.startsWith(Constants.DINGTALK_WEBHOOK_URL_PREFIX)) {
          return "Error: " + Messages.RobotConfigFormValidation_webhook_invalid();
      }

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
          .jobUrl(rootUrl + "configure")
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
