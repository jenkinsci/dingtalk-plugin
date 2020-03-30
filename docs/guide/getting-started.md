# 快速开始

::: warning

请确保你的 Jenkins 版本 >= 2.176.4

:::

## 安装插件

在 `Manage Plugins` 安装 [DingTalk](https://plugins.jenkins.io/dingding-notifications/)。

## 机器人配置

在 `Configure System` 中找到 `钉钉配置` 选项卡，根据自己的需求选择 `通知时机`，然后添加机器人即可。

示例截图：
::: details

![全局配置](@/assets/global-config.jpg)

:::

::: tip

推荐使用 `加密` 模式的安全策略，并测试配置是否正确。

:::

## 简单使用

在项目配置的 `General` 选项卡中找到 `钉钉配置`，勾选需要的机器人，`通知人` 填写同事的手机号。

示例截图：

::: details

![freestyle-project-config](@/assets/freestyle-project-config.jpg)

:::

::: tip

多个手机号码需要 `换行` 填写。

:::
