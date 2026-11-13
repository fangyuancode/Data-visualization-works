<template>
  <div class="rental-page">
    <div class="art-card page-header">
      <div>
        <div class="page-title">租房数据集管理</div>
        <div class="page-desc">
          支持租金、户型、面积、区域、楼层、地铁、供暖方式等租房数据的分页展示、条件查询、新增、编辑和删除。
        </div>
      </div>

      <ElButton v-if="canAdd" type="primary" @click="openDialog('add')">新增房源</ElButton>
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
      <ElForm :model="searchForm" inline label-width="80px" class="search-form">
        <ElFormItem label="区域">
          <ElSelect v-model="searchForm.region" placeholder="请选择区域" clearable filterable style="width: 150px">
            <ElOption v-for="item in options.regions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="房间类型">
          <ElSelect v-model="searchForm.room" placeholder="请选择房间" clearable filterable style="width: 150px">
            <ElOption v-for="item in options.rooms" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="楼层">
          <ElSelect v-model="searchForm.floorGrp" placeholder="请选择楼层" clearable filterable style="width: 150px">
            <ElOption v-for="item in options.floorGrps" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="地铁">
          <ElSelect v-model="searchForm.subway" placeholder="请选择" clearable filterable style="width: 130px">
            <ElOption v-for="item in options.subways" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="供暖">
          <ElSelect v-model="searchForm.heating" placeholder="请选择" clearable filterable style="width: 130px">
            <ElOption v-for="item in options.heatings" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="卧室">
          <ElInputNumber v-model="searchForm.bedroom" :min="0" :controls="false" placeholder="数量" style="width: 100px" />
        </ElFormItem>

        <ElFormItem label="租金">
          <div class="range-input range-input--wide">
            <ElInputNumber v-model="searchForm.rentMin" :min="0" :controls="false" placeholder="最低" />
            <span>-</span>
            <ElInputNumber v-model="searchForm.rentMax" :min="0" :controls="false" placeholder="最高" />
          </div>
        </ElFormItem>

        <ElFormItem label="面积">
          <div class="range-input">
            <ElInputNumber v-model="searchForm.areaMin" :min="0" :controls="false" placeholder="最小" />
            <span>-</span>
            <ElInputNumber v-model="searchForm.areaMax" :min="0" :controls="false" placeholder="最大" />
          </div>
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

        <div class="toolbar-tip">当前共 {{ pagination.total }} 条租房数据</div>
      </div>

      <ElTable v-loading="loading" :data="tableData" height="560" style="width: 100%" @selection-change="handleSelectionChange">
        <ElTableColumn v-if="canWrite" type="selection" width="55" fixed="left" />
        <ElTableColumn prop="id" label="ID" width="80" fixed="left" />

        <ElTableColumn label="房源信息" min-width="190" fixed="left">
          <template #default="{ row }">
            <div class="house-cell">
              <div class="house-main">{{ row.region || '-' }} / {{ row.room || '-' }}</div>
              <div class="house-sub">
                {{ row.bedroom || 0 }}室{{ row.livingroom || 0 }}厅{{ row.bathroom || 0 }}卫 · {{ formatNumber(row.area) }}㎡
              </div>
            </div>
          </template>
        </ElTableColumn>

        <ElTableColumn label="租金" width="120" align="right">
          <template #default="{ row }">
            <span class="rent-text">¥{{ formatMoney(row.rent) }}</span>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="area" label="面积㎡" width="100" align="center" />
        <ElTableColumn prop="bedroom" label="卧室" width="80" align="center" />
        <ElTableColumn prop="livingroom" label="客厅" width="80" align="center" />
        <ElTableColumn prop="bathroom" label="卫生间" width="90" align="center" />

        <ElTableColumn prop="room" label="房间类型" width="120" />
        <ElTableColumn prop="floorGrp" label="楼层分组" width="120" />

        <ElTableColumn label="地铁" width="100" align="center">
          <template #default="{ row }">
            <ElTag :type="getSubwayTagType(row.subway)" effect="light" round>
              {{ row.subway || '-' }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="region" label="区域" width="120" />
        <ElTableColumn prop="heating" label="供暖方式" width="120" />
        <ElTableColumn prop="sourceId" label="原始序号" width="110" align="center" />
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

    <ElDialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增租房数据' : '编辑租房数据'" width="760px" destroy-on-close>
      <ElForm ref="dialogFormRef" :model="dialogForm" label-width="100px">
        <ElDivider content-position="left">基础信息</ElDivider>

        <ElRow :gutter="16">
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="原始序号">
              <ElInputNumber v-model="dialogForm.sourceId" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12">
            <ElFormItem label="区域">
              <ElSelect v-model="dialogForm.region" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.regions" :key="item" :label="item" :value="item" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12">
            <ElFormItem label="租金">
              <ElInputNumber v-model="dialogForm.rent" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12">
            <ElFormItem label="面积">
              <ElInputNumber v-model="dialogForm.area" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElDivider content-position="left">户型信息</ElDivider>

        <ElRow :gutter="16">
          <ElCol :xs="24" :sm="8">
            <ElFormItem label="卧室">
              <ElInputNumber v-model="dialogForm.bedroom" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="8">
            <ElFormItem label="客厅">
              <ElInputNumber v-model="dialogForm.livingroom" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="8">
            <ElFormItem label="卫生间">
              <ElInputNumber v-model="dialogForm.bathroom" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12">
            <ElFormItem label="房间类型">
              <ElSelect v-model="dialogForm.room" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.rooms" :key="item" :label="item" :value="item" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12">
            <ElFormItem label="楼层分组">
              <ElSelect v-model="dialogForm.floorGrp" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.floorGrps" :key="item" :label="item" :value="item" />
              </ElSelect>
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElDivider content-position="left">配套信息</ElDivider>

        <ElRow :gutter="16">
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="是否近地铁">
              <ElSelect v-model="dialogForm.subway" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.subways" :key="item" :label="item" :value="item" />
                <ElOption label="是" value="是" />
                <ElOption label="否" value="否" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12">
            <ElFormItem label="供暖方式">
              <ElSelect v-model="dialogForm.heating" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.heatings" :key="item" :label="item" :value="item" />
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
  fetchCreateRental,
  fetchDeleteRental,
  fetchRentalOptions,
  fetchRentalPage,
  fetchUpdateRental
} from '@/api/rental'
import type { RentalHouseDataItem, RentalHouseOptions } from '@/api/rental'

import { usePagePermission } from '@/hooks/core/usePagePermission'

defineOptions({ name: 'RentalHouseManage' })

const router = useRouter()
const { canWrite, canAdd, canEdit, canDelete, checkWritePermission } = usePagePermission()

type DialogType = 'add' | 'edit'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref<DialogType>('add')
const dialogFormRef = ref<FormInstance>()

const tableData = ref<RentalHouseDataItem[]>([])
const selectedRows = ref<RentalHouseDataItem[]>([])

const options = reactive<RentalHouseOptions>({
  rooms: [],
  floorGrps: [],
  subways: [],
  regions: [],
  heatings: []
})

const searchForm = reactive({
  room: '',
  floorGrp: '',
  subway: '',
  region: '',
  heating: '',
  bedroom: undefined as number | undefined,
  livingroom: undefined as number | undefined,
  bathroom: undefined as number | undefined,
  rentMin: undefined as number | undefined,
  rentMax: undefined as number | undefined,
  areaMin: undefined as number | undefined,
  areaMax: undefined as number | undefined
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const getDefaultForm = (): RentalHouseDataItem => ({
  sourceId: undefined,
  rent: undefined,
  bedroom: undefined,
  livingroom: undefined,
  bathroom: undefined,
  area: undefined,
  room: '',
  floorGrp: '',
  subway: '',
  region: '',
  heating: ''
})

const dialogForm = reactive<RentalHouseDataItem>(getDefaultForm())

const statCards = computed(() => {
  const currentPageTotal = tableData.value.length

  const avgRent = currentPageTotal
    ? tableData.value.reduce((sum, item) => sum + Number(item.rent || 0), 0) / currentPageTotal
    : 0

  const avgArea = currentPageTotal
    ? tableData.value.reduce((sum, item) => sum + Number(item.area || 0), 0) / currentPageTotal
    : 0

  const subwayCount = tableData.value.filter((item) => {
    const value = String(item.subway || '')
    return value.includes('是') || value.includes('有') || value.includes('近')
  }).length

  return [
    {
      label: '房源总量',
      value: pagination.total,
      extra: '租房数据记录总数'
    },
    {
      label: '当前页平均租金',
      value: `¥${formatMoney(avgRent)}`,
      extra: '基于当前页数据计算'
    },
    {
      label: '当前页平均面积',
      value: `${formatNumber(avgArea)}㎡`,
      extra: '基于当前页数据计算'
    },
    {
      label: '当前页近地铁',
      value: subwayCount,
      extra: '根据地铁字段估算'
    }
  ]
})

const loadOptions = async () => {
  const res = await fetchRentalOptions()

  options.rooms = res.rooms || []
  options.floorGrps = res.floorGrps || []
  options.subways = res.subways || []
  options.regions = res.regions || []
  options.heatings = res.heatings || []
}

const loadTableData = async () => {
  loading.value = true

  try {
    const res = await fetchRentalPage({
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
  searchForm.room = ''
  searchForm.floorGrp = ''
  searchForm.subway = ''
  searchForm.region = ''
  searchForm.heating = ''
  searchForm.bedroom = undefined
  searchForm.livingroom = undefined
  searchForm.bathroom = undefined
  searchForm.rentMin = undefined
  searchForm.rentMax = undefined
  searchForm.areaMin = undefined
  searchForm.areaMax = undefined

  pagination.current = 1
  loadTableData()
}

const handleSelectionChange = (rows: RentalHouseDataItem[]) => {
  selectedRows.value = rows
}

const resetDialogForm = () => {
  Object.assign(dialogForm, getDefaultForm())
}

const openDialog = (type: DialogType, row?: RentalHouseDataItem) => {
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
  router.push('/source/rental-analysis')
}

const handleDialogSubmit = async () => {
  if (!checkWritePermission()) {
    return
  }

  submitLoading.value = true

  try {
    const payload = { ...dialogForm }

    if (dialogType.value === 'add') {
      await fetchCreateRental(payload)
    } else {
      await fetchUpdateRental(Number(dialogForm.id), payload)
    }

    dialogVisible.value = false
    await loadTableData()
    await loadOptions()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: RentalHouseDataItem) => {
  if (!checkWritePermission()) {
    return
  }

  await ElMessageBox.confirm(
    `确定删除「${row.region || '-'} / ${row.room || '-'} / ¥${formatMoney(
      row.rent
    )}」这条租房数据吗？`,
    '删除确认',
    {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }
  )

  await fetchDeleteRental(Number(row.id))
  await loadTableData()
}

const formatMoney = (value?: number | string) => {
  if (value === null || value === undefined || value === '') return '-'

  const num = Number(value)
  if (!Number.isFinite(num)) return '-'

  return num.toLocaleString('zh-CN', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2
  })
}

const formatNumber = (value?: number | string, precision = 2) => {
  if (value === null || value === undefined || value === '') return '-'

  const num = Number(value)
  if (!Number.isFinite(num)) return '-'

  return num.toFixed(precision)
}

const getSubwayTagType = (value?: string) => {
  const text = String(value || '')

  if (text.includes('是') || text.includes('有') || text.includes('近')) {
    return 'success'
  }

  if (text.includes('否') || text.includes('无') || text.includes('远')) {
    return 'info'
  }

  return 'warning'
}

onMounted(async () => {
  await loadOptions()
  await loadTableData()
})
</script>

<style scoped lang="scss">
.rental-page {
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

.house-cell {
  min-width: 0;
}

.house-main {
  font-size: 14px;
  font-weight: 600;
  color: var(--art-text-gray-900, #111827);
  line-height: 1.5;
}

.house-sub {
  margin-top: 2px;
  font-size: 12px;
  color: var(--art-text-gray-500, #98a2b3);
  line-height: 1.5;
}

.rent-text {
  font-weight: 600;
  color: #f97316;
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