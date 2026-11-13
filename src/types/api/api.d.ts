declare namespace Api {
  /** 通用类型 */
  namespace Common {
    /** 分页参数 */
    interface PaginationParams {
      /** 当前页码 */
      current: number
      /** 每页条数 */
      size: number
      /** 总条数 */
      total: number
    }

    /** 通用搜索参数 */
    type CommonSearchParams = Pick<PaginationParams, 'current' | 'size'>

    /** 分页响应基础结构 */
    interface PaginatedResponse<T = any> {
      records: T[]
      current: number
      size: number
      total: number
    }

    /** 启用状态 */
    type EnableStatus = '1' | '2'
  }

  /** 认证类型 */
  namespace Auth {
    /** 登录参数 */
    interface LoginParams {
      username: string
      password: string
      loginType?: string
      clientTimezone?: string
      clientLanguage?: string
      screenSize?: string
      platform?: string
    }

    /** 注册参数 */
    interface RegisterParams {
      username: string
      password: string
      name?: string
      email?: string
      phone?: string
      roleId?: number
      status?: string
      avatarUrl?: string
    }

    /** 登录响应 */
    interface LoginResponse {
      token: string
      refreshToken: string
    }

    /** 用户信息 */
    interface UserInfo {
      buttons: string[]
      roles: string[]
      userId: number
      userName: string
      email: string
      avatar?: string
    }
  }

  /** 系统管理类型 */
  namespace SystemManage {
    /** 用户列表 */
    type UserList = Api.Common.PaginatedResponse<UserListItem>

    /** 用户列表项 */
    interface UserListItem {
      id: number
      avatar: string
      status: string
      userName: string
      userGender: string
      nickName: string
      userPhone: string
      userEmail: string
      roleId?: number
      roleName?: string
      roleCode?: string
      userRoles: string[]
      createBy: string
      createTime: string
      updateBy: string
      updateTime: string
    }

    /** 用户搜索参数 */
    type UserSearchParams = Partial<
      Pick<
        UserListItem,
        'id' | 'userName' | 'userGender' | 'userPhone' | 'userEmail' | 'status' | 'roleId'
      > &
        Api.Common.CommonSearchParams
    >

    interface UserSaveParams {
      id?: number
      userName: string
      password?: string
      nickName?: string
      userPhone?: string
      userEmail?: string
      userGender?: string
      avatar?: string
      status: string
      roleId: number
    }


    /** 登录日志列表 */
    type LoginLogList = Api.Common.PaginatedResponse<LoginLogItem>

    /** 登录日志统计 */
    interface LoginLogStats {
      totalCount: number
      successCount: number
      failCount: number
      todayCount: number
      todayFailCount: number
      lastLoginTime?: string
    }

    /** 登录日志列表项 */
    interface LoginLogItem {
      id: number
      userId?: number
      username: string
      nickName?: string
      roleId?: number
      loginStatus: 'SUCCESS' | 'FAIL' | string
      loginMessage?: string
      loginType?: string
      loginTime: string
      ip?: string
      address?: string
      browser?: string
      browserVersion?: string
      os?: string
      device?: string
      platform?: string
      clientTimezone?: string
      clientLanguage?: string
      screenSize?: string
      userAgent?: string
      requestUri?: string
      requestMethod?: string
      referer?: string
      host?: string
      createdAt?: string
    }

    /** 登录日志搜索参数 */
    type LoginLogSearchParams = Partial<
      Pick<LoginLogItem, 'username' | 'loginStatus' | 'ip' | 'address' | 'browser' | 'os' | 'device'> &
        Api.Common.CommonSearchParams & {
          startTime: string | null
          endTime: string | null
        }
    >

    /** 角色列表 */
    type RoleList = Api.Common.PaginatedResponse<RoleListItem>

    /** 角色列表项 */
    interface RoleListItem {
      roleId: number
      roleName: string
      roleCode: string
      description: string
      enabled: boolean
      createTime: string
      updateTime?: string
    }

    interface RoleSaveParams {
      roleId?: number
      roleName: string
      roleCode: string
      description?: string
      enabled: boolean
    }

    /** 角色搜索参数 */
    type RoleSearchParams = Partial<
      Pick<RoleListItem, 'roleId' | 'roleName' | 'roleCode' | 'description' | 'enabled'> &
        Api.Common.CommonSearchParams & {
          startTime: string | null
          endTime: string | null
        }
    >
  }
}
