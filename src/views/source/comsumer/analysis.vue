<template>
  <div class="consumer-analysis-page">
    <div class="page-header art-card">
      <div class="page-header__left">
        <div class="page-title">消费者购物趋势可视化分析</div>
        <div class="page-desc">
          从购物偏好、年龄层、收入层、城市等级、配送体验与评分画像等维度挖掘消费行为规律，为渠道运营、用户分层和营销策略提供依据。
        </div>
      </div>

      <div class="page-header__right">
        <ElSpace wrap>
          <ElButton @click="goBack">返回数据表</ElButton>
          <ElButton type="primary" :loading="loading" @click="loadAnalysis">刷新分析</ElButton>
        </ElSpace>
      </div>
    </div>

    <div class="art-card filter-card">
      <ElForm :model="queryForm" inline label-width="88px" class="filter-form">
        <ElFormItem label="性别">
          <ElSelect v-model="queryForm.gender" placeholder="全部性别" clearable style="width: 140px">
            <ElOption v-for="item in options.genders" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="城市等级">
          <ElSelect v-model="queryForm.cityTier" placeholder="全部城市" clearable style="width: 160px">
            <ElOption v-for="item in options.cityTiers" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="购物偏好">
          <ElSelect v-model="queryForm.shoppingPreference" placeholder="全部偏好" clearable style="width: 160px">
            <ElOption
              v-for="item in options.shoppingPreferences"
              :key="item"
              :label="formatPreference(item)"
              :value="item"
            />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="年龄范围">
          <div class="range-input">
            <ElInputNumber v-model="queryForm.ageMin" :min="0" :controls="false" placeholder="最小" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.ageMax" :min="0" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem label="收入范围">
          <div class="range-input range-input--wide">
            <ElInputNumber v-model="queryForm.incomeMin" :min="0" :controls="false" placeholder="最小" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.incomeMax" :min="0" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem>
          <ElSpace wrap>
            <ElButton type="primary" :loading="loading" @click="loadAnalysis">开始分析</ElButton>
            <ElButton @click="resetQuery">重置</ElButton>
          </ElSpace>
        </ElFormItem>
      </ElForm>
    </div>

    <div class="summary-grid" v-loading="loading" element-loading-text="正在计算核心指标...">
      <div v-for="item in summaryCards" :key="item.label" class="art-card stat-card">
        <div class="stat-card__label">{{ item.label }}</div>
        <div class="stat-card__value">{{ item.value }}</div>
        <div class="stat-card__extra">{{ item.extra }}</div>
      </div>
    </div>

    <div class="analysis-layout" v-loading="loading" element-loading-text="正在渲染可视化图表...">
      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">购物偏好占比</div>
            <div class="chart-subtitle">识别线上、线下与混合消费群体结构</div>
          </div>
        </div>
        <div ref="preferencePieRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">城市等级消费活跃度</div>
            <div class="chart-subtitle">对比不同城市等级的线上订单、门店访问和线上偏好率</div>
          </div>
        </div>
        <div ref="cityTierBarRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">年龄层购物偏好分布</div>
            <div class="chart-subtitle">观察不同年龄段的线上、线下、混合购物偏好变化</div>
          </div>
        </div>
        <div ref="ageStackRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">收入层客单价与线上倾向</div>
            <div class="chart-subtitle">判断高价值消费群体集中在哪些收入区间</div>
          </div>
        </div>
        <div ref="incomeSpendRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">不同购物偏好人群画像雷达图</div>
            <div class="chart-subtitle">综合比较信任、技术、价格敏感、品牌与环保等评分</div>
          </div>
        </div>
        <div ref="radarRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">技术熟练度与线上订单关系</div>
            <div class="chart-subtitle">散点越靠右上，说明越可能形成高频线上消费</div>
          </div>
        </div>
        <div ref="scatterRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">配送与售后体验影响</div>
            <div class="chart-subtitle">比较配送时效、配送费敏感、免费退货重视和退货频率</div>
          </div>
        </div>
        <div ref="deliveryRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">性别样本结构</div>
            <div class="chart-subtitle">辅助判断样本分布是否均衡</div>
          </div>
        </div>
        <div ref="genderPieRef" class="chart-box"></div>
      </div>
    </div>

    <div class="insight-section art-card" v-loading="loading" element-loading-text="正在生成数据解读...">
      <div class="insight-header">
        <div>
          <div class="chart-title">数据规律与业务价值解读</div>
          <div class="chart-subtitle">根据当前筛选条件自动总结可落地的运营启发</div>
        </div>
      </div>

      <div v-if="insightList.length" class="insight-list">
        <div v-for="(item, index) in insightList" :key="index" class="insight-item">
          <span class="insight-index">{{ index + 1 }}</span>
          <span class="insight-text">{{ item }}</span>
        </div>
      </div>

      <ElEmpty v-else description="暂无可解读的数据" />
    </div>
  </div>
</template>

<script setup lang="ts">
import * as echarts from 'echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  fetchConsumerShoppingAnalysis,
  fetchConsumerShoppingOptions,
  type ConsumerShoppingAnalysisData,
  type ConsumerShoppingTrendOptions,
  type ConsumerShoppingTrendQuery
} from '@/api/consumer-shopping'

const router = useRouter()
const loading = ref(false)

const options = reactive<ConsumerShoppingTrendOptions>({
  genders: [],
  cityTiers: [],
  shoppingPreferences: []
})

const queryForm = reactive<ConsumerShoppingTrendQuery>({
  gender: '',
  cityTier: '',
  shoppingPreference: '',
  ageMin: undefined,
  ageMax: undefined,
  incomeMin: undefined,
  incomeMax: undefined,
  ordersMin: undefined,
  ordersMax: undefined
})

const emptyAnalysis = (): ConsumerShoppingAnalysisData => ({
  summary: {},
  preferenceDistribution: [],
  genderDistribution: [],
  cityTierDistribution: [],
  ageSegmentPreference: [],
  incomeSegmentStats: [],
  cityTierPreferenceStats: [],
  radarByPreference: [],
  deliveryExperienceStats: [],
  techOrderScatter: [],
  insights: []
})

const analysisData = ref<ConsumerShoppingAnalysisData>(emptyAnalysis())

const preferencePieRef = ref<HTMLDivElement | null>(null)
const cityTierBarRef = ref<HTMLDivElement | null>(null)
const ageStackRef = ref<HTMLDivElement | null>(null)
const incomeSpendRef = ref<HTMLDivElement | null>(null)
const radarRef = ref<HTMLDivElement | null>(null)
const scatterRef = ref<HTMLDivElement | null>(null)
const deliveryRef = ref<HTMLDivElement | null>(null)
const genderPieRef = ref<HTMLDivElement | null>(null)

let preferencePieChart: echarts.ECharts | null = null
let cityTierBarChart: echarts.ECharts | null = null
let ageStackChart: echarts.ECharts | null = null
let incomeSpendChart: echarts.ECharts | null = null
let radarChart: echarts.ECharts | null = null
let scatterChart: echarts.ECharts | null = null
let deliveryChart: echarts.ECharts | null = null
let genderPieChart: echarts.ECharts | null = null

const summary = computed(() => analysisData.value.summary || {})
const insightList = computed(() => analysisData.value.insights || [])

const summaryCards = computed(() => [
  {
    label: '样本总量',
    value: numberText(summary.value.totalCount),
    extra: '当前筛选条件下的消费者记录数'
  },
  {
    label: '平均月收入',
    value: moneyText(summary.value.avgIncome),
    extra: '用于判断消费能力与客单价空间'
  },
  {
    label: '线上/混合偏好率',
    value: `${numberText(summary.value.onlinePreferenceRate)}%`,
    extra: 'Online 与 Both 用户占比'
  },
  {
    label: '平均线上订单/月',
    value: numberText(summary.value.avgOnlineOrders),
    extra: '衡量线上购买频次'
  },
  {
    label: '平均门店访问/月',
    value: numberText(summary.value.avgStoreVisits),
    extra: '衡量线下触点活跃度'
  },
  {
    label: '线上/线下均消',
    value: `${moneyText(summary.value.avgOnlineSpend)} / ${moneyText(summary.value.avgStoreSpend)}`,
    extra: '对比线上线下客单价差异'
  }
])

async function loadOptions() {
  const res = await fetchConsumerShoppingOptions()
  options.genders = res.genders || []
  options.cityTiers = res.cityTiers || []
  options.shoppingPreferences = res.shoppingPreferences || []
}

async function loadAnalysis() {
  try {
    loading.value = true
    const res = await fetchConsumerShoppingAnalysis({ ...queryForm })
    analysisData.value = res || emptyAnalysis()

    await nextTick()
    renderAllCharts()
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryForm.gender = ''
  queryForm.cityTier = ''
  queryForm.shoppingPreference = ''
  queryForm.ageMin = undefined
  queryForm.ageMax = undefined
  queryForm.incomeMin = undefined
  queryForm.incomeMax = undefined
  queryForm.ordersMin = undefined
  queryForm.ordersMax = undefined
  loadAnalysis()
}

function goBack() {
  router.push('/source/comsumer')
}

function initChart(dom: HTMLDivElement | null, oldChart: echarts.ECharts | null) {
  if (!dom) return null
  if (oldChart) oldChart.dispose()
  return echarts.init(dom)
}

function renderAllCharts() {
  renderPreferencePie()
  renderCityTierBar()
  renderAgeStack()
  renderIncomeSpend()
  renderRadar()
  renderScatter()
  renderDelivery()
  renderGenderPie()
}

function renderPreferencePie() {
  preferencePieChart = initChart(preferencePieRef.value, preferencePieChart)
  if (!preferencePieChart) return

  const data = analysisData.value.preferenceDistribution.map((item) => ({
    name: formatPreference(item.name),
    value: toNumber(item.value)
  }))

  preferencePieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}<br/>数量：{c}<br/>占比：{d}%' },
    legend: { bottom: 0, icon: 'circle' },
    series: [
      {
        name: '购物偏好',
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: true,
        label: { formatter: '{b}\n{d}%' },
        data
      }
    ]
  })
}

function renderGenderPie() {
  genderPieChart = initChart(genderPieRef.value, genderPieChart)
  if (!genderPieChart) return

  const data = analysisData.value.genderDistribution.map((item) => ({
    name: formatGender(item.name),
    value: toNumber(item.value)
  }))

  genderPieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}<br/>数量：{c}<br/>占比：{d}%' },
    legend: { bottom: 0, icon: 'circle' },
    series: [
      {
        name: '性别结构',
        type: 'pie',
        radius: ['38%', '68%'],
        center: ['50%', '45%'],
        label: { formatter: '{b}\n{d}%' },
        data
      }
    ]
  })
}

function renderCityTierBar() {
  cityTierBarChart = initChart(cityTierBarRef.value, cityTierBarChart)
  if (!cityTierBarChart) return

  const list = analysisData.value.cityTierPreferenceStats
  const names = list.map((item) => formatUnknown(item.cityTier))

  cityTierBarChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: 48, right: 42, top: 48, bottom: 40 },
    xAxis: { type: 'category', data: names, axisLabel: { interval: 0 } },
    yAxis: [
      { type: 'value', name: '频次' },
      { type: 'value', name: '线上偏好率', axisLabel: { formatter: '{value}%' } }
    ],
    series: [
      {
        name: '线上订单/月',
        type: 'bar',
        barMaxWidth: 22,
        data: list.map((item) => toNumber(item.avgOnlineOrders))
      },
      {
        name: '门店访问/月',
        type: 'bar',
        barMaxWidth: 22,
        data: list.map((item) => toNumber(item.avgStoreVisits))
      },
      {
        name: '线上偏好率',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.onlineRate))
      }
    ]
  })
}

function renderAgeStack() {
  ageStackChart = initChart(ageStackRef.value, ageStackChart)
  if (!ageStackChart) return

  const list = analysisData.value.ageSegmentPreference
  const names = list.map((item) => item.ageSegment)

  ageStackChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { top: 0 },
    grid: { left: 48, right: 36, top: 48, bottom: 36 },
    xAxis: { type: 'category', data: names },
    yAxis: { type: 'value', name: '人数' },
    series: [
      {
        name: '线上购物',
        type: 'bar',
        stack: 'preference',
        barMaxWidth: 32,
        data: list.map((item) => toNumber(item.onlineCount))
      },
      {
        name: '线下购物',
        type: 'bar',
        stack: 'preference',
        barMaxWidth: 32,
        data: list.map((item) => toNumber(item.offlineCount))
      },
      {
        name: '混合购物',
        type: 'bar',
        stack: 'preference',
        barMaxWidth: 32,
        data: list.map((item) => toNumber(item.bothCount))
      },
      {
        name: '线上/混合偏好率',
        type: 'line',
        smooth: true,
        yAxisIndex: 0,
        data: list.map((item) => toNumber(item.onlineRate))
      }
    ]
  })
}

function renderIncomeSpend() {
  incomeSpendChart = initChart(incomeSpendRef.value, incomeSpendChart)
  if (!incomeSpendChart) return

  const list = analysisData.value.incomeSegmentStats
  const names = list.map((item) => item.incomeSegment)

  incomeSpendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: 56, right: 44, top: 48, bottom: 42 },
    xAxis: { type: 'category', data: names, axisLabel: { interval: 0, rotate: 20 } },
    yAxis: [
      { type: 'value', name: '均消' },
      { type: 'value', name: '线上偏好率', axisLabel: { formatter: '{value}%' } }
    ],
    series: [
      {
        name: '线上均消',
        type: 'bar',
        barMaxWidth: 24,
        data: list.map((item) => toNumber(item.avgOnlineSpend))
      },
      {
        name: '线下均消',
        type: 'bar',
        barMaxWidth: 24,
        data: list.map((item) => toNumber(item.avgStoreSpend))
      },
      {
        name: '线上偏好率',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.onlineRate))
      }
    ]
  })
}

function renderRadar() {
  radarChart = initChart(radarRef.value, radarChart)
  if (!radarChart) return

  const indicators = [
    { name: '支付信任', max: 10 },
    { name: '技术熟练', max: 10 },
    { name: '折扣敏感', max: 10 },
    { name: '免费退货', max: 10 },
    { name: '线上可得', max: 10 },
    { name: '冲动购买', max: 10 },
    { name: '品牌忠诚', max: 10 },
    { name: '环保意识', max: 10 }
  ]

  const data = analysisData.value.radarByPreference.map((item) => ({
    name: formatPreference(item.preference),
    value: [
      item.onlinePaymentTrustScore,
      item.techSavvyScore,
      item.discountSensitivity,
      item.freeReturnImportance,
      item.productAvailabilityOnline,
      item.impulseBuyingScore,
      item.brandLoyaltyScore,
      item.environmentalAwareness
    ].map(toNumber)
  }))

  radarChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, icon: 'circle' },
    radar: {
      indicator: indicators,
      radius: '62%',
      center: ['50%', '45%']
    },
    series: [
      {
        name: '画像评分',
        type: 'radar',
        areaStyle: { opacity: 0.12 },
        data
      }
    ]
  })
}

function renderScatter() {
  scatterChart = initChart(scatterRef.value, scatterChart)
  if (!scatterChart) return

  const data = analysisData.value.techOrderScatter
    .filter((item) => item.techSavvyScore !== null && item.monthlyOnlineOrders !== null)
    .map((item) => [
      toNumber(item.techSavvyScore),
      toNumber(item.monthlyOnlineOrders),
      toNumber(item.monthlyIncome),
      toNumber(item.avgOnlineSpend),
      formatPreference(item.shoppingPreference),
      formatUnknown(item.cityTier)
    ])

  scatterChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const d = params.data || []
        return `
          <div style="line-height: 1.8;">
            <div>技术熟练度：${d[0]}</div>
            <div>线上订单/月：${d[1]}</div>
            <div>月收入：${moneyText(d[2])}</div>
            <div>线上均消：${moneyText(d[3])}</div>
            <div>偏好：${d[4]}</div>
            <div>城市等级：${d[5]}</div>
          </div>
        `
      }
    },
    grid: { left: 52, right: 28, top: 28, bottom: 46 },
    xAxis: { type: 'value', name: '技术熟练度', min: 0, max: 10 },
    yAxis: { type: 'value', name: '线上订单/月' },
    series: [
      {
        name: '消费者样本',
        type: 'scatter',
        data,
        symbolSize: (value: number[]) => Math.max(8, Math.min(28, 8 + Number(value[2] || 0) / 3000)),
        emphasis: { focus: 'series' }
      }
    ]
  })
}

function renderDelivery() {
  deliveryChart = initChart(deliveryRef.value, deliveryChart)
  if (!deliveryChart) return

  const list = analysisData.value.deliveryExperienceStats
  const names = list.map((item) => formatPreference(item.preference))

  deliveryChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: 48, right: 36, top: 48, bottom: 38 },
    xAxis: { type: 'category', data: names },
    yAxis: { type: 'value', name: '评分/天数' },
    series: [
      {
        name: '平均配送天数',
        type: 'bar',
        barMaxWidth: 22,
        data: list.map((item) => toNumber(item.avgDeliveryDays))
      },
      {
        name: '配送费敏感',
        type: 'line',
        smooth: true,
        data: list.map((item) => toNumber(item.deliveryFeeSensitivity))
      },
      {
        name: '免费退货重视',
        type: 'line',
        smooth: true,
        data: list.map((item) => toNumber(item.freeReturnImportance))
      },
      {
        name: '退货频率',
        type: 'line',
        smooth: true,
        data: list.map((item) => toNumber(item.returnFrequency))
      }
    ]
  })
}

function resizeCharts() {
  preferencePieChart?.resize()
  cityTierBarChart?.resize()
  ageStackChart?.resize()
  incomeSpendChart?.resize()
  radarChart?.resize()
  scatterChart?.resize()
  deliveryChart?.resize()
  genderPieChart?.resize()
}

function disposeCharts() {
  preferencePieChart?.dispose()
  cityTierBarChart?.dispose()
  ageStackChart?.dispose()
  incomeSpendChart?.dispose()
  radarChart?.dispose()
  scatterChart?.dispose()
  deliveryChart?.dispose()
  genderPieChart?.dispose()
}

function toNumber(value: unknown) {
  const num = Number(value ?? 0)
  return Number.isFinite(num) ? num : 0
}

function numberText(value: unknown) {
  const num = toNumber(value)
  return num.toLocaleString('zh-CN', {
    minimumFractionDigits: Number.isInteger(num) ? 0 : 2,
    maximumFractionDigits: 2
  })
}

function moneyText(value: unknown) {
  return toNumber(value).toLocaleString('zh-CN', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2
  })
}

function formatPreference(value?: string) {
  const map: Record<string, string> = {
    Online: '线上购物',
    Offline: '线下购物',
    Both: '混合购物',
    Unknown: '未知'
  }
  return map[value || ''] || value || '未知'
}

function formatGender(value?: string) {
  const map: Record<string, string> = {
    Male: '男性',
    Female: '女性',
    Unknown: '未知'
  }
  return map[value || ''] || value || '未知'
}

function formatUnknown(value?: string) {
  if (!value || value === 'Unknown') return '未知'
  return value
}

onMounted(async () => {
  await loadOptions()
  await loadAnalysis()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  disposeCharts()
})
</script>

<style scoped lang="scss">
.consumer-analysis-page {
  .art-card {
    background: #fff;
  }
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 22px 24px;
  margin-bottom: 20px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--art-text-gray-900);
  line-height: 1.2;
}

.page-desc {
  max-width: 900px;
  margin-top: 8px;
  font-size: 14px;
  color: var(--art-text-gray-600);
  line-height: 1.8;
}

.filter-card {
  padding: 18px 20px 2px;
  margin-bottom: 20px;
}

.filter-form {
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

.summary-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  min-height: 126px;
  padding: 18px;
  margin-bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-card__label {
  font-size: 13px;
  color: var(--art-text-gray-600);
}

.stat-card__value {
  margin-top: 10px;
  font-size: 26px;
  font-weight: 700;
  color: var(--art-text-gray-900);
  line-height: 1.15;
}

.stat-card__extra {
  margin-top: 10px;
  font-size: 12px;
  color: var(--art-text-gray-500);
  line-height: 1.6;
}

.analysis-layout {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.chart-card {
  padding: 20px;
  min-width: 0;
}

.chart-card--wide {
  grid-column: span 2;
}

.chart-header,
.insight-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--art-text-gray-900);
}

.chart-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: var(--art-text-gray-500);
  line-height: 1.6;
}

.chart-box {
  width: 100%;
  height: 360px;
}

.chart-box--wide {
  height: 390px;
}

.insight-section {
  padding: 22px 24px;
  margin-top: 20px;
}

.insight-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.insight-item {
  display: flex;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid var(--art-border-color);
  border-radius: 14px;
  background: #fafafa;
}

.insight-index {
  flex: 0 0 auto;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  background: var(--el-color-primary);
}

.insight-text {
  font-size: 14px;
  color: var(--art-text-gray-700);
  line-height: 1.8;
}

@media (max-width: 1400px) {
  .summary-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 992px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .summary-grid,
  .analysis-layout,
  .insight-list {
    grid-template-columns: 1fr;
  }

  .chart-card--wide {
    grid-column: span 1;
  }
}
</style>
