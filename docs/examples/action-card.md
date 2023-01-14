# ACTION_CARD 整体跳转

## 基础用法

```groovy

pipeline {
    agent any
    stages {
        stage('link'){
            steps {
                echo '测试 ACTION_CARD 整体跳转消息...'
            }
            post {
                success {
                    dingtalk (
                        robot: '58f10219-2cd3-4de7-a1af-f85f4010c10a',
                        type: 'ACTION_CARD',
                        title: '你有新的消息，请注意查收',
                        text: [
                            '![screenshot](@lADOpwk3K80C0M0FoA)',
                            '### 乔布斯 20 年前想打造的苹果咖啡厅 ',
                            'Apple Store 的设计正从原来满满的科技感走向生活化，而其生活化的走向其实可以追溯到 20 年前苹果一个建立咖啡馆的计划'
                        ],
                    )
                }
            }
        }
    }
}

```

:::details 查看结果

![action-card-default-example](../assets/action-card-default-example.jpg)

:::

## 单个按钮

```groovy
singleTitle: '查看更多',
singleUrl: 'https://liuweigl.github.io/dingtalk-plugin/'
```

:::details 查看结果

![action-card-single-btn-example](../assets/action-card-single-btn-example.jpg)

:::


## 自定义按钮组

```groovy

  btns: [
          [
              title: '更改记录',
              actionUrl: 'https://www.dingtalk.com/'
          ],
          [
              title: '控制台',
              actionUrl: 'https://www.dingtalk.com/'
          ]
      ]

```

:::details 查看结果

![action-card-custom-btns-example](../assets/action-card-custom-btns-example.jpg)

:::

## 改变按钮排列方式

```groovy

  btnLayout: 'V'

```

:::details 查看结果

![action-card-at-all-example](../assets/action-card-btn-layout-example.jpg)

:::

## At 全部

```groovy

  atAll: true

```

:::details 查看结果

![action-card-at-all-example](../assets/action-card-at-all-example.jpg)

:::
