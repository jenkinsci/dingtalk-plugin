# Jenkins 钉钉通知插件 
Jenkins钉钉配置插件可以让你的build结果第一时间反馈到你所在的工作群中， 帮助你了解build状况

## 如何贡献 
1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -am 'Add some feature')
4. Push to the branch (git push origin my-new-feature)
5. Create new Pull Request
6. Dev build
7. checkout jenkins settings.xml from jenkins-ci.org
```
mvn package -s /PATH/TO/settings.xml 
```


## 如何使用 
1. 创建钉钉机器人 

钉钉 -> ... -> 机器人管理 
![](https://github.com/jenkinsci/dingding-notifications-plugin/blob/master/static/entry.png?raw=true)

![](https://github.com/jenkinsci/dingding-notifications-plugin/blob/master/static/robot.png?raw=true)

![](https://github.com/jenkinsci/dingding-notifications-plugin/blob/master/static/create_robot.png?raw=true)

保存 最后一个参数accesstoken
![](https://github.com/jenkinsci/dingding-notifications-plugin/blob/master/static/token.png?raw=true)

2. 配置Jenkins 
当你拿到钉钉群机器人的accessToken后， 在每一个job之后，你都可以选择添加'钉钉配置通知器'
![](https://github.com/jenkinsci/dingding-notifications-plugin/blob/master/static/config_post_step.png?raw=true)

input the access token in previous step, and please input the Jenkins URL
填入accessToken， 以及很重要的： 填上你Jenkins的URL，因为Jenkins自己不知道自己的URL是什么
![](https://github.com/jenkinsci/dingding-notifications-plugin/blob/master/static/config.png?raw=true)
3. Enjoy!
![](https://github.com/jenkinsci/dingding-notifications-plugin/blob/master/static/result.png?raw=true)
