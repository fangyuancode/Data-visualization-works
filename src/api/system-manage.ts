import request from '@/utils/http'
import { AppRouteRecord } from '@/types/router'

// 获取用户列表
export function fetchGetUserList(params: Api.SystemManage.UserSearchParams) {
  return request.get<Api.SystemManage.UserList>({
    url: '/api/user/list',
    params
  })
}

// 新增用户
export function fetchCreateUser(data: Api.SystemManage.UserSaveParams) {
  return request.post<string>({
    url: '/api/user',
    data,
    showSuccessMessage: true
  })
}

// 修改用户
export function fetchUpdateUser(id: number, data: Api.SystemManage.UserSaveParams) {
  return request.put<string>({
    url: `/api/user/${id}`,
    data,
    showSuccessMessage: true
  })
}

// 删除用户
export function fetchDeleteUser(id: number) {
  return request.del<string>({
    url: `/api/user/${id}`,
    showSuccessMessage: true
  })
}

// 修改用户状态
export function fetchUpdateUserStatus(id: number, status: string) {
  return request.put<string>({
    url: `/api/user/${id}/status`,
    params: { status },
    showSuccessMessage: true
  })
}

// 分配用户角色
export function fetchAssignUserRole(id: number, roleId: number) {
  return request.put<string>({
    url: `/api/user/${id}/role`,
    params: { roleId },
    showSuccessMessage: true
  })
}

// 获取角色列表
export function fetchGetRoleList(params: Api.SystemManage.RoleSearchParams) {
  return request.get<Api.SystemManage.RoleList>({
    url: '/api/role/list',
    params
  })
}

// 获取启用角色，用于下拉框
export function fetchGetAllRoles() {
  return request.get<Api.SystemManage.RoleListItem[]>({
    url: '/api/role/all'
  })
}

// 新增角色
export function fetchCreateRole(data: Api.SystemManage.RoleSaveParams) {
  return request.post<string>({
    url: '/api/role',
    data,
    showSuccessMessage: true
  })
}

// 修改角色
export function fetchUpdateRole(roleId: number, data: Api.SystemManage.RoleSaveParams) {
  return request.put<string>({
    url: `/api/role/${roleId}`,
    data,
    showSuccessMessage: true
  })
}

// 删除角色
export function fetchDeleteRole(roleId: number) {
  return request.del<string>({
    url: `/api/role/${roleId}`,
    showSuccessMessage: true
  })
}

// 获取菜单列表，保留原功能
export function fetchGetMenuList() {
  return request.get<AppRouteRecord[]>({
    url: '/api/v3/system/menus/simple'
  })
}


// 获取用户登录日志列表
export function fetchGetLoginLogList(params: Api.SystemManage.LoginLogSearchParams) {
  return request.get<Api.SystemManage.LoginLogList>({
    url: '/api/login-log/list',
    params
  })
}

// 获取用户登录日志统计
export function fetchGetLoginLogStats(params?: Api.SystemManage.LoginLogSearchParams) {
  return request.get<Api.SystemManage.LoginLogStats>({
    url: '/api/login-log/stats',
    params
  })
}

// 获取用户登录日志详情
export function fetchGetLoginLogDetail(id: number) {
  return request.get<Api.SystemManage.LoginLogItem>({
    url: `/api/login-log/${id}`
  })
}

// 删除单条登录日志
export function fetchDeleteLoginLog(id: number) {
  return request.del<string>({
    url: `/api/login-log/${id}`,
    showSuccessMessage: true
  })
}

// 批量删除登录日志
export function fetchBatchDeleteLoginLog(ids: number[]) {
  return request.del<string>({
    url: '/api/login-log/batch',
    params: { ids: ids.join(',') },
    showSuccessMessage: true
  })
}
