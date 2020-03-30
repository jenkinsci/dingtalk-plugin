const path = require('path')

module.exports = {
  base: '/dingding-notifications-plugin/',
  port: 8888,
  dest: 'docs-dist',
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
    repo: 'jenkinsci/dingding-notifications-plugin',
    sidebarDepth: 1,
    editLinks: true,
    editLinkText: '在 GitHub 上编辑此页',
    lastUpdated: '上次更新',
    nav: [
      {
        text: '发布记录',
        link:
          'https://github.com/jenkinsci/dingding-notifications-plugin/releases'
      }
    ],
    sidebar: [
      {
        title: '指南',
        path: '/guide/getting-started',
        collapsable: false,
        children: ['/guide/getting-started', '/guide/simple','/guide/pipeline']
      },
      {
        title: '示例',
        path: '/examples/text',
        collapsable: false,
        children: [
          '/examples/text',
          '/examples/link',
          '/examples/markdown',
          '/examples/actionCardSingle',
          '/examples/actionCardMultiple'
        ]
      }
    ]
  }
}
