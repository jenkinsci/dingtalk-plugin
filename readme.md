# DingTalk 机器人通知

[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/ding-talk.svg)](https://plugins.jenkins.io/ding-talk)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/ding-talk.svg?label=version)](https://github.com/jenkinsci/ding-talk/releases/latest)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/ding-talk.svg?color=green)](https://plugins.jenkins.io/ding-talk)

## 支持的项目

![支持的项目类型](doc/images/supportProjects.png)

## 使用

1. 在 `系统管理` > `系统设置` > `钉钉机通知配置` 中选择 **通知时机** 并添加机器人信息。
2. 在任务配置中选择需要触发的机器人，并可以填写每个机器人需要 `@` 的人（填写手机号码，以换行分割）。

### 截图

![全局配置](doc/images/globalConfig.png)

![机器人配置测试](doc/images/robotConfigTest.png)

![项目配置](doc/images/projectConfig.png)

![构建测试](doc/images/buildTest.png)

## 注意

1. 如果 `执行人` 字段需要 `@` 效果，请在 `Manage Jenkins` > `Manage Users` 在每个用户设置页面的描述中添加手机号。

![at 执行人](doc/images/atExecutor.png)

---

2. 经测试，钉钉机器人的安全策略配置的选项属于`与` 关系，即如果勾选了多个策略，则需要同时满足。所以，插件的配置最好与钉钉机器人上保持一致。

## TODO

插件开发的一些总结
