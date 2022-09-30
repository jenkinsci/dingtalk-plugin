import path from 'path'

export default {
  base: '/dingtalk-plugin/',
  port: 8888,
  title: '钉钉机器人插件',
  description: '在 Jenkins 中使用钉钉机器人发送消息',
  configureWebpack: {
    resolve: {
      alias: {
        '@': path.resolve(__dirname, '../')
      }
    }
  },
  markdown: {
    anchor: { permalink: true }
  },
  themeConfig: {
    docsDir: 'docs',
    repo: 'jenkinsci/dingtalk-plugin',
    sidebarDepth: 1,
    editLinks: true,
    editLinkText: '在 GitHub 上编辑此页',
    lastUpdated: '上次更新',
    nav: [
      {
        text: '发布记录',
        link: 'https://github.com/jenkinsci/dingtalk-plugin/releases'
      }
    ],
    sidebar: [
      {
        text: '指南',
        items: [
          {
            text: '快速开始',
            link: '/guide/getting-started'
          },
          {
            text: '在 freestyle 项目中使用',
            link: '/guide/freestyle'
          },
          {
            text: '在 pipeline 中使用',
            link: '/guide/pipeline'
          }
        ]
      },
      {
        text: '进阶',
        items: [
          {
            text: '用户属性扩展',
            link: '/advance/user-property'
          },
          {
            text: 'Markdown 语法',
            link: '/advance/markdown'
          }
        ]
      },
      {
        text: '示例',
        items: [
          {
            text: 'Freestyle 项目高级功能',
            link: '/examples/freestyleAdvanced'
          },
          {
            text: 'TEXT 类型的消息',
            link: '/examples/text'
          },
          {
            text: 'LINK 类型的消息',
            link: '/examples/link'
          },
          {
            text: 'MARKDOWN 类型的消息',
            link: '/examples/markdown'
          },
          {
            text: 'ACTION_CARD 整体跳转',
            link: '/examples/actionCard'
          },
          {
            text: 'DTMD 协议的消息',
            link: '/examples/dtmd'
          }
        ]
      }
    ]
  }
}
