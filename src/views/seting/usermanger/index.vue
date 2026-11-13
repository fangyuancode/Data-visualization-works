<template>
  <div class="login-log-page">
    <div class="page-header art-card">
      <div class="page-header__left">
        <div class="page-title">用户登录管理</div>
        <div class="page-desc">
          记录用户每一次登录请求的账号、结果、IP归属地、浏览器、操作系统、设备、前端环境和请求来源，便于排查异常登录与系统审计。
        </div>
      </div>
      <div class="page-header__right">
        <ElSpace wrap>
          <ElButton @click="loadPageData">刷新数据</ElButton>
          <ElButton type="primary" plain @click="exportCurrentPageCsv" :disabled="!tableData.length">
            导出当前页
          </ElButton>
        </ElSpace>
      </div>
    </div>

    <ElRow :gutter="20" class="stat-row">
      <ElCol v-for="item in statisticsCards" :key="item.label" :xs="24" :sm="12" :md="8" :lg="4">
        <div class="art-card stat-card">
          <div class="stat-card__label">{{ item.label }}</div>
          <div class="stat-card__value">{{ item.value }}</div>
          <div class="stat-card__extra">{{ item.extra }}</div>
        </div>
      </ElCol>
    </ElRow>

    <div class="art-card search-card">
      <ElForm :model="searchForm" inline label-width="78px" class="search-form">
        <ElFormItem label="用户名">
          <ElInput v-model.trim="searchForm.username" placeholder="请输入用户名" clearable />
        </ElFormItem>

        <ElFormItem label="登录结果">
          <ElSelect v-model="searchForm.loginStatus" placeholder="请选择结果" clearable style="width: 140px">
            <ElOption label="成功" value="SUCCESS" />
            <ElOption label="失败" value="FAIL" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="IP地址">
          <ElInput v-model.trim="searchForm.ip" placeholder="请输入IP" clearable />
        </ElFormItem>

        <ElFormItem label="归属地">
          <ElInput v-model.trim="searchForm.address" placeholder="请输入归属地" clearable />
        </ElFormItem>

        <ElFormItem label="浏览器">
          <ElSelect v-model="searchForm.browser" placeholder="请选择浏览器" clearable style="width: 140px">
            <ElOption v-for="item in browserOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="操作系统">
          <ElSelect v-model="searchForm.os" placeholder="请选择系统" clearable style="width: 140px">
            <ElOption v-for="item in osOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="设备类型">
          <ElSelect v-model="searchForm.device" placeholder="请选择设备" clearable style="width: 140px">
            <ElOption v-for="item in deviceOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="登录时间">
          <ElDatePicker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 360px"
          />
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
            <ElButton type="danger" plain :disabled="selectedRows.length === 0" @click="handleBatchDelete">
              批量删除
            </ElButton>
            <ElTag type="info" effect="plain">当前筛选 {{ pagination.total }} 条</ElTag>
          </ElSpace>
        </div>
        <div class="table-toolbar__right">
          <span class="toolbar-tip">建议定期查看失败登录、异地IP和异常设备记录</span>
        </div>
      </div>

      <ElTable
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        row-key="id"
        @selection-change="handleSelectionChange"
      >
        <ElTableColumn type="selection" width="55" />

        <ElTableColumn label="登录用户" min-width="180">
          <template #default="{ row }">
            <div class="user-cell">
              <ElAvatar :size="38">{{ row.username?.slice(0, 1)?.toUpperCase() || 'U' }}</ElAvatar>
              <div class="user-cell__content">
                <div class="user-cell__name">{{ row.username || '未知用户' }}</div>
                <div class="user-cell__sub">
                  {{ row.nickName || '暂无昵称' }}<span v-if="row.userId"> · ID {{ row.userId }}</span>
                </div>
              </div>
            </div>
          </template>
        </ElTableColumn>

        <ElTableColumn label="登录结果" width="118">
          <template #default="{ row }">
            <ElTag :type="row.loginStatus === 'SUCCESS' ? 'success' : 'danger'" effect="light" round>
              {{ row.loginStatus === 'SUCCESS' ? '成功' : '失败' }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn label="结果说明" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.loginMessage || '-' }}
          </template>
        </ElTableColumn>

        <ElTableColumn label="IP与归属地" min-width="220">
          <template #default="{ row }">
            <div class="main-text">{{ row.ip || '-' }}</div>
            <div class="sub-text">{{ row.address || '未知归属地' }}</div>
          </template>
        </ElTableColumn>

        <ElTableColumn label="设备环境" min-width="240">
          <template #default="{ row }">
            <div class="tag-line">
              <ElTag size="small" effect="plain">{{ row.device || '未知设备' }}</ElTag>
              <ElTag size="small" effect="plain" type="info">{{ row.os || '未知系统' }}</ElTag>
            </div>
            <div class="sub-text">
              {{ row.browser || '未知浏览器' }} {{ row.browserVersion || '' }}
            </div>
          </template>
        </ElTableColumn>

        <ElTableColumn label="前端环境" min-width="210">
          <template #default="{ row }">
            <div class="main-text">{{ row.platform || '-' }}</div>
            <div class="sub-text">{{ row.clientTimezone || '-' }} · {{ row.screenSize || '-' }}</div>
          </template>
        </ElTableColumn>

        <ElTableColumn label="请求来源" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="main-text">{{ row.requestMethod || 'POST' }} {{ row.requestUri || '-' }}</div>
            <div class="sub-text">{{ row.host || '-' }}</div>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="loginTime" label="登录时间" min-width="170" sortable />

        <ElTableColumn label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <ElSpace wrap>
              <ElButton link type="primary" @click="openDetail(row)">详情</ElButton>
              <ElButton link type="danger" @click="handleDelete(row)">删除</ElButton>
            </ElSpace>
          </template>
        </ElTableColumn>
      </ElTable>

      <div class="table-pagination">
        <ElPagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="loadList"
          @current-change="loadList"
        />
      </div>
    </div>

    <ElDrawer v-model="detailDrawerVisible" title="登录详情" size="520px" destroy-on-close>
      <div v-loading="detailLoading" class="detail-wrap">
        <template v-if="currentDetail">
          <div class="detail-status-card" :class="currentDetail.loginStatus === 'SUCCESS' ? 'is-success' : 'is-fail'">
            <div>
              <div class="detail-status-card__title">
                {{ currentDetail.username || '未知用户' }} · {{ currentDetail.loginStatus === 'SUCCESS' ? '登录成功' : '登录失败' }}
              </div>
              <div class="detail-status-card__desc">{{ currentDetail.loginMessage || '暂无说明' }}</div>
            </div>
            <ElTag :type="currentDetail.loginStatus === 'SUCCESS' ? 'success' : 'danger'" round>
              {{ currentDetail.loginStatus === 'SUCCESS' ? 'SUCCESS' : 'FAIL' }}
            </ElTag>
          </div>

          <ElDescriptions :column="1" border class="detail-descriptions">
            <ElDescriptionsItem label="用户ID">{{ currentDetail.userId || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="用户名">{{ currentDetail.username || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="用户昵称">{{ currentDetail.nickName || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="登录方式">{{ currentDetail.loginType || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="登录时间">{{ currentDetail.loginTime || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="IP地址">{{ currentDetail.ip || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="IP归属地">{{ currentDetail.address || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="设备类型">{{ currentDetail.device || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="操作系统">{{ currentDetail.os || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="浏览器">
              {{ currentDetail.browser || '-' }} {{ currentDetail.browserVersion || '' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="前端平台">{{ currentDetail.platform || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="客户端语言">{{ currentDetail.clientLanguage || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="客户端时区">{{ currentDetail.clientTimezone || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="屏幕分辨率">{{ currentDetail.screenSize || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="请求地址">{{ currentDetail.requestUri || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="请求方法">{{ currentDetail.requestMethod || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="Host">{{ currentDetail.host || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="来源页面">{{ currentDetail.referer || '-' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="User-Agent">
              <div class="ua-text">{{ currentDetail.userAgent || '-' }}</div>
            </ElDescriptionsItem>
          </ElDescriptions>
        </template>
      </div>
    </ElDrawer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  fetchBatchDeleteLoginLog,
  fetchDeleteLoginLog,
  fetchGetLoginLogDetail,
  fetchGetLoginLogList,
  fetchGetLoginLogStats
} from '@/api/system-manage'

defineOptions({ name: 'UserLoginManager' })

type LoginLogItem = Api.SystemManage.LoginLogItem

const browserOptions = ['Chrome', 'Edge', 'Firefox', 'Safari', 'Opera', 'Internet Explorer', '其他', '未知']
const osOptions = ['Windows', 'macOS', 'Linux', 'Android', 'iOS', '其他', '未知']
const deviceOptions = ['PC端', '移动端', '平板', '未知']

const loading = ref(false)
const detailLoading = ref(false)
const tableData = ref<LoginLogItem[]>([])
const selectedRows = ref<LoginLogItem[]>([])
const detailDrawerVisible = ref(false)
const currentDetail = ref<LoginLogItem | null>(null)
const dateRange = ref<[string, string] | [] | null>([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const stats = reactive<Api.SystemManage.LoginLogStats>({
  totalCount: 0,
  successCount: 0,
  failCount: 0,
  todayCount: 0,
  todayFailCount: 0,
  lastLoginTime: ''
})

const searchForm = reactive<Api.SystemManage.LoginLogSearchParams>({
  username: '',
  loginStatus: '',
  ip: '',
  address: '',
  browser: '',
  os: '',
  device: '',
  startTime: null,
  endTime: null
})

const statisticsCards = computed(() => [
  { label: '登录总数', value: stats.totalCount || 0, extra: '累计登录请求记录' },
  { label: '成功次数', value: stats.successCount || 0, extra: '账号密码验证通过' },
  { label: '失败次数', value: stats.failCount || 0, extra: '账号不存在/密码错误等' },
  { label: '今日登录', value: stats.todayCount || 0, extra: '今天产生的登录请求' },
  { label: '今日失败', value: stats.todayFailCount || 0, extra: '今天异常登录次数' },
  { label: '最近登录', value: stats.lastLoginTime || '-', extra: '最后一次登录时间' }
])

const buildQueryParams = (withPage = true): Api.SystemManage.LoginLogSearchParams => {
  const [startTime, endTime] = dateRange.value && dateRange.value.length === 2 ? dateRange.value : [null, null]
  const params: Api.SystemManage.LoginLogSearchParams = {
    ...searchForm,
    startTime,
    endTime
  }

  if (withPage) {
    params.current = pagination.current
    params.size = pagination.size
  }

  return params
}

const loadStats = async () => {
  const res = await fetchGetLoginLogStats(buildQueryParams(false))
  Object.assign(stats, {
    totalCount: res?.totalCount || 0,
    successCount: res?.successCount || 0,
    failCount: res?.failCount || 0,
    todayCount: res?.todayCount || 0,
    todayFailCount: res?.todayFailCount || 0,
    lastLoginTime: res?.lastLoginTime || ''
  })
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await fetchGetLoginLogList(buildQueryParams(true))
    tableData.value = res.records || []
    pagination.total = res.total || 0
    pagination.current = res.current || pagination.current
    pagination.size = res.size || pagination.size
  } finally {
    loading.value = false
  }
}

const loadPageData = async () => {
  await Promise.all([loadStats(), loadList()])
}

const handleSearch = async () => {
  pagination.current = 1
  await loadPageData()
}

const handleReset = async () => {
  Object.assign(searchForm, {
    username: '',
    loginStatus: '',
    ip: '',
    address: '',
    browser: '',
    os: '',
    device: '',
    startTime: null,
    endTime: null
  })
  dateRange.value = []
  pagination.current = 1
  await loadPageData()
}

const handleSelectionChange = (rows: LoginLogItem[]) => {
  selectedRows.value = rows
}

const openDetail = async (row: LoginLogItem) => {
  detailDrawerVisible.value = true
  detailLoading.value = true
  currentDetail.value = row
  try {
    currentDetail.value = await fetchGetLoginLogDetail(row.id)
  } finally {
    detailLoading.value = false
  }
}

const handleDelete = async (row: LoginLogItem) => {
  await ElMessageBox.confirm(`确定删除用户“${row.username || '未知用户'}”在 ${row.loginTime} 的登录记录吗？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
  await fetchDeleteLoginLog(row.id)
  await loadPageData()
}

const handleBatchDelete = async () => {
  if (!selectedRows.value.length) {
    ElMessage.warning('请先选择需要删除的登录记录')
    return
  }

  await ElMessageBox.confirm(`确定批量删除选中的 ${selectedRows.value.length} 条登录记录吗？`, '批量删除确认', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })

  await fetchBatchDeleteLoginLog(selectedRows.value.map((item) => item.id))
  selectedRows.value = []
  await loadPageData()
}

const exportCurrentPageCsv = () => {
  if (!tableData.value.length) return

  const header = [
    'ID',
    '用户名',
    '昵称',
    '登录结果',
    '结果说明',
    '登录时间',
    'IP',
    '归属地',
    '浏览器',
    '浏览器版本',
    '操作系统',
    '设备',
    '平台',
    '时区',
    '语言',
    '屏幕',
    '请求地址',
    'Host',
    'User-Agent'
  ]

  const rows = tableData.value.map((item) => [
    item.id,
    item.username || '',
    item.nickName || '',
    item.loginStatus || '',
    item.loginMessage || '',
    item.loginTime || '',
    item.ip || '',
    item.address || '',
    item.browser || '',
    item.browserVersion || '',
    item.os || '',
    item.device || '',
    item.platform || '',
    item.clientTimezone || '',
    item.clientLanguage || '',
    item.screenSize || '',
    item.requestUri || '',
    item.host || '',
    item.userAgent || ''
  ])

  const csv = [header, ...rows]
    .map((row) => row.map((cell) => `"${String(cell).replace(/"/g, '""')}"`).join(','))
    .join('\n')

  const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `用户登录日志_${new Date().getTime()}.csv`
  link.click()
  URL.revokeObjectURL(url)
}

onMounted(() => {
  loadPageData()
})
</script>

<style scoped lang="scss">
.login-log-page {
  .stat-row,
  .search-card {
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
  max-width: 920px;
  margin-top: 8px;
  font-size: 14px;
  color: var(--art-text-gray-600);
  line-height: 1.8;
}

.stat-card {
  height: 132px;
  padding: 20px;
  margin-bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-card__label {
  font-size: 14px;
  color: var(--art-text-gray-600);
}

.stat-card__value {
  margin-top: 12px;
  font-size: 26px;
  font-weight: 700;
  color: var(--art-text-gray-900);
  line-height: 1.15;
  word-break: break-all;
}

.stat-card__extra {
  margin-top: 10px;
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

.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-cell__content {
  min-width: 0;
}

.user-cell__name,
.main-text {
  font-size: 14px;
  font-weight: 600;
  color: var(--art-text-gray-900);
  line-height: 1.5;
}

.user-cell__sub,
.sub-text {
  margin-top: 2px;
  font-size: 12px;
  color: var(--art-text-gray-500);
  line-height: 1.5;
  word-break: break-all;
}

.tag-line {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 4px;
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
  padding: 18px 20px 20px;
  border-top: 1px solid var(--art-border-color);
  background: #fff;
}

.detail-wrap {
  min-height: 260px;
}

.detail-status-card {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  padding: 18px;
  margin-bottom: 18px;
  border-radius: 14px;
  border: 1px solid var(--art-border-color);
  background: #f8fafc;
}

.detail-status-card.is-success {
  background: rgba(103, 194, 58, 0.08);
}

.detail-status-card.is-fail {
  background: rgba(245, 108, 108, 0.08);
}

.detail-status-card__title {
  font-size: 16px;
  font-weight: 700;
  color: var(--art-text-gray-900);
}

.detail-status-card__desc {
  margin-top: 8px;
  font-size: 13px;
  color: var(--art-text-gray-600);
}

.detail-descriptions {
  :deep(.el-descriptions__label) {
    width: 112px;
    color: var(--art-text-gray-600);
  }
}

.ua-text {
  word-break: break-all;
  line-height: 1.7;
}

:deep(.el-table) {
  --el-table-header-bg-color: #fafafa;
}

:deep(.el-table th.el-table__cell) {
  font-weight: 600;
  color: var(--art-text-gray-700);
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
