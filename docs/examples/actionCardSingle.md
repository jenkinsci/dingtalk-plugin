# ACTION_CARD 整体跳转

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
                    dingTalk (
                        robot: '58f10219-2cd3-4de7-a1af-f85f4010c10a',
                        type: 'ACTION_CARD',
                        title: '你有新的消息，请注意查收',
                        text: [
                            '![screenshot](@lADOpwk3K80C0M0FoA)',
                            '### 乔布斯 20 年前想打造的苹果咖啡厅 ',
                            'Apple Store 的设计正从原来满满的科技感走向生活化，而其生活化的走向其实可以追溯到 20 年前苹果一个建立咖啡馆的计划'
                        ],
                        singleTitle: '查看更多',
                        singleUrl: 'https://liuweigl.github.io/dingTalk-plugin/'
                    )
                }
            }
        }
    }
}

```

:::details 查看结果

![actioonCardSingle-example](@/assets/actionCardSingle-example.jpg)

:::
