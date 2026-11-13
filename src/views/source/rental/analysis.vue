<template>
  <div class="rental-analysis-page">
    <div class="page-header art-card">
      <div class="page-header__left">
        <div class="page-title">北京房屋租赁数据可视化分析</div>
        <div class="page-desc">
          基于租金、面积、户型、区域、楼层、地铁与供暖方式进行数据挖掘，结合北京行政区地图展示区域供给、价格梯度、交通溢价与租赁价值规律。
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
      <ElForm :model="queryForm" inline label-width="80px" class="filter-form">
        <ElFormItem label="区域">
          <ElSelect v-model="queryForm.region" placeholder="全部区域" clearable filterable style="width: 150px">
            <ElOption v-for="item in options.regions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="房间类型">
          <ElSelect v-model="queryForm.room" placeholder="全部房型" clearable filterable style="width: 150px">
            <ElOption v-for="item in options.rooms" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="楼层">
          <ElSelect v-model="queryForm.floorGrp" placeholder="全部楼层" clearable filterable style="width: 140px">
            <ElOption v-for="item in options.floorGrps" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="地铁">
          <ElSelect v-model="queryForm.subway" placeholder="全部" clearable filterable style="width: 130px">
            <ElOption v-for="item in options.subways" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="供暖">
          <ElSelect v-model="queryForm.heating" placeholder="全部" clearable filterable style="width: 130px">
            <ElOption v-for="item in options.heatings" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="卧室">
          <ElInputNumber v-model="queryForm.bedroom" :min="0" :controls="false" placeholder="数量" style="width: 100px" />
        </ElFormItem>

        <ElFormItem label="租金">
          <div class="range-input range-input--wide">
            <ElInputNumber v-model="queryForm.rentMin" :min="0" :controls="false" placeholder="最低" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.rentMax" :min="0" :controls="false" placeholder="最高" />
          </div>
        </ElFormItem>

        <ElFormItem label="面积">
          <div class="range-input">
            <ElInputNumber v-model="queryForm.areaMin" :min="0" :controls="false" placeholder="最小" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.areaMax" :min="0" :controls="false" placeholder="最大" />
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

    <div class="summary-grid" v-loading="loading" element-loading-text="正在计算租赁核心指标...">
      <div v-for="item in summaryCards" :key="item.label" class="art-card stat-card">
        <div class="stat-card__label">{{ item.label }}</div>
        <div class="stat-card__value">{{ item.value }}</div>
        <div class="stat-card__extra">{{ item.extra }}</div>
      </div>
    </div>

    <div class="analysis-layout" v-loading="loading" element-loading-text="正在渲染房屋租赁可视化图表...">
      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">北京区域租金热力地图</div>
            <div class="chart-subtitle">基于北京行政区 GeoJSON 展示各区域平均租金，颜色越深表示平均租金越高</div>
          </div>
        </div>
        <div ref="mapRef" class="chart-box chart-box--map"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">区域租金与供给排行</div>
            <div class="chart-subtitle">对比各区平均租金、单位租金与房源数量，识别高价值区域</div>
          </div>
        </div>
        <div ref="regionRankRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">租金区间分布</div>
            <div class="chart-subtitle">观察北京租赁市场主流预算带，辅助判断租客价格敏感区间</div>
          </div>
        </div>
        <div ref="rentSegmentRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">面积区间结构</div>
            <div class="chart-subtitle">分析不同面积段房源数量与平均租金，识别紧凑型和改善型房源结构</div>
          </div>
        </div>
        <div ref="areaSegmentRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">房间类型占比</div>
            <div class="chart-subtitle">展示不同房间类型占比与均价，分析租赁房源居住形态</div>
          </div>
        </div>
        <div ref="roomPieRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">近地铁租金溢价分析</div>
            <div class="chart-subtitle">对比近地铁、非近地铁与未知房源的平均租金、单位租金和房源数量</div>
          </div>
        </div>
        <div ref="subwayRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">租赁价值画像雷达图</div>
            <div class="chart-subtitle">从房源供给、租金水平、单位租金、面积、卧室数、供暖配套综合评估近地铁价值</div>
          </div>
        </div>
        <div ref="radarRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">楼层分组租金表现</div>
            <div class="chart-subtitle">分析不同楼层分组下的房源数量、租金和单位租金差异</div>
          </div>
        </div>
        <div ref="floorRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">供暖方式租金差异</div>
            <div class="chart-subtitle">对比集中供暖、自采暖、无供暖等房源的数量和租金表现</div>
          </div>
        </div>
        <div ref="heatingRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">区域 × 卧室数量热力矩阵</div>
            <div class="chart-subtitle">识别不同城区中主流户型的供给强度，颜色越深表示房源数量越多</div>
          </div>
        </div>
        <div ref="heatmapRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">面积与租金散点关系</div>
            <div class="chart-subtitle">观察面积扩大后租金变化趋势，并发现高单价、小面积等特殊房源</div>
          </div>
        </div>
        <div ref="scatterRef" class="chart-box chart-box--wide"></div>
      </div>
    </div>

    <div class="insight-section art-card" v-loading="loading" element-loading-text="正在生成数据规律解读...">
      <div class="insight-header">
        <div>
          <div class="chart-title">数据规律与价值解读</div>
          <div class="chart-subtitle">结合当前筛选条件自动总结房屋租赁市场规律，可用于论文、报告或大屏展示说明</div>
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
  fetchRentalAnalysis,
  fetchRentalOptions,
  type RentalAnalysisData,
  type RentalHouseOptions,
  type RentalHouseQuery
} from '@/api/rental'
import { registerProvinceMap } from '@/utils/echarts-map'

const router = useRouter()
const loading = ref(false)

const options = reactive<RentalHouseOptions>({
  rooms: [],
  floorGrps: [],
  subways: [],
  regions: [],
  heatings: []
})

const queryForm = reactive<RentalHouseQuery>({
  room: '',
  floorGrp: '',
  subway: '',
  region: '',
  heating: '',
  bedroom: undefined,
  livingroom: undefined,
  bathroom: undefined,
  rentMin: undefined,
  rentMax: undefined,
  areaMin: undefined,
  areaMax: undefined
})

const emptyAnalysis = (): RentalAnalysisData => ({
  summary: {},
  regionStats: [],
  rentSegmentStats: [],
  areaSegmentStats: [],
  roomDistribution: [],
  bedroomStats: [],
  subwayStats: [],
  floorStats: [],
  heatingStats: [],
  regionBedroomHeatmap: [],
  rentAreaScatter: [],
  featureRadarStats: [],
  insights: []
})

const analysisData = ref<RentalAnalysisData>(emptyAnalysis())

const mapRef = ref<HTMLElement>()
const regionRankRef = ref<HTMLElement>()
const rentSegmentRef = ref<HTMLElement>()
const areaSegmentRef = ref<HTMLElement>()
const roomPieRef = ref<HTMLElement>()
const subwayRef = ref<HTMLElement>()
const radarRef = ref<HTMLElement>()
const floorRef = ref<HTMLElement>()
const heatingRef = ref<HTMLElement>()
const heatmapRef = ref<HTMLElement>()
const scatterRef = ref<HTMLElement>()

const chartList: echarts.ECharts[] = []

const summaryCards = computed(() => {
  const summary = analysisData.value.summary || {}

  return [
    {
      label: '房源总量',
      value: formatInteger(summary.totalCount),
      extra: `覆盖 ${formatInteger(summary.regionCount)} 个北京区域`
    },
    {
      label: '平均租金',
      value: `¥${formatMoney(summary.avgRent)}`,
      extra: `区间 ${formatMoney(summary.minRent)} - ${formatMoney(summary.maxRent)} 元/月`
    },
    {
      label: '平均面积',
      value: `${formatNumber(summary.avgArea, 1)}㎡`,
      extra: `平均 ${formatNumber(summary.avgBedroom, 1)} 间卧室`
    },
    {
      label: '单位租金',
      value: `¥${formatNumber(summary.avgUnitRent, 1)}/㎡`,
      extra: '用于衡量区域价格密度'
    },
    {
      label: '近地铁占比',
      value: `${formatNumber(summary.subwayRate, 1)}%`,
      extra: `近地铁 ${formatInteger(summary.subwayCount)} 套`
    },
    {
      label: '集中供暖占比',
      value: `${formatNumber(summary.centralizedHeatingRate, 1)}%`,
      extra: `集中供暖 ${formatInteger(summary.centralizedHeatingCount)} 套`
    }
  ]
})

const insightList = computed(() => analysisData.value.insights || [])

const loadOptions = async () => {
  const res = await fetchRentalOptions()
  options.rooms = res.rooms || []
  options.floorGrps = res.floorGrps || []
  options.subways = res.subways || []
  options.regions = res.regions || []
  options.heatings = res.heatings || []
}

const loadAnalysis = async () => {
  loading.value = true

  try {
    const res = await fetchRentalAnalysis(cleanQuery(queryForm))
    analysisData.value = {
      ...emptyAnalysis(),
      ...res,
      summary: res.summary || {}
    }

    await nextTick()
    await registerProvinceMap('北京')
    renderCharts()
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.room = ''
  queryForm.floorGrp = ''
  queryForm.subway = ''
  queryForm.region = ''
  queryForm.heating = ''
  queryForm.bedroom = undefined
  queryForm.livingroom = undefined
  queryForm.bathroom = undefined
  queryForm.rentMin = undefined
  queryForm.rentMax = undefined
  queryForm.areaMin = undefined
  queryForm.areaMax = undefined

  loadAnalysis()
}

const cleanQuery = (query: RentalHouseQuery) => {
  const result: RentalHouseQuery = { ...query }
  Object.keys(result).forEach((key) => {
    const targetKey = key as keyof RentalHouseQuery
    const value = result[targetKey]
    if (value === '' || value === null || value === undefined) {
      delete result[targetKey]
    }
  })
  return result
}

const goBack = () => {
  router.push('/source/rental')
}

const renderCharts = () => {
  renderMapChart()
  renderRegionRankChart()
  renderRentSegmentChart()
  renderAreaSegmentChart()
  renderRoomPieChart()
  renderSubwayChart()
  renderRadarChart()
  renderFloorChart()
  renderHeatingChart()
  renderHeatmapChart()
  renderScatterChart()
}

const getChart = (el?: HTMLElement) => {
  if (!el) return null
  const existChart = echarts.getInstanceByDom(el)
  if (existChart) return existChart
  const chart = echarts.init(el)
  chartList.push(chart)
  return chart
}

const renderMapChart = () => {
  const chart = getChart(mapRef.value)
  if (!chart) return

  const data = analysisData.value.regionStats.map((item) => ({
    name: normalizeBeijingRegion(item.regionName),
    value: toNumber(item.avgRent),
    totalCount: toNumber(item.totalCount),
    avgUnitRent: toNumber(item.avgUnitRent),
    avgArea: toNumber(item.avgArea),
    subwayRate: toNumber(item.subwayRate)
  }))

  const maxValue = Math.max(...data.map((item) => item.value), 1000)

  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const dataItem = params.data || {}
        if (!dataItem.value) return `${params.name}<br/>暂无房源数据`
        return [
          `${params.name}`,
          `平均租金：¥${formatMoney(dataItem.value)}/月`,
          `单位租金：¥${formatNumber(dataItem.avgUnitRent, 1)}/㎡`,
          `平均面积：${formatNumber(dataItem.avgArea, 1)}㎡`,
          `房源数量：${formatInteger(dataItem.totalCount)} 套`,
          `近地铁占比：${formatNumber(dataItem.subwayRate, 1)}%`
        ].join('<br/>')
      }
    },
    visualMap: {
      min: 0,
      max: maxValue,
      left: 16,
      bottom: 18,
      text: ['高租金', '低租金'],
      calculable: true,
      inRange: {
        color: ['#dbeafe', '#60a5fa', '#2563eb', '#1e40af']
      }
    },
    series: [
      {
        name: '北京平均租金',
        type: 'map',
        map: '北京',
        roam: true,
        zoom: 1.08,
        label: {
          show: true,
          color: '#344054',
          fontSize: 11
        },
        itemStyle: {
          borderColor: '#ffffff',
          borderWidth: 1
        },
        emphasis: {
          label: {
            color: '#111827',
            fontWeight: 600
          },
          itemStyle: {
            areaColor: '#f59e0b'
          }
        },
        data
      }
    ]
  })
}

const renderRegionRankChart = () => {
  const chart = getChart(regionRankRef.value)
  if (!chart) return

  const list = [...analysisData.value.regionStats]
    .filter((item) => normalizeBeijingRegion(item.regionName) !== '未知区域')
    .sort((a, b) => toNumber(b.avgRent) - toNumber(a.avgRent))
    .slice(0, 12)
    .reverse()

  chart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      top: 0,
      right: 10
    },
    grid: {
      left: 70,
      right: 44,
      top: 48,
      bottom: 28
    },
    xAxis: [
      {
        type: 'value',
        name: '元/月'
      },
      {
        type: 'value',
        name: '套',
        splitLine: { show: false }
      }
    ],
    yAxis: {
      type: 'category',
      data: list.map((item) => normalizeBeijingRegion(item.regionName))
    },
    series: [
      {
        name: '平均租金',
        type: 'bar',
        data: list.map((item) => toNumber(item.avgRent)),
        barWidth: 12,
        itemStyle: { borderRadius: [0, 8, 8, 0] }
      },
      {
        name: '房源数量',
        type: 'line',
        xAxisIndex: 1,
        data: list.map((item) => toNumber(item.totalCount)),
        smooth: true,
        symbolSize: 7
      }
    ]
  })
}

const renderRentSegmentChart = () => {
  const chart = getChart(rentSegmentRef.value)
  if (!chart) return

  const list = analysisData.value.rentSegmentStats

  chart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: { top: 0, right: 10 },
    grid: { left: 46, right: 38, top: 48, bottom: 44 },
    xAxis: {
      type: 'category',
      data: list.map((item) => item.rentSegment),
      axisLabel: { interval: 0, rotate: 20 }
    },
    yAxis: [
      { type: 'value', name: '房源数' },
      { type: 'value', name: '元/㎡', splitLine: { show: false } }
    ],
    series: [
      {
        name: '房源数量',
        type: 'bar',
        data: list.map((item) => toNumber(item.totalCount)),
        barWidth: 24,
        itemStyle: { borderRadius: [8, 8, 0, 0] }
      },
      {
        name: '单位租金',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.avgUnitRent))
      }
    ]
  })
}

const renderAreaSegmentChart = () => {
  const chart = getChart(areaSegmentRef.value)
  if (!chart) return

  const data = analysisData.value.areaSegmentStats.map((item) => ({
    name: item.areaSegment,
    value: toNumber(item.totalCount),
    avgRent: toNumber(item.avgRent),
    avgUnitRent: toNumber(item.avgUnitRent)
  }))

  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const d = params.data || {}
        return `${params.name}<br/>房源数量：${formatInteger(d.value)} 套<br/>平均租金：¥${formatMoney(d.avgRent)}<br/>单位租金：¥${formatNumber(d.avgUnitRent, 1)}/㎡`
      }
    },
    legend: {
      bottom: 0,
      type: 'scroll'
    },
    series: [
      {
        name: '面积区间',
        type: 'pie',
        radius: ['42%', '68%'],
        center: ['50%', '46%'],
        avoidLabelOverlap: true,
        label: {
          formatter: '{b}\n{d}%'
        },
        data
      }
    ]
  })
}

const renderRoomPieChart = () => {
  const chart = getChart(roomPieRef.value)
  if (!chart) return

  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const d = params.data || {}
        return `${params.name}<br/>房源数量：${formatInteger(params.value)} 套<br/>平均租金：¥${formatMoney(d.avgRent)}<br/>平均面积：${formatNumber(d.avgArea, 1)}㎡`
      }
    },
    legend: {
      bottom: 0,
      type: 'scroll'
    },
    series: [
      {
        name: '房间类型',
        type: 'pie',
        radius: ['28%', '68%'],
        center: ['50%', '45%'],
        roseType: 'radius',
        label: {
          formatter: '{b}\n{d}%'
        },
        data: analysisData.value.roomDistribution
      }
    ]
  })
}

const renderSubwayChart = () => {
  const chart = getChart(subwayRef.value)
  if (!chart) return

  const list = analysisData.value.subwayStats

  chart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: { top: 0, right: 10 },
    grid: { left: 52, right: 56, top: 50, bottom: 36 },
    xAxis: {
      type: 'category',
      data: list.map((item) => item.subwayFlag)
    },
    yAxis: [
      { type: 'value', name: '元/月' },
      { type: 'value', name: '套', splitLine: { show: false } }
    ],
    series: [
      {
        name: '平均租金',
        type: 'bar',
        data: list.map((item) => toNumber(item.avgRent)),
        barWidth: 24,
        itemStyle: { borderRadius: [8, 8, 0, 0] }
      },
      {
        name: '单位租金',
        type: 'bar',
        data: list.map((item) => toNumber(item.avgUnitRent)),
        barWidth: 24,
        itemStyle: { borderRadius: [8, 8, 0, 0] }
      },
      {
        name: '房源数量',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.totalCount))
      }
    ]
  })
}

const renderRadarChart = () => {
  const chart = getChart(radarRef.value)
  if (!chart) return

  const list = analysisData.value.featureRadarStats
  const indicators = [
    { name: '房源供给' },
    { name: '平均租金' },
    { name: '单位租金' },
    { name: '平均面积' },
    { name: '卧室数量' },
    { name: '集中供暖' }
  ].map((item) => ({ ...item, max: 100 }))

  const maxValues = {
    totalCount: maxOf(list, 'totalCount'),
    avgRent: maxOf(list, 'avgRent'),
    avgUnitRent: maxOf(list, 'avgUnitRent'),
    avgArea: maxOf(list, 'avgArea'),
    avgBedroom: maxOf(list, 'avgBedroom'),
    centralizedHeatingRate: 100
  }

  const seriesData = list.map((item) => ({
    name: item.subwayFlag,
    value: [
      normalizeValue(item.totalCount, maxValues.totalCount),
      normalizeValue(item.avgRent, maxValues.avgRent),
      normalizeValue(item.avgUnitRent, maxValues.avgUnitRent),
      normalizeValue(item.avgArea, maxValues.avgArea),
      normalizeValue(item.avgBedroom, maxValues.avgBedroom),
      normalizeValue(item.centralizedHeatingRate, maxValues.centralizedHeatingRate)
    ]
  }))

  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    radar: {
      indicator: indicators,
      radius: '62%',
      center: ['50%', '46%'],
      splitNumber: 4
    },
    series: [
      {
        name: '租赁价值画像',
        type: 'radar',
        areaStyle: { opacity: 0.12 },
        data: seriesData
      }
    ]
  })
}

const renderFloorChart = () => {
  const chart = getChart(floorRef.value)
  if (!chart) return

  const list = [...analysisData.value.floorStats].reverse()

  chart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: { top: 0, right: 10 },
    grid: { left: 76, right: 46, top: 48, bottom: 28 },
    xAxis: [
      { type: 'value', name: '元/月' },
      { type: 'value', name: '套', splitLine: { show: false } }
    ],
    yAxis: {
      type: 'category',
      data: list.map((item) => item.floorGrp)
    },
    series: [
      {
        name: '平均租金',
        type: 'bar',
        data: list.map((item) => toNumber(item.avgRent)),
        barWidth: 12,
        itemStyle: { borderRadius: [0, 8, 8, 0] }
      },
      {
        name: '房源数量',
        type: 'line',
        xAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.totalCount))
      }
    ]
  })
}

const renderHeatingChart = () => {
  const chart = getChart(heatingRef.value)
  if (!chart) return

  const list = analysisData.value.heatingStats

  chart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: { top: 0, right: 10 },
    grid: { left: 52, right: 52, top: 48, bottom: 36 },
    xAxis: {
      type: 'category',
      data: list.map((item) => item.heatingFlag)
    },
    yAxis: [
      { type: 'value', name: '元/月' },
      { type: 'value', name: '套', splitLine: { show: false } }
    ],
    series: [
      {
        name: '平均租金',
        type: 'bar',
        data: list.map((item) => toNumber(item.avgRent)),
        barWidth: 24,
        itemStyle: { borderRadius: [8, 8, 0, 0] }
      },
      {
        name: '房源数量',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.totalCount))
      }
    ]
  })
}

const renderHeatmapChart = () => {
  const chart = getChart(heatmapRef.value)
  if (!chart) return

  const source = analysisData.value.regionBedroomHeatmap
  const regions = Array.from(new Set(source.map((item) => normalizeBeijingRegion(item.regionName)))).filter(Boolean)
  const bedroomSegments = Array.from(new Set(source.map((item) => item.bedroomSegment))).filter(Boolean)

  const data = source.map((item) => [
    bedroomSegments.indexOf(item.bedroomSegment),
    regions.indexOf(normalizeBeijingRegion(item.regionName)),
    toNumber(item.totalCount),
    toNumber(item.avgRent),
    toNumber(item.avgUnitRent)
  ])

  const maxValue = Math.max(...data.map((item) => Number(item[2] || 0)), 1)

  chart.setOption({
    tooltip: {
      position: 'top',
      formatter: (params: any) => {
        const value = params.value || []
        return [
          `${regions[value[1]]} / ${bedroomSegments[value[0]]}`,
          `房源数量：${formatInteger(value[2])} 套`,
          `平均租金：¥${formatMoney(value[3])}/月`,
          `单位租金：¥${formatNumber(value[4], 1)}/㎡`
        ].join('<br/>')
      }
    },
    grid: { left: 82, right: 28, top: 36, bottom: 62 },
    xAxis: {
      type: 'category',
      data: bedroomSegments,
      splitArea: { show: true }
    },
    yAxis: {
      type: 'category',
      data: regions,
      splitArea: { show: true }
    },
    visualMap: {
      min: 0,
      max: maxValue,
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: 8,
      inRange: {
        color: ['#eff6ff', '#93c5fd', '#2563eb']
      }
    },
    series: [
      {
        name: '区域户型热力',
        type: 'heatmap',
        data,
        label: {
          show: true,
          formatter: (params: any) => String(params.value?.[2] || '')
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowColor: 'rgba(0, 0, 0, 0.18)'
          }
        }
      }
    ]
  })
}

const renderScatterChart = () => {
  const chart = getChart(scatterRef.value)
  if (!chart) return

  const data = analysisData.value.rentAreaScatter.map((item) => [
    toNumber(item.area),
    toNumber(item.rent),
    toNumber(item.unitRent),
    normalizeBeijingRegion(item.regionName),
    item.room || '-'
  ])

  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const v = params.value || []
        return [
          `${v[3]} / ${v[4]}`,
          `面积：${formatNumber(v[0], 1)}㎡`,
          `租金：¥${formatMoney(v[1])}/月`,
          `单位租金：¥${formatNumber(v[2], 1)}/㎡`
        ].join('<br/>')
      }
    },
    grid: { left: 56, right: 28, top: 32, bottom: 44 },
    xAxis: {
      type: 'value',
      name: '面积㎡',
      splitLine: { lineStyle: { type: 'dashed' } }
    },
    yAxis: {
      type: 'value',
      name: '租金 元/月',
      splitLine: { lineStyle: { type: 'dashed' } }
    },
    series: [
      {
        name: '房源散点',
        type: 'scatter',
        symbolSize: (value: number[]) => {
          const unitRent = Number(value?.[2] || 0)
          return Math.max(6, Math.min(unitRent / 12, 22))
        },
        data
      }
    ]
  })
}

const resizeCharts = () => {
  chartList.forEach((chart) => chart.resize())
}

const disposeCharts = () => {
  chartList.forEach((chart) => chart.dispose())
  chartList.length = 0
}

const normalizeBeijingRegion = (value?: string) => {
  const text = String(value || '').trim()
  if (!text) return '未知区域'
  const regionMap: Record<string, string> = {
    东城: '东城区',
    西城: '西城区',
    朝阳: '朝阳区',
    丰台: '丰台区',
    石景山: '石景山区',
    海淀: '海淀区',
    门头沟: '门头沟区',
    房山: '房山区',
    通州: '通州区',
    顺义: '顺义区',
    昌平: '昌平区',
    大兴: '大兴区',
    怀柔: '怀柔区',
    平谷: '平谷区',
    密云: '密云区',
    延庆: '延庆区'
  }
  const hit = Object.keys(regionMap).find((key) => text.includes(key))
  return hit ? regionMap[hit] : text
}

const toNumber = (value?: number | string) => {
  const num = Number(value)
  return Number.isFinite(num) ? num : 0
}

const maxOf = (list: any[], key: string) => Math.max(...list.map((item) => toNumber(item[key])), 1)

const normalizeValue = (value: number | undefined, max: number) => {
  if (!max) return 0
  return Number(((toNumber(value) / max) * 100).toFixed(2))
}

const formatInteger = (value?: number | string) => {
  const num = toNumber(value)
  return num.toLocaleString('zh-CN', { maximumFractionDigits: 0 })
}

const formatMoney = (value?: number | string) => {
  const num = toNumber(value)
  if (!num) return '0'
  return num.toLocaleString('zh-CN', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  })
}

const formatNumber = (value?: number | string, precision = 2) => {
  const num = toNumber(value)
  return num.toLocaleString('zh-CN', {
    minimumFractionDigits: precision,
    maximumFractionDigits: precision
  })
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
.rental-analysis-page {
  min-height: 100%;

  .art-card {
    border: 1px solid var(--art-border-color, #eaecf0);
    box-shadow: 0 10px 28px rgba(15, 23, 42, 0.04);
  }
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
  font-size: 23px;
  font-weight: 700;
  color: var(--art-text-gray-900, #111827);
  line-height: 1.25;
}

.page-desc {
  max-width: 920px;
  margin-top: 9px;
  font-size: 14px;
  line-height: 1.8;
  color: var(--art-text-gray-600, #667085);
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
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-card__label {
  font-size: 13px;
  color: var(--art-text-gray-600, #667085);
}

.stat-card__value {
  margin-top: 11px;
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
  padding: 18px 20px 14px;
  min-height: 430px;
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
  height: 340px;
}

.chart-box--wide {
  height: 380px;
}

.chart-box--map {
  height: 460px;
}

.insight-section {
  padding: 22px 24px 24px;
  margin-top: 20px;
}

.insight-header {
  margin-bottom: 16px;
}

.insight-list {
  display: grid;
  gap: 12px;
}

.insight-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  padding: 14px 16px;
  border-radius: 14px;
  background: #f8fafc;
  border: 1px solid #eef2f7;
}

.insight-index {
  width: 24px;
  height: 24px;
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  color: #1d4ed8;
  background: #dbeafe;
}

.insight-text {
  font-size: 14px;
  line-height: 1.8;
  color: var(--art-text-gray-700, #344054);
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
}

@media (max-width: 640px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
