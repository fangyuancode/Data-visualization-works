import { AppRouteRecordRaw } from '@/utils/router'
import { RoutesAlias } from '../routesAlias'

/**
 * 静态路由配置（不需要动态菜单注册即可匹配的路由）
 *
 * 说明：
 * 1. 登录、注册、找回密码、错误页、大屏等公共页面放在这里。
 * 2. /dashboard、/source、/system 等业务页面仍然由动态路由注册。
 * 3. Fallback 兜底路由必须放在最后，用来解决刷新动态路由页面时 Vue Router 先提示
 *    “No match found for location ...” 的问题。
 */
export const staticRoutes: AppRouteRecordRaw[] = [
  {
    path: '/',
    redirect: RoutesAlias.Login
  },
  {
    path: RoutesAlias.Login,
    name: 'Login',
    component: () => import('@views/auth/login/index.vue'),
    meta: { title: 'menus.login.title', isHideTab: true }
  },
  {
    path: '/auth/register',
    name: 'Register',
    component: () => import('@views/auth/register/index.vue'),
    meta: { title: 'menus.register.title', isHideTab: true }
  },
  {
    path: '/auth/forget-password',
    name: 'ForgetPassword',
    component: () => import('@views/auth/forget-password/index.vue'),
    meta: { title: 'menus.forgetPassword.title', isHideTab: true }
  },
  {
    path: '/screen',
    name: 'screen',
    component: () => import('@/views/screen/index.vue'),
    meta: { title: '数据总览大屏' }
  },
  {
    path: '/outside',
    component: () => import('@views/index/index.vue'),
    name: 'Outside',
    meta: { title: 'menus.outside.title' },
    children: [
      {
        path: '/outside/iframe/:path',
        name: 'Iframe',
        component: () => import('@/views/outside/Iframe.vue'),
        meta: { title: 'iframe' }
      }
    ]
  },
  {
    path: '/exception',
    name: 'Exception',
    component: () => import('@views/index/index.vue'),
    meta: { title: 'menus.exception.title', isHideTab: true },
    children: [
      {
        path: '403',
        name: 'Exception403',
        component: () => import('@/views/exception/403/index.vue'),
        meta: {
          title: 'menus.exception.forbidden',
          keepAlive: true,
          isHideTab: true,
          isFullPage: true
        }
      },
      {
        path: '404',
        name: 'Exception404',
        component: () => import('@/views/exception/404/index.vue'),
        meta: {
          title: 'menus.exception.notFound',
          keepAlive: true,
          isHideTab: true,
          isFullPage: true
        }
      },
      {
        path: '500',
        name: 'Exception500',
        component: () => import('@/views/exception/500/index.vue'),
        meta: {
          title: 'menus.exception.serverError',
          keepAlive: true,
          isHideTab: true,
          isFullPage: true
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'Fallback',
    component: () => import('@/views/exception/404/index.vue'),
    meta: { title: 'menus.exception.notFound', isHideTab: true }
  }
]
