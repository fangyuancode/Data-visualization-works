import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

/**
 * 页面写操作权限 Hook。
 *
 * 说明：
 * 1. R_SUPER、R_ADMIN 允许新增、编辑、删除。
 * 2. test 账号强制只读，即使数据库误分配管理员角色，也不能进行写操作。
 * 3. 该 Hook 主要用于前端按钮显示与方法兜底，真正安全控制仍以后端拦截器为准。
 */
const WRITE_ROLES = ['R_SUPER', 'R_ADMIN']
const READONLY_USERNAMES = ['test']

export function usePagePermission () {
  const userStore = useUserStore()

  const userInfo = computed(() => userStore.info || {})

  const roles = computed<string[]>(() => userInfo.value.roles || [])

  const userName = computed(() => {
    const rawInfo = userInfo.value as Partial<Api.Auth.UserInfo> & { username?: string }
    return String(rawInfo.userName || rawInfo.username || '').trim().toLowerCase()
  })

  const isReadonlyUser = computed(() => READONLY_USERNAMES.includes(userName.value))

  const canWrite = computed(() => {
    if (isReadonlyUser.value) {
      return false
    }

    return roles.value.some((role) => WRITE_ROLES.includes(role))
  })

  const canAdd = computed(() => canWrite.value)
  const canEdit = computed(() => canWrite.value)
  const canDelete = computed(() => canWrite.value)

  const checkWritePermission = () => {
    if (canWrite.value) {
      return true
    }

    ElMessage.warning('当前账号只有查看权限，不能新增、编辑或删除数据')
    return false
  }

  return {
    roles,
    userName,
    isReadonlyUser,
    canWrite,
    canAdd,
    canEdit,
    canDelete,
    checkWritePermission
  }
}
