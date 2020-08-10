# 在 pipeline 中使用

取消项目中勾选的机器人，防止重复发送消息。

## 语法

```groovy

dingtalk (
    robot: '',
    type: '',
    at: [],
    atAll: false,
    title: '',
    text: [],
    messageUrl: '',
    picUrl: '',
    singleTitle: '',
    btns: [],
    btnLayout: '',
    hideAvatar: false
)

```

## 参数说明

### 通用的参数

| 参数  |            类型             | 说明              |
| ----- | :-------------------------: | ----------------- |
| robot |           String            | 机器人 id         |
| type  | [MsgTypeEnum](#MsgTypeEnum) | 消息类型          |
| at    |       List\<String\>        | 需要 @ 的手机号码 |
| atAll |           boolean           | 是否 @ 全部       |

::: tip

`robot` 参数可以在机器人配置中找到。

:::

::: details 查看详情

![robotId.jpg](@/assets/robotId.jpg)

:::

### MsgTypeEnum

```java

public enum MsgTypeEnum {
  TEXT,

  LINK,

  MARKDOWN,

  ACTION_CARD
}

```

### TEXT 类型的消息

| 参数 |      类型      | 说明         |
| ---- | :------------: | ------------ |
| text | List\<String\> | 发送文本消息 |

### LINK 类型的消息

| 参数                                         |      类型      | 说明                           |
| -------------------------------------------- | :------------: | ------------------------------ |
| title                                        |     String     | 消息标题                       |
| text                                         | List\<String\> | 消息内容，如果太长只会部分展示 |
| messageUrl <Badge type="error" text="必填"/> |     String     | 点击消息跳转的 URL             |
| picUrl                                       |     String     | 图片 URL                       |

::: warning

该类型的消息不支持 At 功能。

:::

### MARKDOWN 类型的消息

| 参数  |      类型      | 说明                                                    |
| ----- | :------------: | ------------------------------------------------------- |
| title |     String     | [首屏会话](#首屏会话) 透出的展示内容                    |
| text  | List\<String\> | 消息内容，支持 [受限的 markdonw](#受限的 markdonw) 语法 |

### ACTION_CARD 类型的消息

| 参数       |              类型               | 说明                                                    |
| ---------- | :-----------------------------: | ------------------------------------------------------- |
| title      |             String              | [首屏会话](#首屏会话) 透出的展示内容                    |
| text       |         List\<String\>          | 消息内容，支持 [受限的 markdonw](#受限的 markdonw) 语法 |
| btnLayout  | [BtnLayoutEnum](#BtnLayoutEnum) | 按钮的排列方式                                          |
| hideAvatar |             boolean             | 是否隐藏发消息者头像                                    |

### BtnLayoutEnum

```java

public enum BtnLayoutEnum {

  /**
   * horizotal：水平排列
   */
  H,

  /**
   * vertical：垂直排列
   */
  V;
}


```

#### 整体跳转

| 参数        |  类型  | 说明                                                    |
| ----------- | :----: | ------------------------------------------------------- |
| singleTitle | String | 单个按钮的方案（设置此项和 `singleUrl` 后 `btns` 无效） |
| singleUrl   | String | 点击 `singleTitle` 按钮触发的 URL                       |

#### 独立跳转

| 参数 |               类型                | 说明         |
| ---- | :-------------------------------: | ------------ |
| btns | List<[ButtonModel](#ButtonModel)> | 自定义按钮组 |

### ButtonModel

```java

public class ButtonModel{

  /**
   * 按钮标题
   */
  private String title;

  /**
   * 点击按钮跳转的链接
   */
  private String actionUrl;

}
```

## 默认设置

### title 参数

当参数为空时，默认会使用 _Jenkins 通知_。

### ACTION_CARD 类型的消息

当 `singleTitle` 与 `btns` 都为空时，默认会创建 『更改记录』『控制台』 2 个按钮。

## 效果展示

### TEXT 消息

:::details 点击查看

![text](https://img.alicdn.com/tfs/TB1jFpqaRxRMKJjy0FdXXaifFXa-497-133.png)

:::

### LINK 消息

:::details 点击查看

![link](https://ding-doc.oss-cn-beijing.aliyuncs.com/images/0.0.239/1570679827267-6243216b-d1c3-48b7-9b1e-0f0b4211b50b.png)

:::

### MARKDOWN 消息

:::details 点击查看

![markdown](https://img.alicdn.com/tfs/TB1yL3taUgQMeJjy0FeXXXOEVXa-492-380.png)

:::

### ACTION_CARD 整体跳转

:::details 点击查看

![actionCard](https://img.alicdn.com/tfs/TB1nhWCiBfH8KJjy1XbXXbLdXXa-547-379.png)

:::

### ACTION_CARD 独立跳转

:::details 点击查看

![actionCard](https://ding-doc.oss-cn-beijing.aliyuncs.com/images/0.0.239/1570679939723-c1fb7861-5bcb-4c30-9e1b-033932f6b72f.png)

:::

### 首屏会话

:::details 点击查看

![首屏会话](https://img.alicdn.com/tfs/TB1ehWCiBfH8KJjy1XbXXbLdXXa-334-218.png)

:::

## 其他

更多细节请参考 [示例](/examples/text) 章节
