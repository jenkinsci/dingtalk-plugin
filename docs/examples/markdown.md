# MARKDOWN 类型的消息

```groovy

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
                        robot: '58f10219-2cd3-4de7-a1af-f85f4010c10a',
                        type: 'MARKDOWN',
                        title: '你有新的消息，请注意查收',
                        text: [
                            '# 这是消息内容的标题',
                            '消息正文：测试 markdown 类型的消息',
                            '',
                            '---',
                            '我有分割线，哈哈哈哈',
                            '> 引用内容',
                            '#### 展示列表',
                            '- 两个黄鹂鸣翠柳',
                            '- 一行白鹭上青天',
                            '- 窗含西岭千秋雪',
                            '- 门泊东吴万里船'
                        ],
                        at: [
                          '185166001234'
                        ]
                    )
                }
            }
        }
    }
}

```

:::details 查看结果

![markdown-example](../assets/markdown-example.jpg)

:::
