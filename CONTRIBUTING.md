# Contributing

欢迎 PR ~

## 开发服务

在 `IDEA` 右侧 `maven` 控制面板中添加 `hpi:run` 到启动配置：
![启动配置](./docs/assets/contribuiting-config.png) 

## 开发约定

1. 使用 [Alibaba Java Coding Guidelines](https://plugins.jetbrains.com/plugin/10046-alibaba-java-coding-guidelines/) 校验代码规范
2. 使用 [Google Style Guide](https://github.com/google/styleguide) 统一代码风格
> `IDEA` 下可以下载 [intellij-java-google-style.xml](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)，然后在 `Settings` -> `Editor` -> `Code Style` 进行导入

## 参考文档

1. [Plugin tutorial](https://wiki.jenkins.io/display/JENKINS/Plugin+tutorial#Plugintutorial-SettingUpEnvironment)
2. [Jenkins 插件开发之旅：两天内从 idea 到发布(上篇)](https://jenkins-zh.cn/wechat/articles/2019/05/2019-05-06-jenkins-plugin-develop-within-two-days-part01/)
3. [Jenkins 插件开发之旅：两天内从 idea 到发布(下篇)](https://jenkins-zh.github.io/wechat/articles/2019/05/2019-05-08-jenkins-plugin-develop-within-two-days-part02/)

---

## 文档服务

1. 安装 node.js 环境
2. 安装 yarn 包管理器
3. 在项目根目录执行 `yarn` 安装依赖
4. 执行 `yarn docs:dev` 启动文档开发环境