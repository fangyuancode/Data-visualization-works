<template>
  <div class="user-page">
    <div class="page-header art-card">
      <div class="page-header__left">
        <div class="page-title">用户管理</div>
        <div class="page-desc">用于查看系统用户、维护用户状态，并为用户分配系统角色</div>
      </div>
      <div class="page-header__right">
        <ElButton v-if="canAdd" type="primary" @click="openDialog('add')">新增用户</ElButton>
      </div>
    </div>

    <ElRow :gutter="20" class="mb-20">
      <ElCol :xs="24" :sm="12" :lg="6" v-for="item in statisticsCards" :key="item.label">
        <div class="art-card stat-card">
          <div class="stat-card__label">{{ item.label }}</div>
          <div class="stat-card__value">{{ item.value }}</div>
          <div class="stat-card__extra">{{ item.extra }}</div>
        </div>
      </ElCol>
    </ElRow>

    <div class="art-card search-card mb-20">
      <ElForm :model="searchForm" inline label-width="68px" class="search-form">
        <ElFormItem label="用户名">
          <ElInput v-model="searchForm.userName" placeholder="请输入用户名" clearable />
        </ElFormItem>

        <ElFormItem label="手机号">
          <ElInput v-model="searchForm.userPhone" placeholder="请输入手机号" clearable />
        </ElFormItem>

        <ElFormItem label="邮箱">
          <ElInput v-model="searchForm.userEmail" placeholder="请输入邮箱" clearable />
        </ElFormItem>

        <ElFormItem label="角色">
          <ElSelect v-model="searchForm.roleId" placeholder="请选择角色" clearable style="width: 150px">
            <ElOption v-for="role in roleOptions" :key="role.roleId" :label="role.roleName" :value="role.roleId" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="性别">
          <ElSelect v-model="searchForm.userGender" placeholder="请选择性别" clearable style="width: 120px">
            <ElOption label="男" value="男" />
            <ElOption label="女" value="女" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="状态">
          <ElSelect v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <ElOption label="启用" value="1" />
            <ElOption label="禁用" value="0" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem>
          <ElSpace wrap>
            <ElButton type="primary" @click="handleSearch">查询</ElButton>
            <ElButton @click="handleReset">重置</ElButton>
          </ElSpace>
        </ElFormItem>
      </ElForm>
    </div>

    <div class="art-card table-card">
      <div class="table-toolbar">
        <div class="table-toolbar__left">
          <ElSpace wrap>
            <ElButton v-if="canAdd" type="primary" plain @click="openDialog('add')" :disabled="userInfo.userId === 3">新增</ElButton>
            <ElButton v-if="canEdit" :disabled="selectedRows.length !== 1" @click="handleBatchEdit">编辑</ElButton>
          </ElSpace>
        </div>

        <div class="table-toolbar__right">
          <div class="toolbar-tip">当前共 {{ pagination.total }} 条数据</div>
        </div>
      </div>

      <ElTable v-loading="loading" :data="tableData" style="width: 100%" @selection-change="handleSelectionChange">
        <ElTableColumn v-if="canWrite" type="selection" width="55" />

        <ElTableColumn label="用户信息" min-width="240">
          <template #default="{ row }">
            <div class="user-info">
              <ElAvatar :size="42" :src="row.avatar">
                {{ row.userName?.slice(0, 1) || 'U' }}
              </ElAvatar>
              <div class="user-info__content">
                <div class="user-info__name">{{ row.userName }}</div>
                <div class="user-info__sub">{{ row.userEmail || '暂无邮箱' }}</div>
              </div>
            </div>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="nickName" label="昵称" min-width="120" />
        <ElTableColumn prop="userGender" label="性别" width="90" />
        <ElTableColumn prop="userPhone" label="手机号" min-width="140" />

        <ElTableColumn label="角色分配" min-width="190">
          <template #default="{ row }">
            <ElSelect :model-value="row.roleId" placeholder="请选择角色" style="width: 150px" :disabled="!canWrite" @change="(roleId: number) => handleAssignRole(row, roleId)">
              <ElOption v-for="role in roleOptions" :key="role.roleId" :label="role.roleName" :value="role.roleId" />
            </ElSelect>
          </template>
        </ElTableColumn>

        <ElTableColumn label="状态" width="100">
          <template #default="{ row }">
            <ElTag :type="row.status === '1' ? 'success' : 'danger'" effect="light" round>
              {{ row.status === '1' ? '启用' : '禁用' }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="createTime" label="创建时间" min-width="170" />

        <ElTableColumn v-if="canWrite" label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <ElSpace wrap>
              <ElButton v-if="canEdit" link type="primary" @click="openDialog('edit', row)" :disabled="userInfo.userId === 3">编辑</ElButton>
              <ElButton v-if="canEdit" link type="warning" @click="toggleUserStatus(row)" :disabled="userInfo.userId === 3">
                {{ row.status === '1' ? '禁用' : '启用' }}
              </ElButton>
              <ElButton v-if="canDelete" link type="danger" @click="handleDelete(row)" :disabled="userInfo.userId === 3">删除</ElButton>
            </ElSpace>
          </template>
        </ElTableColumn>
      </ElTable>

      <div class="table-pagination">
        <ElPagination v-model:current-page="pagination.current" v-model:page-size="pagination.size" :page-sizes="[5, 10, 20, 50]" background layout="total, sizes, prev, pager, next, jumper" :total="pagination.total" @size-change="loadUserList" @current-change="loadUserList" />
      </div>
    </div>

    <ElDialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增用户' : '编辑用户'" width="620px" destroy-on-close>
      <ElForm ref="dialogFormRef" :model="dialogForm" :rules="dialogRules" label-width="90px">
        <ElRow :gutter="16">
          <ElCol :span="12">
            <ElFormItem label="用户名" prop="userName">
              <ElInput v-model="dialogForm.userName" placeholder="请输入用户名" />
            </ElFormItem>
          </ElCol>

          <ElCol :span="12">
            <ElFormItem :label="dialogType === 'add' ? '密码' : '新密码'" :prop="dialogType === 'add' ? 'password' : ''">
              <ElInput v-model="dialogForm.password" type="password" show-password :placeholder="dialogType === 'add' ? '请输入密码' : '不修改可留空'" />
            </ElFormItem>
          </ElCol>

          <ElCol :span="12">
            <ElFormItem label="昵称" prop="nickName">
              <ElInput v-model="dialogForm.nickName" placeholder="请输入昵称" />
            </ElFormItem>
          </ElCol>

          <ElCol :span="12">
            <ElFormItem label="手机号" prop="userPhone">
              <ElInput v-model="dialogForm.userPhone" placeholder="请输入手机号" />
            </ElFormItem>
          </ElCol>

          <ElCol :span="12">
            <ElFormItem label="邮箱" prop="userEmail">
              <ElInput v-model="dialogForm.userEmail" placeholder="请输入邮箱" />
            </ElFormItem>
          </ElCol>

          <ElCol :span="12">
            <ElFormItem label="性别" prop="userGender">
              <ElSelect v-model="dialogForm.userGender" placeholder="请选择性别" style="width: 100%">
                <ElOption label="男" value="男" />
                <ElOption label="女" value="女" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :span="12">
            <ElFormItem label="状态" prop="status">
              <ElSelect v-model="dialogForm.status" placeholder="请选择状态" style="width: 100%">
                <ElOption label="启用" value="1" />
                <ElOption label="禁用" value="0" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :span="12">
            <ElFormItem label="角色" prop="roleId">
              <ElSelect v-model="dialogForm.roleId" placeholder="请选择角色" style="width: 100%">
                <ElOption v-for="role in roleOptions" :key="role.roleId" :label="role.roleName" :value="role.roleId" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :span="24">
            <ElFormItem label="头像地址">
              <ElInput v-model="dialogForm.avatar" placeholder="请输入头像地址，可为空" />
            </ElFormItem>
          </ElCol>
        </ElRow>
      </ElForm>

      <template #footer>
        <ElSpace>
          <ElButton @click="dialogVisible = false">取消</ElButton>
          <ElButton type="primary" :loading="submitLoading" @click="handleDialogSubmit">确定</ElButton>
        </ElSpace>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessageBox } from 'element-plus'
import { usePagePermission } from '@/hooks/core/usePagePermission'
import { useUserStore } from '@/store/modules/user'

import {
  fetchAssignUserRole,
  fetchCreateUser,
  fetchDeleteUser,
  fetchGetAllRoles,
  fetchGetUserList,
  fetchUpdateUser,
  fetchUpdateUserStatus
} from '@/api/system-manage'

defineOptions({ name: 'User' })

type UserItem = Api.SystemManage.UserListItem
type DialogType = 'add' | 'edit'

const { canWrite, canAdd, canEdit, canDelete, checkWritePermission } = usePagePermission()

const tableData = ref<UserItem[]>([])
const roleOptions = ref<Api.SystemManage.RoleListItem[]>([])
const selectedRows = ref<UserItem[]>([])
const loading = ref(false)
const submitLoading = ref(false)

const searchForm = reactive<Api.SystemManage.UserSearchParams>({
  userName: '',
  userPhone: '',
  userEmail: '',
  userGender: '',
  status: '',
  roleId: undefined
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})
const userStore = useUserStore()
let userInfo = computed(() => userStore.getUserInfo)
// 获取用户信息
// console.log('userStore', userInfo.value)
const statisticsCards = computed(() => {
  const total = pagination.total
  const enabled = tableData.value.filter((item) => item.status === '1').length
  const disabled = tableData.value.filter((item) => item.status === '0').length
  const admins = tableData.value.filter((item) =>
    ['R_SUPER', 'R_ADMIN'].includes(item.roleCode || '')
  ).length

  return [
    { label: '用户总数', value: total, extra: '系统当前已录入用户' },
    { label: '当前页启用', value: enabled, extra: '当前页可正常登录用户' },
    { label: '当前页禁用', value: disabled, extra: '当前页已停用用户' },
    { label: '当前页管理员', value: admins, extra: '超级管理员与系统管理员' }
  ]
})

const dialogVisible = ref(false)
const dialogType = ref<DialogType>('add')
const dialogFormRef = ref<FormInstance>()

const getDefaultDialogForm = (): Api.SystemManage.UserSaveParams => ({
  id: 0,
  avatar: '',
  status: '1',
  userName: '',
  password: '',
  userGender: '男',
  nickName: '',
  userPhone: '',
  userEmail: '',
  roleId: 3
})

const dialogForm = reactive<Api.SystemManage.UserSaveParams>(getDefaultDialogForm())

const dialogRules = computed<FormRules>(() => ({
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度为 2-20 位', trigger: 'blur' }
  ],
  password:
    dialogType.value === 'add'
      ? [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 32, message: '密码长度为 6-32 位', trigger: 'blur' }
        ]
      : [{ min: 6, max: 32, message: '密码长度为 6-32 位', trigger: 'blur' }],
  nickName: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  userEmail: [{ type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] }],
  userGender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
}))

const loadRoleOptions = async () => {
  roleOptions.value = await fetchGetAllRoles()
}

const loadUserList = async () => {
  loading.value = true
  try {
    const res = await fetchGetUserList({
      ...searchForm,
      current: pagination.current,
      size: pagination.size
    })
    tableData.value = res.records || []
    pagination.total = res.total || 0
    pagination.current = res.current || pagination.current
    pagination.size = res.size || pagination.size
  } finally {
    loading.value = false
  }
}

const resetDialogForm = () => {
  Object.assign(dialogForm, getDefaultDialogForm())
}

const handleSearch = () => {
  pagination.current = 1
  loadUserList()
}

const handleReset = () => {
  searchForm.userName = ''
  searchForm.userPhone = ''
  searchForm.userEmail = ''
  searchForm.userGender = ''
  searchForm.status = ''
  searchForm.roleId = undefined
  pagination.current = 1
  loadUserList()
}

const handleSelectionChange = (rows: UserItem[]) => {
  selectedRows.value = rows
}

const openDialog = (type: DialogType, row?: UserItem) => {
  if ((type === 'add' || type === 'edit') && !checkWritePermission()) {
    return
  }
  dialogType.value = type
  resetDialogForm()

  if (type === 'edit' && row) {
    Object.assign(dialogForm, {
      id: row.id,
      avatar: row.avatar || '',
      status: row.status || '1',
      userName: row.userName || '',
      password: '',
      userGender: row.userGender || '男',
      nickName: row.nickName || '',
      userPhone: row.userPhone || '',
      userEmail: row.userEmail || '',
      roleId: row.roleId || 3
    })
  }

  dialogVisible.value = true
}

const handleDialogSubmit = async () => {
  if (!checkWritePermission()) {
    return
  }

  if (!dialogFormRef.value) return

  await dialogFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      const payload = { ...dialogForm }
      if (dialogType.value === 'add') {
        await fetchCreateUser(payload)
      } else {
        await fetchUpdateUser(dialogForm.id || 0, payload)
      }
      dialogVisible.value = false
      loadUserList()
    } finally {
      submitLoading.value = false
    }
  })
}

const handleAssignRole = async (row: UserItem, roleId: number) => {
  if (!checkWritePermission()) {
    loadUserList()
    return
  }

  await fetchAssignUserRole(row.id, roleId)
  await loadUserList()
}

const toggleUserStatus = async (row: UserItem) => {
  if (!checkWritePermission()) {
    return
  }

  const nextStatus = row.status === '1' ? '0' : '1'
  await fetchUpdateUserStatus(row.id, nextStatus)
  await loadUserList()
}

const handleDelete = async (row: UserItem) => {
  if (!checkWritePermission()) {
    return
  }

  await ElMessageBox.confirm(`确定删除用户 “${row.userName}” 吗？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })

  await fetchDeleteUser(row.id)
  await loadUserList()
}

const handleBatchEdit = () => {
  if (!checkWritePermission()) {
    return
  }

  if (selectedRows.value.length === 1) {
    openDialog('edit', selectedRows.value[0])
  }
}

onMounted(async () => {
  await loadRoleOptions()
  await loadUserList()
})
</script>

<style scoped lang="scss">
.user-page {
  .mb-20 {
    margin-bottom: 20px;
  }
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22px 24px;
  margin-bottom: 20px;
  gap: 16px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--art-text-gray-900);
  line-height: 1.2;
}

.page-desc {
  margin-top: 8px;
  font-size: 14px;
  color: var(--art-text-gray-600);
  line-height: 1.8;
}

.stat-card {
  padding: 20px;
  height: 132px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin-bottom: 20px;
}

.stat-card__label {
  font-size: 14px;
  color: var(--art-text-gray-600);
}

.stat-card__value {
  margin-top: 12px;
  font-size: 30px;
  font-weight: 700;
  color: var(--art-text-gray-900);
  line-height: 1;
}

.stat-card__extra {
  margin-top: 12px;
  font-size: 13px;
  color: var(--art-text-gray-500);
}

.search-card {
  padding: 18px 20px 2px;
}

.search-form {
  :deep(.el-form-item) {
    margin-bottom: 16px;
  }
}

.table-card {
  padding: 0;
  overflow: hidden;
}

.table-toolbar {
  padding: 18px 20px;
  border-bottom: 1px solid var(--art-border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.toolbar-tip {
  font-size: 13px;
  color: var(--art-text-gray-500);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info__content {
  min-width: 0;
}

.user-info__name {
  font-size: 14px;
  font-weight: 600;
  color: var(--art-text-gray-900);
  line-height: 1.5;
}

.user-info__sub {
  font-size: 12px;
  color: var(--art-text-gray-500);
  line-height: 1.5;
  word-break: break-all;
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
  padding: 18px 20px 20px;
  border-top: 1px solid var(--art-border-color);
  background: #fff;
}

:deep(.el-table) {
  --el-table-header-bg-color: #fafafa;
}

:deep(.el-table th.el-table__cell) {
  font-weight: 600;
  color: var(--art-text-gray-700);
}

:deep(.el-dialog__body) {
  padding-top: 20px;
  padding-bottom: 10px;
}

@media (max-width: 992px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .table-pagination {
    overflow-x: auto;
    justify-content: flex-start;
  }
}
</style>
