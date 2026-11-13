<template>
  <div class="world-page">
    <div class="art-card page-header">
      <div>
        <div class="page-title">世界幸福报告数据管理</div>
        <div class="page-desc">
          支持国家、年份、幸福指数、年度排名及各类解释性指标的分页展示、条件查询、新增、编辑和删除。
        </div>
      </div>

      <ElSpace wrap>
        <ElButton type="success" plain @click="handleOpenAnalysis">可视化分析</ElButton>
        <ElButton v-if="canAdd" type="primary" @click="openDialog('add')">新增数据</ElButton>
      </ElSpace>
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
        <ElFormItem label="年份">
          <ElSelect v-model="searchForm.year" placeholder="请选择年份" clearable filterable style="width: 130px">
            <ElOption v-for="item in options.years" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="国家/地区">
          <ElSelect v-model="searchForm.country" placeholder="请选择或输入" clearable filterable style="width: 180px">
            <ElOption v-for="item in options.countries" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="幸福分数">
          <div class="range-input">
            <ElInputNumber v-model="searchForm.scoreMin" :min="0" :max="10" :precision="2" :controls="false" placeholder="最低" />
            <span>-</span>
            <ElInputNumber v-model="searchForm.scoreMax" :min="0" :max="10" :precision="2" :controls="false" placeholder="最高" />
          </div>
        </ElFormItem>

        <ElFormItem label="排名">
          <div class="range-input">
            <ElInputNumber v-model="searchForm.rankMin" :min="1" :controls="false" placeholder="最小" />
            <span>-</span>
            <ElInputNumber v-model="searchForm.rankMax" :min="1" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem label="完整数据">
          <ElSwitch v-model="searchForm.completeOnly" active-text="仅完整" inactive-text="全部" />
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

        <div class="toolbar-tip">当前共 {{ pagination.total }} 条世界幸福报告数据</div>
      </div>

      <ElTable v-loading="loading" :data="tableData" height="560" style="width: 100%" @selection-change="handleSelectionChange">
        <ElTableColumn v-if="canWrite" type="selection" width="55" fixed="left" />
        <ElTableColumn prop="id" label="ID" width="80" fixed="left" />

        <ElTableColumn label="国家信息" min-width="190" fixed="left">
          <template #default="{ row }">
            <div class="country-cell">
              <div class="country-main">{{ row.country || '-' }}</div>
              <div class="country-sub">
                {{ row.year || '-' }} 年 / 排名 {{ row.rankInYear || '-' }}
              </div>
            </div>
          </template>
        </ElTableColumn>

        <ElTableColumn label="完整性" width="100" align="center">
          <template #default="{ row }">
            <ElTag :type="row.completeData ? 'success' : 'warning'" effect="light" round>
              {{ row.completeData ? '完整' : '缺失' }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn label="幸福分数" width="110" align="center">
          <template #default="{ row }">
            <span class="score-text">{{ formatNumber(row.happinessScore, 3) }}</span>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="lowerWhisker" label="下限" width="100" align="center">
          <template #default="{ row }">{{ formatNumber(row.lowerWhisker, 3) }}</template>
        </ElTableColumn>

        <ElTableColumn prop="upperWhisker" label="上限" width="100" align="center">
          <template #default="{ row }">{{ formatNumber(row.upperWhisker, 3) }}</template>
        </ElTableColumn>

        <ElTableColumn label="人均GDP解释值" width="140" align="center">
          <template #default="{ row }">{{ formatNumber(row.explainedLogGdpPerCapita, 3) }}</template>
        </ElTableColumn>

        <ElTableColumn label="社会支持" width="120" align="center">
          <template #default="{ row }">{{ formatNumber(row.explainedSocialSupport, 3) }}</template>
        </ElTableColumn>

        <ElTableColumn label="健康寿命" width="120" align="center">
          <template #default="{ row }">{{ formatNumber(row.explainedHealthyLifeExpectancy, 3) }}</template>
        </ElTableColumn>

        <ElTableColumn label="自由度" width="110" align="center">
          <template #default="{ row }">{{ formatNumber(row.explainedFreedom, 3) }}</template>
        </ElTableColumn>

        <ElTableColumn label="慷慨程度" width="120" align="center">
          <template #default="{ row }">{{ formatNumber(row.explainedGenerosity, 3) }}</template>
        </ElTableColumn>

        <ElTableColumn label="腐败感知" width="120" align="center">
          <template #default="{ row }">{{ formatNumber(row.explainedCorruption, 3) }}</template>
        </ElTableColumn>

        <ElTableColumn label="残差值" width="120" align="center">
          <template #default="{ row }">{{ formatNumber(row.dystopiaPlusResidual, 3) }}</template>
        </ElTableColumn>

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

    <ElDialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增世界幸福报告数据' : '编辑世界幸福报告数据'" width="960px" destroy-on-close>
      <ElForm ref="dialogFormRef" :model="dialogForm" label-width="150px">
        <ElDivider content-position="left">基础信息</ElDivider>

        <ElRow :gutter="16">
          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="年份">
              <ElInputNumber v-model="dialogForm.year" :min="2000" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="年度排名">
              <ElInputNumber v-model="dialogForm.rankInYear" :min="1" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="国家/地区">
              <ElSelect v-model="dialogForm.country" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.countries" :key="item" :label="item" :value="item" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="幸福分数">
              <ElInputNumber v-model="dialogForm.happinessScore" :min="0" :max="10" :precision="4" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="置信区间下限">
              <ElInputNumber v-model="dialogForm.lowerWhisker" :precision="4" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="置信区间上限">
              <ElInputNumber v-model="dialogForm.upperWhisker" :precision="4" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElDivider content-position="left">解释性指标</ElDivider>

        <ElRow :gutter="16">
          <ElCol v-for="field in factorFields" :key="field.prop" :xs="24" :sm="12" :lg="8">
            <ElFormItem :label="field.label">
              <ElInputNumber v-model="dialogForm[field.prop]" :precision="4" :controls="false" style="width: 100%" />
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
  fetchCreateWorldHappiness,
  fetchDeleteWorldHappiness,
  fetchUpdateWorldHappiness,
  fetchWorldHappinessOptions,
  fetchWorldHappinessPage
} from '@/api/world-happiness'
import type { WorldHappinessReportItem, WorldHappinessReportOptions } from '@/api/world-happiness'

import { usePagePermission } from '@/hooks/core/usePagePermission'

defineOptions({ name: 'WorldHappinessReportManage' })

type DialogType = 'add' | 'edit'

type FactorField = {
  label: string
  prop: keyof WorldHappinessReportItem
}

const router = useRouter()
const { canWrite, canAdd, canEdit, canDelete, checkWritePermission } = usePagePermission()

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref<DialogType>('add')
const dialogFormRef = ref<FormInstance>()

const tableData = ref<WorldHappinessReportItem[]>([])
const selectedRows = ref<WorldHappinessReportItem[]>([])

const options = reactive<WorldHappinessReportOptions>({
  years: [],
  countries: []
})

const searchForm = reactive({
  year: undefined as number | undefined,
  country: '',
  rankMin: undefined as number | undefined,
  rankMax: undefined as number | undefined,
  scoreMin: undefined as number | undefined,
  scoreMax: undefined as number | undefined,
  completeOnly: false
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const factorFields: FactorField[] = [
  { label: '人均GDP解释值', prop: 'explainedLogGdpPerCapita' },
  { label: '社会支持解释值', prop: 'explainedSocialSupport' },
  { label: '健康寿命解释值', prop: 'explainedHealthyLifeExpectancy' },
  { label: '自由度解释值', prop: 'explainedFreedom' },
  { label: '慷慨程度解释值', prop: 'explainedGenerosity' },
  { label: '腐败感知解释值', prop: 'explainedCorruption' },
  { label: '反乌托邦残差值', prop: 'dystopiaPlusResidual' }
]

const getDefaultForm = (): WorldHappinessReportItem => ({
  year: undefined,
  rankInYear: undefined,
  country: '',
  happinessScore: undefined,
  lowerWhisker: undefined,
  upperWhisker: undefined,
  explainedLogGdpPerCapita: undefined,
  explainedSocialSupport: undefined,
  explainedHealthyLifeExpectancy: undefined,
  explainedFreedom: undefined,
  explainedGenerosity: undefined,
  explainedCorruption: undefined,
  dystopiaPlusResidual: undefined
})

const dialogForm = reactive<WorldHappinessReportItem>(getDefaultForm())

const statCards = computed(() => {
  const currentPageTotal = tableData.value.length

  const avgScore = currentPageTotal
    ? tableData.value.reduce((sum, item) => sum + Number(item.happinessScore || 0), 0) /
      currentPageTotal
    : 0

  const completeCount = tableData.value.filter((item) => item.completeData).length

  const top10Count = tableData.value.filter((item) => Number(item.rankInYear || 9999) <= 10).length

  return [
    {
      label: '数据总量',
      value: pagination.total,
      extra: '世界幸福报告记录总数'
    },
    {
      label: '当前页平均分',
      value: formatNumber(avgScore, 2),
      extra: '基于当前分页数据计算'
    },
    {
      label: '当前页完整数据',
      value: completeCount,
      extra: '核心可视化字段完整'
    },
    {
      label: '当前页TOP10',
      value: top10Count,
      extra: '年度排名前10的记录'
    }
  ]
})

const loadOptions = async () => {
  const res = await fetchWorldHappinessOptions()

  options.years = res.years || []
  options.countries = res.countries || []
}

const loadTableData = async () => {
  loading.value = true

  try {
    const res = await fetchWorldHappinessPage({
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
  searchForm.year = undefined
  searchForm.country = ''
  searchForm.rankMin = undefined
  searchForm.rankMax = undefined
  searchForm.scoreMin = undefined
  searchForm.scoreMax = undefined
  searchForm.completeOnly = false

  pagination.current = 1
  loadTableData()
}

const handleSelectionChange = (rows: WorldHappinessReportItem[]) => {
  selectedRows.value = rows
}

const handleOpenAnalysis = () => {
  router.push('/source/world-analysis')
}

const resetDialogForm = () => {
  Object.assign(dialogForm, getDefaultForm())
}

const openDialog = (type: DialogType, row?: WorldHappinessReportItem) => {
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

const handleDialogSubmit = async () => {
  if (!checkWritePermission()) {
    return
  }

  submitLoading.value = true

  try {
    const payload = { ...dialogForm }

    if (dialogType.value === 'add') {
      await fetchCreateWorldHappiness(payload)
    } else {
      await fetchUpdateWorldHappiness(Number(dialogForm.id), payload)
    }

    dialogVisible.value = false
    await loadTableData()
    await loadOptions()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: WorldHappinessReportItem) => {
  if (!checkWritePermission()) {
    return
  }

  await ElMessageBox.confirm(
    `确定删除「${row.country || row.id} / ${row.year || '-'}」这条世界幸福报告数据吗？`,
    '删除确认',
    {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }
  )

  await fetchDeleteWorldHappiness(Number(row.id))
  await loadTableData()
}

const formatNumber = (value?: number | string, precision = 2) => {
  if (value === null || value === undefined || value === '') return '-'

  const num = Number(value)
  if (!Number.isFinite(num)) return '-'

  return num.toFixed(precision)
}

onMounted(async () => {
  await loadOptions()
  await loadTableData()
})
</script>

<style scoped lang="scss">
.world-page {
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

.country-cell {
  min-width: 0;
}

.country-main {
  font-size: 14px;
  font-weight: 600;
  color: var(--art-text-gray-900, #111827);
  line-height: 1.5;
}

.country-sub {
  margin-top: 2px;
  font-size: 12px;
  color: var(--art-text-gray-500, #98a2b3);
  line-height: 1.5;
}

.score-text {
  font-weight: 600;
  color: #2563eb;
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