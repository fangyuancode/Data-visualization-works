<template>
  <div class="teen-analysis-page">
    <div class="page-header art-card">
      <div class="page-header__left">
        <div class="page-title">青少年心理健康数据可视化分析</div>
        <div class="page-desc">
          围绕社交媒体使用时长、平台偏好、睡眠、睡前屏幕暴露、学业表现、运动、压力、焦虑、成瘾与抑郁风险标签进行数据挖掘，识别影响青少年心理健康的关键行为规律。
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
      <ElForm :model="queryForm" inline label-width="96px" class="filter-form">
        <ElFormItem label="性别">
          <ElSelect v-model="queryForm.gender" placeholder="全部性别" clearable filterable style="width: 130px">
            <ElOption v-for="item in options.genders" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="使用平台">
          <ElSelect v-model="queryForm.platformUsage" placeholder="全部平台" clearable filterable style="width: 150px">
            <ElOption v-for="item in options.platformUsages" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="社交水平">
          <ElSelect v-model="queryForm.socialInteractionLevel" placeholder="全部水平" clearable filterable style="width: 150px">
            <ElOption v-for="item in options.socialInteractionLevels" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="抑郁风险">
          <ElSelect v-model="queryForm.depressionLabel" placeholder="全部风险" clearable style="width: 150px">
            <ElOption v-for="item in options.depressionLabels" :key="item" :label="formatDepressionLabel(item)" :value="item" />
            <ElOption label="暂无明显风险" :value="0" />
            <ElOption label="存在风险" :value="1" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="年龄范围">
          <div class="range-input">
            <ElInputNumber v-model="queryForm.ageMin" :min="0" :controls="false" placeholder="最小" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.ageMax" :min="0" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem label="社媒时长">
          <div class="range-input">
            <ElInputNumber v-model="queryForm.dailySocialMediaHoursMin" :min="0" :controls="false" placeholder="最小" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.dailySocialMediaHoursMax" :min="0" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem label="睡眠时长">
          <div class="range-input">
            <ElInputNumber v-model="queryForm.sleepHoursMin" :min="0" :controls="false" placeholder="最小" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.sleepHoursMax" :min="0" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem label="学业表现">
          <div class="range-input">
            <ElInputNumber v-model="queryForm.academicPerformanceMin" :min="0" :controls="false" placeholder="最低" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.academicPerformanceMax" :min="0" :controls="false" placeholder="最高" />
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

    <div class="summary-grid" v-loading="loading" element-loading-text="正在计算心理健康核心指标...">
      <div v-for="item in summaryCards" :key="item.label" class="art-card stat-card">
        <div class="stat-card__label">{{ item.label }}</div>
        <div class="stat-card__value">{{ item.value }}</div>
        <div class="stat-card__extra">{{ item.extra }}</div>
      </div>
    </div>

    <div class="analysis-layout" v-loading="loading" element-loading-text="正在渲染心理健康可视化图表...">
      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">抑郁风险标签分布</div>
            <div class="chart-subtitle">观察存在风险、暂无明显风险和未知标签的比例</div>
          </div>
        </div>
        <div ref="depressionPieRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">性别样本结构与风险占比</div>
            <div class="chart-subtitle">分析不同性别样本量、平均社媒时长和风险占比</div>
          </div>
        </div>
        <div ref="genderPieRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">平台使用差异分析</div>
            <div class="chart-subtitle">比较不同社交平台下的样本量、风险占比、压力和焦虑水平</div>
          </div>
        </div>
        <div ref="platformBarRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">年龄层心理状态表现</div>
            <div class="chart-subtitle">按年龄区间分析社媒使用、睡眠、压力和风险占比</div>
          </div>
        </div>
        <div ref="ageSegmentRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">社媒使用时长分层</div>
            <div class="chart-subtitle">识别不同使用强度下的焦虑、成瘾和风险差异</div>
          </div>
        </div>
        <div ref="socialHoursRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">睡眠时长分层分析</div>
            <div class="chart-subtitle">观察睡眠不足、睡前屏幕暴露与心理风险之间的关系</div>
          </div>
        </div>
        <div ref="sleepSegmentRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">社交互动水平影响</div>
            <div class="chart-subtitle">比较不同社交互动水平下的风险占比、运动和学业表现</div>
          </div>
        </div>
        <div ref="interactionRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">风险标签心理画像雷达图</div>
            <div class="chart-subtitle">从社媒、睡前屏幕、压力、焦虑、成瘾、睡眠等维度刻画群体画像</div>
          </div>
        </div>
        <div ref="riskRadarRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">平台心理状态雷达图</div>
            <div class="chart-subtitle">对比主要平台用户在社媒时长、压力、焦虑和成瘾上的差异</div>
          </div>
        </div>
        <div ref="platformRadarRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">社媒时长与焦虑水平散点关系</div>
            <div class="chart-subtitle">每个点代表一条记录，横轴为每日社媒时长，纵轴为焦虑水平</div>
          </div>
        </div>
        <div ref="socialScatterRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">睡前屏幕时长与睡眠关系</div>
            <div class="chart-subtitle">观察睡前屏幕暴露是否伴随睡眠时长下降或压力升高</div>
          </div>
        </div>
        <div ref="sleepScatterRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">平台 × 抑郁风险热力矩阵</div>
            <div class="chart-subtitle">识别不同平台中风险标签的集中程度</div>
          </div>
        </div>
        <div ref="heatmapRef" class="chart-box chart-box--wide"></div>
      </div>
    </div>

    <div class="insight-section art-card" v-loading="loading" element-loading-text="正在生成心理健康数据规律解读...">
      <div class="insight-header">
        <div>
          <div class="chart-title">数据规律与价值解读</div>
          <div class="chart-subtitle">结合当前筛选条件自动总结青少年心理健康数据规律，可用于数据分析报告、论文说明或系统展示文案</div>
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
import type { EChartsOption } from 'echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchTeenAnalysis, fetchTeenOptions } from '@/api/teen'
import type {
  TeenMentalHealthAnalysisResult,
  TeenMentalHealthOptions,
  TeenMentalHealthQuery
} from '@/api/teen'

defineOptions({ name: 'TeenMentalHealthAnalysis' })

type ChartDataRow = Record<string, any>

type MixedSeriesConfig = {
  name: string
  key: string
  type: 'bar' | 'line'
  yAxisIndex?: number
}

const router = useRouter()
const loading = ref(false)
const chartMap = new Map<string, echarts.ECharts>()

const depressionPieRef = ref<HTMLElement | null>(null)
const genderPieRef = ref<HTMLElement | null>(null)
const platformBarRef = ref<HTMLElement | null>(null)
const ageSegmentRef = ref<HTMLElement | null>(null)
const socialHoursRef = ref<HTMLElement | null>(null)
const sleepSegmentRef = ref<HTMLElement | null>(null)
const interactionRef = ref<HTMLElement | null>(null)
const riskRadarRef = ref<HTMLElement | null>(null)
const platformRadarRef = ref<HTMLElement | null>(null)
const socialScatterRef = ref<HTMLElement | null>(null)
const sleepScatterRef = ref<HTMLElement | null>(null)
const heatmapRef = ref<HTMLElement | null>(null)

const options = reactive<TeenMentalHealthOptions>({
  genders: [],
  platformUsages: [],
  socialInteractionLevels: [],
  depressionLabels: []
})

const queryForm = reactive<TeenMentalHealthQuery>({
  gender: '',
  platformUsage: '',
  socialInteractionLevel: '',
  depressionLabel: undefined,
  ageMin: undefined,
  ageMax: undefined,
  dailySocialMediaHoursMin: undefined,
  dailySocialMediaHoursMax: undefined,
  sleepHoursMin: undefined,
  sleepHoursMax: undefined,
  academicPerformanceMin: undefined,
  academicPerformanceMax: undefined,
  stressLevel: undefined,
  anxietyLevel: undefined,
  addictionLevel: undefined
})

const analysisData = ref<TeenMentalHealthAnalysisResult>({
  summary: {},
  depressionDistribution: [],
  genderDistribution: [],
  platformStats: [],
  ageSegmentStats: [],
  socialMediaSegmentStats: [],
  sleepSegmentStats: [],
  socialInteractionStats: [],
  mentalRadarByRisk: [],
  platformRadarStats: [],
  socialAnxietyScatter: [],
  screenSleepScatter: [],
  platformRiskHeatmap: [],
  insights: []
})

const summaryCards = computed(() => {
  const summary = analysisData.value.summary || {}
  return [
    {
      label: '数据总量',
      value: formatNumber(summary.totalCount, 0),
      extra: '当前筛选条件下的记录数'
    },
    {
      label: '抑郁风险占比',
      value: `${formatNumber(summary.riskRate, 2)}%`,
      extra: `风险样本 ${formatNumber(summary.riskCount, 0)} 条`
    },
    {
      label: '平均社媒时长',
      value: `${formatNumber(summary.avgSocialHours, 2)}h`,
      extra: `高强度使用 ${formatNumber(summary.highSocialUseRate, 2)}%`
    },
    {
      label: '平均睡眠时长',
      value: `${formatNumber(summary.avgSleepHours, 2)}h`,
      extra: `睡眠不足 ${formatNumber(summary.sleepInsufficientRate, 2)}%`
    },
    {
      label: '平均压力水平',
      value: formatNumber(summary.avgStress, 2),
      extra: `高压力 ${formatNumber(summary.highStressRate, 2)}%`
    },
    {
      label: '平均焦虑水平',
      value: formatNumber(summary.avgAnxiety, 2),
      extra: `高焦虑 ${formatNumber(summary.highAnxietyRate, 2)}%`
    }
  ]
})

const insightList = computed(() => analysisData.value.insights || [])

const loadOptions = async () => {
  const res = await fetchTeenOptions()
  options.genders = res.genders || []
  options.platformUsages = res.platformUsages || []
  options.socialInteractionLevels = res.socialInteractionLevels || []
  options.depressionLabels = res.depressionLabels || []
}

const loadAnalysis = async () => {
  loading.value = true
  try {
    const res = await fetchTeenAnalysis({ ...queryForm })
    analysisData.value = {
      summary: res.summary || {},
      depressionDistribution: res.depressionDistribution || [],
      genderDistribution: res.genderDistribution || [],
      platformStats: res.platformStats || [],
      ageSegmentStats: res.ageSegmentStats || [],
      socialMediaSegmentStats: res.socialMediaSegmentStats || [],
      sleepSegmentStats: res.sleepSegmentStats || [],
      socialInteractionStats: res.socialInteractionStats || [],
      mentalRadarByRisk: res.mentalRadarByRisk || [],
      platformRadarStats: res.platformRadarStats || [],
      socialAnxietyScatter: res.socialAnxietyScatter || [],
      screenSleepScatter: res.screenSleepScatter || [],
      platformRiskHeatmap: res.platformRiskHeatmap || [],
      insights: res.insights || []
    }
    await nextTick()
    renderAllCharts()
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.gender = ''
  queryForm.platformUsage = ''
  queryForm.socialInteractionLevel = ''
  queryForm.depressionLabel = undefined
  queryForm.ageMin = undefined
  queryForm.ageMax = undefined
  queryForm.dailySocialMediaHoursMin = undefined
  queryForm.dailySocialMediaHoursMax = undefined
  queryForm.sleepHoursMin = undefined
  queryForm.sleepHoursMax = undefined
  queryForm.academicPerformanceMin = undefined
  queryForm.academicPerformanceMax = undefined
  queryForm.stressLevel = undefined
  queryForm.anxietyLevel = undefined
  queryForm.addictionLevel = undefined
  loadAnalysis()
}

const goBack = () => {
  router.push('/source/teen')
}

const renderAllCharts = () => {
  renderDepressionPie()
  renderGenderPie()
  renderPlatformBar()
  renderAgeSegmentChart()
  renderSocialHoursChart()
  renderSleepSegmentChart()
  renderInteractionChart()
  renderRiskRadar()
  renderPlatformRadar()
  renderSocialScatter()
  renderSleepScatter()
  renderHeatmap()
}

const renderDepressionPie = () => {
  const list = analysisData.value.depressionDistribution || []
  renderChart('depressionPie', depressionPieRef.value, {
    tooltip: { trigger: 'item', formatter: '{b}<br/>数量：{c}<br/>占比：{d}%' },
    legend: { bottom: 0 },
    series: [
      {
        name: '抑郁风险',
        type: 'pie',
        radius: ['42%', '68%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: true,
        itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
        label: { formatter: '{b}\n{d}%' },
        data: list.map((item) => ({ name: item.labelName, value: toNumber(item.totalCount) }))
      }
    ]
  })
}

const renderGenderPie = () => {
  const list = analysisData.value.genderDistribution || []
  renderChart('genderPie', genderPieRef.value, {
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const item = list[params.dataIndex] || {}
        return `${params.name}<br/>数量：${params.value}<br/>风险占比：${formatNumber(
          item.riskRate,
          2
        )}%<br/>平均社媒：${formatNumber(item.avgSocialHours, 2)}h`
      }
    },
    legend: { bottom: 0 },
    series: [
      {
        name: '性别结构',
        type: 'pie',
        radius: [20, 105],
        center: ['50%', '45%'],
        roseType: 'radius',
        label: { formatter: '{b}\n{d}%' },
        data: list.map((item) => ({ name: item.labelName, value: toNumber(item.totalCount) }))
      }
    ]
  })
}

const renderPlatformBar = () => {
  const list = analysisData.value.platformStats || []
  const names = list.map((item) => item.labelName)
  renderChart('platformBar', platformBarRef.value, {
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: 48, right: 50, top: 58, bottom: 72 },
    xAxis: {
      type: 'category',
      data: names,
      axisLabel: { interval: 0, rotate: names.length > 6 ? 25 : 0 }
    },
    yAxis: [
      { type: 'value', name: '数量/均值' },
      { type: 'value', name: '风险占比(%)' }
    ],
    dataZoom: [{ type: 'inside' }, { type: 'slider', height: 18, bottom: 22 }],
    series: [
      {
        name: '样本量',
        type: 'bar',
        data: list.map((item) => toNumber(item.totalCount)),
        barMaxWidth: 28
      },
      {
        name: '平均压力',
        type: 'bar',
        data: list.map((item) => toNumber(item.avgStress)),
        barMaxWidth: 28
      },
      {
        name: '平均焦虑',
        type: 'bar',
        data: list.map((item) => toNumber(item.avgAnxiety)),
        barMaxWidth: 28
      },
      {
        name: '风险占比',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.riskRate))
      }
    ]
  })
}

const renderAgeSegmentChart = () => {
  const list = analysisData.value.ageSegmentStats || []
  const names = list.map((item) => item.segmentName)
  renderChart(
    'ageSegment',
    ageSegmentRef.value,
    createMixedOption(
      names,
      [
        { name: '平均社媒时长', key: 'avgSocialHours', type: 'bar' },
        { name: '平均睡眠', key: 'avgSleepHours', type: 'bar' },
        { name: '平均压力', key: 'avgStress', type: 'line' },
        { name: '风险占比', key: 'riskRate', type: 'line', yAxisIndex: 1 }
      ],
      list
    )
  )
}

const renderSocialHoursChart = () => {
  const list = analysisData.value.socialMediaSegmentStats || []
  const names = list.map((item) => item.segmentName)
  renderChart(
    'socialHours',
    socialHoursRef.value,
    createMixedOption(
      names,
      [
        { name: '样本量', key: 'totalCount', type: 'bar' },
        { name: '平均焦虑', key: 'avgAnxiety', type: 'bar' },
        { name: '平均成瘾', key: 'avgAddiction', type: 'bar' },
        { name: '风险占比', key: 'riskRate', type: 'line', yAxisIndex: 1 }
      ],
      list
    )
  )
}

const renderSleepSegmentChart = () => {
  const list = analysisData.value.sleepSegmentStats || []
  const names = list.map((item) => item.segmentName)
  renderChart(
    'sleepSegment',
    sleepSegmentRef.value,
    createMixedOption(
      names,
      [
        { name: '平均睡前屏幕', key: 'avgScreenBeforeSleep', type: 'bar' },
        { name: '平均学业表现', key: 'avgAcademicPerformance', type: 'bar' },
        { name: '平均压力', key: 'avgStress', type: 'line' },
        { name: '风险占比', key: 'riskRate', type: 'line', yAxisIndex: 1 }
      ],
      list
    )
  )
}

const renderInteractionChart = () => {
  const list = analysisData.value.socialInteractionStats || []
  const names = list.map((item) => item.labelName)
  renderChart(
    'interaction',
    interactionRef.value,
    createMixedOption(
      names,
      [
        { name: '样本量', key: 'totalCount', type: 'bar' },
        { name: '平均运动', key: 'avgPhysicalActivity', type: 'bar' },
        { name: '平均学业', key: 'avgAcademicPerformance', type: 'bar' },
        { name: '风险占比', key: 'riskRate', type: 'line', yAxisIndex: 1 }
      ],
      list
    )
  )
}

const renderRiskRadar = () => {
  const list = analysisData.value.mentalRadarByRisk || []
  renderChart('riskRadar', riskRadarRef.value, createRadarOption(list, '风险标签画像'))
}

const renderPlatformRadar = () => {
  const list = analysisData.value.platformRadarStats || []
  renderChart('platformRadar', platformRadarRef.value, createRadarOption(list, '平台画像'))
}

const renderSocialScatter = () => {
  const list = analysisData.value.socialAnxietyScatter || []
  renderChart('socialScatter', socialScatterRef.value, {
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const row = params.data?.raw || {}
        return `平台：${row.platformName || '未知'}<br/>社媒时长：${formatNumber(
          row.socialHours,
          2
        )}h<br/>焦虑：${formatNumber(row.anxietyLevel, 0)}<br/>压力：${formatNumber(
          row.stressLevel,
          0
        )}<br/>成瘾：${formatNumber(row.addictionLevel, 0)}<br/>风险：${formatDepressionLabel(
          row.depressionLabel
        )}`
      }
    },
    grid: { left: 50, right: 24, top: 42, bottom: 46 },
    xAxis: { type: 'value', name: '社媒时长/h' },
    yAxis: { type: 'value', name: '焦虑水平' },
    series: [
      {
        name: '社媒-焦虑',
        type: 'scatter',
        symbolSize: (value: number[]) => Math.max(8, Math.min(24, 8 + value[2] * 1.5)),
        data: list.map((item) => ({
          value: [
            toNumber(item.socialHours),
            toNumber(item.anxietyLevel),
            toNumber(item.stressLevel)
          ],
          raw: item
        }))
      }
    ]
  })
}

const renderSleepScatter = () => {
  const list = analysisData.value.screenSleepScatter || []
  renderChart('sleepScatter', sleepScatterRef.value, {
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const row = params.data?.raw || {}
        return `平台：${row.platformName || '未知'}<br/>睡前屏幕：${formatNumber(
          row.screenBeforeSleep,
          2
        )}h<br/>睡眠：${formatNumber(row.sleepHours, 2)}h<br/>压力：${formatNumber(
          row.stressLevel,
          0
        )}<br/>风险：${formatDepressionLabel(row.depressionLabel)}`
      }
    },
    grid: { left: 50, right: 24, top: 42, bottom: 46 },
    xAxis: { type: 'value', name: '睡前屏幕/h' },
    yAxis: { type: 'value', name: '睡眠/h' },
    series: [
      {
        name: '睡前屏幕-睡眠',
        type: 'scatter',
        symbolSize: (value: number[]) => Math.max(8, Math.min(24, 8 + value[2] * 1.5)),
        data: list.map((item) => ({
          value: [
            toNumber(item.screenBeforeSleep),
            toNumber(item.sleepHours),
            toNumber(item.stressLevel)
          ],
          raw: item
        }))
      }
    ]
  })
}

const renderHeatmap = () => {
  const list = analysisData.value.platformRiskHeatmap || []
  const platforms = Array.from(new Set(list.map((item) => String(item.platformName || '未知平台'))))
  const risks = Array.from(new Set(list.map((item) => String(item.riskName || '未知'))))
  const data = list.map((item) => [
    platforms.indexOf(String(item.platformName || '未知平台')),
    risks.indexOf(String(item.riskName || '未知')),
    toNumber(item.totalCount)
  ])

  renderChart('heatmap', heatmapRef.value, {
    tooltip: {
      position: 'top',
      formatter: (params: any) =>
        `${platforms[params.data[0]]}<br/>${risks[params.data[1]]}<br/>数量：${params.data[2]}`
    },
    grid: { left: 96, right: 24, top: 36, bottom: 72 },
    xAxis: {
      type: 'category',
      data: platforms,
      splitArea: { show: true },
      axisLabel: { interval: 0, rotate: platforms.length > 6 ? 25 : 0 }
    },
    yAxis: { type: 'category', data: risks, splitArea: { show: true } },
    visualMap: {
      min: 0,
      max: Math.max(1, ...data.map((item) => Number(item[2]))),
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: 8
    },
    series: [
      {
        name: '平台风险热力',
        type: 'heatmap',
        data,
        label: { show: true },
        emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 0, 0, 0.2)' } }
      }
    ]
  })
}

const createMixedOption = (
  names: string[],
  configs: MixedSeriesConfig[],
  list: ChartDataRow[]
): EChartsOption => ({
  tooltip: { trigger: 'axis' },
  legend: { top: 0 },
  grid: { left: 48, right: 48, top: 58, bottom: 50 },
  xAxis: {
    type: 'category',
    data: names,
    axisLabel: { interval: 0, rotate: names.length > 5 ? 25 : 0 }
  },
  yAxis: [
    { type: 'value', name: '数量/均值' },
    { type: 'value', name: '占比(%)' }
  ],
  series: configs.map((config) => ({
    name: config.name,
    type: config.type,
    yAxisIndex: config.yAxisIndex || 0,
    smooth: config.type === 'line',
    barMaxWidth: 30,
    data: list.map((item) => toNumber(item[config.key]))
  }))
})

const createRadarOption = (list: ChartDataRow[], title: string): EChartsOption => ({
  tooltip: { trigger: 'item' },
  legend: { bottom: 0 },
  radar: {
    radius: '62%',
    center: ['50%', '45%'],
    indicator: [
      { name: '社媒时长', max: 10 },
      { name: '睡前屏幕', max: 10 },
      { name: '压力', max: 10 },
      { name: '焦虑', max: 10 },
      { name: '成瘾', max: 10 },
      { name: '睡眠', max: 10 }
    ]
  },
  series: [
    {
      name: title,
      type: 'radar',
      data: list.map((item) => ({
        name: String(item.labelName || '未知'),
        value: [
          toNumber(item.socialMediaHours),
          toNumber(item.screenBeforeSleep),
          toNumber(item.stressLevel),
          toNumber(item.anxietyLevel),
          toNumber(item.addictionLevel),
          toNumber(item.sleepHoursScore)
        ]
      }))
    }
  ]
})

const renderChart = (key: string, el: HTMLElement | null, option: EChartsOption) => {
  if (!el) return
  let chart = chartMap.get(key)
  if (!chart) {
    chart = echarts.init(el)
    chartMap.set(key, chart)
  }
  chart.setOption(option, true)
}

const resizeCharts = () => {
  chartMap.forEach((chart) => chart.resize())
}

const formatNumber = (value?: number | string, precision = 2) => {
  if (value === null || value === undefined || value === '') return precision === 0 ? '0' : '0.00'
  const num = Number(value)
  if (!Number.isFinite(num)) return precision === 0 ? '0' : '0.00'
  return num.toFixed(precision)
}

const toNumber = (value?: number | string) => {
  const num = Number(value)
  return Number.isFinite(num) ? num : 0
}

const formatDepressionLabel = (value?: number | string) => {
  const num = Number(value)
  if (num === 1) return '存在风险'
  if (num === 0) return '暂无明显风险'
  return '未知'
}

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
.teen-analysis-page {
  padding-bottom: 24px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 22px 24px;
  margin-bottom: 20px;
}

.page-header__left {
  min-width: 0;
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--art-text-gray-900, #111827);
  line-height: 1.2;
}

.page-desc {
  max-width: 980px;
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
  gap: 8px;
  width: 218px;

  :deep(.el-input-number) {
    width: 100px;
  }
}

.range-line {
  color: var(--art-text-gray-400, #98a2b3);
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  height: 132px;
  padding: 20px;
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
  margin-top: 12px;
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
  color: var(--art-text-gray-900, #111827);
}

.stat-card__extra {
  margin-top: 12px;
  font-size: 12px;
  color: var(--art-text-gray-500, #98a2b3);
  line-height: 1.5;
}

.analysis-layout {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.chart-card {
  min-width: 0;
  padding: 20px;
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

.insight-header {
  margin-bottom: 16px;
}

.insight-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
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
  flex: 0 0 auto;
  width: 28px;
  height: 28px;
  border-radius: 999px;
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
  min-width: 0;
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
