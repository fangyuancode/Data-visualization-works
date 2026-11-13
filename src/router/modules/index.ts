import { AppRouteRecord } from '@/types/router'
import { dashboardRoutes } from './dashboard'
import { systemRoutes } from './system'
import { resultRoutes } from './result'
import { exceptionRoutes } from './exception'
import { sourceRoutes } from './source'
import { toolboxRoutes } from "./toolbox"
import { setingRoutes } from "./seting"
/**
 * 导出所有模块化路由
 */
export const routeModules: AppRouteRecord[] = [
  dashboardRoutes,
  systemRoutes,
  sourceRoutes,
  setingRoutes

]
