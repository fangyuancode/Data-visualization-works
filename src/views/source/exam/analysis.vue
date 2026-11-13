<template>
  <div class="exam-analysis-page">
    <div class="page-header art-card">
      <div class="page-header__left">
        <div class="page-title">学生考试表现数据可视化分析</div>
        <div class="page-desc">
          围绕成绩结果、学习行为、家庭背景、资源条件和通过状态进行数据挖掘，识别影响学生成绩的关键因素，并生成可解释的数据规律。
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
        <ElFormItem label="性别">
          <ElSelect v-model="queryForm.gender" placeholder="全部性别" clearable filterable style="width: 130px">
            <ElOption v-for="item in options.genders" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="父母教育">
          <ElSelect v-model="queryForm.parentalEducation" placeholder="全部教育" clearable filterable style="width: 160px">
            <ElOption v-for="item in options.parentalEducations" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="家庭收入">
          <ElSelect v-model="queryForm.familyIncome" placeholder="全部收入" clearable filterable style="width: 150px">
            <ElOption v-for="item in options.familyIncomes" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="网络条件">
          <ElSelect v-model="queryForm.internetAccess" placeholder="全部网络" clearable filterable style="width: 140px">
            <ElOption v-for="item in options.internetAccessOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="学习环境">
          <ElSelect v-model="queryForm.studyEnvironment" placeholder="全部环境" clearable filterable style="width: 150px">
            <ElOption v-for="item in options.studyEnvironments" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="是否辅导">
          <ElSelect v-model="queryForm.tutoring" placeholder="全部" clearable filterable style="width: 120px">
            <ElOption v-for="item in options.tutoringOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="通过状态">
          <ElSelect v-model="queryForm.passFail" placeholder="全部" clearable filterable style="width: 120px">
            <ElOption v-for="item in options.passFailOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="成绩等级">
          <ElSelect v-model="queryForm.gradeCategory" placeholder="全部等级" clearable filterable style="width: 130px">
            <ElOption v-for="item in options.gradeCategories" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="年龄范围">
          <div class="range-input">
            <ElInputNumber v-model="queryForm.ageMin" :min="0" :controls="false" placeholder="最小" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.ageMax" :min="0" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem label="成绩范围">
          <div class="range-input">
            <ElInputNumber v-model="queryForm.finalScoreMin" :min="0" :controls="false" placeholder="最低" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.finalScoreMax" :min="0" :controls="false" placeholder="最高" />
          </div>
        </ElFormItem>

        <ElFormItem label="学习时长">
          <div class="range-input">
            <ElInputNumber v-model="queryForm.studyHoursMin" :min="0" :controls="false" placeholder="最少" />
            <span class="range-line">-</span>
            <ElInputNumber v-model="queryForm.studyHoursMax" :min="0" :controls="false" placeholder="最多" />
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

    <div class="summary-grid" v-loading="loading" element-loading-text="正在计算考试表现核心指标...">
      <div v-for="item in summaryCards" :key="item.label" class="art-card stat-card">
        <div class="stat-card__label">{{ item.label }}</div>
        <div class="stat-card__value">{{ item.value }}</div>
        <div class="stat-card__extra">{{ item.extra }}</div>
      </div>
    </div>

    <div class="analysis-layout" v-loading="loading" element-loading-text="正在渲染学生考试表现可视化图表...">
      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">通过状态分布</div>
            <div class="chart-subtitle">观察通过、未通过和未知状态的比例，判断整体考试达成情况</div>
          </div>
        </div>
        <div ref="passPieRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">成绩等级结构</div>
            <div class="chart-subtitle">分析 A/B/C/D 等成绩等级的分布，并对比各等级平均分</div>
          </div>
        </div>
        <div ref="gradePieRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">各学科平均成绩对比</div>
            <div class="chart-subtitle">比较数学、阅读、写作、科学和期末综合分，识别优势学科与薄弱学科</div>
          </div>
        </div>
        <div ref="subjectBarRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">年龄层成绩表现</div>
            <div class="chart-subtitle">按年龄分层分析平均成绩、通过率与学习时长</div>
          </div>
        </div>
        <div ref="ageSegmentRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">学习时长分层效果</div>
            <div class="chart-subtitle">分析不同学习投入区间下的平均成绩、通过率和作业完成情况</div>
          </div>
        </div>
        <div ref="studyHourRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">家庭收入与成绩表现</div>
            <div class="chart-subtitle">观察家庭经济条件与平均成绩、通过率之间的差异</div>
          </div>
        </div>
        <div ref="incomeRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">父母教育背景影响</div>
            <div class="chart-subtitle">比较不同父母教育背景下的学生平均成绩和通过率</div>
          </div>
        </div>
        <div ref="parentalRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">网络条件与学习环境对比</div>
            <div class="chart-subtitle">分析网络资源、学习环境与成绩表现、作业完成率之间的关系</div>
          </div>
        </div>
        <div ref="resourceRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">成绩等级学科能力雷达图</div>
            <div class="chart-subtitle">从数学、阅读、写作、科学、期末分和通过率刻画不同等级学生画像</div>
          </div>
        </div>
        <div ref="subjectRadarRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--large">
        <div class="chart-header">
          <div>
            <div class="chart-title">通过状态学习行为雷达图</div>
            <div class="chart-subtitle">对比通过与未通过学生在学习时长、出勤、睡眠、作业和参与度上的差异</div>
          </div>
        </div>
        <div ref="behaviorRadarRef" class="chart-box"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">学习时长与期末成绩散点关系</div>
            <div class="chart-subtitle">每个点代表一名学生，横轴为每日学习时长，纵轴为期末成绩</div>
          </div>
        </div>
        <div ref="scatterRef" class="chart-box chart-box--wide"></div>
      </div>

      <div class="chart-card art-card chart-card--wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">成绩等级 × 学习时长热力矩阵</div>
            <div class="chart-subtitle">识别不同学习投入区间中各成绩等级的集中程度</div>
          </div>
        </div>
        <div ref="heatmapRef" class="chart-box chart-box--wide"></div>
      </div>
    </div>

    <div class="insight-section art-card" v-loading="loading" element-loading-text="正在生成考试表现数据规律解读...">
      <div class="insight-header">
        <div>
          <div class="chart-title">数据规律与价值解读</div>
          <div class="chart-subtitle">结合当前筛选条件自动总结考试表现规律，可用于数据分析报告、论文说明或可视化大屏文案</div>
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
  fetchExamAnalysis,
  fetchExamOptions,
  type ExamAnalysisData,
  type StudentExamPerformanceOptions,
  type StudentExamPerformanceQuery
} from '@/api/exam'

const router = useRouter()
const loading = ref(false)

const options = reactive<StudentExamPerformanceOptions>({
  genders: [],
  parentalEducations: [],
  familyIncomes: [],
  internetAccessOptions: [],
  studyEnvironments: [],
  tutoringOptions: [],
  passFailOptions: [],
  gradeCategories: []
})
interface LabelStatItem {
  labelName: string
  avgFinalScore?: number | string | null
  passRate?: number | string | null
  totalCount?: number | string | null
}

interface ResourceStatItem extends LabelStatItem {
  avgStudyHours?: number | string | null
  avgAssignmentRate?: number | string | null
  avgPreviousGpa?: number | string | null
}
const queryForm = reactive<StudentExamPerformanceQuery>({
  gender: '',
  parentalEducation: '',
  familyIncome: '',
  internetAccess: '',
  studyEnvironment: '',
  tutoring: '',
  passFail: '',
  gradeCategory: '',
  ageMin: undefined,
  ageMax: undefined,
  finalScoreMin: undefined,
  finalScoreMax: undefined,
  studyHoursMin: undefined,
  studyHoursMax: undefined
})

const emptyAnalysis = (): ExamAnalysisData => ({
  summary: {},
  passFailDistribution: [],
  gradeDistribution: [],
  genderDistribution: [],
  subjectAverage: [],
  ageSegmentStats: [],
  studyHourSegmentStats: [],
  familyIncomeStats: [],
  parentalEducationStats: [],
  internetAccessStats: [],
  studyEnvironmentStats: [],
  subjectRadarByGrade: [],
  behaviorRadarByPass: [],
  behaviorScoreScatter: [],
  gradeStudyHourHeatmap: [],
  insights: []
})

const analysisData = ref<ExamAnalysisData>(emptyAnalysis())

const passPieRef = ref<HTMLElement>()
const gradePieRef = ref<HTMLElement>()
const subjectBarRef = ref<HTMLElement>()
const ageSegmentRef = ref<HTMLElement>()
const studyHourRef = ref<HTMLElement>()
const incomeRef = ref<HTMLElement>()
const parentalRef = ref<HTMLElement>()
const resourceRef = ref<HTMLElement>()
const subjectRadarRef = ref<HTMLElement>()
const behaviorRadarRef = ref<HTMLElement>()
const scatterRef = ref<HTMLElement>()
const heatmapRef = ref<HTMLElement>()

const chartList: echarts.ECharts[] = []

const summaryCards = computed(() => {
  const summary = analysisData.value.summary || {}

  return [
    {
      label: '样本总量',
      value: formatInteger(summary.totalCount),
      extra: `覆盖 ${formatInteger(summary.genderCount)} 类性别、${formatInteger(
        summary.gradeCategoryCount
      )} 类成绩等级`
    },
    {
      label: '平均期末成绩',
      value: `${formatNumber(summary.avgFinalScore, 1)}分`,
      extra: `数学 ${formatNumber(summary.avgMathScore, 1)} / 阅读 ${formatNumber(
        summary.avgReadingScore,
        1
      )}`
    },
    {
      label: '整体通过率',
      value: `${formatNumber(summary.passRate, 1)}%`,
      extra: `通过 ${formatInteger(summary.passCount)} 人，未通过 ${formatInteger(
        summary.failCount
      )} 人`
    },
    {
      label: '平均学习时长',
      value: `${formatNumber(summary.avgStudyHours, 1)}h`,
      extra: `平均出勤率 ${formatNumber(summary.avgAttendanceRate, 1)}%`
    },
    {
      label: '作业完成率',
      value: `${formatNumber(summary.avgAssignmentRate, 1)}%`,
      extra: `课堂参与 ${formatNumber(summary.avgParticipationScore, 1)} 分`
    },
    {
      label: '辅导参与率',
      value: `${formatNumber(summary.tutoringRate, 1)}%`,
      extra: `在线课程均值 ${formatNumber(summary.avgOnlineCourses, 1)} 门`
    }
  ]
})

const insightList = computed(() => analysisData.value.insights || [])

const loadOptions = async () => {
  const res = await fetchExamOptions()
  options.genders = res.genders || []
  options.parentalEducations = res.parentalEducations || []
  options.familyIncomes = res.familyIncomes || []
  options.internetAccessOptions = res.internetAccessOptions || []
  options.studyEnvironments = res.studyEnvironments || []
  options.tutoringOptions = res.tutoringOptions || []
  options.passFailOptions = res.passFailOptions || []
  options.gradeCategories = res.gradeCategories || []
}

const loadAnalysis = async () => {
  loading.value = true

  try {
    const res = await fetchExamAnalysis(cleanQuery(queryForm))
    analysisData.value = {
      ...emptyAnalysis(),
      ...res,
      summary: res.summary || {}
    }

    await nextTick()
    renderCharts()
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.gender = ''
  queryForm.parentalEducation = ''
  queryForm.familyIncome = ''
  queryForm.internetAccess = ''
  queryForm.studyEnvironment = ''
  queryForm.tutoring = ''
  queryForm.passFail = ''
  queryForm.gradeCategory = ''
  queryForm.ageMin = undefined
  queryForm.ageMax = undefined
  queryForm.finalScoreMin = undefined
  queryForm.finalScoreMax = undefined
  queryForm.studyHoursMin = undefined
  queryForm.studyHoursMax = undefined

  loadAnalysis()
}

const cleanQuery = (query: StudentExamPerformanceQuery) => {
  const result: StudentExamPerformanceQuery = { ...query }
  Object.keys(result).forEach((key) => {
    const targetKey = key as keyof StudentExamPerformanceQuery
    const value = result[targetKey]
    if (value === '' || value === null || value === undefined) {
      delete result[targetKey]
    }
  })
  return result
}

const goBack = () => {
  router.push('/source/exam')
}

const renderCharts = () => {
  renderPassPieChart()
  renderGradePieChart()
  renderSubjectBarChart()
  renderAgeSegmentChart()
  renderStudyHourChart()
  renderIncomeChart()
  renderParentalChart()
  renderResourceChart()
  renderSubjectRadarChart()
  renderBehaviorRadarChart()
  renderScatterChart()
  renderHeatmapChart()
}

const getChart = (el?: HTMLElement) => {
  if (!el) return null
  const existChart = echarts.getInstanceByDom(el)
  if (existChart) return existChart
  const chart = echarts.init(el)
  chartList.push(chart)
  return chart
}

const renderPassPieChart = () => {
  const chart = getChart(passPieRef.value)
  if (!chart) return

  const data = analysisData.value.passFailDistribution.map((item) => ({
    name: item.labelName || '未知状态',
    value: toNumber(item.totalCount),
    ratio: toNumber(item.ratio),
    avgFinalScore: toNumber(item.avgFinalScore),
    avgStudyHours: toNumber(item.avgStudyHours)
  }))

  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const item = params.data || {}
        return [
          `${params.name}`,
          `人数：${formatInteger(item.value)}`,
          `占比：${formatNumber(item.ratio, 1)}%`,
          `平均分：${formatNumber(item.avgFinalScore, 1)}`,
          `学习时长：${formatNumber(item.avgStudyHours, 1)}h`
        ].join('<br/>')
      }
    },
    legend: { bottom: 0 },
    series: [
      {
        name: '通过状态',
        type: 'pie',
        radius: ['46%', '68%'],
        center: ['50%', '44%'],
        avoidLabelOverlap: true,
        label: { formatter: '{b}\n{d}%' },
        data
      }
    ]
  })
}

const renderGradePieChart = () => {
  const chart = getChart(gradePieRef.value)
  if (!chart) return

  const data = analysisData.value.gradeDistribution.map((item) => ({
    name: item.labelName || '未知等级',
    value: toNumber(item.totalCount),
    avgFinalScore: toNumber(item.avgFinalScore),
    passRate: toNumber(item.passRate),
    ratio: toNumber(item.ratio)
  }))

  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const item = params.data || {}
        return [
          `${params.name}`,
          `人数：${formatInteger(item.value)}`,
          `占比：${formatNumber(item.ratio, 1)}%`,
          `平均分：${formatNumber(item.avgFinalScore, 1)}`,
          `通过率：${formatNumber(item.passRate, 1)}%`
        ].join('<br/>')
      }
    },
    legend: { bottom: 0 },
    series: [
      {
        name: '成绩等级',
        type: 'pie',
        radius: ['32%', '70%'],
        roseType: 'area',
        center: ['50%', '44%'],
        label: { formatter: '{b}\n{d}%' },
        data
      }
    ]
  })
}

const renderSubjectBarChart = () => {
  const chart = getChart(subjectBarRef.value)
  if (!chart) return

  const list = analysisData.value.subjectAverage || []
  // const list = data as LabelStatItem[]
  const names = list.map((item) => item.subjectName || '-')

  chart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { top: 0, right: 8 },
    grid: { left: 48, right: 32, top: 48, bottom: 36 },
    xAxis: { type: 'category', data: names, axisTick: { alignWithLabel: true } },
    yAxis: { type: 'value', name: '分', min: 0, max: 100 },
    series: [
      {
        name: '平均分',
        type: 'bar',
        barWidth: 28,
        data: list.map((item) => toNumber(item.avgScore))
      },
      {
        name: '最高分',
        type: 'line',
        smooth: true,
        data: list.map((item) => toNumber(item.maxScore))
      },
      {
        name: '最低分',
        type: 'line',
        smooth: true,
        data: list.map((item) => toNumber(item.minScore))
      }
    ]
  })
}

const renderAgeSegmentChart = () => {
  const chart = getChart(ageSegmentRef.value)
  if (!chart) return

  const list = analysisData.value.ageSegmentStats || []
  chart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { top: 0, right: 8 },
    grid: { left: 46, right: 46, top: 48, bottom: 36 },
    xAxis: { type: 'category', data: list.map((item) => item.segmentName || '-') },
    yAxis: [
      { type: 'value', name: '分/人' },
      { type: 'value', name: '通过率', axisLabel: { formatter: '{value}%' } }
    ],
    series: [
      { name: '人数', type: 'bar', data: list.map((item) => toNumber(item.totalCount)) },
      { name: '平均分', type: 'bar', data: list.map((item) => toNumber(item.avgFinalScore)) },
      {
        name: '通过率',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.passRate))
      }
    ]
  })
}

const renderStudyHourChart = () => {
  const chart = getChart(studyHourRef.value)
  if (!chart) return

  const list = analysisData.value.studyHourSegmentStats || []
  chart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { top: 0, right: 8 },
    grid: { left: 46, right: 46, top: 48, bottom: 36 },
    xAxis: { type: 'category', data: list.map((item) => item.segmentName || '-') },
    yAxis: [
      { type: 'value', name: '分/%' },
      { type: 'value', name: '人数', splitLine: { show: false } }
    ],
    series: [
      { name: '平均分', type: 'bar', data: list.map((item) => toNumber(item.avgFinalScore)) },
      {
        name: '作业完成率',
        type: 'line',
        smooth: true,
        data: list.map((item) => toNumber(item.avgAssignmentRate))
      },
      {
        name: '人数',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.totalCount))
      }
    ]
  })
}

const renderIncomeChart = () => {
  const chart = getChart(incomeRef.value)
  if (!chart) return

  const list = analysisData.value.familyIncomeStats || []
  chart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { top: 0, right: 8 },
    grid: { left: 46, right: 46, top: 48, bottom: 40 },
    xAxis: {
      type: 'category',
      data: list.map((item) => item.labelName || '-'),
      axisLabel: { interval: 0, rotate: 20 }
    },
    yAxis: [
      { type: 'value', name: '分' },
      { type: 'value', name: '通过率', axisLabel: { formatter: '{value}%' } }
    ],
    series: [
      { name: '平均分', type: 'bar', data: list.map((item) => toNumber(item.avgFinalScore)) },
      { name: '历史GPA', type: 'bar', data: list.map((item) => toNumber(item.avgPreviousGpa)) },
      {
        name: '通过率',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.passRate))
      }
    ]
  })
}

const renderParentalChart = () => {
  const chart = getChart(parentalRef.value)
  if (!chart) return

  const list = analysisData.value.parentalEducationStats || []
  chart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { top: 0, right: 8 },
    grid: { left: 46, right: 46, top: 48, bottom: 42 },
    xAxis: {
      type: 'category',
      data: list.map((item) => item.labelName || '-'),
      axisLabel: { interval: 0, rotate: 20 }
    },
    yAxis: [
      { type: 'value', name: '分' },
      { type: 'value', name: '通过率', axisLabel: { formatter: '{value}%' } }
    ],
    series: [
      { name: '平均分', type: 'bar', data: list.map((item) => toNumber(item.avgFinalScore)) },
      { name: '学习时长', type: 'bar', data: list.map((item) => toNumber(item.avgStudyHours)) },
      {
        name: '通过率',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.passRate))
      }
    ]
  })
}

const renderResourceChart = () => {
  const chart = getChart(resourceRef.value)
  if (!chart) return

  const internetList: ResourceStatItem[] = (analysisData.value.internetAccessStats || []).map(
    (item) => {
      const stat = item as ResourceStatItem
      return {
        ...stat,
        labelName: `网络-${stat.labelName || '未知'}`
      }
    }
  )

  const envList: ResourceStatItem[] = (analysisData.value.studyEnvironmentStats || []).map(
    (item) => {
      const stat = item as ResourceStatItem
      return {
        ...stat,
        labelName: `环境-${stat.labelName || '未知'}`
      }
    }
  )

  const list: ResourceStatItem[] = [...internetList, ...envList]

  chart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { top: 0, right: 8 },
    grid: { left: 48, right: 46, top: 48, bottom: 70 },
    xAxis: {
      type: 'category',
      data: list.map((item) => item.labelName),
      axisLabel: { interval: 0, rotate: 28 }
    },
    yAxis: [
      { type: 'value', name: '分/%' },
      { type: 'value', name: '人数', splitLine: { show: false } }
    ],
    series: [
      { name: '平均分', type: 'bar', data: list.map((item) => toNumber(item.avgFinalScore)) },
      {
        name: '通过率',
        type: 'line',
        smooth: true,
        data: list.map((item) => toNumber(item.passRate))
      },
      {
        name: '人数',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: list.map((item) => toNumber(item.totalCount))
      }
    ]
  })
}

const renderSubjectRadarChart = () => {
  const chart = getChart(subjectRadarRef.value)
  if (!chart) return

  const list = analysisData.value.subjectRadarByGrade || []
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    radar: {
      center: ['50%', '46%'],
      radius: '62%',
      indicator: [
        { name: '数学', max: 100 },
        { name: '阅读', max: 100 },
        { name: '写作', max: 100 },
        { name: '科学', max: 100 },
        { name: '期末分', max: 100 },
        { name: '通过率', max: 100 }
      ]
    },
    series: [
      {
        name: '学科能力画像',
        type: 'radar',
        data: list.map((item) => ({
          name: item.gradeCategory || '未知等级',
          value: [
            toNumber(item.avgMathScore),
            toNumber(item.avgReadingScore),
            toNumber(item.avgWritingScore),
            toNumber(item.avgScienceScore),
            toNumber(item.avgFinalScore),
            toNumber(item.passRate)
          ]
        }))
      }
    ]
  })
}

const renderBehaviorRadarChart = () => {
  const chart = getChart(behaviorRadarRef.value)
  if (!chart) return

  const list = analysisData.value.behaviorRadarByPass || []
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    radar: {
      center: ['50%', '46%'],
      radius: '62%',
      indicator: [
        { name: '学习时长', max: 100 },
        { name: '出勤率', max: 100 },
        { name: '睡眠', max: 100 },
        { name: '作业完成', max: 100 },
        { name: '课堂参与', max: 100 },
        { name: '低社媒干扰', max: 100 }
      ]
    },
    series: [
      {
        name: '学习行为画像',
        type: 'radar',
        data: list.map((item) => ({
          name: item.labelName || '未知状态',
          value: [
            clamp(toNumber(item.avgStudyHours) * 12.5, 0, 100),
            clamp(toNumber(item.avgAttendanceRate), 0, 100),
            clamp(toNumber(item.avgSleepHours) * 12.5, 0, 100),
            clamp(toNumber(item.avgAssignmentRate), 0, 100),
            clamp(toNumber(item.avgParticipationScore), 0, 100),
            clamp(100 - toNumber(item.avgSocialMediaHours) * 12.5, 0, 100)
          ]
        }))
      }
    ]
  })
}

const renderScatterChart = () => {
  const chart = getChart(scatterRef.value)
  if (!chart) return

  const data = (analysisData.value.behaviorScoreScatter || []).map((item) => [
    toNumber(item.studyHoursPerDay),
    toNumber(item.finalExamScore),
    item.studentId || '-',
    item.passFail || '-',
    item.gradeCategory || '-',
    toNumber(item.attendanceRate),
    toNumber(item.assignmentCompletionRate),
    toNumber(item.socialMediaHours)
  ])

  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const item = params.data || []
        return [
          `学生编号：${item[2]}`,
          `学习时长：${formatNumber(item[0], 1)}h`,
          `期末成绩：${formatNumber(item[1], 1)}分`,
          `通过状态：${item[3]}`,
          `成绩等级：${item[4]}`,
          `出勤率：${formatNumber(item[5], 1)}%`,
          `作业完成率：${formatNumber(item[6], 1)}%`,
          `社媒时长：${formatNumber(item[7], 1)}h`
        ].join('<br/>')
      }
    },
    grid: { left: 54, right: 32, top: 28, bottom: 44 },
    xAxis: { type: 'value', name: '每日学习时长/h' },
    yAxis: { type: 'value', name: '期末成绩/分', min: 0, max: 100 },
    dataZoom: [{ type: 'inside' }, { type: 'slider', height: 18, bottom: 8 }],
    series: [
      {
        name: '学生样本',
        type: 'scatter',
        symbolSize: (value: number[]) => Math.max(6, Math.min(18, value[5] / 8)),
        data
      }
    ]
  })
}

const renderHeatmapChart = () => {
  const chart = getChart(heatmapRef.value)
  if (!chart) return

  const list = analysisData.value.gradeStudyHourHeatmap || []
  const xLabels = Array.from(new Set(list.map((item) => item.studyHourSegment || '-')))
  const yLabels = Array.from(new Set(list.map((item) => item.gradeCategory || '-')))
  const data = list.map((item) => [
    xLabels.indexOf(item.studyHourSegment || '-'),
    yLabels.indexOf(item.gradeCategory || '-'),
    toNumber(item.totalCount),
    toNumber(item.avgFinalScore)
  ])
  const maxValue = Math.max(...data.map((item) => Number(item[2])), 1)

  chart.setOption({
    tooltip: {
      position: 'top',
      formatter: (params: any) => {
        const item = params.data || []
        return [
          `学习时长：${xLabels[item[0]]}`,
          `成绩等级：${yLabels[item[1]]}`,
          `人数：${formatInteger(item[2])}`,
          `平均分：${formatNumber(item[3], 1)}`
        ].join('<br/>')
      }
    },
    grid: { left: 80, right: 30, top: 28, bottom: 48 },
    xAxis: { type: 'category', data: xLabels, splitArea: { show: true } },
    yAxis: { type: 'category', data: yLabels, splitArea: { show: true } },
    visualMap: {
      min: 0,
      max: maxValue,
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: 0
    },
    series: [
      {
        name: '等级学习时长热力',
        type: 'heatmap',
        data,
        label: { show: true, formatter: (params: any) => String(params.data?.[2] ?? '') },
        emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 0, 0, 0.25)' } }
      }
    ]
  })
}

const resizeCharts = () => {
  chartList.forEach((chart) => chart.resize())
}

const toNumber = (value: any) => {
  if (value === null || value === undefined || value === '') return 0
  const num = Number(value)
  return Number.isFinite(num) ? num : 0
}

const clamp = (value: number, min: number, max: number) => Math.max(min, Math.min(max, value))

const formatNumber = (value: any, precision = 2) => toNumber(value).toFixed(precision)

const formatInteger = (value: any) => `${Math.round(toNumber(value))}`

onMounted(async () => {
  await loadOptions()
  await loadAnalysis()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  chartList.forEach((chart) => chart.dispose())
})
</script>

<style scoped lang="scss">
.exam-analysis-page {
  .art-card {
    border-radius: 16px;
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
  font-weight: 650;
  color: var(--art-text-gray-900, #111827);
  line-height: 1.25;
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
  width: 210px;

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
  min-height: 128px;
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
  margin-top: 12px;
  font-size: 26px;
  font-weight: 700;
  color: var(--art-text-gray-900, #111827);
  line-height: 1;
}

.stat-card__extra {
  margin-top: 12px;
  font-size: 12px;
  color: var(--art-text-gray-500, #98a2b3);
  line-height: 1.5;
}

.analysis-layout {
  display: grid;
  grid-template-columns: repeat(12, minmax(0, 1fr));
  gap: 20px;
}

.chart-card {
  padding: 18px 20px 20px;
  min-height: 408px;
}

.chart-card--large {
  grid-column: span 6;
}

.chart-card--wide {
  grid-column: span 12;
}

.chart-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.chart-title {
  font-size: 16px;
  font-weight: 650;
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
  height: 320px;
}

.chart-box--wide {
  height: 360px;
}

.insight-section {
  margin-top: 20px;
  padding: 20px;
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
  align-items: flex-start;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid var(--art-border-color, #eaecf0);
  border-radius: 12px;
  background: #fafafa;
}

.insight-index {
  width: 24px;
  height: 24px;
  flex: 0 0 24px;
  border-radius: 999px;
  background: #2563eb;
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  line-height: 24px;
  text-align: center;
}

.insight-text {
  flex: 1;
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

  .chart-card--large,
  .chart-card--wide {
    grid-column: span 12;
  }
}

@media (max-width: 640px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
