import { AppRouteRecord } from '@/types/router'

export const toolboxRoutes: AppRouteRecord = {
  path: '/toolbox',
  name: 'toolbox',
  component: '/index/index',
  meta: {
    title: 'menus.toolbox.title',
    icon: 'ri-send-ins-line',
    roles: ['R_SUPER', 'R_ADMIN', 'R_USER']
  },
  children: [
    {
      path: 'jobanalysis',
      name: 'jobanalysis',
      component: '/toolbox/jobanalysis',
      meta: {
        title: 'menus.toolbox.jobanalysis',
        keepAlive: false,
        fixedTab: true
      }
    },


  ]
}