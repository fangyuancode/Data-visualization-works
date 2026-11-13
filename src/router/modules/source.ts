import { AppRouteRecord } from '@/types/router'

export const sourceRoutes: AppRouteRecord = {
  path: '/source',
  name: 'source',
  component: '/index/index',
  meta: {
    title: 'menus.source.title',
    icon: 'ri-table-line',
    roles: ['R_SUPER', 'R_ADMIN', 'R_USER']
  },
  children: [
    {
      path: 'comsumer',
      name: 'comsumer',
      component: '/source/comsumer',
      meta: {
        title: 'menus.source.consumer',
        keepAlive: false,
        fixedTab: true
      }
    },
    {
      path: 'comsumer-analysis',
      name: 'ConsumerShoppingAnalysis',
      component: '/source/comsumer/analysis',
      meta: {
        title: 'menus.source.consumerAnalysis',
        keepAlive: false,
        isHide: true,
        activePath: '/source/comsumer'
      }
    },
    {
      path: 'nipt',
      name: 'nipt',
      component: '/source/nipt',
      meta: {
        title: 'menus.source.nipt',
        keepAlive: false,
        fixedTab: true
      }
    },
    {
      path: 'nipt-analysis',
      name: 'NiptAnalysis',
      component: '/source/nipt/analysis',
      meta: {
        title: 'menus.source.niptAnalysis',
        keepAlive: false,
        isHide: true,
        activePath: '/source/nipt'
      }
    },
    {
      path: 'rental',
      name: 'rental',
      component: '/source/rental',
      meta: {
        title: 'menus.source.rental',
        keepAlive: false,
        fixedTab: true
      }
    },
    {
      path: 'rental-analysis',
      name: 'RentalAnalysis',
      component: '/source/rental/analysis',
      meta: {
        title: 'menus.source.rentalAnalysis',
        keepAlive: false,
        isHide: true,
        activePath: '/source/rental'
      }
    },
    {
      path: 'exam',
      name: 'exam',
      component: '/source/exam',
      meta: {
        title: 'menus.source.exam',
        keepAlive: false,
        fixedTab: true
      }
    },
    {
      path: 'exam-analysis',
      name: 'ExamAnalysis',
      component: '/source/exam/analysis',
      meta: {
        title: 'menus.source.examAnalysis',
        keepAlive: false,
        isHide: true,
        activePath: '/source/exam'
      }
    },
    {
      path: 'teen',
      name: 'teen',
      component: '/source/teen',
      meta: {
        title: 'menus.source.teen',
        keepAlive: false,
        fixedTab: true
      }
    },
    {
      path: 'teen-analysis',
      name: 'TeenMentalHealthAnalysis',
      component: '/source/teen/analysis',
      meta: {
        title: 'menus.source.teenAnalysis',
        keepAlive: false,
        isHide: true,
        activePath: '/source/teen'
      }
    },
    {
      path: 'tvplay',
      name: 'tvplay',
      component: '/source/tvplay',
      meta: {
        title: 'menus.source.tvplay',
        keepAlive: false,
        fixedTab: true
      }
    },
    {
      path: 'tvplay-analysis',
      name: 'TvDramaPlayAnalysis',
      component: '/source/tvplay/analysis',
      meta: {
        title: 'menus.source.tvplayAnalysis',
        keepAlive: false,
        isHide: true,
        activePath: '/source/tvplay'
      }
    },
    {
      path: 'world',
      name: 'world',
      component: '/source/world',
      meta: {
        title: 'menus.source.world',
        keepAlive: false,
        fixedTab: true
      }
    },
    {
      path: 'world-analysis',
      name: 'WorldHappinessAnalysis',
      component: '/source/world/analysis',
      meta: {
        title: 'menus.source.worldAnalysis',
        keepAlive: false,
        isHide: true,
        activePath: '/source/world'
      }
    },
  ]
}
