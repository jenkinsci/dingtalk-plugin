# TEXT 类型的消息

```pipeline

pipeline {
    agent any
    stages {
        stage('test'){
            steps {
                echo '测试文本消息...'
            }
            post {
                success {
                    dingtalk (
                        robot: '527a09c9-1ba8-4361-b739-f0f41687514c',
                        text: '测试文本类型的消息，哈哈哈哈',
                        at: [
                            '18516601234',
                            '18516603333'
                        ]
                    )
                }
            }
        }
    }
}

```
