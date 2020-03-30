# Contributing

欢迎 PR ~

## 开发环境

1. 使用/参考 [settings.xml](./maven/settings.xml) 配置 `maven`。
2. `windows` 环境下执行 [install.cmd](./maven/taobao-sdk-java-auto/install.cmd) 安装 `taobao-sdk`，其他环境请拷贝文件中的脚本手动执行安装。

## 开发服务

在 `IDEA` 右侧 `maven` 控制面板中添加 `hpi:run` 到启动配置：
![启动配置](./doc/images/contribuitingConfig.png) 

## 开发约定

1. 使用 [Alibaba Java Coding Guidelines](https://plugins.jetbrains.com/plugin/10046-alibaba-java-coding-guidelines/) 校验代码规范。
2. 使用 [Google Style Guide](https://github.com/google/styleguide) 统一代码风格。
> `IDEA` 下可以下载 [intellij-java-google-style.xml](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)，然后在 `Settings` -> `Editor` -> `Code Style` 进行导入。

## 文档服务

1. 安装 node.js 环境
2. 安装 yarn 包管理器
3. 在项目根目录执行 `yarn` 安装依赖
4. 执行 `yarn docs:dev` 启动文档开发环境
5. 代码合并到 `master` 之后会自动发布