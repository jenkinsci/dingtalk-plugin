import { defineConfig } from 'vitepress'

export default defineConfig({
  base: '/dingtalk-plugin/',
  lang: 'zh-CN',
  title: '钉钉机器人插件',
  description: '在 Jenkins 中使用钉钉机器人发送消息',
  head: [['link', { rel: 'icon', href: '/dingtalk-plugin/favicon.ico' }]],
  lastUpdated: true,
  sitemap: {
    hostname: 'https://jenkinsci.github.io/dingtalk-plugin/'
  },
  vue: {
    template: {
      compilerOptions: {
        isCustomElement: tag => ['font'].includes(tag)
      }
    }
  },
  themeConfig: {
    logo: '/dingtalk-logo.png',
    outline: [2, 3],
    outlineTitle: '本页目录',
    lastUpdatedText: '最后更新',
    returnToTopLabel: '回到顶部',
    sidebarMenuLabel: '目录',
    darkModeSwitchLabel: '外观',
    docFooter: {
      prev: '上一页',
      next: '下一页'
    },
    search: {
      provider: 'local',
      options: {
        translations: {
          button: {
            buttonText: '搜索文档',
            buttonAriaLabel: '搜索文档'
          },
          modal: {
            noResultsText: '无法找到相关结果',
            resetButtonTitle: '清除查询条件',
            displayDetails: '显示详细列表',
            footer: {
              selectText: '选择',
              navigateText: '切换',
              closeText: '关闭'
            }
          }
        }
      }
    },
    socialLinks: [
      {
        icon: 'github',
        link: 'https://github.com/jenkinsci/dingtalk-plugin'
      }
    ],
    editLink: {
      pattern:
        'https://github.com/jenkinsci/dingtalk-plugin/edit/main/docs/:path',
      text: '在 GitHub 上编辑此页'
    },
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
          },
          {
            text: '环境变量',
            link: '/guide/environment-variables'
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
            text: '自定义消息',
            link: '/advance/custom-message'
          },
          {
            text: '@ 人',
            link: '/advance/at-mention'
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
            link: '/examples/freestyle-advanced'
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
            link: '/examples/action-card'
          },
          {
            text: 'DTMD 协议的消息',
            link: '/examples/dtmd'
          }
        ]
      }
    ]
  }
})
