<!-- 角色管理页面 -->
<template>
  <div class="art-full-height">
    <RoleSearch v-show="showSearchBar" v-model="searchForm" @search="handleSearch" @reset="resetSearchParams" />

    <ElCard class="art-table-card" :style="{ 'margin-top': showSearchBar ? '12px' : '0' }">
      <ArtTableHeader v-model:columns="columnChecks" v-model:showSearchBar="showSearchBar" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElSpace wrap>
            <ElButton v-if="canAdd" type="primary" @click="showDialog('add')" v-ripple>
              新增角色
            </ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <ArtTable :loading="loading" :data="data" :columns="columns" :pagination="pagination" @pagination:size-change="handleSizeChange" @pagination:current-change="handleCurrentChange" />
    </ElCard>

    <RoleEditDialog v-model="dialogVisible" :dialog-type="dialogType" :role-data="currentRoleData" @success="refreshData" />
  </div>
</template>

<script setup lang="ts">
import { h, ref } from 'vue'
import type { ColumnOption } from '@/types/component'
import type { ButtonMoreItem } from '@/components/core/forms/art-button-more/index.vue'
import { useTable } from '@/hooks/core/useTable'
import { fetchDeleteRole, fetchGetRoleList } from '@/api/system-manage'
import ArtButtonMore from '@/components/core/forms/art-button-more/index.vue'
import RoleSearch from './modules/role-search.vue'
import RoleEditDialog from './modules/role-edit-dialog.vue'
import { ElMessage, ElMessageBox, ElTag } from 'element-plus'
import { usePagePermission } from '@/hooks/core/usePagePermission'

defineOptions({ name: 'Role' })

type RoleListItem = Api.SystemManage.RoleListItem
type RoleSearchFormParams = Api.SystemManage.RoleSearchParams & {
  daterange?: string[]
}

type DialogType = 'add' | 'edit'

const { canWrite, canAdd, canEdit, canDelete, checkWritePermission } = usePagePermission()

const searchForm = ref<RoleSearchFormParams>({
  roleName: undefined,
  roleCode: undefined,
  description: undefined,
  enabled: undefined,
  daterange: undefined
})

const showSearchBar = ref(false)
const dialogVisible = ref(false)
const currentRoleData = ref<RoleListItem | undefined>(undefined)
const dialogType = ref<DialogType>('add')

/**
 * 角色表格列配置。
 * 显式声明 ColumnOption<RoleListItem>[]，避免 fixed: 'right' 被推断成普通 string，
 * 从而导致 pnpm build 时 columnsFactory 类型不匹配。
 */
const createRoleColumns = (): ColumnOption<RoleListItem>[] => {
  const baseColumns: ColumnOption<RoleListItem>[] = [
    {
      prop: 'roleId',
      label: '角色ID',
      width: 100
    },
    {
      prop: 'roleName',
      label: '角色名称',
      minWidth: 120
    },
    {
      prop: 'roleCode',
      label: '角色编码',
      minWidth: 130
    },
    {
      prop: 'description',
      label: '角色描述',
      minWidth: 180,
      showOverflowTooltip: true
    },
    {
      prop: 'enabled',
      label: '角色状态',
      width: 100,
      formatter: (row: RoleListItem) =>
        h(ElTag, { type: row.enabled ? 'success' : 'warning' }, () =>
          row.enabled ? '启用' : '禁用'
        )
    },
    {
      prop: 'createTime',
      label: '创建日期',
      width: 180,
      sortable: true
    }
  ]

  if (!canWrite.value) {
    return baseColumns
  }

  const operationColumn: ColumnOption<RoleListItem> = {
    prop: 'operation',
    label: '操作',
    width: 90,
    fixed: 'right',
    formatter: (row: RoleListItem) => {
      const actionList: ButtonMoreItem[] = [
        ...(canEdit.value
          ? [
              {
                key: 'edit',
                label: '编辑角色',
                icon: 'ri:edit-2-line'
              } satisfies ButtonMoreItem
            ]
          : []),
        ...(canDelete.value
          ? [
              {
                key: 'delete',
                label: '删除角色',
                icon: 'ri:delete-bin-4-line',
                color: '#f56c6c'
              } satisfies ButtonMoreItem
            ]
          : [])
      ]

      return h('div', [
        h(ArtButtonMore, {
          list: actionList,
          onClick: (item: ButtonMoreItem) => buttonMoreClick(item, row)
        })
      ])
    }
  }

  return [...baseColumns, operationColumn]
}

const {
  columns,
  columnChecks,
  data,
  loading,
  pagination,
  getData,
  replaceSearchParams,
  resetSearchParams,
  handleSizeChange,
  handleCurrentChange,
  refreshData
} = useTable({
  core: {
    apiFn: fetchGetRoleList,
    apiParams: {
      current: 1,
      size: 20
    },
    excludeParams: ['daterange'],
    columnsFactory: createRoleColumns
  }
})

const showDialog = (type: DialogType, row?: RoleListItem) => {
  if ((type === 'add' || type === 'edit') && !checkWritePermission()) {
    return
  }

  dialogVisible.value = true
  dialogType.value = type
  currentRoleData.value = row
}

const handleSearch = (params: RoleSearchFormParams) => {
  const { daterange, ...filtersParams } = params
  const [startTime, endTime] = Array.isArray(daterange) ? daterange : [null, null]
  replaceSearchParams({ ...filtersParams, startTime, endTime })
  getData()
}

const buttonMoreClick = (item: ButtonMoreItem, row: RoleListItem) => {
  if (!checkWritePermission()) {
    return
  }

  switch (item.key) {
    case 'edit':
      showDialog('edit', row)
      break
    case 'delete':
      deleteRole(row)
      break
  }
}

const deleteRole = (row: RoleListItem) => {
  if (!checkWritePermission()) {
    return
  }

  ElMessageBox.confirm(`确定删除角色“${row.roleName}”吗？此操作不可恢复！`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await fetchDeleteRole(row.roleId)
      refreshData()
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}
</script>
