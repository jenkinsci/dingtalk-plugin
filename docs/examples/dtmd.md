# DTMD 协议的消息

关于 DTMD 协议的消息，可以参考钉钉官方文档 资料较少

[如何通过钉钉链接发送消息给机器人](https://dingtalk.com/qidian/help-detail-1060976699.html)

[dtmd介绍](https://www.dingtalk.com/qidian/help-detail-1066741046.html)

[dtmd协议只能在markdown actioncard feedcard 消息类型中使用](https://open.dingtalk.com/document/orgapp/dingtalk-chatbot-for-one-on-one-query)

::: warning

`dtmd` 协议只能在markdown、actioncard、feedcard 消息类型中使用

:::

```groovy{17,18}

pipeline {
    agent any
    stages {
        stage('link'){
            steps {
                echo '测试 MARKDOWN 消息...'
            }
            post {
                success {
                    dingtalk (
                        robot: '3141dbb8-9d32-4344-8324-df6e2b522117',
                        type: 'MARKDOWN',
                        title: '你有新的消息，请注意查收',
                        text: [
                            '# DTMD 消息',
                            '- [点我](dtmd://dingtalkclient/sendMessage?content=你好)',
                            '- [再点](dtmd://dingtalkclient/sendMessage?content=傻逼)'
                        ]
                    )
                }
            }
        }
    }
}

```

:::details 查看结果

![dtmt-example](../assets/dtmt-example.jpg)

:::
