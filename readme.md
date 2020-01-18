DingTalk 机器人通知
====================

[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/dingding-notifications.svg)](https://plugins.jenkins.io/dingding-notifications)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/dingding-notifications.svg?label=version)](https://github.com/jenkinsci/dingding-notifications-plugin/releases/latest)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/dingding-notifications.svg?color=green)](https://plugins.jenkins.io/dingding-notifications)

## 使用

1. 在 `系统管理` > `系统设置` > `钉钉机器人配置` 中选择 **通知时机** 并添加机器人信息。
2. 在任务配置中选择需要触发的机器人。

> 目前仅支持在 `freeStyle` 项目中使用。

## 相关截图

![全局配置](doc/images/globalConfig.png)

![项目配置](doc/images/projectConfig.png)

## 注意

经测试，钉钉机器人的安全策略配置的选项属于`与` 关系，即如果勾选了多个策略，则需要同时满足。
所以，插件的配置最好与钉钉机器人上保持一致。

## TODO

1. 控制台日志输出
2. 推送信息添加 git changelog
3. 插件开发的一些总结