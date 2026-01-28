# 快速开始

::: warning

请确保你的 Jenkins 版本 >= 2.479.3

**本文档只针对最新版插件，请务必升级插件**

:::

## 注意

如果 jenkins 更新中心地址（升级站点）不是官方的，可能无法获取最新的版本（第三方镜像有延迟）

请切回官方镜像源：https://updates.jenkins.io/update-center.json 

## 安装插件

在 `Manage Plugins` 安装 [DingTalk](https://plugins.jenkins.io/dingtalk/)

## 机器人配置

在 `Configure System` 中找到 `钉钉配置` 选项卡，根据自己的需求选择 `通知时机`，然后添加机器人即可

示例截图：
::: details

![全局配置](../assets/global-config.jpg)
![钉钉配置](../assets/dingtalk-config.jpg)

:::

::: tip

推荐使用 `加密` 模式的安全策略，并测试配置是否正确

:::

## 机器人类型与 Webhook

钉钉机器人分为 **企业内部机器人** 与 **自定义机器人** 两种：

- **自定义机器人**：在钉钉群内创建后会生成 Webhook 地址。可在钉钉侧选择安全方式（关键词/加签/IP 白名单）。
  - 关键词文档：https://open.dingtalk.com/document/dingstart/customize-robot-security-settings#title-jk6-ksi-zur
  - 加签文档：https://open.dingtalk.com/document/dingstart/customize-robot-security-settings#title-7fs-kgs-36x
  - IP 白名单文档：https://open.dingtalk.com/document/dingstart/customize-robot-security-settings#title-hvj-mm1-5xu
- **企业内部机器人**：获取 Webhook 的方式不同，请参考官方 FAQ：
  - https://open.dingtalk.com/document/development/faq-robot#ba79fa80c4c0g

注意：只有 **自定义机器人** 需要在 Jenkins 的“安全设置”中配置关键词或加签密钥。
如果使用 **IP 白名单** 方式，只需将 Jenkins 出口 IP 配置到钉钉机器人中，Jenkins 侧无需填写。
