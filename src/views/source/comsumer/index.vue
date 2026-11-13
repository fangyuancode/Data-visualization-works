<template>
  <div class="consumer-page">
    <div class="page-header art-card">
      <div class="page-header__left">
        <div class="page-title">消费者购物趋势分析</div>
        <div class="page-desc">
          基于消费者收入、互联网使用、线上线下消费、配送体验和购物偏好等维度进行数据维护与查询。
        </div>
      </div>

      <div class="page-header__right">
        <ElButton v-if="canAdd" type="primary" @click="openDialog('add')">新增数据</ElButton>
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
      <ElForm :model="searchForm" inline label-width="88px" class="search-form">
        <ElFormItem label="性别">
          <ElSelect v-model="searchForm.gender" placeholder="请选择性别" clearable style="width: 140px">
            <ElOption v-for="item in options.genders" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="城市等级">
          <ElSelect v-model="searchForm.cityTier" placeholder="请选择城市等级" clearable style="width: 150px">
            <ElOption v-for="item in options.cityTiers" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="购物偏好">
          <ElSelect v-model="searchForm.shoppingPreference" placeholder="请选择购物偏好" clearable style="width: 160px">
            <ElOption v-for="item in options.shoppingPreferences" :key="item" :label="formatPreference(item)" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="年龄">
          <div class="range-input">
            <ElInputNumber v-model="searchForm.ageMin" :min="0" :controls="false" placeholder="最小" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="searchForm.ageMax" :min="0" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem label="月收入">
          <div class="range-input range-input--wide">
            <ElInputNumber v-model="searchForm.incomeMin" :min="0" :controls="false" placeholder="最小" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="searchForm.incomeMax" :min="0" :controls="false" placeholder="最大" />
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
        <div class="table-toolbar__left">
          <ElSpace wrap>
            <ElButton v-if="canAdd" type="primary" plain @click="openDialog('add')">新增</ElButton>

            <ElButton v-if="canEdit" :disabled="selectedRows.length !== 1" @click="handleBatchEdit">编辑</ElButton>
            <ElButton type="success" plain @click="handleOpenAnalysis">可视化分析</ElButton>
          </ElSpace>
        </div>

        <div class="table-toolbar__right">
          <div class="toolbar-tip">当前共 {{ pagination.total }} 条数据</div>
        </div>
      </div>

      <ElTable v-loading="loading" element-loading-text="加载中..." element-loading-background="rgba(122, 122, 122, 0.8)" :data="tableData" style="width: 100%" height="560" @selection-change="handleSelectionChange">
        <ElTableColumn v-if="canWrite" type="selection" width="55" fixed="left" />

        <ElTableColumn prop="id" label="ID" width="90" fixed="left" />

        <ElTableColumn label="用户画像" min-width="180" fixed="left">
          <template #default="{ row }">
            <div class="profile-cell">
              <div class="profile-cell__main">
                {{ row.gender || '-' }} / {{ row.age || '-' }} 岁
              </div>
              <div class="profile-cell__sub">
                {{ row.cityTier || '未知城市等级' }}
              </div>
            </div>
          </template>
        </ElTableColumn>

        <ElTableColumn label="购物偏好" width="120">
          <template #default="{ row }">
            <ElTag :type="getPreferenceTagType(row.shoppingPreference)" effect="light" round>
              {{ formatPreference(row.shoppingPreference) }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn label="月收入" min-width="130" align="right">
          <template #default="{ row }">
            {{ formatMoney(row.monthlyIncome) }}
          </template>
        </ElTableColumn>

        <ElTableColumn label="线上订单/月" prop="monthlyOnlineOrders" width="120" align="center" />
        <ElTableColumn label="门店访问/月" prop="monthlyStoreVisits" width="120" align="center" />

        <ElTableColumn label="线上均消" min-width="130" align="right">
          <template #default="{ row }">
            {{ formatMoney(row.avgOnlineSpend) }}
          </template>
        </ElTableColumn>

        <ElTableColumn label="线下均消" min-width="130" align="right">
          <template #default="{ row }">
            {{ formatMoney(row.avgStoreSpend) }}
          </template>
        </ElTableColumn>

        <ElTableColumn prop="dailyInternetHours" label="上网时长/h" width="120" align="center" />
        <ElTableColumn prop="socialMediaHours" label="社媒时长/h" width="120" align="center" />
        <ElTableColumn prop="smartphoneUsageYears" label="手机使用年限" width="120" align="center" />

        <ElTableColumn prop="onlinePaymentTrustScore" label="支付信任" width="100" align="center" />
        <ElTableColumn prop="techSavvyScore" label="技术熟练" width="100" align="center" />
        <ElTableColumn prop="discountSensitivity" label="折扣敏感" width="100" align="center" />
        <ElTableColumn prop="returnFrequency" label="退货频率" width="100" align="center" />
        <ElTableColumn prop="avgDeliveryDays" label="配送天数" width="100" align="center" />
        <ElTableColumn prop="deliveryFeeSensitivity" label="配送费敏感" width="120" align="center" />
        <ElTableColumn prop="freeReturnImportance" label="免费退货重视" width="130" align="center" />
        <ElTableColumn prop="productAvailabilityOnline" label="线上可得性" width="120" align="center" />
        <ElTableColumn prop="impulseBuyingScore" label="冲动购买" width="110" align="center" />
        <ElTableColumn prop="needTouchFeelScore" label="触摸体验需求" width="130" align="center" />
        <ElTableColumn prop="brandLoyaltyScore" label="品牌忠诚" width="110" align="center" />
        <ElTableColumn prop="environmentalAwareness" label="环保意识" width="110" align="center" />
        <ElTableColumn prop="timePressureLevel" label="时间压力" width="110" align="center" />

        <ElTableColumn prop="createTime" label="导入时间" min-width="180" />

        <ElTableColumn v-if="canWrite" label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <ElSpace wrap>
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

    <ElDialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增消费者购物数据' : '编辑消费者购物数据'" width="920px" destroy-on-close>
      <ElForm ref="dialogFormRef" :model="dialogForm" label-width="120px">
        <ElDivider content-position="left">基础画像</ElDivider>

        <ElRow :gutter="16">
          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="年龄">
              <ElInputNumber v-model="dialogForm.age" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="性别">
              <ElSelect v-model="dialogForm.gender" placeholder="请选择性别" clearable style="width: 100%">
                <ElOption v-for="item in options.genders" :key="item" :label="item" :value="item" />
                <ElOption label="Male" value="Male" />
                <ElOption label="Female" value="Female" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="城市等级">
              <ElSelect v-model="dialogForm.cityTier" placeholder="请选择城市等级" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.cityTiers" :key="item" :label="item" :value="item" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="购物偏好">
              <ElSelect v-model="dialogForm.shoppingPreference" placeholder="请选择购物偏好" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.shoppingPreferences" :key="item" :label="formatPreference(item)" :value="item" />
                <ElOption label="线上购物" value="Online" />
                <ElOption label="线下购物" value="Offline" />
                <ElOption label="混合购物" value="Both" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="月收入">
              <ElInputNumber v-model="dialogForm.monthlyIncome" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElDivider content-position="left">使用行为</ElDivider>

        <ElRow :gutter="16">
          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="每日上网时长">
              <ElInputNumber v-model="dialogForm.dailyInternetHours" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="手机使用年限">
              <ElInputNumber v-model="dialogForm.smartphoneUsageYears" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="社媒使用时长">
              <ElInputNumber v-model="dialogForm.socialMediaHours" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="线上订单/月">
              <ElInputNumber v-model="dialogForm.monthlyOnlineOrders" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="门店访问/月">
              <ElInputNumber v-model="dialogForm.monthlyStoreVisits" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="线上均消">
              <ElInputNumber v-model="dialogForm.avgOnlineSpend" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="线下均消">
              <ElInputNumber v-model="dialogForm.avgStoreSpend" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElDivider content-position="left">评分指标</ElDivider>

        <ElRow :gutter="16">
          <ElCol v-for="field in scoreFields" :key="field.prop" :xs="24" :sm="12" :lg="8">
            <ElFormItem :label="field.label">
              <ElInputNumber v-model="dialogForm[field.prop]" :min="0" :max="10" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="平均配送天数">
              <ElInputNumber v-model="dialogForm.avgDeliveryDays" :min="0" :controls="false" style="width: 100%" />
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
  ConsumerShoppingTrendItem,
  ConsumerShoppingTrendOptions,
  fetchConsumerShoppingOptions,
  fetchConsumerShoppingPage,
  fetchCreateConsumerShopping,
  fetchDeleteConsumerShopping,
  fetchUpdateConsumerShopping
} from '@/api/consumer-shopping'

import { usePagePermission } from '@/hooks/core/usePagePermission'

defineOptions({ name: 'ConsumerShopping' })

type DialogType = 'add' | 'edit'

type ScoreField = {
  label: string
  prop: keyof ConsumerShoppingTrendItem
}

const router = useRouter()
const { canWrite, canAdd, canEdit, canDelete, checkWritePermission } = usePagePermission()

const tableData = ref<ConsumerShoppingTrendItem[]>([])
const selectedRows = ref<ConsumerShoppingTrendItem[]>([])

const loading = ref(false)
const submitLoading = ref(false)

const dialogVisible = ref(false)
const dialogType = ref<DialogType>('add')
const dialogFormRef = ref<FormInstance>()

const options = reactive<ConsumerShoppingTrendOptions>({
  genders: [],
  cityTiers: [],
  shoppingPreferences: []
})

const searchForm = reactive({
  gender: '',
  cityTier: '',
  shoppingPreference: '',
  ageMin: undefined as number | undefined,
  ageMax: undefined as number | undefined,
  incomeMin: undefined as number | undefined,
  incomeMax: undefined as number | undefined,
  ordersMin: undefined as number | undefined,
  ordersMax: undefined as number | undefined
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const scoreFields: ScoreField[] = [
  { label: '支付信任评分', prop: 'onlinePaymentTrustScore' },
  { label: '技术熟练评分', prop: 'techSavvyScore' },
  { label: '折扣敏感度', prop: 'discountSensitivity' },
  { label: '退货频率', prop: 'returnFrequency' },
  { label: '配送费敏感度', prop: 'deliveryFeeSensitivity' },
  { label: '免费退货重视', prop: 'freeReturnImportance' },
  { label: '线上商品可得性', prop: 'productAvailabilityOnline' },
  { label: '冲动购买评分', prop: 'impulseBuyingScore' },
  { label: '触摸体验需求', prop: 'needTouchFeelScore' },
  { label: '品牌忠诚度', prop: 'brandLoyaltyScore' },
  { label: '环保意识', prop: 'environmentalAwareness' },
  { label: '时间压力水平', prop: 'timePressureLevel' }
]

const getDefaultForm = (): ConsumerShoppingTrendItem => ({
  age: undefined,
  monthlyIncome: undefined,
  dailyInternetHours: undefined,
  smartphoneUsageYears: undefined,
  socialMediaHours: undefined,
  onlinePaymentTrustScore: undefined,
  techSavvyScore: undefined,
  monthlyOnlineOrders: undefined,
  monthlyStoreVisits: undefined,
  avgOnlineSpend: undefined,
  avgStoreSpend: undefined,
  discountSensitivity: undefined,
  returnFrequency: undefined,
  avgDeliveryDays: undefined,
  deliveryFeeSensitivity: undefined,
  freeReturnImportance: undefined,
  productAvailabilityOnline: undefined,
  impulseBuyingScore: undefined,
  needTouchFeelScore: undefined,
  brandLoyaltyScore: undefined,
  environmentalAwareness: undefined,
  timePressureLevel: undefined,
  gender: '',
  cityTier: '',
  shoppingPreference: ''
})

const dialogForm = reactive<ConsumerShoppingTrendItem>(getDefaultForm())

const statisticsCards = computed(() => {
  const total = pagination.total

  const currentOnline = tableData.value.filter(
    (item) => item.shoppingPreference === 'Online'
  ).length
  const currentOffline = tableData.value.filter(
    (item) => item.shoppingPreference === 'Offline'
  ).length

  const avgIncome = tableData.value.length
    ? tableData.value.reduce((sum, item) => sum + Number(item.monthlyIncome || 0), 0) /
      tableData.value.length
    : 0

  return [
    {
      label: '数据总量',
      value: total,
      extra: '消费者购物趋势数据记录'
    },
    {
      label: '当前页线上偏好',
      value: currentOnline,
      extra: '购物偏好为 Online 的记录'
    },
    {
      label: '当前页线下偏好',
      value: currentOffline,
      extra: '购物偏好为 Offline 的记录'
    },
    {
      label: '当前页平均收入',
      value: formatMoney(avgIncome),
      extra: '按当前分页数据计算'
    }
  ]
})

const loadOptions = async () => {
  const res = await fetchConsumerShoppingOptions()

  options.genders = res.genders || []
  options.cityTiers = res.cityTiers || []
  options.shoppingPreferences = res.shoppingPreferences || []
}

const loadTableData = async () => {
  loading.value = true

  try {
    const res = await fetchConsumerShoppingPage({
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
  searchForm.cityTier = ''
  searchForm.shoppingPreference = ''
  searchForm.ageMin = undefined
  searchForm.ageMax = undefined
  searchForm.incomeMin = undefined
  searchForm.incomeMax = undefined
  searchForm.ordersMin = undefined
  searchForm.ordersMax = undefined

  pagination.current = 1
  loadTableData()
}

const handleSelectionChange = (rows: ConsumerShoppingTrendItem[]) => {
  selectedRows.value = rows
}

const handleOpenAnalysis = () => {
  router.push('/source/comsumer-analysis')
}

const resetDialogForm = () => {
  Object.assign(dialogForm, getDefaultForm())
}

const openDialog = (type: DialogType, row?: ConsumerShoppingTrendItem) => {
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
      await fetchCreateConsumerShopping(payload)
    } else {
      await fetchUpdateConsumerShopping(Number(dialogForm.id), payload)
    }

    dialogVisible.value = false
    await loadTableData()
    await loadOptions()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: ConsumerShoppingTrendItem) => {
  if (!checkWritePermission()) {
    return
  }

  await ElMessageBox.confirm(`确定删除 ID 为 ${row.id} 的消费者购物数据吗？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })

  await fetchDeleteConsumerShopping(Number(row.id))
  await loadTableData()
}

const formatMoney = (value?: number | string) => {
  const num = Number(value || 0)

  if (!Number.isFinite(num)) {
    return '-'
  }

  return num.toLocaleString('zh-CN', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2
  })
}

const formatPreference = (value?: string) => {
  if (!value) return '-'

  const map: Record<string, string> = {
    Online: '线上购物',
    Offline: '线下购物',
    Both: '混合购物'
  }

  return map[value] || value
}

const getPreferenceTagType = (value?: string) => {
  if (value === 'Online') return 'success'
  if (value === 'Offline') return 'warning'
  if (value === 'Both') return 'primary'
  return 'info'
}

onMounted(async () => {
  await loadOptions()
  await loadTableData()
})
</script>

<style scoped lang="scss">
.consumer-page {
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
  height: 132px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  // margin-bottom: 20px;
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

.range-input {
  display: flex;
  align-items: center;
  width: 210px;
  gap: 8px;

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

.range-line {
  color: var(--art-text-gray-500);
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

.profile-cell {
  min-width: 0;
}

.profile-cell__main {
  font-size: 14px;
  font-weight: 600;
  color: var(--art-text-gray-900);
  line-height: 1.5;
}

.profile-cell__sub {
  margin-top: 2px;
  font-size: 12px;
  color: var(--art-text-gray-500);
  line-height: 1.5;
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
  padding-top: 10px;
  padding-bottom: 10px;
}

:deep(.el-divider__text) {
  font-weight: 600;
  color: var(--art-text-gray-700);
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



