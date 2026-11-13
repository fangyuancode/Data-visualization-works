<template>
  <div class="nipt-analysis-page">
    <div class="page-header art-card">
      <div class="page-header__left">
        <div class="page-title">孕妇 NIPT 检测数据可视化分析</div>
        <div class="page-desc">
          围绕孕妇年龄、BMI、检测孕周、胎儿类型、胎儿健康、染色体非整倍体、Z值、测序质量与胎儿DNA浓度进行数据挖掘，识别检测数据中的风险分层、质量规律与分析价值。
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
      <ElForm :model="queryForm" inline label-width="92px" class="filter-form">
        <ElFormItem label="胎儿类型">
          <ElSelect v-model="queryForm.fetusGender" placeholder="全部类型" clearable style="width: 140px">
            <ElOption v-for="item in options.fetusGenders" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="胎儿健康">
          <ElSelect v-model="queryForm.fetusHealthy" placeholder="全部状态" clearable style="width: 140px">
            <ElOption v-for="item in options.fetusHealthyOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="非整倍体">
          <ElSelect v-model="queryForm.chromosomeAneuploidy" placeholder="全部结果" clearable style="width: 160px">
            <ElOption v-for="item in options.chromosomeAneuploidyOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="年龄">
          <div class="range-input">
            <ElInputNumber v-model="queryForm.ageMin" :min="0" :controls="false" placeholder="最小" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.ageMax" :min="0" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem label="BMI">
          <div class="range-input">
            <ElInputNumber v-model="queryForm.bmiMin" :min="0" :precision="2" :controls="false" placeholder="最小" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.bmiMax" :min="0" :precision="2" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem label="检测日期">
          <ElDatePicker
            v-model="dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 260px"
            @change="handleDateRangeChange"
          />
        </ElFormItem>

        <ElFormItem>
          <ElSpace wrap>
            <ElButton type="primary" :loading="loading" @click="loadAnalysis">开始分析</ElButton>
            <ElButton @click="resetQuery">重置</ElButton>
          </ElSpace>
        </ElFormItem>
      </ElForm>
    </div>

    <div class="summary-grid" v-loading="loading" element-loading-text="正在计算NIPT核心指标...">
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
            <div class="chart-title">胎儿类型分布</div>
            <div class="chart-subtitle">观察男胎、女胎及未知样本占比，辅助判断样本结构是否均衡</div>
          </div>
        </div>
        <div ref="fetusPieRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">胎儿健康状态占比</div>
            <div class="chart-subtitle">展示健康状态结构，辅助识别筛查结果总体表现</div>
          </div>
        </div>
        <div ref="healthyPieRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">年龄层风险分布</div>
            <div class="chart-subtitle">对比不同年龄段的样本量、健康率与非整倍体提示率</div>
          </div>
        </div>
        <div ref="ageRiskRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">BMI分层与检测表现</div>
            <div class="chart-subtitle">分析BMI区间下的Y染色体浓度、过滤比例、比对比例与异常提示率</div>
          </div>
        </div>
        <div ref="bmiStatsRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">孕周区间与测序质量</div>
            <div class="chart-subtitle">分析检测孕周对GC含量、比对率、过滤率和胎儿DNA信号的影响</div>
          </div>
        </div>
        <div ref="gestationalQualityRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">胎儿类型测序质量雷达图</div>
            <div class="chart-subtitle">从比对率、非重复率、清洁读段、GC含量和染色体浓度综合评估</div>
          </div>
        </div>
        <div ref="qualityRadarRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">BMI与Y染色体浓度散点</div>
            <div class="chart-subtitle">观察孕妇BMI与胎儿游离DNA信号之间的关系</div>
          </div>
        </div>
        <div ref="scatterRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">年龄层Z值变化趋势</div>
            <div class="chart-subtitle">对比13、18、21、X、Y染色体Z值在不同年龄段的均值变化</div>
          </div>
        </div>
        <div ref="zValueRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">检测月份趋势</div>
            <div class="chart-subtitle">观察不同月份样本量、平均孕周与异常提示率变化</div>
          </div>
        </div>
        <div ref="monthTrendRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">染色体非整倍体结果结构</div>
            <div class="chart-subtitle">识别主要异常提示类型，为重点染色体风险分析提供依据</div>
          </div>
        </div>
        <div ref="aneuploidyPieRef" class="chart-box"></div>
      </div>
    </div>

    <div class="insight-section art-card" v-loading="loading" element-loading-text="正在生成数据解读...">
      <div class="insight-header">
        <div>
          <div class="chart-title">数据规律与价值解读</div>
          <div class="chart-subtitle">根据当前筛选条件自动总结数据挖掘结论，便于论文、报告或大屏展示使用</div>
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
  fetchNiptAnalysis,
  fetchNiptOptions,
  type MaternalNiptAnalysisData,
  type MaternalNiptOptions,
  type MaternalNiptQuery
} from '@/api/nipt'

const router = useRouter()
const loading = ref(false)
const dateRange = ref<string[]>([])

const options = reactive<MaternalNiptOptions>({
  fetusGenders: [],
  fetusHealthyOptions: [],
  chromosomeAneuploidyOptions: []
})

const queryForm = reactive<MaternalNiptQuery>({
  fetusGender: '',
  pregnantWomanCode: '',
  fetusHealthy: '',
  chromosomeAneuploidy: '',
  ageMin: undefined,
  ageMax: undefined,
  bmiMin: undefined,
  bmiMax: undefined,
  gestationalDaysMin: undefined,
  gestationalDaysMax: undefined,
  testStartDate: '',
  testEndDate: ''
})

const emptyAnalysis = (): MaternalNiptAnalysisData => ({
  summary: {},
  fetusGenderDistribution: [],
  healthyDistribution: [],
  aneuploidyDistribution: [],
  ageSegmentStats: [],
  bmiSegmentStats: [],
  gestationalSegmentStats: [],
  zValueByAgeSegment: [],
  qualityRadarByFetusGender: [],
  bmiYConcentrationScatter: [],
  testMonthTrend: [],
  insights: []
})

const analysisData = ref<MaternalNiptAnalysisData>(emptyAnalysis())

const fetusPieRef = ref<HTMLDivElement | null>(null)
const healthyPieRef = ref<HTMLDivElement | null>(null)
const ageRiskRef = ref<HTMLDivElement | null>(null)
const bmiStatsRef = ref<HTMLDivElement | null>(null)
const gestationalQualityRef = ref<HTMLDivElement | null>(null)
const qualityRadarRef = ref<HTMLDivElement | null>(null)
const scatterRef = ref<HTMLDivElement | null>(null)
const zValueRef = ref<HTMLDivElement | null>(null)
const monthTrendRef = ref<HTMLDivElement | null>(null)
const aneuploidyPieRef = ref<HTMLDivElement | null>(null)

let fetusPieChart: echarts.ECharts | null = null
let healthyPieChart: echarts.ECharts | null = null
let ageRiskChart: echarts.ECharts | null = null
let bmiStatsChart: echarts.ECharts | null = null
let gestationalQualityChart: echarts.ECharts | null = null
let qualityRadarChart: echarts.ECharts | null = null
let scatterChart: echarts.ECharts | null = null
let zValueChart: echarts.ECharts | null = null
let monthTrendChart: echarts.ECharts | null = null
let aneuploidyPieChart: echarts.ECharts | null = null

const summary = computed(() => analysisData.value.summary || {})
const insightList = computed(() => analysisData.value.insights || [])

const summaryCards = computed(() => [
  {
    label: '样本总量',
    value: numberText(summary.value.totalCount),
    extra: '当前筛选条件下的NIPT记录数'
  },
  {
    label: '平均年龄 / BMI',
    value: `${numberText(summary.value.avgAge)}岁 / ${numberText(summary.value.avgBmi)}`,
    extra: '用于判断样本人群基础特征'
  },
  {
    label: '平均检测孕周',
    value: `${numberText(toNumber(summary.value.avgGestationalDays) / 7)}周`,
    extra: '基于孕周天数换算'
  },
  {
    label: '胎儿健康率',
    value: `${numberText(summary.value.healthyRate)}%`,
    extra: '胎儿健康为“是”的样本占比'
  },
  {
    label: '非整倍体提示率',
    value: `${numberText(summary.value.abnormalRate)}%`,
    extra: '存在异常提示结果的样本占比'
  },
  {
    label: '平均比对率 / GC',
    value: `${percentText(summary.value.avgMappingRatio)} / ${percentText(summary.value.avgGcContent)}`,
    extra: '衡量测序数据质量稳定性'
  }
])

async function loadOptions() {
  const res = await fetchNiptOptions()
  options.fetusGenders = res.fetusGenders || []
  options.fetusHealthyOptions = res.fetusHealthyOptions || []
  options.chromosomeAneuploidyOptions = res.chromosomeAneuploidyOptions || []
}

async function loadAnalysis() {
  try {
    loading.value = true
    const res = await fetchNiptAnalysis({ ...queryForm })
    analysisData.value = res || emptyAnalysis()

    await nextTick()
    renderAllCharts()
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryForm.fetusGender = ''
  queryForm.pregnantWomanCode = ''
  queryForm.fetusHealthy = ''
  queryForm.chromosomeAneuploidy = ''
  queryForm.ageMin = undefined
  queryForm.ageMax = undefined
  queryForm.bmiMin = undefined
  queryForm.bmiMax = undefined
  queryForm.gestationalDaysMin = undefined
  queryForm.gestationalDaysMax = undefined
  queryForm.testStartDate = ''
  queryForm.testEndDate = ''
  dateRange.value = []
  loadAnalysis()
}

function handleDateRangeChange(value: string[] | null) {
  queryForm.testStartDate = value?.[0] || ''
  queryForm.testEndDate = value?.[1] || ''
}

function goBack() {
  router.push('/source/nipt')
}

function initChart(dom: HTMLDivElement | null, oldChart: echarts.ECharts | null) {
  if (!dom) return null
  if (oldChart) oldChart.dispose()
  return echarts.init(dom)
}

function renderAllCharts() {
  renderFetusPie()
  renderHealthyPie()
  renderAgeRisk()
  renderBmiStats()
  renderGestationalQuality()
  renderQualityRadar()
  renderScatter()
  renderZValue()
  renderMonthTrend()
  renderAneuploidyPie()
}

function renderFetusPie() {
  fetusPieChart = initChart(fetusPieRef.value, fetusPieChart)
  if (!fetusPieChart) return

  const data = analysisData.value.fetusGenderDistribution.map((item) => ({
    name: formatUnknown(item.name),
    value: toNumber(item.value)
  }))

  fetusPieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}<br/>数量：{c}<br/>占比：{d}%' },
    legend: { bottom: 0, icon: 'circle' },
    series: [
      {
        name: '胎儿类型',
        type: 'pie',
        radius: ['44%', '70%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: true,
        label: { formatter: '{b}\n{d}%' },
        data
      }
    ]
  })
}

function renderHealthyPie() {
  healthyPieChart = initChart(healthyPieRef.value, healthyPieChart)
  if (!healthyPieChart) return

  const data = analysisData.value.healthyDistribution.map((item) => ({
    name: formatUnknown(item.name),
    value: toNumber(item.value)
  }))

  healthyPieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}<br/>数量：{c}<br/>占比：{d}%' },
    legend: { bottom: 0, icon: 'circle' },
    series: [
      {
        name: '健康状态',
        type: 'pie',
        radius: ['40%', '68%'],
        center: ['50%', '45%'],
        label: { formatter: '{b}\n{d}%' },
        data
      }
    ]
  })
}

function renderAneuploidyPie() {
  aneuploidyPieChart = initChart(aneuploidyPieRef.value, aneuploidyPieChart)
  if (!aneuploidyPieChart) return

  const data = analysisData.value.aneuploidyDistribution.map((item) => ({
    name: formatUnknown(item.name),
    value: toNumber(item.value)
  }))

  aneuploidyPieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}<br/>数量：{c}<br/>占比：{d}%' },
    legend: { bottom: 0, icon: 'circle', type: 'scroll' },
    series: [
      {
        name: '非整倍体结果',
        type: 'pie',
        radius: ['36%', '66%'],
        center: ['50%', '45%'],
        label: { formatter: '{b}\n{d}%' },
        data
      }
    ]
  })
}

function renderAgeRisk() {
  ageRiskChart = initChart(ageRiskRef.value, ageRiskChart)
  if (!ageRiskChart) return

  const list = analysisData.value.ageSegmentStats
  const names = list.map((item) => item.ageSegment)

  ageRiskChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { top: 0 },
    grid: { left: 52, right: 52, top: 52, bottom: 38 },
    xAxis: { type: 'category', data: names },
    yAxis: [
      { type: 'value', name: '数量' },
      { type: 'value', name: '比例', axisLabel: { formatter: '{value}%' } }
    ],
    series: [
      {
        name: '样本量',
        type: 'bar',
        barMaxWidth: 32,
        data: list.map((item) => toNumber(item.totalCount))
      },
      {
        name: '健康率',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.healthyRate))
      },
      {
        name: '非整倍体提示率',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.abnormalRate))
      }
    ]
  })
}

function renderBmiStats() {
  bmiStatsChart = initChart(bmiStatsRef.value, bmiStatsChart)
  if (!bmiStatsChart) return

  const list = analysisData.value.bmiSegmentStats
  const names = list.map((item) => item.bmiSegment)

  bmiStatsChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: 52, right: 48, top: 52, bottom: 46 },
    xAxis: { type: 'category', data: names, axisLabel: { interval: 0, rotate: 18 } },
    yAxis: [
      { type: 'value', name: '百分比' },
      { type: 'value', name: '比例', axisLabel: { formatter: '{value}%' } }
    ],
    series: [
      {
        name: '平均Y浓度(%)',
        type: 'bar',
        barMaxWidth: 24,
        data: list.map((item) => toNumber(item.avgYConcentration))
      },
      {
        name: '过滤读段比例(%)',
        type: 'bar',
        barMaxWidth: 24,
        data: list.map((item) => toNumber(item.avgFilteredReadRatio))
      },
      {
        name: '比对率(%)',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.avgMappingRatio))
      },
      {
        name: '异常提示率(%)',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.abnormalRate))
      }
    ]
  })
}

function renderGestationalQuality() {
  gestationalQualityChart = initChart(gestationalQualityRef.value, gestationalQualityChart)
  if (!gestationalQualityChart) return

  const list = analysisData.value.gestationalSegmentStats
  const names = list.map((item) => item.gestationalSegment)

  gestationalQualityChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0, type: 'scroll' },
    grid: { left: 52, right: 42, top: 58, bottom: 40 },
    xAxis: { type: 'category', data: names },
    yAxis: { type: 'value', name: '百分比', axisLabel: { formatter: '{value}%' } },
    series: [
      {
        name: 'Y浓度',
        type: 'bar',
        barMaxWidth: 24,
        data: list.map((item) => toNumber(item.avgYConcentration))
      },
      {
        name: 'GC含量',
        type: 'bar',
        barMaxWidth: 24,
        data: list.map((item) => toNumber(item.avgGcContent))
      },
      {
        name: '比对率',
        type: 'line',
        smooth: true,
        data: list.map((item) => toNumber(item.avgMappingRatio))
      },
      {
        name: '过滤比例',
        type: 'line',
        smooth: true,
        data: list.map((item) => toNumber(item.avgFilteredReadRatio))
      },
      {
        name: '异常提示率',
        type: 'line',
        smooth: true,
        data: list.map((item) => toNumber(item.abnormalRate))
      }
    ]
  })
}

function renderQualityRadar() {
  qualityRadarChart = initChart(qualityRadarRef.value, qualityRadarChart)
  if (!qualityRadarChart) return

  const indicators = [
    { name: '比对率', max: 100 },
    { name: '非重复率', max: 100 },
    { name: '清洁读段', max: 100 },
    { name: 'GC含量', max: 100 },
    { name: 'X浓度', max: 100 },
    { name: 'Y浓度', max: 100 }
  ]

  const data = analysisData.value.qualityRadarByFetusGender.map((item) => ({
    name: formatUnknown(item.fetusGender),
    value: [
      item.mappingScore,
      item.nonDuplicateScore,
      item.cleanReadScore,
      item.gcScore,
      item.xConcentrationScore,
      item.yConcentrationScore
    ].map(toNumber)
  }))

  qualityRadarChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, icon: 'circle' },
    radar: {
      indicator: indicators,
      radius: '62%',
      center: ['50%', '45%']
    },
    series: [
      {
        name: '测序质量',
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

  const data = analysisData.value.bmiYConcentrationScatter.map((item) => [
    toNumber(item.bmi),
    toNumber(item.yConcentration),
    toNumber(item.gestationalDays),
    toNumber(item.age),
    formatUnknown(item.fetusGender),
    formatUnknown(item.fetusHealthy),
    toNumber(item.chr21ZValue)
  ])

  scatterChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const d = params.data || []
        return `
          <div style="line-height: 1.8;">
            <div>BMI：${d[0]}</div>
            <div>Y染色体浓度：${d[1]}%</div>
            <div>孕周天数：${d[2]}</div>
            <div>年龄：${d[3]}</div>
            <div>胎儿类型：${d[4]}</div>
            <div>健康状态：${d[5]}</div>
            <div>21号Z值：${d[6]}</div>
          </div>
        `
      }
    },
    grid: { left: 52, right: 28, top: 28, bottom: 46 },
    xAxis: { type: 'value', name: 'BMI' },
    yAxis: { type: 'value', name: 'Y浓度(%)' },
    series: [
      {
        name: '样本点',
        type: 'scatter',
        data,
        symbolSize: (value: number[]) => Math.max(8, Math.min(28, 8 + Number(value[2] || 0) / 20)),
        emphasis: { focus: 'series' }
      }
    ]
  })
}

function renderZValue() {
  zValueChart = initChart(zValueRef.value, zValueChart)
  if (!zValueChart) return

  const list = analysisData.value.zValueByAgeSegment
  const names = list.map((item) => item.ageSegment)

  zValueChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: 52, right: 38, top: 52, bottom: 40 },
    xAxis: { type: 'category', data: names },
    yAxis: { type: 'value', name: 'Z值均值' },
    series: [
      { name: '13号Z值', type: 'line', smooth: true, data: list.map((item) => toNumber(item.avgChr13ZValue)) },
      { name: '18号Z值', type: 'line', smooth: true, data: list.map((item) => toNumber(item.avgChr18ZValue)) },
      { name: '21号Z值', type: 'line', smooth: true, data: list.map((item) => toNumber(item.avgChr21ZValue)) },
      { name: 'X染色体Z值', type: 'line', smooth: true, data: list.map((item) => toNumber(item.avgChrXZValue)) },
      { name: 'Y染色体Z值', type: 'line', smooth: true, data: list.map((item) => toNumber(item.avgChrYZValue)) }
    ]
  })
}

function renderMonthTrend() {
  monthTrendChart = initChart(monthTrendRef.value, monthTrendChart)
  if (!monthTrendChart) return

  const list = analysisData.value.testMonthTrend
  const names = list.map((item) => item.month)

  monthTrendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: 52, right: 52, top: 52, bottom: 42 },
    xAxis: { type: 'category', data: names, axisLabel: { rotate: names.length > 8 ? 25 : 0 } },
    yAxis: [
      { type: 'value', name: '数量/孕周' },
      { type: 'value', name: '异常率', axisLabel: { formatter: '{value}%' } }
    ],
    series: [
      {
        name: '样本量',
        type: 'bar',
        barMaxWidth: 28,
        data: list.map((item) => toNumber(item.totalCount))
      },
      {
        name: '平均孕周',
        type: 'line',
        smooth: true,
        data: list.map((item) => toNumber(item.avgGestationalWeek))
      },
      {
        name: '异常提示率',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.abnormalRate))
      }
    ]
  })
}

function resizeCharts() {
  fetusPieChart?.resize()
  healthyPieChart?.resize()
  ageRiskChart?.resize()
  bmiStatsChart?.resize()
  gestationalQualityChart?.resize()
  qualityRadarChart?.resize()
  scatterChart?.resize()
  zValueChart?.resize()
  monthTrendChart?.resize()
  aneuploidyPieChart?.resize()
}

function disposeCharts() {
  fetusPieChart?.dispose()
  healthyPieChart?.dispose()
  ageRiskChart?.dispose()
  bmiStatsChart?.dispose()
  gestationalQualityChart?.dispose()
  qualityRadarChart?.dispose()
  scatterChart?.dispose()
  zValueChart?.dispose()
  monthTrendChart?.dispose()
  aneuploidyPieChart?.dispose()
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

function percentText(value: unknown) {
  return `${(toNumber(value) * 100).toLocaleString('zh-CN', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2
  })}%`
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
.nipt-analysis-page {
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
  color: var(--art-text-gray-900, #111827);
  line-height: 1.2;
}

.page-desc {
  max-width: 960px;
  margin-top: 8px;
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
  width: 210px;
  gap: 8px;

  :deep(.el-input-number) {
    width: 96px;
  }
}

.range-line {
  color: var(--art-text-gray-500, #98a2b3);
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
  color: var(--art-text-gray-600, #667085);
}

.stat-card__value {
  margin-top: 10px;
  font-size: 24px;
  font-weight: 700;
  color: var(--art-text-gray-900, #111827);
  line-height: 1.15;
}

.stat-card__extra {
  margin-top: 10px;
  font-size: 12px;
  color: var(--art-text-gray-500, #98a2b3);
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
  color: var(--art-text-gray-900, #111827);
}

.chart-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: var(--art-text-gray-500, #98a2b3);
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
  border: 1px solid var(--art-border-color, #eaecf0);
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
  color: var(--art-text-gray-700, #344054);
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
