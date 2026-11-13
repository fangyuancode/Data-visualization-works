<template>
  <div class="screen-page">
    <div class="screen-bg screen-bg--left"></div>
    <div class="screen-bg screen-bg--right"></div>

    <header class="screen-header">
      <div class="header-left">
        <div class="header-title">数据可视化项目 · 全局总览大屏</div>
        <div class="header-subtitle">
          Multi-source Data Mining & Visualization Overview
        </div>
      </div>

      <div class="header-center">
        <div class="system-title">多源数据智能分析中心</div>
        <div class="system-line"></div>
      </div>

      <div class="header-right">
        <div class="time-box">
          <span>{{ currentDate }}</span>
          <strong>{{ currentTime }}</strong>
        </div>
        <ElButton class="screen-btn" size="small" @click="loadScreenData">
          刷新
        </ElButton>
        <ElButton class="screen-btn" size="small" @click="toggleFullScreen">
          全屏
        </ElButton>
      </div>
    </header>

    <main v-loading="loading" class="screen-main" element-loading-text="加载中..." element-loading-svg-view-box="-10, -10, 50, 50" element-loading-background="rgba(122, 122, 122, 0.8)">
      <section class="kpi-grid">
        <div v-for="item in kpiCards" :key="item.label" class="stat-card">
          <div class="stat-card__icon">{{ item.icon }}</div>
          <div class="stat-card__content">
            <div class="stat-card__value">{{ item.value }}</div>
            <div class="stat-card__label">{{ item.label }}</div>
          </div>
        </div>
      </section>

      <section class="screen-layout">
        <div class="left-panel">
          <div class="panel-card dataset-panel">
            <div class="panel-title">
              <span>数据主题运行状态</span>
              <em>DATASET STATUS</em>
            </div>

            <div class="dataset-list">
              <div v-for="item in datasetCards" :key="item.key" class="dataset-item">
                <div class="dataset-item__head">
                  <div class="dataset-icon">{{ item.icon }}</div>
                  <div class="dataset-info">
                    <h3>{{ item.name }}</h3>
                    <p>{{ item.tableName }}</p>
                  </div>
                  <div class="dataset-status" :class="{ 'is-error': !item.success }">
                    {{ item.success ? '正常' : '异常' }}
                  </div>
                </div>

                <div class="dataset-metrics">
                  <div>
                    <span>数据量</span>
                    <strong>{{ formatNumber(item.totalCount) }}</strong>
                  </div>
                  <div>
                    <span>{{ item.metricLabel }}</span>
                    <strong>{{ formatMetric(item.metricValue, item.metricLabel) }}</strong>
                  </div>
                  <div>
                    <span>分析维度</span>
                    <strong>{{ item.dimensionCount }}</strong>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="panel-card">
            <div class="panel-title">
              <span>数据领域占比</span>
              <em>DOMAIN RATIO</em>
            </div>
            <div ref="domainChartRef" class="chart-box"></div>
          </div>
        </div>

        <div class="center-panel">
          <div class="map-summary-card">
            <div class="summary-glow"></div>
            <div class="summary-title">系统分析能力总览</div>

            <div class="summary-core">
              <div class="core-number">{{ formatNumber(totalRecordCount) }}</div>
              <div class="core-label">全平台汇总数据量</div>
            </div>

            <div class="summary-ring-grid">
              <div class="ring-item">
                <div class="ring-value">{{ successDatasetCount }}/{{ sourceList.length }}</div>
                <div class="ring-label">接口正常</div>
              </div>
              <div class="ring-item">
                <div class="ring-value">{{ totalDimensionCount }}</div>
                <div class="ring-label">分析维度</div>
              </div>
              <div class="ring-item">
                <div class="ring-value">8+</div>
                <div class="ring-label">图表类型</div>
              </div>
            </div>

            <div class="summary-desc">
              平台已整合消费者购物、NIPT检测、北京房屋租赁、学生考试、青少年心理健康、
              电视剧播放量与世界幸福报告等主题数据，形成“数据管理—统计挖掘—可视化展示—规律解读”的分析闭环。
            </div>
          </div>

          <div class="panel-card center-chart">
            <div class="panel-title">
              <span>主题数据量对比</span>
              <em>DATA VOLUME</em>
            </div>
            <div ref="countChartRef" class="chart-box chart-box--large"></div>
          </div>
        </div>

        <div class="right-panel">
          <div class="panel-card">
            <div class="panel-title">
              <span>核心指标对比</span>
              <em>CORE METRIC</em>
            </div>
            <div ref="metricChartRef" class="chart-box"></div>
          </div>

          <div class="panel-card">
            <div class="panel-title">
              <span>平台能力雷达</span>
              <em>ABILITY RADAR</em>
            </div>
            <div ref="radarChartRef" class="chart-box"></div>
          </div>

          <div class="panel-card insight-panel">
            <div class="panel-title">
              <span>全局规律解读</span>
              <em>INSIGHTS</em>
            </div>

            <div class="insight-list">
              <div v-for="item in insightList" :key="item" class="insight-item">
                {{ item }}
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="bottom-chart-grid">
        <div class="panel-card">
          <div class="panel-title">
            <span>分析维度覆盖度</span>
            <em>ANALYSIS COVERAGE</em>
          </div>
          <div ref="coverageChartRef" class="chart-box chart-box--bottom"></div>
        </div>

        <div class="panel-card">
          <div class="panel-title">
            <span>图表类型结构</span>
            <em>CHART STRUCTURE</em>
          </div>
          <div ref="chartTypeRef" class="chart-box chart-box--bottom"></div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import request from '@/utils/http'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'

interface SourceItem {
  key: string
  name: string
  shortName: string
  tableName: string
  api: string
  icon: string
  domain: string
  dimensionCount: number
  metricLabel: string
  metricKeys: string[]
}

interface DatasetCard {
  key: string
  name: string
  shortName: string
  tableName: string
  icon: string
  domain: string
  totalCount: number
  metricLabel: string
  metricValue: number
  dimensionCount: number
  success: boolean
}

const loading = ref(false)
const currentDate = ref('')
const currentTime = ref('')

const datasetCards = ref<DatasetCard[]>([])

const countChartRef = ref<HTMLDivElement | null>(null)
const metricChartRef = ref<HTMLDivElement | null>(null)
const domainChartRef = ref<HTMLDivElement | null>(null)
const radarChartRef = ref<HTMLDivElement | null>(null)
const coverageChartRef = ref<HTMLDivElement | null>(null)
const chartTypeRef = ref<HTMLDivElement | null>(null)

let countChart: echarts.ECharts | null = null
let metricChart: echarts.ECharts | null = null
let domainChart: echarts.ECharts | null = null
let radarChart: echarts.ECharts | null = null
let coverageChart: echarts.ECharts | null = null
let chartTypeChart: echarts.ECharts | null = null
let timer: number | null = null

const sourceList: SourceItem[] = [
  {
    key: 'consumer',
    name: '消费者购物趋势分析',
    shortName: '购物',
    tableName: 'consumer_shopping_trends',
    api: '/api/common/consumer-shopping/analysis',
    icon: '购',
    domain: '消费行为',
    dimensionCount: 8,
    metricLabel: '线上偏好率',
    metricKeys: ['onlinePreferenceRate', 'onlineRate', 'avgOnlinePreference', '线上偏好率']
  },
  {
    key: 'nipt',
    name: '孕妇 NIPT 检测数据',
    shortName: 'NIPT',
    tableName: 'maternal_nipt_data',
    api: '/api/common/nipt/analysis',
    icon: '检',
    domain: '医疗健康',
    dimensionCount: 10,
    metricLabel: '健康样本率',
    metricKeys: ['healthyRate', 'fetusHealthyRate', 'normalRate', '健康率']
  },
  {
    key: 'rental',
    name: '北京房屋租赁数据',
    shortName: '租赁',
    tableName: 'rental_house',
    api: '/api/common/rental/analysis',
    icon: '房',
    domain: '城市居住',
    dimensionCount: 10,
    metricLabel: '平均租金',
    metricKeys: ['avgRent', 'averageRent', 'rentAvg', '平均租金']
  },
  {
    key: 'exam',
    name: '学生考试表现数据',
    shortName: '考试',
    tableName: 'student_exam_performance',
    api: '/api/common/exam/analysis',
    icon: '学',
    domain: '教育学习',
    dimensionCount: 12,
    metricLabel: '平均成绩',
    metricKeys: ['avgFinalScore', 'averageScore', 'avgScore', '平均成绩']
  },
  {
    key: 'teen',
    name: '青少年心理健康数据',
    shortName: '心理',
    tableName: 'teen_mental_health',
    api: '/api/common/teen/analysis',
    icon: '心',
    domain: '医疗健康',
    dimensionCount: 10,
    metricLabel: '风险占比',
    metricKeys: ['depressionRate', 'riskRate', 'depressionRiskRate', '风险占比']
  },
  {
    key: 'tvplay',
    name: '电视剧播放量数据',
    shortName: '剧集',
    tableName: 'tv_drama_play_data',
    api: '/api/common/tvplay/analysis',
    icon: '剧',
    domain: '文娱内容',
    dimensionCount: 11,
    metricLabel: '平均评分',
    metricKeys: ['avgScore', 'averageScore', 'scoreAvg', '平均评分']
  },
  {
    key: 'world',
    name: '世界幸福报告数据',
    shortName: '幸福',
    tableName: 'world_happiness_report',
    api: '/api/common/world-happiness/analysis',
    icon: '幸',
    domain: '社会指数',
    dimensionCount: 10,
    metricLabel: '平均幸福分',
    metricKeys: ['avgScore', 'averageScore', 'avgHappinessScore', '平均幸福分']
  }
]

const totalRecordCount = computed(() => {
  return datasetCards.value.reduce((sum, item) => sum + Number(item.totalCount || 0), 0)
})

const successDatasetCount = computed(() => {
  return datasetCards.value.filter((item) => item.success).length
})

const totalDimensionCount = computed(() => {
  return datasetCards.value.reduce((sum, item) => sum + item.dimensionCount, 0)
})

const kpiCards = computed(() => [
  {
    label: '汇总数据量',
    value: formatNumber(totalRecordCount.value),
    icon: '数'
  },
  {
    label: '数据主题',
    value: `${datasetCards.value.length || sourceList.length}类`,
    icon: '源'
  },
  {
    label: '正常接口',
    value: `${successDatasetCount.value}/${sourceList.length}`,
    icon: '接'
  },
  {
    label: '分析维度',
    value: `${totalDimensionCount.value}+`,
    icon: '析'
  }
])

const insightList = computed(() => {
  const list: string[] = []

  const normalCount = successDatasetCount.value
  if (normalCount === sourceList.length) {
    list.push('所有主题分析接口均正常返回，说明当前数据挖掘模块已经形成较完整的前后端闭环。')
  } else {
    list.push(
      `当前有 ${
        sourceList.length - normalCount
      } 个主题接口未正常返回，建议检查对应后端服务或路由配置。`
    )
  }

  const maxDataset = [...datasetCards.value].sort((a, b) => b.totalCount - a.totalCount)[0]
  if (maxDataset && maxDataset.totalCount > 0) {
    list.push(
      `${maxDataset.name} 的数据量最高，是当前平台中最适合作为重点展示和深入分析的数据资产。`
    )
  }

  list.push(
    '从分析类型看，平台已经覆盖分布分析、分层分析、排行分析、趋势分析、相关性分析和综合画像分析。'
  )
  list.push(
    '后续可以继续增加 PDF 报告导出、跨数据集关联分析和大模型自然语言问答，让系统更适合答辩与竞赛展示。'
  )

  return list
})

const updateTime = () => {
  const now = new Date()
  const weekList = ['日', '一', '二', '三', '四', '五', '六']

  currentDate.value = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} 星期${
    weekList[now.getDay()]
  }`
  currentTime.value = `${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`
}

const pad = (num: number) => {
  return String(num).padStart(2, '0')
}

const unwrapResponse = (res: any) => {
  if (!res) return {}

  // 兼容两种返回：
  // 1. 原生 axios 返回：{ data: { code, msg, data } }
  // 2. 项目 request 封装返回：后端 data 本身
  if (res?.data?.data !== undefined) return res.data.data
  if (res?.data?.result !== undefined) return res.data.result
  if (res?.data !== undefined) return res.data

  return res
}

const normalizeKey = (key: string) => {
  return String(key || '')
    .replace(/_/g, '')
    .replace(/-/g, '')
    .toLowerCase()
}

const parseNumber = (value: any) => {
  if (value === null || value === undefined || value === '') return 0
  if (typeof value === 'number') return Number.isFinite(value) ? value : 0

  const str = String(value).replace(/,/g, '').replace('%', '')
  const num = Number(str)
  return Number.isFinite(num) ? num : 0
}

const findNumberByKeys = (obj: Record<string, any>, keys: string[]) => {
  if (!obj || typeof obj !== 'object') return 0

  for (const key of keys) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {
      return parseNumber(obj[key])
    }
  }

  const entries = Object.entries(obj)
  for (const targetKey of keys) {
    const normalizedTarget = normalizeKey(targetKey)
    const found = entries.find(([key]) => normalizeKey(key).includes(normalizedTarget))
    if (found) return parseNumber(found[1])
  }

  return 0
}

const findTotalCount = (summary: Record<string, any>, data: Record<string, any>) => {
  const total = findNumberByKeys(summary, [
    'totalCount',
    'recordCount',
    'sampleCount',
    'total',
    'count',
    'dataCount',
    'houseCount',
    'studentCount',
    'countryCount',
    'dramaCount',
    '总数',
    '样本量',
    '数据量'
  ])

  if (total > 0) return total

  const distributions = [
    data.preferenceDistribution,
    data.fetusGenderDistribution,
    data.regionStats,
    data.passFailDistribution,
    data.depressionDistribution,
    data.typeDistribution,
    data.scoreSegmentStats
  ]

  for (const list of distributions) {
    if (Array.isArray(list) && list.length) {
      const sum = list.reduce((acc, item) => {
        return acc + findNumberByKeys(item, ['count', 'value', 'total', '数量'])
      }, 0)

      if (sum > 0) return sum
    }
  }

  return 0
}

const buildDatasetCard = (
  source: SourceItem,
  data: Record<string, any>,
  success: boolean
): DatasetCard => {
  const summary = data?.summary || {}
  const totalCount = findTotalCount(summary, data)
  const metricValue = findNumberByKeys(summary, source.metricKeys)

  return {
    key: source.key,
    name: source.name,
    shortName: source.shortName,
    tableName: source.tableName,
    icon: source.icon,
    domain: source.domain,
    totalCount,
    metricLabel: source.metricLabel,
    metricValue,
    dimensionCount: source.dimensionCount,
    success
  }
}

const loadScreenData = async () => {
  loading.value = true

  try {
    const results = await Promise.allSettled(
      sourceList.map((item) =>
        request.get<Record<string, any>>({
          url: item.api,
          showErrorMessage: false
        })
      )
    )

    datasetCards.value = results.map((result, index) => {
      const source = sourceList[index]

      if (result.status === 'fulfilled') {
        const data = unwrapResponse(result.value)
        return buildDatasetCard(source, data, true)
      }

      return buildDatasetCard(source, {}, false)
    })

    await nextTick()
    initCharts()
  } catch (error) {
    ElMessage.error('总览大屏数据加载失败，请检查后端服务是否正常')
  } finally {
    loading.value = false
  }
}

const formatNumber = (value: number) => {
  const num = Number(value || 0)

  if (num >= 100000000) {
    return `${(num / 100000000).toFixed(1)}亿`
  }

  if (num >= 10000) {
    return `${(num / 10000).toFixed(1)}万`
  }

  return String(Math.round(num))
}

const formatMetric = (value: number, label: string) => {
  const num = Number(value || 0)

  if (!num) return '--'

  if (label.includes('率') || label.includes('占比')) {
    return `${num.toFixed(1)}%`
  }

  if (label.includes('租金')) {
    return `¥${Math.round(num)}`
  }

  if (label.includes('评分') || label.includes('成绩') || label.includes('幸福分')) {
    return num.toFixed(2)
  }

  return num.toFixed(1)
}

const getChartTextColor = () => '#b8c7e0'
const getChartLineColor = () => 'rgba(96, 165, 250, 0.22)'

const initCharts = () => {
  initCountChart()
  initMetricChart()
  initDomainChart()
  initRadarChart()
  initCoverageChart()
  initChartTypeChart()
}

const initCountChart = () => {
  if (!countChartRef.value) return

  countChart?.dispose()
  countChart = echarts.init(countChartRef.value)

  countChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(15, 23, 42, 0.92)',
      borderColor: 'rgba(96, 165, 250, 0.4)',
      textStyle: { color: '#e5f0ff' }
    },
    grid: {
      top: 40,
      left: 56,
      right: 32,
      bottom: 42
    },
    xAxis: {
      type: 'category',
      data: datasetCards.value.map((item) => item.shortName),
      axisTick: { show: false },
      axisLine: { lineStyle: { color: getChartLineColor() } },
      axisLabel: { color: getChartTextColor() }
    },
    yAxis: {
      type: 'value',
      name: '数据量',
      nameTextStyle: { color: getChartTextColor() },
      splitLine: { lineStyle: { color: getChartLineColor() } },
      axisLabel: {
        color: getChartTextColor(),
        formatter: (value: number) => formatNumber(value)
      }
    },
    series: [
      {
        name: '数据量',
        type: 'bar',
        barWidth: 26,
        data: datasetCards.value.map((item) => item.totalCount),
        itemStyle: {
          borderRadius: [8, 8, 0, 0],
          color: '#38bdf8'
        }
      }
    ]
  })
}

const initMetricChart = () => {
  if (!metricChartRef.value) return

  metricChart?.dispose()
  metricChart = echarts.init(metricChartRef.value)

  metricChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(15, 23, 42, 0.92)',
      borderColor: 'rgba(96, 165, 250, 0.4)',
      textStyle: { color: '#e5f0ff' },
      formatter: (params: any[]) => {
        const item = params?.[0]
        const dataset = datasetCards.value[item.dataIndex]
        return `${dataset.name}<br/>${dataset.metricLabel}：${formatMetric(
          dataset.metricValue,
          dataset.metricLabel
        )}`
      }
    },
    grid: {
      top: 34,
      left: 42,
      right: 22,
      bottom: 38
    },
    xAxis: {
      type: 'category',
      data: datasetCards.value.map((item) => item.shortName),
      axisTick: { show: false },
      axisLine: { lineStyle: { color: getChartLineColor() } },
      axisLabel: { color: getChartTextColor() }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: getChartLineColor() } },
      axisLabel: { color: getChartTextColor() }
    },
    series: [
      {
        name: '核心指标',
        type: 'bar',
        barWidth: 22,
        data: datasetCards.value.map((item) => item.metricValue),
        itemStyle: {
          borderRadius: [8, 8, 0, 0],
          color: '#22c55e'
        }
      }
    ]
  })
}

const initDomainChart = () => {
  if (!domainChartRef.value) return

  domainChart?.dispose()
  domainChart = echarts.init(domainChartRef.value)

  const domainMap = new Map<string, number>()

  datasetCards.value.forEach((item) => {
    const oldValue = domainMap.get(item.domain) || 0
    domainMap.set(item.domain, oldValue + (item.totalCount || 1))
  })

  const data = Array.from(domainMap.entries()).map(([name, value]) => ({
    name,
    value
  }))

  domainChart.setOption({
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(15, 23, 42, 0.92)',
      borderColor: 'rgba(96, 165, 250, 0.4)',
      textStyle: { color: '#e5f0ff' }
    },
    legend: {
      bottom: 0,
      itemWidth: 10,
      itemHeight: 10,
      textStyle: { color: getChartTextColor() }
    },
    series: [
      {
        name: '数据领域',
        type: 'pie',
        radius: ['46%', '68%'],
        center: ['50%', '42%'],
        avoidLabelOverlap: true,
        label: {
          color: getChartTextColor(),
          formatter: '{b}\n{d}%'
        },
        itemStyle: {
          borderColor: '#071426',
          borderWidth: 2,
          borderRadius: 8
        },
        data
      }
    ]
  })
}

const initRadarChart = () => {
  if (!radarChartRef.value) return

  radarChart?.dispose()
  radarChart = echarts.init(radarChartRef.value)

  const successScore = Math.round((successDatasetCount.value / sourceList.length) * 100)
  const dimensionScore = Math.min(100, Math.round(totalDimensionCount.value * 1.2))
  const dataScore = Math.min(100, Math.round(totalRecordCount.value / 1000))

  radarChart.setOption({
    tooltip: {
      backgroundColor: 'rgba(15, 23, 42, 0.92)',
      borderColor: 'rgba(96, 165, 250, 0.4)',
      textStyle: { color: '#e5f0ff' }
    },
    radar: {
      radius: '64%',
      indicator: [
        { name: '数据规模', max: 100 },
        { name: '接口状态', max: 100 },
        { name: '分析维度', max: 100 },
        { name: '图表表达', max: 100 },
        { name: '规律解读', max: 100 },
        { name: '展示价值', max: 100 }
      ],
      axisName: { color: getChartTextColor() },
      axisLine: { lineStyle: { color: getChartLineColor() } },
      splitLine: { lineStyle: { color: getChartLineColor() } },
      splitArea: {
        areaStyle: {
          color: ['rgba(14, 165, 233, 0.05)', 'rgba(37, 99, 235, 0.08)']
        }
      }
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            name: '平台能力',
            value: [dataScore, successScore, dimensionScore, 92, 88, 90],
            areaStyle: {
              color: 'rgba(56, 189, 248, 0.24)'
            },
            lineStyle: {
              width: 2,
              color: '#38bdf8'
            },
            itemStyle: {
              color: '#38bdf8'
            }
          }
        ]
      }
    ]
  })
}

const initCoverageChart = () => {
  if (!coverageChartRef.value) return

  coverageChart?.dispose()
  coverageChart = echarts.init(coverageChartRef.value)

  coverageChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(15, 23, 42, 0.92)',
      borderColor: 'rgba(96, 165, 250, 0.4)',
      textStyle: { color: '#e5f0ff' }
    },
    grid: {
      top: 38,
      left: 48,
      right: 24,
      bottom: 36
    },
    xAxis: {
      type: 'category',
      data: datasetCards.value.map((item) => item.shortName),
      axisTick: { show: false },
      axisLine: { lineStyle: { color: getChartLineColor() } },
      axisLabel: { color: getChartTextColor() }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: getChartLineColor() } },
      axisLabel: { color: getChartTextColor() }
    },
    series: [
      {
        name: '分析维度',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        data: datasetCards.value.map((item) => item.dimensionCount),
        lineStyle: {
          width: 3,
          color: '#a78bfa'
        },
        itemStyle: {
          color: '#a78bfa'
        },
        areaStyle: {
          color: 'rgba(167, 139, 250, 0.18)'
        }
      }
    ]
  })
}

const initChartTypeChart = () => {
  if (!chartTypeRef.value) return

  chartTypeChart?.dispose()
  chartTypeChart = echarts.init(chartTypeRef.value)

  chartTypeChart.setOption({
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(15, 23, 42, 0.92)',
      borderColor: 'rgba(96, 165, 250, 0.4)',
      textStyle: { color: '#e5f0ff' }
    },
    series: [
      {
        name: '图表类型',
        type: 'pie',
        roseType: 'radius',
        radius: ['22%', '70%'],
        center: ['50%', '50%'],
        label: {
          color: getChartTextColor()
        },
        itemStyle: {
          borderColor: '#071426',
          borderWidth: 2,
          borderRadius: 8
        },
        data: [
          { name: '柱状图', value: 28 },
          { name: '饼图', value: 18 },
          { name: '雷达图', value: 14 },
          { name: '散点图', value: 12 },
          { name: '地图', value: 8 },
          { name: '热力图', value: 8 },
          { name: '折线图', value: 10 }
        ]
      }
    ]
  })
}

const resizeCharts = () => {
  countChart?.resize()
  metricChart?.resize()
  domainChart?.resize()
  radarChart?.resize()
  coverageChart?.resize()
  chartTypeChart?.resize()
}

const toggleFullScreen = () => {
  const doc = document as any
  const element = document.documentElement as any

  if (!document.fullscreenElement) {
    element.requestFullscreen?.()
  } else {
    doc.exitFullscreen?.()
  }
}

onMounted(() => {
  updateTime()
  timer = window.setInterval(updateTime, 1000)

  loadScreenData()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  if (timer) {
    clearInterval(timer)
  }

  window.removeEventListener('resize', resizeCharts)

  countChart?.dispose()
  metricChart?.dispose()
  domainChart?.dispose()
  radarChart?.dispose()
  coverageChart?.dispose()
  chartTypeChart?.dispose()
})
</script>

<style scoped lang="scss">
.screen-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  color: #e5f0ff;
  background: radial-gradient(circle at 15% 15%, rgba(14, 165, 233, 0.24), transparent 34%),
    radial-gradient(circle at 82% 22%, rgba(99, 102, 241, 0.2), transparent 30%),
    linear-gradient(135deg, #06101f 0%, #071426 46%, #08111f 100%);
}

.screen-bg {
  position: absolute;
  width: 420px;
  height: 420px;
  border-radius: 50%;
  pointer-events: none;
  filter: blur(20px);
  opacity: 0.28;
}

.screen-bg--left {
  left: -160px;
  top: 180px;
  background: #0ea5e9;
}

.screen-bg--right {
  right: -180px;
  bottom: 120px;
  background: #6366f1;
}

.screen-header {
  position: relative;
  z-index: 2;
  display: grid;
  grid-template-columns: 360px minmax(0, 1fr) 380px;
  align-items: center;
  height: 86px;
  padding: 0 28px;
  border-bottom: 1px solid rgba(56, 189, 248, 0.18);
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.86), rgba(15, 23, 42, 0.16));
}

.header-title {
  font-size: 17px;
  font-weight: 800;
  color: #ffffff;
}

.header-subtitle {
  margin-top: 6px;
  font-size: 12px;
  color: #7dd3fc;
  letter-spacing: 0.5px;
}

.header-center {
  text-align: center;
}

.system-title {
  font-size: 30px;
  font-weight: 900;
  color: #ffffff;
  letter-spacing: 4px;
  text-shadow: 0 0 18px rgba(56, 189, 248, 0.42);
}

.system-line {
  width: 420px;
  height: 2px;
  margin: 12px auto 0;
  background: linear-gradient(90deg, transparent, #38bdf8, transparent);
}

.header-right {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
}

.time-box {
  text-align: right;
  margin-right: 6px;
}

.time-box span {
  display: block;
  font-size: 12px;
  color: #93c5fd;
}

.time-box strong {
  display: block;
  margin-top: 4px;
  font-size: 20px;
  color: #ffffff;
  letter-spacing: 1px;
}

.screen-btn {
  color: #dbeafe;
  border-color: rgba(56, 189, 248, 0.38);
  background: rgba(15, 23, 42, 0.42);
}

.screen-main {
  position: relative;
  z-index: 2;
  padding: 18px 24px 24px;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.stat-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 14px;
  min-height: 92px;
  padding: 18px;
  border: 1px solid rgba(56, 189, 248, 0.22);
  border-radius: 16px;
  background: linear-gradient(145deg, rgba(15, 23, 42, 0.88), rgba(30, 41, 59, 0.58));
  box-shadow: inset 0 0 24px rgba(56, 189, 248, 0.05), 0 14px 30px rgba(0, 0, 0, 0.16);
  margin-bottom: 0;
}

.stat-card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 16px;
  background: linear-gradient(90deg, rgba(56, 189, 248, 0.16), transparent);
  pointer-events: none;
}

.stat-card__icon {
  position: relative;
  z-index: 1;
  width: 50px;
  height: 50px;
  flex: 0 0 50px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #00111f;
  font-weight: 900;
  background: linear-gradient(135deg, #38bdf8, #a5f3fc);
  box-shadow: 0 0 20px rgba(56, 189, 248, 0.36);
}

.stat-card__content {
  position: relative;
  z-index: 1;
}

.stat-card__value {
  font-size: 28px;
  line-height: 1;
  font-weight: 900;
  color: #ffffff;
}

.stat-card__label {
  margin-top: 8px;
  color: #93c5fd;
  font-size: 13px;
}

.screen-layout {
  display: grid;
  grid-template-columns: 420px minmax(0, 1fr) 420px;
  gap: 16px;
}

.left-panel,
.right-panel,
.center-panel {
  display: grid;
  gap: 16px;
  align-content: start;
}

.panel-card {
  min-width: 0;
  padding: 16px;
  border: 1px solid rgba(56, 189, 248, 0.22);
  border-radius: 16px;
  background: rgba(8, 20, 38, 0.72);
  box-shadow: inset 0 0 28px rgba(56, 189, 248, 0.04), 0 14px 30px rgba(0, 0, 0, 0.16);
  backdrop-filter: blur(10px);
}

.panel-title {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.panel-title span {
  font-size: 16px;
  font-weight: 800;
  color: #ffffff;
}

.panel-title em {
  font-size: 11px;
  color: rgba(125, 211, 252, 0.74);
  font-style: normal;
  letter-spacing: 1px;
}

.dataset-panel {
  height: 700px;
}

.dataset-list {
  display: grid;
  gap: 10px;
  max-height: 620px;
  overflow-y: auto;
  padding-right: 4px;
  // padding-bottom: 10px;
}
/* 针对 WebKit 浏览器隐藏滚动条 */
.dataset-list::-webkit-scrollbar {
  display: none;
}
.dataset-list::-webkit-scrollbar {
  width: 4px;
}

.dataset-list::-webkit-scrollbar-thumb {
  border-radius: 999px;
  background: rgba(56, 189, 248, 0.36);
}

.dataset-item {
  padding: 12px;
  border: 1px solid rgba(96, 165, 250, 0.16);
  border-radius: 14px;
  background: rgba(15, 23, 42, 0.56);
}

.dataset-item__head {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dataset-icon {
  width: 38px;
  height: 38px;
  flex: 0 0 38px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #0f172a;
  font-weight: 900;
  background: linear-gradient(135deg, #60a5fa, #67e8f9);
}

.dataset-info {
  min-width: 0;
  flex: 1;
}

.dataset-info h3 {
  margin: 0;
  color: #ffffff;
  font-size: 14px;
}

.dataset-info p {
  margin: 4px 0 0;
  color: #7dd3fc;
  font-size: 11px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dataset-status {
  padding: 4px 8px;
  border-radius: 999px;
  color: #86efac;
  font-size: 12px;
  background: rgba(34, 197, 94, 0.12);
  border: 1px solid rgba(34, 197, 94, 0.28);
}

.dataset-status.is-error {
  color: #fca5a5;
  background: rgba(239, 68, 68, 0.12);
  border-color: rgba(239, 68, 68, 0.28);
}

.dataset-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  margin-top: 10px;
}

.dataset-metrics div {
  padding: 8px;
  border-radius: 10px;
  background: rgba(2, 8, 23, 0.32);
}

.dataset-metrics span {
  display: block;
  color: #94a3b8;
  font-size: 11px;
}

.dataset-metrics strong {
  display: block;
  margin-top: 4px;
  color: #ffffff;
  font-size: 14px;
}

.chart-box {
  width: 100%;
  height: 260px;
}

.chart-box--large {
  height: 326px;
}

.chart-box--bottom {
  height: 250px;
}

.map-summary-card {
  position: relative;
  min-height: 620px;
  padding: 34px 28px;
  overflow: hidden;
  border-radius: 18px;
  border: 1px solid rgba(56, 189, 248, 0.26);
  background: radial-gradient(circle at 50% 36%, rgba(56, 189, 248, 0.24), transparent 34%),
    linear-gradient(145deg, rgba(8, 20, 38, 0.88), rgba(15, 23, 42, 0.66));
  text-align: center;
  box-shadow: inset 0 0 70px rgba(56, 189, 248, 0.08);
}

.summary-glow {
  position: absolute;
  left: 50%;
  top: 35%;
  width: 260px;
  height: 260px;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  border: 1px solid rgba(56, 189, 248, 0.34);
  box-shadow: 0 0 28px rgba(56, 189, 248, 0.22), inset 0 0 34px rgba(56, 189, 248, 0.12);
}

.summary-title {
  position: relative;
  z-index: 1;
  color: #bfdbfe;
  font-size: 16px;
  letter-spacing: 2px;
}

.summary-core {
  position: relative;
  z-index: 1;
  margin-top: 120px;
}

.core-number {
  font-size: 54px;
  line-height: 1;
  font-weight: 900;
  color: #ffffff;
  text-shadow: 0 0 24px rgba(56, 189, 248, 0.48);
}

.core-label {
  margin-top: 10px;
  color: #7dd3fc;
  font-size: 14px;
}

.summary-ring-grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 130px;
}

.ring-item {
  padding: 12px;
  border-radius: 14px;
  background: rgba(2, 8, 23, 0.38);
  border: 1px solid rgba(96, 165, 250, 0.18);
}

.ring-value {
  color: #ffffff;
  font-size: 22px;
  font-weight: 900;
}

.ring-label {
  margin-top: 6px;
  color: #94a3b8;
  font-size: 12px;
}

.summary-desc {
  position: relative;
  z-index: 1;
  margin-top: 20px;
  color: #b8c7e0;
  font-size: 13px;
  line-height: 1.8;
  text-align: left;
}

.center-chart {
  min-height: 384px;
}

.insight-panel {
  min-height: 244px;
}

.insight-list {
  display: grid;
  gap: 10px;
}

.insight-item {
  position: relative;
  padding: 10px 12px 10px 28px;
  border-radius: 12px;
  color: #dbeafe;
  line-height: 1.65;
  font-size: 13px;
  background: rgba(15, 23, 42, 0.48);
  border: 1px solid rgba(96, 165, 250, 0.14);
}

.insight-item::before {
  content: '';
  position: absolute;
  left: 12px;
  top: 18px;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #38bdf8;
  box-shadow: 0 0 10px rgba(56, 189, 248, 0.8);
}

.bottom-chart-grid {
  display: grid;
  grid-template-columns: 1.35fr 0.65fr;
  gap: 16px;
  margin-top: 16px;
}

@media (max-width: 1500px) {
  .screen-header {
    grid-template-columns: 300px minmax(0, 1fr) 330px;
  }

  .screen-layout {
    grid-template-columns: 360px minmax(0, 1fr) 360px;
  }

  .system-title {
    font-size: 26px;
  }
}

@media (max-width: 1200px) {
  .screen-page {
    overflow: auto;
  }

  .screen-header {
    height: auto;
    grid-template-columns: 1fr;
    gap: 14px;
    padding: 18px;
  }

  .header-left,
  .header-right {
    justify-content: center;
    text-align: center;
  }

  .header-right {
    flex-wrap: wrap;
  }

  .kpi-grid,
  .screen-layout,
  .bottom-chart-grid {
    grid-template-columns: 1fr;
  }

  .dataset-panel {
    height: auto;
  }
}
</style>