# DingTalk 机器人通知

## 机器人头像

![JenkinsLogo](jenkins-logo.png)

## 文档

💯 [查看文档](https://jenkinsci.github.io/dingtalk-plugin/)

## Contributing

🍻 [Contributing](./CONTRIBUTING.md)

## TODO

支持 Outgoing 模式的机器人。

## 常见问题

#### 出现 java.lang.NullPointerException 异常 [#49](https://github.com/jenkinsci/dingtalk-plugin/issues/49) [#56](https://github.com/jenkinsci/dingtalk-plugin/issues/56)

一般出现在刚安装完插件之后进行消息发送时，由于 jenkins 实例未读取到插件的配置导致，重启 jenkins 服务即可。
