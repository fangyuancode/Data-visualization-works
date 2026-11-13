<template>
  <div class="world-analysis-page">
    <div class="page-header art-card">
      <div class="page-header__left">
        <div class="page-title">世界幸福报告数据可视化分析</div>
        <div class="page-desc">
          围绕国家/地区、年份、年度排名、幸福分数和 GDP、社会支持、健康寿命、自由度、慷慨程度、腐败感知等解释性指标进行数据挖掘，识别幸福水平差异和影响因素结构。
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
      <ElForm :model="queryForm" inline label-width="90px" class="filter-form">
        <ElFormItem label="年份">
          <ElSelect v-model="queryForm.year" placeholder="全部年份" clearable filterable style="width: 140px">
            <ElOption v-for="item in options.years" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="国家/地区">
          <ElSelect v-model="queryForm.country" placeholder="全部国家" clearable filterable style="width: 180px">
            <ElOption v-for="item in options.countries" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="幸福分数">
          <div class="range-input">
            <ElInputNumber v-model="queryForm.scoreMin" :min="0" :max="10" :precision="2" :controls="false" placeholder="最低" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.scoreMax" :min="0" :max="10" :precision="2" :controls="false" placeholder="最高" />
          </div>
        </ElFormItem>

        <ElFormItem label="年度排名">
          <div class="range-input">
            <ElInputNumber v-model="queryForm.rankMin" :min="1" :controls="false" placeholder="最小" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.rankMax" :min="1" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem label="完整数据">
          <ElSwitch v-model="queryForm.completeOnly" active-text="仅完整" inactive-text="全部" />
        </ElFormItem>

        <ElFormItem>
          <ElSpace wrap>
            <ElButton type="primary" :loading="loading" @click="loadAnalysis">开始分析</ElButton>
            <ElButton @click="resetQuery">重置</ElButton>
          </ElSpace>
        </ElFormItem>
      </ElForm>
    </div>

    <div class="summary-grid" v-loading="loading" element-loading-text="正在计算世界幸福报告核心指标...">
      <div v-for="item in summaryCards" :key="item.label" class="art-card stat-card">
        <div class="stat-card__label">{{ item.label }}</div>
        <div class="stat-card__value">{{ item.value }}</div>
        <div class="stat-card__extra">{{ item.extra }}</div>
      </div>
    </div>

    <div class="analysis-layout" v-loading="loading" element-loading-text="正在渲染幸福报告可视化图表...">
      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">幸福分数分层结构</div>
            <div class="chart-subtitle">观察不同幸福指数区间的国家/地区数量占比</div>
          </div>
        </div>
        <div ref="scorePieRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">解释性指标平均贡献</div>
            <div class="chart-subtitle">比较 GDP、社会支持、健康寿命、自由等因素的平均解释值</div>
          </div>
        </div>
        <div ref="factorBarRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">年份趋势：幸福分数与样本量</div>
            <div class="chart-subtitle">按年份观察平均幸福分数、样本量和主要解释性指标变化</div>
          </div>
        </div>
        <div ref="yearTrendRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">年度排名梯队对比</div>
            <div class="chart-subtitle">对比 TOP10、11-30名、31-60名等排名区间的幸福分数和样本量</div>
          </div>
        </div>
        <div ref="rankBarRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">国家/地区幸福分数 TOP15</div>
            <div class="chart-subtitle">定位当前筛选条件下幸福分数最高的国家/地区</div>
          </div>
        </div>
        <div ref="topCountryRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">国家多年平均表现排行</div>
            <div class="chart-subtitle">按国家/地区聚合平均幸福分数、平均排名和样本量</div>
          </div>
        </div>
        <div ref="countryAverageRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">幸福水平人群画像雷达图</div>
            <div class="chart-subtitle">对比低幸福组、中等幸福组、高幸福组在核心因素上的结构差异</div>
          </div>
        </div>
        <div ref="radarRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">GDP解释值与幸福分数散点</div>
            <div class="chart-subtitle">横轴为人均 GDP 解释值，纵轴为幸福分数，可观察经济因素与幸福水平的关系</div>
          </div>
        </div>
        <div ref="scatterRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">年份 × 幸福分数区间热力矩阵</div>
            <div class="chart-subtitle">识别不同年份中幸福分数区间的集中程度</div>
          </div>
        </div>
        <div ref="heatmapRef" class="chart-box chart-box--wide"></div>
      </div>
    </div>

    <div class="insight-section art-card" v-loading="loading" element-loading-text="正在生成世界幸福报告数据规律解读...">
      <div class="insight-header">
        <div>
          <div class="chart-title">数据规律与价值解读</div>
          <div class="chart-subtitle">结合当前筛选条件自动总结世界幸福报告数据规律，可用于数据分析报告、论文说明或系统展示文案</div>
        </div>
      </div>

      <div v-if="insightList.length" class="insight-list">
        <div v-for="(item, index) in insightList" :key="index" class="insight-item">
          <div class="insight-index">{{ index + 1 }}</div>
          <div class="insight-text">{{ item }}</div>
        </div>
      </div>
      <ElEmpty v-else description="暂无可解读的数据规律" />
    </div>
  </div>
</template>

<script setup lang="ts">
import * as echarts from 'echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchWorldHappinessAnalysis, fetchWorldHappinessOptions } from '@/api/world-happiness'
import type { WorldHappinessAnalysisItem, WorldHappinessAnalysisResult, WorldHappinessReportOptions, WorldHappinessReportQuery } from '@/api/world-happiness'

defineOptions({ name: 'WorldHappinessAnalysis' })

const router = useRouter()
const loading = ref(false)
const chartMap = new Map<string, echarts.ECharts>()

const scorePieRef = ref<HTMLElement | null>(null)
const factorBarRef = ref<HTMLElement | null>(null)
const yearTrendRef = ref<HTMLElement | null>(null)
const rankBarRef = ref<HTMLElement | null>(null)
const topCountryRef = ref<HTMLElement | null>(null)
const countryAverageRef = ref<HTMLElement | null>(null)
const radarRef = ref<HTMLElement | null>(null)
const scatterRef = ref<HTMLElement | null>(null)
const heatmapRef = ref<HTMLElement | null>(null)

const options = reactive<WorldHappinessReportOptions>({
  years: [],
  countries: []
})

const queryForm = reactive<WorldHappinessReportQuery>({
  year: undefined,
  country: '',
  rankMin: undefined,
  rankMax: undefined,
  scoreMin: undefined,
  scoreMax: undefined,
  completeOnly: true
})

const analysisData = ref<WorldHappinessAnalysisResult>({
  summary: {},
  scoreSegmentStats: [],
  rankSegmentStats: [],
  yearTrend: [],
  factorContribution: [],
  topCountryRank: [],
  countryAverageRank: [],
  factorRadarByScoreSegment: [],
  scoreFactorScatter: [],
  yearScoreHeatmap: [],
  insights: []
})

const summaryCards = computed(() => {
  const summary = analysisData.value.summary || {}
  return [
    {
      label: '分析样本量',
      value: formatInteger(summary.totalCount),
      extra: '当前筛选条件下的记录数量'
    },
    {
      label: '平均幸福分数',
      value: formatNumber(summary.avgScore, 2),
      extra: `最高 ${formatNumber(summary.maxScore, 2)} / 最低 ${formatNumber(summary.minScore, 2)}`
    },
    {
      label: '平均年度排名',
      value: formatNumber(summary.avgRank, 1),
      extra: '数值越小代表整体排名越靠前'
    },
    {
      label: '完整数据率',
      value: `${formatNumber(summary.completeRate, 1)}%`,
      extra: `完整记录 ${formatInteger(summary.completeCount)} 条`
    },
    {
      label: 'GDP解释值',
      value: formatNumber(summary.avgGdp, 2),
      extra: '经济水平相关贡献'
    },
    {
      label: '社会支持',
      value: formatNumber(summary.avgSocialSupport, 2),
      extra: '社会关系与支持体系'
    },
    {
      label: '健康寿命',
      value: formatNumber(summary.avgHealthyLife, 2),
      extra: '健康预期寿命解释值'
    },
    {
      label: '自由度',
      value: formatNumber(summary.avgFreedom, 2),
      extra: '个体选择自由解释值'
    }
  ]
})

const insightList = computed(() => analysisData.value.insights || [])

const goBack = () => {
  router.push('/source/world')
}

const loadOptions = async () => {
  const res = await fetchWorldHappinessOptions()
  options.years = res.years || []
  options.countries = res.countries || []
}

const loadAnalysis = async () => {
  loading.value = true
  try {
    const res = await fetchWorldHappinessAnalysis({ ...queryForm })
    analysisData.value = {
      summary: res.summary || {},
      scoreSegmentStats: res.scoreSegmentStats || [],
      rankSegmentStats: res.rankSegmentStats || [],
      yearTrend: res.yearTrend || [],
      factorContribution: res.factorContribution || [],
      topCountryRank: res.topCountryRank || [],
      countryAverageRank: res.countryAverageRank || [],
      factorRadarByScoreSegment: res.factorRadarByScoreSegment || [],
      scoreFactorScatter: res.scoreFactorScatter || [],
      yearScoreHeatmap: res.yearScoreHeatmap || [],
      insights: res.insights || []
    }

    await nextTick()
    renderCharts()
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.year = undefined
  queryForm.country = ''
  queryForm.rankMin = undefined
  queryForm.rankMax = undefined
  queryForm.scoreMin = undefined
  queryForm.scoreMax = undefined
  queryForm.completeOnly = true
  loadAnalysis()
}

const renderCharts = () => {
  renderScorePie()
  renderFactorBar()
  renderYearTrend()
  renderRankBar()
  renderTopCountry()
  renderCountryAverage()
  renderRadar()
  renderScatter()
  renderHeatmap()
}

const getChart = (key: string, el: HTMLElement | null) => {
  if (!el) return null
  const cached = chartMap.get(key)
  if (cached) return cached

  const chart = echarts.init(el)
  chartMap.set(key, chart)
  return chart
}

const renderScorePie = () => {
  const chart = getChart('scorePie', scorePieRef.value)
  if (!chart) return

  const data = analysisData.value.scoreSegmentStats || []
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, type: 'scroll' },
    series: [
      {
        name: '幸福分数区间',
        type: 'pie',
        radius: ['38%', '68%'],
        center: ['50%', '46%'],
        roseType: 'radius',
        label: { formatter: '{b}\n{d}%' },
        data: data.map((item) => ({
          name: item.segmentName || '未知',
          value: numberValue(item.totalCount)
        }))
      }
    ]
  })
}

const renderFactorBar = () => {
  const chart = getChart('factorBar', factorBarRef.value)
  if (!chart) return

  const data = analysisData.value.factorContribution || []
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { top: 28, right: 24, bottom: 56, left: 52 },
    xAxis: {
      type: 'category',
      data: data.map((item) => item.factorName || '未知'),
      axisLabel: { interval: 0, rotate: 28 }
    },
    yAxis: { type: 'value', name: '平均解释值' },
    series: [
      {
        name: '平均解释值',
        type: 'bar',
        barMaxWidth: 34,
        data: data.map((item) => numberValue(item.avgValue))
      }
    ]
  })
}

const renderYearTrend = () => {
  const chart = getChart('yearTrend', yearTrendRef.value)
  if (!chart) return

  const data = analysisData.value.yearTrend || []
  const years = data.map((item) => String(item.yearName || '未知'))
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { top: 48, right: 52, bottom: 36, left: 52 },
    xAxis: { type: 'category', data: years },
    yAxis: [
      { type: 'value', name: '平均分' },
      { type: 'value', name: '样本量' }
    ],
    series: [
      {
        name: '平均幸福分数',
        type: 'line',
        smooth: true,
        data: data.map((item) => numberValue(item.avgScore))
      },
      {
        name: '平均GDP解释值',
        type: 'line',
        smooth: true,
        data: data.map((item) => numberValue(item.avgGdp))
      },
      {
        name: '样本量',
        type: 'bar',
        yAxisIndex: 1,
        barMaxWidth: 28,
        data: data.map((item) => numberValue(item.totalCount))
      }
    ]
  })
}

const renderRankBar = () => {
  const chart = getChart('rankBar', rankBarRef.value)
  if (!chart) return

  const data = analysisData.value.rankSegmentStats || []
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { top: 46, right: 42, bottom: 36, left: 52 },
    xAxis: { type: 'category', data: data.map((item) => item.segmentName || '未知') },
    yAxis: [
      { type: 'value', name: '幸福分数' },
      { type: 'value', name: '数量' }
    ],
    series: [
      {
        name: '平均幸福分数',
        type: 'bar',
        barMaxWidth: 30,
        data: data.map((item) => numberValue(item.avgScore))
      },
      {
        name: '样本量',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: data.map((item) => numberValue(item.totalCount))
      }
    ]
  })
}

const renderTopCountry = () => {
  const chart = getChart('topCountry', topCountryRef.value)
  if (!chart) return

  const data = [...(analysisData.value.topCountryRank || [])].reverse()
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { top: 24, right: 34, bottom: 28, left: 120 },
    xAxis: { type: 'value', name: '幸福分数' },
    yAxis: {
      type: 'category',
      data: data.map((item) => `${item.country || '未知'}(${item.year || '-'})`)
    },
    series: [
      {
        name: '幸福分数',
        type: 'bar',
        barMaxWidth: 24,
        label: { show: true, position: 'right', formatter: '{@score}' },
        encode: { x: 'score', y: 'country' },
        data: data.map((item) => ({ value: numberValue(item.happinessScore), score: formatNumber(item.happinessScore, 2) }))
      }
    ]
  })
}

const renderCountryAverage = () => {
  const chart = getChart('countryAverage', countryAverageRef.value)
  if (!chart) return

  const data = [...(analysisData.value.countryAverageRank || [])].reverse()
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { top: 48, right: 58, bottom: 30, left: 130 },
    xAxis: [
      { type: 'value', name: '平均幸福分数' },
      { type: 'value', name: '样本量', show: false }
    ],
    yAxis: { type: 'category', data: data.map((item) => item.country || '未知') },
    series: [
      {
        name: '平均幸福分数',
        type: 'bar',
        barMaxWidth: 24,
        data: data.map((item) => numberValue(item.avgScore))
      },
      {
        name: '样本量',
        type: 'scatter',
        xAxisIndex: 1,
        symbolSize: (value: number) => Math.max(8, Math.min(26, Number(value) * 2)),
        data: data.map((item) => numberValue(item.totalCount))
      }
    ]
  })
}

const renderRadar = () => {
  const chart = getChart('radar', radarRef.value)
  if (!chart) return

  const data = analysisData.value.factorRadarByScoreSegment || []
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    radar: {
      radius: '62%',
      center: ['50%', '46%'],
      indicator: [
        { name: 'GDP', max: getRadarMax(data, 'avgGdp') },
        { name: '社会支持', max: getRadarMax(data, 'avgSocialSupport') },
        { name: '健康寿命', max: getRadarMax(data, 'avgHealthyLife') },
        { name: '自由度', max: getRadarMax(data, 'avgFreedom') },
        { name: '慷慨', max: getRadarMax(data, 'avgGenerosity') },
        { name: '腐败感知', max: getRadarMax(data, 'avgCorruption') },
        { name: '残差', max: getRadarMax(data, 'avgResidual') }
      ]
    },
    series: [
      {
        name: '幸福水平画像',
        type: 'radar',
        data: data.map((item) => ({
          name: item.segmentName || '未知',
          value: [
            numberValue(item.avgGdp),
            numberValue(item.avgSocialSupport),
            numberValue(item.avgHealthyLife),
            numberValue(item.avgFreedom),
            numberValue(item.avgGenerosity),
            numberValue(item.avgCorruption),
            numberValue(item.avgResidual)
          ]
        }))
      }
    ]
  })
}

const renderScatter = () => {
  const chart = getChart('scatter', scatterRef.value)
  if (!chart) return

  const data = analysisData.value.scoreFactorScatter || []
  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const raw = params.data?.raw || {}
        return `${raw.country || '未知'} / ${raw.year || '-'}<br/>GDP解释值：${formatNumber(raw.gdp, 3)}<br/>幸福分数：${formatNumber(raw.happinessScore, 3)}<br/>排名：${raw.rankInYear || '-'}`
      }
    },
    grid: { top: 28, right: 26, bottom: 42, left: 52 },
    xAxis: { type: 'value', name: 'GDP解释值' },
    yAxis: { type: 'value', name: '幸福分数' },
    series: [
      {
        name: '国家样本',
        type: 'scatter',
        symbolSize: (value: number[]) => Math.max(8, Math.min(26, Number(value?.[2] || 8))),
        data: data.map((item) => ({
          value: [numberValue(item.gdp), numberValue(item.happinessScore), 100 / Math.max(1, numberValue(item.rankInYear))],
          raw: item
        }))
      }
    ]
  })
}

const renderHeatmap = () => {
  const chart = getChart('heatmap', heatmapRef.value)
  if (!chart) return

  const data = analysisData.value.yearScoreHeatmap || []
  const years = unique(data.map((item) => String(item.yearName || '未知')))
  const segments = ['4分以下', '4-5分', '5-6分', '6-7分', '7分及以上']
  const values = data.map((item) => [
    years.indexOf(String(item.yearName || '未知')),
    segments.indexOf(String(item.segmentName || '未知')),
    numberValue(item.totalCount)
  ]).filter((item) => item[0] >= 0 && item[1] >= 0)

  chart.setOption({
    tooltip: { position: 'top' },
    grid: { top: 40, right: 28, bottom: 64, left: 82 },
    xAxis: { type: 'category', data: years, axisLabel: { rotate: years.length > 8 ? 35 : 0 } },
    yAxis: { type: 'category', data: segments },
    visualMap: {
      min: 0,
      max: Math.max(1, ...values.map((item) => Number(item[2]))),
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: 4
    },
    series: [
      {
        name: '样本数量',
        type: 'heatmap',
        data: values,
        label: { show: true },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowColor: 'rgba(0, 0, 0, 0.2)'
          }
        }
      }
    ]
  })
}

const resizeCharts = () => {
  chartMap.forEach((chart) => chart.resize())
}

const numberValue = (value: unknown) => {
  const num = Number(value)
  return Number.isFinite(num) ? num : 0
}

const formatNumber = (value: unknown, precision = 2) => {
  const num = Number(value)
  if (!Number.isFinite(num)) return '0.00'
  return num.toFixed(precision)
}

const formatInteger = (value: unknown) => {
  const num = Number(value)
  if (!Number.isFinite(num)) return '0'
  return Math.round(num).toLocaleString()
}

const getRadarMax = (list: WorldHappinessAnalysisItem[], key: string) => {
  const max = Math.max(1, ...list.map((item) => numberValue(item[key])))
  return Math.ceil(max * 1.25 * 10) / 10
}

const unique = (list: string[]) => Array.from(new Set(list.filter(Boolean)))

onMounted(async () => {
  await loadOptions()
  await loadAnalysis()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  chartMap.forEach((chart) => chart.dispose())
  chartMap.clear()
})
</script>

<style scoped lang="scss">
.world-analysis-page {
  min-height: 100%;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 24px;
  margin-bottom: 20px;
}

.page-header__left {
  min-width: 0;
}

.page-header__right {
  flex-shrink: 0;
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--art-text-gray-900, #111827);
  line-height: 1.2;
}

.page-desc {
  max-width: 960px;
  margin-top: 10px;
  font-size: 14px;
  color: var(--art-text-gray-600, #667085);
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
  gap: 8px;
  width: 220px;

  :deep(.el-input-number) {
    width: 100px;
  }
}

.range-line {
  color: var(--art-text-gray-400, #98a2b3);
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  height: 126px;
  padding: 20px;
  margin-bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-card__label {
  font-size: 13px;
  color: var(--art-text-gray-500, #98a2b3);
}

.stat-card__value {
  margin-top: 10px;
  font-size: 28px;
  font-weight: 700;
  color: var(--art-text-gray-900, #111827);
  line-height: 1;
}

.stat-card__extra {
  margin-top: 12px;
  font-size: 13px;
  color: var(--art-text-gray-500, #98a2b3);
  line-height: 1.5;
}

.analysis-layout {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.chart-card {
  padding: 20px;
  overflow: hidden;
}

.chart-card--wide {
  grid-column: span 2;
}

.chart-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 12px;
}

.chart-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--art-text-gray-900, #111827);
  line-height: 1.4;
}

.chart-subtitle {
  margin-top: 4px;
  font-size: 13px;
  color: var(--art-text-gray-500, #98a2b3);
  line-height: 1.6;
}

.chart-box {
  width: 100%;
  height: 360px;
}

.chart-box--wide {
  height: 380px;
}

.insight-section {
  padding: 22px;
  margin-top: 20px;
}

.insight-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.insight-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.insight-item {
  display: flex;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid var(--art-border-color, #eaecf0);
  border-radius: 12px;
  background: #fafafa;
}

.insight-index {
  width: 26px;
  height: 26px;
  flex-shrink: 0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  color: #2563eb;
  background: #eff6ff;
}

.insight-text {
  flex: 1;
  font-size: 14px;
  color: var(--art-text-gray-700, #344054);
  line-height: 1.8;
}

@media (max-width: 1200px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 992px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .analysis-layout {
    grid-template-columns: 1fr;
  }

  .chart-card--wide {
    grid-column: span 1;
  }
}

@media (max-width: 640px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }

  .chart-box,
  .chart-box--wide {
    height: 320px;
  }
}
</style>
