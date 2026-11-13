<template>
  <div class="tvplay-page">
    <div class="art-card page-header">
      <div>
        <div class="page-title">电视剧播放量数据管理</div>
        <div class="page-desc">
          支持电视剧名称、类型、播放量、点赞、差评、评分和采集时间等数据的分页展示、条件查询、新增、编辑和删除。
        </div>
      </div>

      <ElButton v-if="canAdd" type="primary" @click="openDialog('add')">新增电视剧数据</ElButton>
    </div>

    <ElRow :gutter="20" class="mb-20">
      <ElCol v-for="item in statCards" :key="item.label" :xs="24" :sm="12" :lg="6">
        <div class="art-card stat-card">
          <div class="stat-label">{{ item.label }}</div>
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-extra">{{ item.extra }}</div>
        </div>
      </ElCol>
    </ElRow>

    <div class="art-card search-card mb-20">
      <ElForm :model="searchForm" inline label-width="90px" class="search-form">
        <ElFormItem label="剧名">
          <ElInput v-model="searchForm.dramaName" placeholder="请输入剧名" clearable style="width: 180px" />
        </ElFormItem>

        <ElFormItem label="类型">
          <ElSelect v-model="searchForm.dramaType" placeholder="请选择类型" clearable filterable style="width: 180px">
            <ElOption v-for="item in options.dramaTypes" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="播放量">
          <div class="range-input range-input--wide">
            <ElInputNumber v-model="searchForm.playCountMin" :min="0" :controls="false" placeholder="最低" />
            <span>-</span>
            <ElInputNumber v-model="searchForm.playCountMax" :min="0" :controls="false" placeholder="最高" />
          </div>
        </ElFormItem>

        <ElFormItem label="评分">
          <div class="range-input">
            <ElInputNumber v-model="searchForm.scoreMin" :min="0" :max="10" :precision="1" :controls="false" placeholder="最低" />
            <span>-</span>
            <ElInputNumber v-model="searchForm.scoreMax" :min="0" :max="10" :precision="1" :controls="false" placeholder="最高" />
          </div>
        </ElFormItem>

        <ElFormItem label="采集时间">
          <ElDatePicker v-model="collectRange" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" style="width: 340px" @change="handleCollectRangeChange" />
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
        <ElSpace wrap>
          <ElButton v-if="canAdd" type="primary" plain @click="openDialog('add')">新增</ElButton>
          <ElButton v-if="canEdit" :disabled="selectedRows.length !== 1" @click="handleBatchEdit">编辑</ElButton>
          <ElButton type="success" plain @click="handleOpenAnalysis">可视化分析</ElButton>
        </ElSpace>

        <div class="toolbar-tip">当前共 {{ pagination.total }} 条电视剧播放量数据</div>
      </div>

      <ElTable v-loading="loading" :data="tableData" height="560" style="width: 100%" @selection-change="handleSelectionChange">
        <ElTableColumn v-if="canWrite" type="selection" width="55" fixed="left" />
        <ElTableColumn prop="id" label="ID" width="80" fixed="left" />

        <ElTableColumn label="电视剧信息" min-width="220" fixed="left">
          <template #default="{ row }">
            <div class="drama-cell">
              <div class="drama-main">{{ row.dramaName || '-' }}</div>
              <div class="drama-sub">{{ row.dramaType || '暂无类型' }}</div>
            </div>
          </template>
        </ElTableColumn>

        <ElTableColumn label="评分" width="100" align="center">
          <template #default="{ row }">
            <ElTag :type="getScoreTagType(row.score)" effect="light" round>
              {{ formatNumber(row.score, 1) }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn label="播放量" width="150" align="right">
          <template #default="{ row }">
            <span class="play-text">{{ formatCount(row.playCount) }}</span>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="playCountText" label="原始播放量" width="120" align="center" />

        <ElTableColumn label="点赞" width="130" align="right">
          <template #default="{ row }">
            {{ formatCount(row.likeCount) }}
          </template>
        </ElTableColumn>

        <ElTableColumn label="差评" width="130" align="right">
          <template #default="{ row }">
            {{ formatCount(row.negativeCount) }}
          </template>
        </ElTableColumn>

        <ElTableColumn label="好评倾向" width="120" align="center">
          <template #default="{ row }">
            <ElProgress :percentage="getLikePercent(row)" :stroke-width="8" :show-text="false" />
            <span class="percent-text">{{ getLikePercent(row) }}%</span>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="collectTime" label="采集时间" min-width="180" />
        <ElTableColumn prop="createTime" label="导入时间" min-width="180" />

        <ElTableColumn v-if="canWrite" label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <ElSpace>
              <ElButton v-if="canEdit" link type="primary" @click="openDialog('edit', row)">编辑</ElButton>
              <ElButton v-if="canDelete" link type="danger" @click="handleDelete(row)">删除</ElButton>
            </ElSpace>
          </template>
        </ElTableColumn>
      </ElTable>

      <div class="table-pagination">
        <ElPagination v-model:current-page="pagination.current" v-model:page-size="pagination.size" :page-sizes="[10, 20, 50, 100]" background layout="total, sizes, prev, pager, next, jumper" :total="pagination.total" @size-change="loadTableData" @current-change="loadTableData" />
      </div>
    </div>

    <ElDialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增电视剧播放量数据' : '编辑电视剧播放量数据'" width="780px" destroy-on-close>
      <ElForm ref="dialogFormRef" :model="dialogForm" label-width="110px">
        <ElDivider content-position="left">基础信息</ElDivider>

        <ElRow :gutter="16">
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="剧名">
              <ElInput v-model="dialogForm.dramaName" placeholder="请输入剧名" clearable />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12">
            <ElFormItem label="电视剧类型">
              <ElSelect v-model="dialogForm.dramaType" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.dramaTypes" :key="item" :label="item" :value="item" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12">
            <ElFormItem label="评分">
              <ElInputNumber v-model="dialogForm.score" :min="0" :max="10" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12">
            <ElFormItem label="采集时间">
              <ElDatePicker v-model="dialogForm.collectTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElDivider content-position="left">播放与互动数据</ElDivider>

        <ElRow :gutter="16">
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="原始播放量">
              <ElInput v-model="dialogForm.playCountText" placeholder="例如 3.07亿、3454万" clearable />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12">
            <ElFormItem label="播放量">
              <ElInputNumber v-model="dialogForm.playCount" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12">
            <ElFormItem label="点赞数">
              <ElInputNumber v-model="dialogForm.likeCount" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12">
            <ElFormItem label="差评数">
              <ElInputNumber v-model="dialogForm.negativeCount" :min="0" :controls="false" style="width: 100%" />
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
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import {
  fetchCreateTvPlay,
  fetchDeleteTvPlay,
  fetchTvPlayOptions,
  fetchTvPlayPage,
  fetchUpdateTvPlay
} from '@/api/tvplay'
import type { TvDramaPlayDataItem, TvDramaPlayOptions } from '@/api/tvplay'

import { usePagePermission } from '@/hooks/core/usePagePermission'

defineOptions({ name: 'TvDramaPlayManage' })

const router = useRouter()
const { canWrite, canAdd, canEdit, canDelete, checkWritePermission } = usePagePermission()

type DialogType = 'add' | 'edit'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref<DialogType>('add')
const dialogFormRef = ref<FormInstance>()

const tableData = ref<TvDramaPlayDataItem[]>([])
const selectedRows = ref<TvDramaPlayDataItem[]>([])
const collectRange = ref<string[]>([])

const options = reactive<TvDramaPlayOptions>({
  dramaTypes: []
})

const searchForm = reactive({
  dramaName: '',
  dramaType: '',

  playCountMin: undefined as number | undefined,
  playCountMax: undefined as number | undefined,

  likeCountMin: undefined as number | undefined,
  likeCountMax: undefined as number | undefined,

  negativeCountMin: undefined as number | undefined,
  negativeCountMax: undefined as number | undefined,

  scoreMin: undefined as number | undefined,
  scoreMax: undefined as number | undefined,

  collectStartTime: '',
  collectEndTime: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const getDefaultForm = (): TvDramaPlayDataItem => ({
  dramaName: '',
  dramaType: '',
  playCountText: '',
  playCount: undefined,
  likeCount: undefined,
  negativeCount: undefined,
  score: undefined,
  collectTime: ''
})

const dialogForm = reactive<TvDramaPlayDataItem>(getDefaultForm())

const statCards = computed(() => {
  const currentPageTotal = tableData.value.length

  const avgScore = currentPageTotal
    ? tableData.value.reduce((sum, item) => sum + Number(item.score || 0), 0) / currentPageTotal
    : 0

  const totalPlay = tableData.value.reduce((sum, item) => sum + Number(item.playCount || 0), 0)

  const highScoreCount = tableData.value.filter((item) => Number(item.score || 0) >= 8).length

  return [
    {
      label: '数据总量',
      value: pagination.total,
      extra: '电视剧播放量记录总数'
    },
    {
      label: '当前页播放量',
      value: formatCount(totalPlay),
      extra: '当前分页播放量合计'
    },
    {
      label: '当前页平均评分',
      value: formatNumber(avgScore, 1),
      extra: '基于当前分页数据计算'
    },
    {
      label: '当前页高分剧',
      value: highScoreCount,
      extra: '评分大于等于 8 分'
    }
  ]
})

const loadOptions = async () => {
  const res = await fetchTvPlayOptions()
  options.dramaTypes = res.dramaTypes || []
}

const loadTableData = async () => {
  loading.value = true

  try {
    const res = await fetchTvPlayPage({
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

const handleSearch = () => {
  pagination.current = 1
  loadTableData()
}

const handleReset = () => {
  searchForm.dramaName = ''
  searchForm.dramaType = ''

  searchForm.playCountMin = undefined
  searchForm.playCountMax = undefined

  searchForm.likeCountMin = undefined
  searchForm.likeCountMax = undefined

  searchForm.negativeCountMin = undefined
  searchForm.negativeCountMax = undefined

  searchForm.scoreMin = undefined
  searchForm.scoreMax = undefined

  searchForm.collectStartTime = ''
  searchForm.collectEndTime = ''
  collectRange.value = []

  pagination.current = 1
  loadTableData()
}

const handleCollectRangeChange = (value: string[] | null) => {
  searchForm.collectStartTime = value?.[0] || ''
  searchForm.collectEndTime = value?.[1] || ''
}

const handleSelectionChange = (rows: TvDramaPlayDataItem[]) => {
  selectedRows.value = rows
}

const resetDialogForm = () => {
  Object.assign(dialogForm, getDefaultForm())
}

const openDialog = (type: DialogType, row?: TvDramaPlayDataItem) => {
  if ((type === 'add' || type === 'edit') && !checkWritePermission()) {
    return
  }
  dialogType.value = type
  resetDialogForm()

  if (type === 'edit' && row) {
    Object.assign(dialogForm, { ...row })
  }

  dialogVisible.value = true
}

const handleBatchEdit = () => {
  if (!checkWritePermission()) {
    return
  }
  if (selectedRows.value.length === 1) {
    openDialog('edit', selectedRows.value[0])
  }
}

const handleOpenAnalysis = () => {
  router.push('/source/tvplay-analysis')
}

const handleDialogSubmit = async () => {
  if (!checkWritePermission()) {
    return
  }

  submitLoading.value = true

  try {
    const payload = { ...dialogForm }

    if (dialogType.value === 'add') {
      await fetchCreateTvPlay(payload)
    } else {
      await fetchUpdateTvPlay(Number(dialogForm.id), payload)
    }

    dialogVisible.value = false
    await loadTableData()
    await loadOptions()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: TvDramaPlayDataItem) => {
  if (!checkWritePermission()) {
    return
  }

  await ElMessageBox.confirm(
    `确定删除电视剧「${row.dramaName || row.id}」的播放量数据吗？`,
    '删除确认',
    {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }
  )

  await fetchDeleteTvPlay(Number(row.id))
  await loadTableData()
}

const formatNumber = (value?: number | string, precision = 2) => {
  if (value === null || value === undefined || value === '') return '-'

  const num = Number(value)
  if (!Number.isFinite(num)) return '-'

  return num.toFixed(precision)
}

const formatCount = (value?: number | string) => {
  const num = Number(value || 0)

  if (!Number.isFinite(num)) return '-'

  if (num >= 100000000) {
    return `${(num / 100000000).toFixed(2)}亿`
  }

  if (num >= 10000) {
    return `${(num / 10000).toFixed(2)}万`
  }

  return num.toLocaleString('zh-CN')
}

const getScoreTagType = (value?: number | string) => {
  const score = Number(value)

  if (!Number.isFinite(score)) return 'info'
  if (score >= 8.5) return 'success'
  if (score >= 7) return 'primary'
  if (score >= 6) return 'warning'
  return 'danger'
}

const getLikePercent = (row: TvDramaPlayDataItem) => {
  const like = Number(row.likeCount || 0)
  const negative = Number(row.negativeCount || 0)
  const total = like + negative

  if (!total) return 0

  return Math.round((like / total) * 100)
}

onMounted(async () => {
  await loadOptions()
  await loadTableData()
})
</script>

<style scoped lang="scss">
.tvplay-page {
  .mb-20 {
    margin-bottom: 20px;
  }
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 22px 24px;
  margin-bottom: 20px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--art-text-gray-900, #111827);
  line-height: 1.2;
}

.page-desc {
  margin-top: 8px;
  font-size: 14px;
  color: var(--art-text-gray-600, #667085);
  line-height: 1.8;
}

.stat-card {
  height: 126px;
  padding: 20px;
  margin-bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-label {
  font-size: 14px;
  color: var(--art-text-gray-600, #667085);
}

.stat-value {
  margin-top: 12px;
  font-size: 28px;
  font-weight: 700;
  color: var(--art-text-gray-900, #111827);
  line-height: 1;
}

.stat-extra {
  margin-top: 12px;
  font-size: 13px;
  color: var(--art-text-gray-500, #98a2b3);
}

.search-card {
  padding: 18px 20px 2px;
}

.search-form {
  :deep(.el-form-item) {
    margin-bottom: 16px;
  }
}

.range-input {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 210px;

  :deep(.el-input-number) {
    width: 96px;
  }
}

.range-input--wide {
  width: 250px;

  :deep(.el-input-number) {
    width: 116px;
  }
}

.table-card {
  padding: 0;
  overflow: hidden;
}

.table-toolbar {
  padding: 18px 20px;
  border-bottom: 1px solid var(--art-border-color, #eaecf0);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.toolbar-tip {
  font-size: 13px;
  color: var(--art-text-gray-500, #98a2b3);
}

.drama-cell {
  min-width: 0;
}

.drama-main {
  font-size: 14px;
  font-weight: 600;
  color: var(--art-text-gray-900, #111827);
  line-height: 1.5;
}

.drama-sub {
  margin-top: 2px;
  font-size: 12px;
  color: var(--art-text-gray-500, #98a2b3);
  line-height: 1.5;
}

.play-text {
  font-weight: 600;
  color: #2563eb;
}

.percent-text {
  display: inline-block;
  margin-top: 4px;
  font-size: 12px;
  color: var(--art-text-gray-600, #667085);
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
  padding: 18px 20px 20px;
  border-top: 1px solid var(--art-border-color, #eaecf0);
  background: #fff;
}

:deep(.el-table) {
  --el-table-header-bg-color: #fafafa;
}

:deep(.el-table th.el-table__cell) {
  font-weight: 600;
  color: var(--art-text-gray-700, #344054);
}

:deep(.el-dialog__body) {
  padding-top: 10px;
  padding-bottom: 10px;
}

:deep(.el-divider__text) {
  font-weight: 600;
  color: var(--art-text-gray-700, #344054);
}

@media (max-width: 992px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .table-pagination {
    justify-content: flex-start;
    overflow-x: auto;
  }
}
</style>