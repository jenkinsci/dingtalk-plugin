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

![actionCardMultiple-default-example](@/assets/actionCardMultiple-default-example.jpg)

:::

## 自定义按钮组

```groovy

  btns: [
          [
              title: '内容不错',
              actionUrl: 'https://www.dingtalk.com/'
          ],
          [
              title: '不感兴趣',
              actionUrl: 'https://www.dingtalk.com/'
          ]
      ]

```

:::details 查看结果

![actionCardMultiple-btns-example](@/assets/actionCardMultiple-btns-example.jpg)

:::

## 改变按钮排列方式

```groovy

  btnLayout: 'V'

```

:::details 查看结果

![actionCardMultiple-btnLayout-example](@/assets/actionCardMultiple-btnLayout-example.jpg)

:::

## At 全部

```groovy

  atAll: true

```

:::details 查看结果

![actionCardMultiple-atAll-example](@/assets/actionCardMultiple-atAll-example.jpg)

:::
