# TEXT 类型的消息

```groovy

pipeline {
    agent any
    stages {
        stage('text'){
            steps {
                echo '测试 TEXT 消息...'
            }
            post {
                success {
                    dingtalk (
                        robot: '58f10219-2cd3-4de7-a1af-f85f4010c10a',
                        type: 'TEXT',
                        text: [
                            '测试文本类型的消息',
                            '分行显示，哈哈哈哈'
                        ],
                        at: [
                            '18516601234'
                        ]
                    )
                }
            }
        }
    }
}

```

:::details 查看结果

![text-example](@/assets/text-example.jpg)

:::
