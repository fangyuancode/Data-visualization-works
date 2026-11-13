import { AppRouteRecord } from '@/types/router'

export const setingRoutes: AppRouteRecord = {
  path: '/seting',
  name: 'seting',
  component: '/index/index',
  meta: {
    title: 'menus.seting.title',
    // <i class="ri-user-settings-line"></i>
    icon: 'ri-user-settings-line',
    roles: ['R_SUPER']
  },
  children: [
    {
      path: 'usermanger',
      name: 'usermanger ',
      component: '/seting/usermanger',
      meta: {
        title: 'menus.seting.usermanger',
        roles: ['R_SUPER'],
        keepAlive: false,
        fixedTab: true
      }
    },


  ]
}