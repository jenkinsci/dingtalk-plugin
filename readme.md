# 钉钉通知器插件
## Summary
该插件用于通知指定钉钉群build的触发, 成功和失败, 作为钉钉和jenkins使用者强烈推荐使用, 可以不用再使用邮件通知build情况.
陆续还将增加@指定用户的功能

## 安装
等待jenkins hosting, 目前开发版本可以自行build
```
mvn package --settings=./settings.xml
```

然后使用target/dingding.hpi上传到jenkins使用

## 如何配置
1. 新建一个机器人

钉钉->设置...->机器人管理

![](https://github.com/ztbsuper/dingding-plugin/blob/master/static/entry.png?raw=true)

选择自定义机器人

![](https://github.com/ztbsuper/dingding-plugin/blob/master/static/robot.png?raw=true)

设定机器人

![](https://github.com/ztbsuper/dingding-plugin/blob/master/static/create_robot.png?raw=true)

获取最重要的token

![](https://github.com/ztbsuper/dingding-plugin/blob/master/static/token.png?raw=true)

2. 配置jenkins

在安装完插件后, 每个jenkins job都可以新增一个构建后的步骤

![](https://github.com/ztbsuper/dingding-plugin/blob/master/static/config_post_step.png?raw=true)

把刚才记录下的token填入, 以及jenkins的ROOT url填上, 勾选需要的通知

![](https://github.com/ztbsuper/dingding-plugin/blob/master/static/config.png?raw=true)

3. Enjoy!

![](https://github.com/ztbsuper/dingding-plugin/blob/master/static/result.png?raw=true)