<template>
  <div class="teen-page">
    <div class="art-card page-header">
      <div>
        <div class="page-title">青少年心理健康数据管理</div>
        <div class="page-desc">
          支持社交媒体使用时长、平台偏好、睡眠、学习表现、压力、焦虑、成瘾水平与抑郁风险标签的数据查询和维护。
        </div>
      </div>

      <ElButton v-if="canAdd" type="primary" @click="openDialog('add')">新增数据</ElButton>
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
      <ElForm :model="searchForm" inline label-width="96px" class="search-form">
        <ElFormItem label="性别">
          <ElSelect v-model="searchForm.gender" placeholder="请选择" clearable style="width: 120px">
            <ElOption v-for="item in options.genders" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="使用平台">
          <ElSelect v-model="searchForm.platformUsage" placeholder="请选择平台" clearable filterable style="width: 150px">
            <ElOption v-for="item in options.platformUsages" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="社交水平">
          <ElSelect v-model="searchForm.socialInteractionLevel" placeholder="请选择" clearable filterable style="width: 150px">
            <ElOption v-for="item in options.socialInteractionLevels" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="抑郁风险">
          <ElSelect v-model="searchForm.depressionLabel" placeholder="请选择" clearable style="width: 150px">
            <ElOption v-for="item in options.depressionLabels" :key="item" :label="formatDepressionLabel(item)" :value="item" />
            <ElOption label="暂无明显风险" :value="0" />
            <ElOption label="存在风险" :value="1" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="年龄">
          <div class="range-input">
            <ElInputNumber v-model="searchForm.ageMin" :min="0" :controls="false" placeholder="最小" />
            <span>-</span>
            <ElInputNumber v-model="searchForm.ageMax" :min="0" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem label="社媒时长">
          <div class="range-input">
            <ElInputNumber v-model="searchForm.dailySocialMediaHoursMin" :min="0" :controls="false" placeholder="最小" />
            <span>-</span>
            <ElInputNumber v-model="searchForm.dailySocialMediaHoursMax" :min="0" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem label="睡眠时长">
          <div class="range-input">
            <ElInputNumber v-model="searchForm.sleepHoursMin" :min="0" :controls="false" placeholder="最小" />
            <span>-</span>
            <ElInputNumber v-model="searchForm.sleepHoursMax" :min="0" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem label="压力等级">
          <ElInputNumber v-model="searchForm.stressLevel" :min="0" :controls="false" placeholder="压力" style="width: 100px" />
        </ElFormItem>

        <ElFormItem label="焦虑等级">
          <ElInputNumber v-model="searchForm.anxietyLevel" :min="0" :controls="false" placeholder="焦虑" style="width: 100px" />
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

        <div class="toolbar-tip">当前共 {{ pagination.total }} 条青少年心理健康数据</div>
      </div>

      <ElTable v-loading="loading" :data="tableData" height="560" style="width: 100%" @selection-change="handleSelectionChange">
        <ElTableColumn v-if="canWrite" type="selection" width="55" fixed="left" />
        <ElTableColumn prop="id" label="ID" width="80" fixed="left" />

        <ElTableColumn label="青少年信息" min-width="180" fixed="left">
          <template #default="{ row }">
            <div class="teen-cell">
              <div class="teen-main">{{ row.gender || '-' }} / {{ row.age || '-' }}岁</div>
              <div class="teen-sub">{{ row.platformUsage || '未知平台' }} · {{ row.dailySocialMediaHours || 0 }}h/天</div>
            </div>
          </template>
        </ElTableColumn>

        <ElTableColumn label="抑郁风险" width="130" align="center">
          <template #default="{ row }">
            <ElTag :type="row.depressionLabel === 1 ? 'danger' : 'success'" effect="light" round>
              {{ formatDepressionLabel(row.depressionLabel) }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="dailySocialMediaHours" label="社媒时长/h" width="120" align="center" />
        <ElTableColumn prop="platformUsage" label="主要平台" width="130" />
        <ElTableColumn prop="sleepHours" label="睡眠/h" width="100" align="center" />
        <ElTableColumn prop="screenTimeBeforeSleep" label="睡前屏幕/h" width="130" align="center" />
        <ElTableColumn prop="academicPerformance" label="学业表现" width="110" align="center" />
        <ElTableColumn prop="physicalActivity" label="运动时长" width="110" align="center" />
        <ElTableColumn prop="socialInteractionLevel" label="社交水平" width="120" />

        <ElTableColumn label="压力" width="100" align="center">
          <template #default="{ row }">
            <ElTag :type="getLevelTagType(row.stressLevel)" effect="light" round>
              {{ row.stressLevel ?? '-' }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn label="焦虑" width="100" align="center">
          <template #default="{ row }">
            <ElTag :type="getLevelTagType(row.anxietyLevel)" effect="light" round>
              {{ row.anxietyLevel ?? '-' }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn label="成瘾" width="100" align="center">
          <template #default="{ row }">
            <ElTag :type="getLevelTagType(row.addictionLevel)" effect="light" round>
              {{ row.addictionLevel ?? '-' }}
            </ElTag>
          </template>
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

    <ElDialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增青少年心理健康数据' : '编辑青少年心理健康数据'" width="860px" destroy-on-close>
      <ElForm ref="dialogFormRef" :model="dialogForm" label-width="130px">
        <ElDivider content-position="left">基础信息</ElDivider>

        <ElRow :gutter="16">
          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="年龄">
              <ElInputNumber v-model="dialogForm.age" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="性别">
              <ElSelect v-model="dialogForm.gender" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.genders" :key="item" :label="item" :value="item" />
                <ElOption label="Male" value="Male" />
                <ElOption label="Female" value="Female" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="主要平台">
              <ElSelect v-model="dialogForm.platformUsage" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.platformUsages" :key="item" :label="item" :value="item" />
              </ElSelect>
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElDivider content-position="left">行为与生活习惯</ElDivider>

        <ElRow :gutter="16">
          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="每日社媒时长">
              <ElInputNumber v-model="dialogForm.dailySocialMediaHours" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="睡眠时长">
              <ElInputNumber v-model="dialogForm.sleepHours" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="睡前屏幕时长">
              <ElInputNumber v-model="dialogForm.screenTimeBeforeSleep" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="学业表现">
              <ElInputNumber v-model="dialogForm.academicPerformance" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="体育活动">
              <ElInputNumber v-model="dialogForm.physicalActivity" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="社交互动水平">
              <ElSelect v-model="dialogForm.socialInteractionLevel" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.socialInteractionLevels" :key="item" :label="item" :value="item" />
              </ElSelect>
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElDivider content-position="left">心理状态指标</ElDivider>

        <ElRow :gutter="16">
          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="压力水平">
              <ElInputNumber v-model="dialogForm.stressLevel" :min="0" :max="10" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="焦虑水平">
              <ElInputNumber v-model="dialogForm.anxietyLevel" :min="0" :max="10" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="成瘾水平">
              <ElInputNumber v-model="dialogForm.addictionLevel" :min="0" :max="10" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="抑郁风险标签">
              <ElSelect v-model="dialogForm.depressionLabel" clearable style="width: 100%">
                <ElOption label="暂无明显风险" :value="0" />
                <ElOption label="存在风险" :value="1" />
              </ElSelect>
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
  fetchCreateTeen,
  fetchDeleteTeen,
  fetchTeenOptions,
  fetchTeenPage,
  fetchUpdateTeen
} from '@/api/teen'
import type { TeenMentalHealthItem, TeenMentalHealthOptions } from '@/api/teen'

import { usePagePermission } from '@/hooks/core/usePagePermission'

defineOptions({ name: 'TeenMentalHealthManage' })

type DialogType = 'add' | 'edit'

const router = useRouter()
const { canWrite, canAdd, canEdit, canDelete, checkWritePermission } = usePagePermission()

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref<DialogType>('add')
const dialogFormRef = ref<FormInstance>()

const tableData = ref<TeenMentalHealthItem[]>([])
const selectedRows = ref<TeenMentalHealthItem[]>([])

const options = reactive<TeenMentalHealthOptions>({
  genders: [],
  platformUsages: [],
  socialInteractionLevels: [],
  depressionLabels: []
})

const searchForm = reactive({
  gender: '',
  platformUsage: '',
  socialInteractionLevel: '',
  depressionLabel: undefined as number | undefined,

  ageMin: undefined as number | undefined,
  ageMax: undefined as number | undefined,

  dailySocialMediaHoursMin: undefined as number | undefined,
  dailySocialMediaHoursMax: undefined as number | undefined,

  sleepHoursMin: undefined as number | undefined,
  sleepHoursMax: undefined as number | undefined,

  academicPerformanceMin: undefined as number | undefined,
  academicPerformanceMax: undefined as number | undefined,

  stressLevel: undefined as number | undefined,
  anxietyLevel: undefined as number | undefined,
  addictionLevel: undefined as number | undefined
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const getDefaultForm = (): TeenMentalHealthItem => ({
  age: undefined,
  gender: '',
  dailySocialMediaHours: undefined,
  platformUsage: '',
  sleepHours: undefined,
  screenTimeBeforeSleep: undefined,
  academicPerformance: undefined,
  physicalActivity: undefined,
  socialInteractionLevel: '',
  stressLevel: undefined,
  anxietyLevel: undefined,
  addictionLevel: undefined,
  depressionLabel: undefined
})

const dialogForm = reactive<TeenMentalHealthItem>(getDefaultForm())

const statCards = computed(() => {
  const currentPageTotal = tableData.value.length

  const avgSocialHours = currentPageTotal
    ? tableData.value.reduce((sum, item) => sum + Number(item.dailySocialMediaHours || 0), 0) /
      currentPageTotal
    : 0

  const avgSleepHours = currentPageTotal
    ? tableData.value.reduce((sum, item) => sum + Number(item.sleepHours || 0), 0) /
      currentPageTotal
    : 0

  const riskCount = tableData.value.filter((item) => item.depressionLabel === 1).length

  return [
    {
      label: '数据总量',
      value: pagination.total,
      extra: '青少年心理健康记录总数'
    },
    {
      label: '当前页平均社媒时长',
      value: `${formatNumber(avgSocialHours, 1)}h`,
      extra: '每日社交媒体使用均值'
    },
    {
      label: '当前页平均睡眠',
      value: `${formatNumber(avgSleepHours, 1)}h`,
      extra: '睡眠时长均值'
    },
    {
      label: '当前页风险标签',
      value: riskCount,
      extra: '抑郁风险标签为 1 的记录'
    }
  ]
})

const loadOptions = async () => {
  const res = await fetchTeenOptions()

  options.genders = res.genders || []
  options.platformUsages = res.platformUsages || []
  options.socialInteractionLevels = res.socialInteractionLevels || []
  options.depressionLabels = res.depressionLabels || []
}

const loadTableData = async () => {
  loading.value = true

  try {
    const res = await fetchTeenPage({
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
  searchForm.gender = ''
  searchForm.platformUsage = ''
  searchForm.socialInteractionLevel = ''
  searchForm.depressionLabel = undefined

  searchForm.ageMin = undefined
  searchForm.ageMax = undefined

  searchForm.dailySocialMediaHoursMin = undefined
  searchForm.dailySocialMediaHoursMax = undefined

  searchForm.sleepHoursMin = undefined
  searchForm.sleepHoursMax = undefined

  searchForm.academicPerformanceMin = undefined
  searchForm.academicPerformanceMax = undefined

  searchForm.stressLevel = undefined
  searchForm.anxietyLevel = undefined
  searchForm.addictionLevel = undefined

  pagination.current = 1
  loadTableData()
}

const handleSelectionChange = (rows: TeenMentalHealthItem[]) => {
  selectedRows.value = rows
}

const resetDialogForm = () => {
  Object.assign(dialogForm, getDefaultForm())
}

const openDialog = (type: DialogType, row?: TeenMentalHealthItem) => {
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
  router.push('/source/teen-analysis')
}

const handleDialogSubmit = async () => {
  if (!checkWritePermission()) {
    return
  }

  submitLoading.value = true

  try {
    const payload = { ...dialogForm }

    if (dialogType.value === 'add') {
      await fetchCreateTeen(payload)
    } else {
      await fetchUpdateTeen(Number(dialogForm.id), payload)
    }

    dialogVisible.value = false
    await loadTableData()
    await loadOptions()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: TeenMentalHealthItem) => {
  if (!checkWritePermission()) {
    return
  }

  await ElMessageBox.confirm(`确定删除 ID 为「${row.id}」的青少年心理健康数据吗？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })

  await fetchDeleteTeen(Number(row.id))
  await loadTableData()
}

const formatNumber = (value?: number | string, precision = 2) => {
  if (value === null || value === undefined || value === '') return '-'

  const num = Number(value)
  if (!Number.isFinite(num)) return '-'

  return num.toFixed(precision)
}

const formatDepressionLabel = (value?: number) => {
  if (value === 1) return '存在风险'
  if (value === 0) return '暂无明显风险'
  return '-'
}

const getLevelTagType = (value?: number) => {
  const num = Number(value)

  if (!Number.isFinite(num)) return 'info'
  if (num >= 8) return 'danger'
  if (num >= 5) return 'warning'
  return 'success'
}

onMounted(async () => {
  await loadOptions()
  await loadTableData()
})
</script>

<style scoped lang="scss">
.teen-page {
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

.teen-cell {
  min-width: 0;
}

.teen-main {
  font-size: 14px;
  font-weight: 600;
  color: var(--art-text-gray-900, #111827);
  line-height: 1.5;
}

.teen-sub {
  margin-top: 2px;
  font-size: 12px;
  color: var(--art-text-gray-500, #98a2b3);
  line-height: 1.5;
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