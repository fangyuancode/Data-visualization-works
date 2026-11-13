<template>
  <div class="nipt-page">
    <div class="art-card page-header">
      <div>
        <div class="page-title">孕妇 NIPT 检测数据管理</div>
        <div class="page-desc">
          支持孕妇基础信息、检测信息、染色体指标、BMI、孕周及胎儿健康状态的分页查询、维护与管理。
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
      <ElForm :model="searchForm" inline label-width="92px" class="search-form">
        <ElFormItem label="胎儿类型">
          <ElSelect v-model="searchForm.fetusGender" placeholder="请选择" clearable style="width: 140px">
            <ElOption v-for="item in options.fetusGenders" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="孕妇代码">
          <ElInput v-model="searchForm.pregnantWomanCode" placeholder="请输入孕妇代码" clearable style="width: 180px" />
        </ElFormItem>

        <ElFormItem label="胎儿健康">
          <ElSelect v-model="searchForm.fetusHealthy" placeholder="请选择" clearable style="width: 140px">
            <ElOption v-for="item in options.fetusHealthyOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="非整倍体">
          <ElSelect v-model="searchForm.chromosomeAneuploidy" placeholder="请选择" clearable style="width: 160px">
            <ElOption v-for="item in options.chromosomeAneuploidyOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="年龄">
          <div class="range-input">
            <ElInputNumber v-model="searchForm.ageMin" :min="0" :controls="false" placeholder="最小" />
            <span>-</span>
            <ElInputNumber v-model="searchForm.ageMax" :min="0" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem label="BMI">
          <div class="range-input">
            <ElInputNumber v-model="searchForm.bmiMin" :min="0" :precision="2" :controls="false" placeholder="最小" />
            <span>-</span>
            <ElInputNumber v-model="searchForm.bmiMax" :min="0" :precision="2" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem label="检测日期">
          <ElDatePicker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 260px" @change="handleDateRangeChange" />
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

        <div class="toolbar-tip">当前共 {{ pagination.total }} 条检测数据</div>
      </div>

      <ElTable v-loading="loading" :data="tableData" height="560" style="width: 100%" @selection-change="handleSelectionChange">
        <ElTableColumn v-if="canWrite" type="selection" width="55" fixed="left" />
        <ElTableColumn prop="id" label="ID" width="80" fixed="left" />

        <ElTableColumn label="孕妇信息" min-width="190" fixed="left">
          <template #default="{ row }">
            <div class="person-cell">
              <div class="person-main">{{ row.pregnantWomanCode || '-' }}</div>
              <div class="person-sub">
                {{ row.fetusGender || '-' }} / {{ row.age || '-' }}岁 / BMI {{ formatNumber(row.bmi, 2) }}
              </div>
            </div>
          </template>
        </ElTableColumn>

        <ElTableColumn label="胎儿健康" width="110" align="center">
          <template #default="{ row }">
            <ElTag :type="row.fetusHealthy === '是' ? 'success' : 'warning'" effect="light" round>
              {{ row.fetusHealthy || '-' }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="testDate" label="检测日期" width="120" />
        <ElTableColumn prop="gestationalWeek" label="检测孕周" width="120" />
        <ElTableColumn prop="gestationalDays" label="孕周天数" width="100" align="center" />
        <ElTableColumn prop="bloodDrawCount" label="抽血次数" width="100" align="center" />

        <ElTableColumn prop="height" label="身高" width="90" align="center" />
        <ElTableColumn prop="weight" label="体重" width="90" align="center" />
        <ElTableColumn prop="ivfPregnancy" label="IVF妊娠" width="120" />
        <ElTableColumn prop="lastMenstrualPeriod" label="末次月经" width="120" />

        <ElTableColumn prop="rawReadCount" label="原始读段数" width="140" align="right" />
        <ElTableColumn label="比对比例" width="110" align="right">
          <template #default="{ row }">{{ formatPercent(row.mappingRatio) }}</template>
        </ElTableColumn>
        <ElTableColumn label="重复比例" width="110" align="right">
          <template #default="{ row }">{{ formatPercent(row.duplicateReadRatio) }}</template>
        </ElTableColumn>
        <ElTableColumn prop="uniqueMappedReads" label="唯一比对读段" width="140" align="right" />
        <ElTableColumn label="GC含量" width="100" align="right">
          <template #default="{ row }">{{ formatPercent(row.gcContent) }}</template>
        </ElTableColumn>

        <ElTableColumn prop="chr13ZValue" label="13号Z值" width="110" align="right" />
        <ElTableColumn prop="chr18ZValue" label="18号Z值" width="110" align="right" />
        <ElTableColumn prop="chr21ZValue" label="21号Z值" width="110" align="right" />
        <ElTableColumn prop="chrXZValue" label="X染色体Z值" width="120" align="right" />
        <ElTableColumn prop="chrYZValue" label="Y染色体Z值" width="120" align="right" />

        <ElTableColumn label="Y染色体浓度" width="130" align="right">
          <template #default="{ row }">{{ formatPercent(row.chrYConcentration) }}</template>
        </ElTableColumn>
        <ElTableColumn label="X染色体浓度" width="130" align="right">
          <template #default="{ row }">{{ formatPercent(row.chrXConcentration) }}</template>
        </ElTableColumn>

        <ElTableColumn prop="chromosomeAneuploidy" label="染色体非整倍体" width="140" />
        <ElTableColumn prop="pregnancyCount" label="怀孕次数" width="100" align="center" />
        <ElTableColumn prop="productionCount" label="生产次数" width="100" align="center" />
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

    <ElDialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增NIPT检测数据' : '编辑NIPT检测数据'" width="1080px" destroy-on-close>
      <ElForm ref="dialogFormRef" :model="dialogForm" label-width="130px">
        <ElDivider content-position="left">基础信息</ElDivider>

        <ElRow :gutter="16">
          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="胎儿类型">
              <ElSelect v-model="dialogForm.fetusGender" clearable filterable allow-create style="width: 100%">
                <ElOption label="男胎" value="男胎" />
                <ElOption label="女胎" value="女胎" />
                <ElOption v-for="item in options.fetusGenders" :key="item" :label="item" :value="item" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="序号">
              <ElInputNumber v-model="dialogForm.serialNo" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="孕妇代码">
              <ElInput v-model="dialogForm.pregnantWomanCode" placeholder="请输入孕妇代码" clearable />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="年龄">
              <ElInputNumber v-model="dialogForm.age" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="身高">
              <ElInputNumber v-model="dialogForm.height" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="体重">
              <ElInputNumber v-model="dialogForm.weight" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="BMI">
              <ElInputNumber v-model="dialogForm.bmi" :min="0" :precision="4" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="末次月经">
              <ElDatePicker v-model="dialogForm.lastMenstrualPeriod" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="IVF妊娠">
              <ElInput v-model="dialogForm.ivfPregnancy" clearable />
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElDivider content-position="left">检测信息</ElDivider>

        <ElRow :gutter="16">
          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="检测日期">
              <ElDatePicker v-model="dialogForm.testDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="抽血次数">
              <ElInputNumber v-model="dialogForm.bloodDrawCount" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="检测孕周">
              <ElInput v-model="dialogForm.gestationalWeek" placeholder="例如 12w+3" clearable />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="孕周天数">
              <ElInputNumber v-model="dialogForm.gestationalDays" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="怀孕次数">
              <ElInput v-model="dialogForm.pregnancyCount" clearable />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="生产次数">
              <ElInputNumber v-model="dialogForm.productionCount" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="胎儿是否健康">
              <ElSelect v-model="dialogForm.fetusHealthy" clearable filterable allow-create style="width: 100%">
                <ElOption label="是" value="是" />
                <ElOption label="否" value="否" />
                <ElOption v-for="item in options.fetusHealthyOptions" :key="item" :label="item" :value="item" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="染色体非整倍体">
              <ElSelect v-model="dialogForm.chromosomeAneuploidy" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.chromosomeAneuploidyOptions" :key="item" :label="item" :value="item" />
              </ElSelect>
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElDivider content-position="left">测序与染色体指标</ElDivider>

        <ElRow :gutter="16">
          <ElCol v-for="field in numberFields" :key="field.prop" :xs="24" :sm="12" :lg="8">
            <ElFormItem :label="field.label">
              <ElInputNumber v-model="dialogForm[field.prop]" :min="field.min" :precision="field.precision" :controls="false" style="width: 100%" />
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
  fetchCreateNipt,
  fetchDeleteNipt,
  fetchNiptOptions,
  fetchNiptPage,
  fetchUpdateNipt
} from '@/api/nipt'
import type { MaternalNiptDataItem, MaternalNiptOptions } from '@/api/nipt'

import { usePagePermission } from '@/hooks/core/usePagePermission'

defineOptions({ name: 'NiptDataManage' })

const router = useRouter()
const { canWrite, canAdd, canEdit, canDelete, checkWritePermission } = usePagePermission()

type DialogType = 'add' | 'edit'

type NumberField = {
  label: string
  prop: keyof MaternalNiptDataItem
  min: number
  precision: number
}

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref<DialogType>('add')
const dialogFormRef = ref<FormInstance>()
const tableData = ref<MaternalNiptDataItem[]>([])
const selectedRows = ref<MaternalNiptDataItem[]>([])
const dateRange = ref<string[]>([])

const options = reactive<MaternalNiptOptions>({
  fetusGenders: [],
  fetusHealthyOptions: [],
  chromosomeAneuploidyOptions: []
})

const searchForm = reactive({
  fetusGender: '',
  pregnantWomanCode: '',
  fetusHealthy: '',
  chromosomeAneuploidy: '',
  ageMin: undefined as number | undefined,
  ageMax: undefined as number | undefined,
  bmiMin: undefined as number | undefined,
  bmiMax: undefined as number | undefined,
  gestationalDaysMin: undefined as number | undefined,
  gestationalDaysMax: undefined as number | undefined,
  testStartDate: '',
  testEndDate: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const getDefaultForm = (): MaternalNiptDataItem => ({
  fetusGender: '',
  serialNo: undefined,
  pregnantWomanCode: '',
  age: undefined,
  height: undefined,
  weight: undefined,
  lastMenstrualPeriod: '',
  ivfPregnancy: '',
  testDate: '',
  bloodDrawCount: undefined,
  gestationalWeek: '',
  gestationalDays: undefined,
  bmi: undefined,
  rawReadCount: undefined,
  mappingRatio: undefined,
  duplicateReadRatio: undefined,
  uniqueMappedReads: undefined,
  gcContent: undefined,
  chr13ZValue: undefined,
  chr18ZValue: undefined,
  chr21ZValue: undefined,
  chrXZValue: undefined,
  chrYZValue: undefined,
  chrYConcentration: undefined,
  chrXConcentration: undefined,
  chr13GcContent: undefined,
  chr18GcContent: undefined,
  chr21GcContent: undefined,
  filteredReadRatio: undefined,
  chromosomeAneuploidy: '',
  pregnancyCount: '',
  productionCount: undefined,
  fetusHealthy: ''
})

const dialogForm = reactive<MaternalNiptDataItem>(getDefaultForm())

const numberFields: NumberField[] = [
  { label: '原始读段数', prop: 'rawReadCount', min: 0, precision: 0 },
  { label: '比对比例', prop: 'mappingRatio', min: 0, precision: 8 },
  { label: '重复读段比例', prop: 'duplicateReadRatio', min: 0, precision: 8 },
  { label: '唯一比对读段', prop: 'uniqueMappedReads', min: 0, precision: 0 },
  { label: 'GC含量', prop: 'gcContent', min: 0, precision: 8 },
  { label: '13号Z值', prop: 'chr13ZValue', min: -999, precision: 8 },
  { label: '18号Z值', prop: 'chr18ZValue', min: -999, precision: 8 },
  { label: '21号Z值', prop: 'chr21ZValue', min: -999, precision: 8 },
  { label: 'X染色体Z值', prop: 'chrXZValue', min: -999, precision: 8 },
  { label: 'Y染色体Z值', prop: 'chrYZValue', min: -999, precision: 8 },
  { label: 'Y染色体浓度', prop: 'chrYConcentration', min: 0, precision: 8 },
  { label: 'X染色体浓度', prop: 'chrXConcentration', min: 0, precision: 8 },
  { label: '13号GC含量', prop: 'chr13GcContent', min: 0, precision: 8 },
  { label: '18号GC含量', prop: 'chr18GcContent', min: 0, precision: 8 },
  { label: '21号GC含量', prop: 'chr21GcContent', min: 0, precision: 8 },
  { label: '过滤读段比例', prop: 'filteredReadRatio', min: 0, precision: 8 }
]

const statCards = computed(() => {
  const maleCount = tableData.value.filter((item) => item.fetusGender === '男胎').length
  const femaleCount = tableData.value.filter((item) => item.fetusGender === '女胎').length

  const avgBmi = tableData.value.length
    ? tableData.value.reduce((sum, item) => sum + Number(item.bmi || 0), 0) / tableData.value.length
    : 0

  return [
    {
      label: '检测数据总量',
      value: pagination.total,
      extra: 'NIPT检测记录总数'
    },
    {
      label: '当前页男胎',
      value: maleCount,
      extra: '当前分页数据统计'
    },
    {
      label: '当前页女胎',
      value: femaleCount,
      extra: '当前分页数据统计'
    },
    {
      label: '当前页平均BMI',
      value: formatNumber(avgBmi, 2),
      extra: '基于当前页数据计算'
    }
  ]
})

const loadOptions = async () => {
  const res = await fetchNiptOptions()

  options.fetusGenders = res.fetusGenders || []
  options.fetusHealthyOptions = res.fetusHealthyOptions || []
  options.chromosomeAneuploidyOptions = res.chromosomeAneuploidyOptions || []
}

const loadTableData = async () => {
  loading.value = true

  try {
    const res = await fetchNiptPage({
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
  searchForm.fetusGender = ''
  searchForm.pregnantWomanCode = ''
  searchForm.fetusHealthy = ''
  searchForm.chromosomeAneuploidy = ''
  searchForm.ageMin = undefined
  searchForm.ageMax = undefined
  searchForm.bmiMin = undefined
  searchForm.bmiMax = undefined
  searchForm.gestationalDaysMin = undefined
  searchForm.gestationalDaysMax = undefined
  searchForm.testStartDate = ''
  searchForm.testEndDate = ''
  dateRange.value = []

  pagination.current = 1
  loadTableData()
}

const handleDateRangeChange = (value: string[] | null) => {
  searchForm.testStartDate = value?.[0] || ''
  searchForm.testEndDate = value?.[1] || ''
}

const handleSelectionChange = (rows: MaternalNiptDataItem[]) => {
  selectedRows.value = rows
}

const resetDialogForm = () => {
  Object.assign(dialogForm, getDefaultForm())
}

const openDialog = (type: DialogType, row?: MaternalNiptDataItem) => {
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
  router.push('/source/nipt-analysis')
}

const handleDialogSubmit = async () => {
  if (!checkWritePermission()) {
    return
  }

  submitLoading.value = true

  try {
    const payload = { ...dialogForm }

    if (dialogType.value === 'add') {
      await fetchCreateNipt(payload)
    } else {
      await fetchUpdateNipt(Number(dialogForm.id), payload)
    }

    dialogVisible.value = false
    await loadTableData()
    await loadOptions()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: MaternalNiptDataItem) => {
  if (!checkWritePermission()) {
    return
  }

  await ElMessageBox.confirm(
    `确定删除孕妇代码为「${row.pregnantWomanCode || row.id}」的NIPT检测数据吗？`,
    '删除确认',
    {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }
  )

  await fetchDeleteNipt(Number(row.id))
  await loadTableData()
}

const formatNumber = (value?: number | string, precision = 2) => {
  if (value === null || value === undefined || value === '') return '-'

  const num = Number(value)
  if (!Number.isFinite(num)) return '-'

  return num.toFixed(precision)
}

const formatPercent = (value?: number | string) => {
  if (value === null || value === undefined || value === '') return '-'

  const num = Number(value)
  if (!Number.isFinite(num)) return '-'

  return `${(num * 100).toFixed(2)}%`
}

onMounted(async () => {
  await loadOptions()
  await loadTableData()
})
</script>

<style scoped lang="scss">
.nipt-page {
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
  // margin-bottom: 20px;
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
  font-size: 30px;
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

.person-cell {
  min-width: 0;
}

.person-main {
  font-size: 14px;
  font-weight: 600;
  color: var(--art-text-gray-900, #111827);
  line-height: 1.5;
}

.person-sub {
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