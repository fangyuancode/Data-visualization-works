<template>
  <div class="tvplay-analysis-page">
    <div class="page-header art-card">
      <div class="page-header__left">
        <div class="page-title">电视剧播放量数据可视化分析</div>
        <div class="page-desc">
          围绕电视剧类型、播放热度、用户点赞、差评反馈、评分口碑和采集时间趋势进行数据挖掘，识别不同题材内容的传播能力、口碑表现和商业价值。
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
      <ElForm :model="queryForm" inline label-width="86px" class="filter-form">
        <ElFormItem label="剧名">
          <ElInput v-model="queryForm.dramaName" placeholder="请输入剧名" clearable style="width: 170px" />
        </ElFormItem>

        <ElFormItem label="类型">
          <ElSelect v-model="queryForm.dramaType" placeholder="全部类型" clearable filterable style="width: 150px">
            <ElOption v-for="item in options.dramaTypes" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="播放量">
          <div class="range-input range-input--wide">
            <ElInputNumber v-model="queryForm.playCountMin" :min="0" :controls="false" placeholder="最低" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.playCountMax" :min="0" :controls="false" placeholder="最高" />
          </div>
        </ElFormItem>

        <ElFormItem label="评分">
          <div class="range-input">
            <ElInputNumber v-model="queryForm.scoreMin" :min="0" :max="10" :precision="1" :controls="false" placeholder="最低" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.scoreMax" :min="0" :max="10" :precision="1" :controls="false" placeholder="最高" />
          </div>
        </ElFormItem>

        <ElFormItem label="采集时间">
          <ElDatePicker
            v-model="collectRange"
            type="datetimerange"
            value-format="YYYY-MM-DD HH:mm:ss"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 340px"
            @change="handleCollectRangeChange"
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

    <div class="summary-grid" v-loading="loading" element-loading-text="正在计算播放量核心指标...">
      <div v-for="item in summaryCards" :key="item.label" class="art-card stat-card">
        <div class="stat-card__label">{{ item.label }}</div>
        <div class="stat-card__value">{{ item.value }}</div>
        <div class="stat-card__extra">{{ item.extra }}</div>
      </div>
    </div>

    <div class="analysis-layout" v-loading="loading" element-loading-text="正在渲染电视剧播放量可视化图表...">
      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">电视剧类型播放占比</div>
            <div class="chart-subtitle">按题材类型统计播放量贡献与样本结构</div>
          </div>
        </div>
        <div ref="typePieRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">评分区间结构</div>
            <div class="chart-subtitle">观察高分、中分和低分剧集的数量占比与平均播放量</div>
          </div>
        </div>
        <div ref="scorePieRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">类型播放热度与口碑对比</div>
            <div class="chart-subtitle">比较不同题材的总播放量、平均评分和好评倾向</div>
          </div>
        </div>
        <div ref="typeBarRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">播放量区间分布</div>
            <div class="chart-subtitle">识别头部爆款、中腰部剧集与长尾剧集的分布结构</div>
          </div>
        </div>
        <div ref="playSegmentRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">评分区间互动反馈</div>
            <div class="chart-subtitle">对比不同评分段的点赞、差评和好评倾向</div>
          </div>
        </div>
        <div ref="interactionRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">采集月份播放趋势</div>
            <div class="chart-subtitle">按采集时间观察播放量、样本量和评分变化</div>
          </div>
        </div>
        <div ref="monthTrendRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">题材综合表现雷达图</div>
            <div class="chart-subtitle">从播放、评分、好评、低争议、互动和供给数量刻画题材画像</div>
          </div>
        </div>
        <div ref="typeRadarRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">播放量 TOP10 剧集</div>
            <div class="chart-subtitle">定位头部爆款剧集及其评分表现</div>
          </div>
        </div>
        <div ref="topRankRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">评分与播放量散点关系</div>
            <div class="chart-subtitle">横轴为评分，纵轴为播放量，可观察高口碑高播放、低口碑高播放等样本</div>
          </div>
        </div>
        <div ref="scatterRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">类型 × 评分区间热力矩阵</div>
            <div class="chart-subtitle">识别不同题材在各评分区间的集中程度</div>
          </div>
        </div>
        <div ref="heatmapRef" class="chart-box chart-box--wide"></div>
      </div>
    </div>

    <div class="insight-section art-card" v-loading="loading" element-loading-text="正在生成播放量数据规律解读...">
      <div class="insight-header">
        <div>
          <div class="chart-title">数据规律与价值解读</div>
          <div class="chart-subtitle">结合当前筛选条件自动总结电视剧播放量数据规律，可用于数据分析报告、论文说明或系统展示文案</div>
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
import { fetchTvPlayAnalysis, fetchTvPlayOptions } from '@/api/tvplay'
import type { TvDramaAnalysisItem, TvDramaPlayAnalysisResult, TvDramaPlayOptions, TvDramaPlayQuery } from '@/api/tvplay'

defineOptions({ name: 'TvDramaPlayAnalysis' })

const router = useRouter()
const loading = ref(false)
const chartMap = new Map<string, echarts.ECharts>()

const typePieRef = ref<HTMLElement | null>(null)
const scorePieRef = ref<HTMLElement | null>(null)
const typeBarRef = ref<HTMLElement | null>(null)
const playSegmentRef = ref<HTMLElement | null>(null)
const interactionRef = ref<HTMLElement | null>(null)
const monthTrendRef = ref<HTMLElement | null>(null)
const typeRadarRef = ref<HTMLElement | null>(null)
const topRankRef = ref<HTMLElement | null>(null)
const scatterRef = ref<HTMLElement | null>(null)
const heatmapRef = ref<HTMLElement | null>(null)

const collectRange = ref<string[]>([])

const options = reactive<TvDramaPlayOptions>({
  dramaTypes: []
})

const queryForm = reactive<TvDramaPlayQuery>({
  dramaName: '',
  dramaType: '',
  playCountMin: undefined,
  playCountMax: undefined,
  likeCountMin: undefined,
  likeCountMax: undefined,
  negativeCountMin: undefined,
  negativeCountMax: undefined,
  scoreMin: undefined,
  scoreMax: undefined,
  collectStartTime: '',
  collectEndTime: ''
})

const analysisData = ref<TvDramaPlayAnalysisResult>({
  summary: {},
  typeDistribution: [],
  scoreSegmentStats: [],
  playSegmentStats: [],
  monthTrend: [],
  typeRadarStats: [],
  topDramaRank: [],
  scorePlayScatter: [],
  typeScoreHeatmap: [],
  interactionByScoreSegment: [],
  insights: []
})

const summaryCards = computed(() => {
  const summary = analysisData.value.summary || {}
  return [
    {
      label: '数据总量',
      value: formatNumber(summary.totalCount, 0),
      extra: '当前筛选条件下的剧集记录数'
    },
    {
      label: '累计播放量',
      value: formatCount(summary.totalPlayCount),
      extra: `平均播放 ${formatCount(summary.avgPlayCount)}`
    },
    {
      label: '平均评分',
      value: formatNumber(summary.avgScore, 2),
      extra: `高分剧占比 ${formatNumber(summary.highScoreRate, 2)}%`
    },
    {
      label: '好评倾向',
      value: `${formatNumber(summary.positiveRate, 2)}%`,
      extra: `点赞 ${formatCount(summary.totalLikeCount)} / 差评 ${formatCount(summary.totalNegativeCount)}`
    },
    {
      label: '最高播放量',
      value: formatCount(summary.maxPlayCount),
      extra: '当前条件下头部剧集播放峰值'
    },
    {
      label: '亿级播放占比',
      value: `${formatNumber(summary.billionDramaRate, 2)}%`,
      extra: '播放量大于等于 1 亿的剧集比例'
    }
  ]
})

const insightList = computed(() => analysisData.value.insights || [])

const loadOptions = async () => {
  const res = await fetchTvPlayOptions()
  options.dramaTypes = res.dramaTypes || []
}

const loadAnalysis = async () => {
  loading.value = true
  try {
    const res = await fetchTvPlayAnalysis({ ...queryForm })
    analysisData.value = {
      summary: res.summary || {},
      typeDistribution: res.typeDistribution || [],
      scoreSegmentStats: res.scoreSegmentStats || [],
      playSegmentStats: res.playSegmentStats || [],
      monthTrend: res.monthTrend || [],
      typeRadarStats: res.typeRadarStats || [],
      topDramaRank: res.topDramaRank || [],
      scorePlayScatter: res.scorePlayScatter || [],
      typeScoreHeatmap: res.typeScoreHeatmap || [],
      interactionByScoreSegment: res.interactionByScoreSegment || [],
      insights: res.insights || []
    }
    await nextTick()
    renderAllCharts()
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.dramaName = ''
  queryForm.dramaType = ''
  queryForm.playCountMin = undefined
  queryForm.playCountMax = undefined
  queryForm.likeCountMin = undefined
  queryForm.likeCountMax = undefined
  queryForm.negativeCountMin = undefined
  queryForm.negativeCountMax = undefined
  queryForm.scoreMin = undefined
  queryForm.scoreMax = undefined
  queryForm.collectStartTime = ''
  queryForm.collectEndTime = ''
  collectRange.value = []
  loadAnalysis()
}

const handleCollectRangeChange = (value: string[] | null) => {
  queryForm.collectStartTime = value?.[0] || ''
  queryForm.collectEndTime = value?.[1] || ''
}

const goBack = () => {
  router.push('/source/tvplay')
}

const renderAllCharts = () => {
  renderTypePie()
  renderScorePie()
  renderTypeBar()
  renderPlaySegment()
  renderInteraction()
  renderMonthTrend()
  renderTypeRadar()
  renderTopRank()
  renderScatter()
  renderHeatmap()
}

const renderTypePie = () => {
  const list = analysisData.value.typeDistribution || []
  renderChart('typePie', typePieRef.value, {
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const item = list[params.dataIndex] || {}
        return `${params.name}<br/>播放量：${formatCount(item.totalPlayCount)}<br/>剧集数：${formatNumber(item.totalCount, 0)}<br/>平均评分：${formatNumber(item.avgScore, 2)}<br/>占比：${params.percent}%`
      }
    },
    legend: { bottom: 0, type: 'scroll' },
    series: [
      {
        name: '类型播放占比',
        type: 'pie',
        radius: ['42%', '68%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: true,
        itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
        label: { formatter: '{b}\n{d}%' },
        data: list.map((item) => ({ name: item.labelName || '未知类型', value: toNumber(item.totalPlayCount) }))
      }
    ]
  })
}

const renderScorePie = () => {
  const list = analysisData.value.scoreSegmentStats || []
  renderChart('scorePie', scorePieRef.value, {
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const item = list[params.dataIndex] || {}
        return `${params.name}<br/>数量：${params.value}<br/>平均播放：${formatCount(item.avgPlayCount)}<br/>好评倾向：${formatNumber(item.positiveRate, 2)}%`
      }
    },
    legend: { bottom: 0 },
    series: [
      {
        name: '评分区间',
        type: 'pie',
        radius: [20, 105],
        center: ['50%', '45%'],
        roseType: 'radius',
        label: { formatter: '{b}\n{d}%' },
        data: list.map((item) => ({ name: item.segmentName || '未知评分', value: toNumber(item.totalCount) }))
      }
    ]
  })
}

const renderTypeBar = () => {
  const list = (analysisData.value.typeDistribution || []).slice(0, 12)
  const names = list.map((item) => item.labelName || '未知类型')
  renderChart('typeBar', typeBarRef.value, {
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: 56, right: 58, top: 58, bottom: 78 },
    xAxis: { type: 'category', data: names, axisLabel: { interval: 0, rotate: names.length > 6 ? 28 : 0 } },
    yAxis: [
      { type: 'value', name: '播放量', axisLabel: { formatter: (value: number) => formatCount(value) } },
      { type: 'value', name: '评分/好评率' }
    ],
    dataZoom: [{ type: 'inside' }, { type: 'slider', height: 18, bottom: 24 }],
    series: [
      { name: '总播放量', type: 'bar', barMaxWidth: 32, data: list.map((item) => toNumber(item.totalPlayCount)) },
      { name: '平均评分', type: 'line', yAxisIndex: 1, smooth: true, data: list.map((item) => toNumber(item.avgScore)) },
      { name: '好评倾向', type: 'line', yAxisIndex: 1, smooth: true, data: list.map((item) => toNumber(item.positiveRate)) }
    ]
  })
}

const renderPlaySegment = () => {
  const list = analysisData.value.playSegmentStats || []
  const names = list.map((item) => item.segmentName || '未知播放量')
  renderChart('playSegment', playSegmentRef.value, createMixedOption(names, [
    { name: '剧集数量', key: 'totalCount', type: 'bar' },
    { name: '平均评分', key: 'avgScore', type: 'line', yAxisIndex: 1 },
    { name: '好评倾向', key: 'positiveRate', type: 'line', yAxisIndex: 1 }
  ], list))
}

const renderInteraction = () => {
  const list = analysisData.value.interactionByScoreSegment || []
  const names = list.map((item) => item.segmentName || '未知评分')
  renderChart('interaction', interactionRef.value, {
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: 54, right: 54, top: 58, bottom: 42 },
    xAxis: { type: 'category', data: names },
    yAxis: [
      { type: 'value', name: '互动量', axisLabel: { formatter: (value: number) => formatCount(value) } },
      { type: 'value', name: '比例(%)' }
    ],
    series: [
      { name: '点赞量', type: 'bar', stack: 'interaction', barMaxWidth: 28, data: list.map((item) => toNumber(item.totalLikeCount)) },
      { name: '差评量', type: 'bar', stack: 'interaction', barMaxWidth: 28, data: list.map((item) => toNumber(item.totalNegativeCount)) },
      { name: '好评倾向', type: 'line', yAxisIndex: 1, smooth: true, data: list.map((item) => toNumber(item.positiveRate)) }
    ]
  })
}

const renderMonthTrend = () => {
  const list = analysisData.value.monthTrend || []
  const names = list.map((item) => item.monthName || '未知月份')
  renderChart('monthTrend', monthTrendRef.value, {
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: 58, right: 58, top: 58, bottom: 72 },
    xAxis: { type: 'category', data: names, axisLabel: { interval: 0, rotate: names.length > 6 ? 25 : 0 } },
    yAxis: [
      { type: 'value', name: '播放量/数量', axisLabel: { formatter: (value: number) => formatCount(value) } },
      { type: 'value', name: '评分/比例' }
    ],
    dataZoom: [{ type: 'inside' }, { type: 'slider', height: 18, bottom: 24 }],
    series: [
      { name: '总播放量', type: 'bar', barMaxWidth: 30, data: list.map((item) => toNumber(item.totalPlayCount)) },
      { name: '剧集数量', type: 'bar', barMaxWidth: 30, data: list.map((item) => toNumber(item.totalCount)) },
      { name: '平均评分', type: 'line', yAxisIndex: 1, smooth: true, data: list.map((item) => toNumber(item.avgScore)) },
      { name: '好评倾向', type: 'line', yAxisIndex: 1, smooth: true, data: list.map((item) => toNumber(item.positiveRate)) }
    ]
  })
}

const renderTypeRadar = () => {
  const list = analysisData.value.typeRadarStats || []
  const maxAvgPlay = getMax(list, 'avgPlayCount')
  const maxAvgLike = getMax(list, 'avgLikeCount')
  const maxCount = getMax(list, 'totalCount')

  renderChart('typeRadar', typeRadarRef.value, {
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, type: 'scroll' },
    radar: {
      center: ['50%', '46%'],
      radius: '62%',
      indicator: [
        { name: '播放指数', max: 100 },
        { name: '评分口碑', max: 100 },
        { name: '好评倾向', max: 100 },
        { name: '低争议度', max: 100 },
        { name: '互动指数', max: 100 },
        { name: '供给数量', max: 100 }
      ]
    },
    series: [
      {
        name: '题材画像',
        type: 'radar',
        data: list.map((item) => ({
          name: item.labelName || '未知类型',
          value: [
            normalizeValue(item.avgPlayCount, maxAvgPlay),
            Math.min(toNumber(item.avgScore) * 10, 100),
            Math.min(toNumber(item.positiveRate), 100),
            Math.max(0, 100 - toNumber(item.controversyRate)),
            normalizeValue(item.avgLikeCount, maxAvgLike),
            normalizeValue(item.totalCount, maxCount)
          ]
        }))
      }
    ]
  })
}

const renderTopRank = () => {
  const list = analysisData.value.topDramaRank || []
  const names = list.map((item) => item.dramaName || '未知剧名').reverse()
  renderChart('topRank', topRankRef.value, {
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const index = list.length - 1 - params[0].dataIndex
        const item = list[index] || {}
        return `${item.dramaName || '未知剧名'}<br/>类型：${item.dramaType || '-'}<br/>播放量：${formatCount(item.playCount)}<br/>评分：${formatNumber(item.score, 2)}<br/>好评倾向：${formatNumber(item.positiveRate, 2)}%`
      }
    },
    grid: { left: 110, right: 42, top: 30, bottom: 36 },
    xAxis: { type: 'value', axisLabel: { formatter: (value: number) => formatCount(value) } },
    yAxis: { type: 'category', data: names, axisLabel: { width: 92, overflow: 'truncate' } },
    series: [
      {
        name: '播放量',
        type: 'bar',
        barMaxWidth: 22,
        label: { show: true, position: 'right', formatter: (params: any) => formatCount(params.value) },
        data: list.map((item) => toNumber(item.playCount)).reverse()
      }
    ]
  })
}

const renderScatter = () => {
  const list = analysisData.value.scorePlayScatter || []
  renderChart('scatter', scatterRef.value, {
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const item = params.data?.raw || {}
        return `${item.dramaName || '未知剧名'}<br/>类型：${item.dramaType || '-'}<br/>评分：${formatNumber(item.score, 2)}<br/>播放量：${formatCount(item.playCount)}<br/>好评倾向：${formatNumber(item.positiveRate, 2)}%`
      }
    },
    grid: { left: 62, right: 48, top: 42, bottom: 50 },
    xAxis: { type: 'value', name: '评分', min: 0, max: 10 },
    yAxis: { type: 'value', name: '播放量', axisLabel: { formatter: (value: number) => formatCount(value) } },
    series: [
      {
        name: '剧集样本',
        type: 'scatter',
        symbolSize: (value: number[]) => Math.max(8, Math.min(32, Math.sqrt(value[1] || 0) / 1500)),
        data: list.map((item) => ({
          value: [toNumber(item.score), toNumber(item.playCount)],
          raw: item
        }))
      }
    ]
  })
}

const renderHeatmap = () => {
  const list = analysisData.value.typeScoreHeatmap || []
  const typeNames = unique(list.map((item) => item.labelName || '未知类型')).slice(0, 12)
  const scoreSegments = ['6分以下', '6-7分', '7-8分', '8-9分', '9分及以上', '未知评分']
  const data = list
    .filter((item) => typeNames.includes(item.labelName || '未知类型'))
    .map((item) => [
      scoreSegments.indexOf(item.scoreSegment || '未知评分'),
      typeNames.indexOf(item.labelName || '未知类型'),
      toNumber(item.totalCount)
    ])
    .filter((item) => item[0] >= 0 && item[1] >= 0)

  renderChart('heatmap', heatmapRef.value, {
    tooltip: {
      position: 'top',
      formatter: (params: any) => `${typeNames[params.data[1]]}<br/>${scoreSegments[params.data[0]]}<br/>数量：${params.data[2]}`
    },
    grid: { left: 96, right: 36, top: 46, bottom: 58 },
    xAxis: { type: 'category', data: scoreSegments, splitArea: { show: true } },
    yAxis: { type: 'category', data: typeNames, splitArea: { show: true }, axisLabel: { width: 82, overflow: 'truncate' } },
    visualMap: {
      min: 0,
      max: Math.max(1, ...data.map((item) => item[2])),
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: 8
    },
    series: [
      {
        name: '数量',
        type: 'heatmap',
        data,
        label: { show: true },
        emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 0, 0, 0.25)' } }
      }
    ]
  })
}

const createMixedOption = (names: string[], seriesConfig: Array<{ name: string; key: string; type: string; yAxisIndex?: number }>, list: TvDramaAnalysisItem[]) => ({
  tooltip: { trigger: 'axis' },
  legend: { top: 0 },
  grid: { left: 52, right: 48, top: 58, bottom: 42 },
  xAxis: { type: 'category', data: names, axisLabel: { interval: 0, rotate: names.length > 5 ? 20 : 0 } },
  yAxis: [
    { type: 'value', name: '数量/均值' },
    { type: 'value', name: '比例/评分' }
  ],
  series: seriesConfig.map((item) => ({
    name: item.name,
    type: item.type,
    yAxisIndex: item.yAxisIndex || 0,
    smooth: item.type === 'line',
    barMaxWidth: 28,
    data: list.map((row) => toNumber(row[item.key]))
  }))
})

const renderChart = (key: string, el: HTMLElement | null, option: any) => {
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

const destroyCharts = () => {
  chartMap.forEach((chart) => chart.dispose())
  chartMap.clear()
}

const toNumber = (value: unknown) => {
  const num = Number(value)
  return Number.isFinite(num) ? num : 0
}

const getMax = (list: TvDramaAnalysisItem[], key: string) => {
  const max = Math.max(0, ...list.map((item) => toNumber(item[key])))
  return max || 1
}

const normalizeValue = (value: unknown, max: number) => {
  if (!max) return 0
  return Math.min(100, Math.round((toNumber(value) / max) * 100))
}

const unique = (list: string[]) => Array.from(new Set(list.filter(Boolean)))

const formatNumber = (value?: number | string, precision = 2) => {
  if (value === null || value === undefined || value === '') return precision === 0 ? '0' : '0.00'

  const num = Number(value)
  if (!Number.isFinite(num)) return precision === 0 ? '0' : '0.00'

  return num.toLocaleString('zh-CN', {
    minimumFractionDigits: precision,
    maximumFractionDigits: precision
  })
}

const formatCount = (value?: number | string) => {
  const num = Number(value || 0)

  if (!Number.isFinite(num)) return '-'

  if (num >= 100000000) {
    return `${(num / 100000000).toFixed(2)}亿`
  }

  if (num >= 10000) {
    return `${(num / 10000).toFixed(2)}万`
  }

  return num.toLocaleString('zh-CN')
}

onMounted(async () => {
  window.addEventListener('resize', resizeCharts)
  await loadOptions()
  await loadAnalysis()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  destroyCharts()
})
</script>

<style scoped lang="scss">
.tvplay-analysis-page {
  .art-card {
    border-radius: 14px;
    background: #fff;
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

.page-header__left {
  min-width: 0;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--art-text-gray-900, #111827);
  line-height: 1.25;
}

.page-desc {
  max-width: 920px;
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
  min-height: 126px;
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
  font-size: 26px;
  font-weight: 700;
  color: var(--art-text-gray-900, #111827);
  line-height: 1.1;
}

.stat-card__extra {
  margin-top: 10px;
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
  min-height: 430px;
  padding: 20px;
  overflow: hidden;
}

.chart-card--wide {
  grid-column: span 2;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 12px;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
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
  height: 342px;
}

.chart-box--wide {
  height: 360px;
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
  flex: 0 0 28px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #2563eb;
  color: #fff;
  font-size: 13px;
  font-weight: 700;
}

.insight-text {
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

  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .analysis-layout {
    grid-template-columns: 1fr;
  }

  .chart-card--wide {
    grid-column: span 1;
  }

  .insight-list {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 576px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }

  .chart-card {
    padding: 16px;
  }
}
</style>
