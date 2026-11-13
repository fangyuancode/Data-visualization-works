import request from '@/utils/http'

/**
 * 登录
 */
export function fetchLogin (params: Api.Auth.LoginParams) {
  return request.post<Api.Auth.LoginResponse>({
    url: '/api/auth/login',
    data: params
  })
}


/**
 * 注册
 */
export function fetchRegister (params: Api.Auth.RegisterParams) {
  return request.post<string>({
    url: '/api/auth/register',
    data: params
  })
}

/**
 * 获取用户信息
 */
export function fetchGetUserInfo () {
  return request.get<Api.Auth.UserInfo>({
    url: '/api/user/info'
  })
}