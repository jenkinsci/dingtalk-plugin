# LINK 类型的消息

```groovy

pipeline {
    agent any
    stages {
        stage('link'){
            steps {
                echo '测试 LINK 消息...'
            }
            post {
                success {
                    dingtalk (
                        robot: '58f10219-2cd3-4de7-a1af-f85f4010c10a',
                        type: 'LINK',
                        title: '你有新的消息，请注意查收',
                        text: [
                            '测试链接类型的消息',
                            '分行显示，哈哈哈哈'
                        ],
                        messageUrl: 'http://www.baidu.com',
                        picUrl: 'https://www.picdiet.com/img/photographer_compressed.jpg'
                    )
                }
            }
        }
    }
}

```

:::details 查看结果

![link-example](@/assets/link-example.jpg)

:::
