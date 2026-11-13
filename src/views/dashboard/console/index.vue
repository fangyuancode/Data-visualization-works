<template>
  <div class="console-page">
    <section class="hero-card">
      <div class="hero-card__content">
        <div class="hero-tag">数据可视化项目 · 工作台</div>
        <h1>多源数据管理与智能可视化分析平台</h1>
        <p>
          面向消费者购物、NIPT检测、北京房屋租赁、学生考试、青少年心理健康、电视剧播放量、世界幸福报告等数据集，
          提供数据管理、统计挖掘、图表分析和规律解读的一体化能力。
        </p>

        <div class="hero-actions">
          <ElButton type="primary" size="large" @click="go('/source/comsumer')">
            进入数据管理
          </ElButton>
          <ElButton size="large" plain @click="openScreen">
            查看总览大屏
          </ElButton>
        </div>
      </div>

      <div class="hero-card__panel">
        <div class="panel-title">平台能力概览</div>
        <div class="panel-list">
          <div v-for="item in abilityList" :key="item.label" class="panel-item">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
          </div>
        </div>
      </div>
    </section>

    <section class="stat-grid">
      <div v-for="item in statCards" :key="item.label" class="stat-card">
        <div class="stat-card__icon" :class="item.className">{{ item.icon }}</div>
        <div>
          <div class="stat-card__value">{{ item.value }}</div>
          <div class="stat-card__label">{{ item.label }}</div>
          <div class="stat-card__desc">{{ item.desc }}</div>
        </div>
      </div>
    </section>

    <section class="section-card">
      <div class="section-head">
        <div>
          <h2>数据资产与分析入口</h2>
          <p>围绕不同主题数据表，提供增删改查、统计分析、可视化图表和规律解读。</p>
        </div>
      </div>

      <div class="dataset-grid">
        <div v-for="item in datasetList" :key="item.name" class="dataset-card">
          <div class="dataset-card__top">
            <div class="dataset-icon">{{ item.icon }}</div>
            <div>
              <h3>{{ item.name }}</h3>
              <p>{{ item.table }}</p>
            </div>
          </div>

          <div class="dataset-card__desc">{{ item.desc }}</div>

          <div class="dataset-tags">
            <span v-for="tag in item.tags" :key="tag">{{ tag }}</span>
          </div>

          <div class="dataset-actions">
            <ElButton size="small" plain @click="go(item.managePath)">数据管理</ElButton>
            <ElButton size="small" type="primary" plain @click="go(item.analysisPath)">
              可视化分析
            </ElButton>
          </div>
        </div>
      </div>
    </section>

    <section class="chart-layout">
      <div class="chart-card chart-card--wide">
        <div class="chart-head">
          <div>
            <h2>各数据集分析覆盖度</h2>
            <p>统计每个主题模块已覆盖的分析维度数量。</p>
          </div>
        </div>
        <div ref="coverageChartRef" class="chart-box"></div>
      </div>

      <div class="chart-card">
        <div class="chart-head">
          <div>
            <h2>图表类型占比</h2>
            <p>柱状图、饼图、雷达图、地图、散点图、热力图等组合展示。</p>
          </div>
        </div>
        <div ref="typeChartRef" class="chart-box"></div>
      </div>

      <div class="chart-card">
        <div class="chart-head">
          <div>
            <h2>平台能力雷达</h2>
            <p>从数据治理、挖掘分析、图表表达等维度评估。</p>
          </div>
        </div>
        <div ref="radarChartRef" class="chart-box"></div>
      </div>
    </section>

    <section class="bottom-layout">
      <div class="section-card compact-card">
        <div class="section-head">
          <div>
            <h2>系统能力矩阵</h2>
            <p>当前平台已经形成从数据维护到规律解读的完整闭环。</p>
          </div>
        </div>

        <div class="ability-grid">
          <div v-for="item in featureList" :key="item.title" class="ability-card">
            <h3>{{ item.title }}</h3>
            <p>{{ item.desc }}</p>
          </div>
        </div>
      </div>

      <div class="section-card compact-card">
        <div class="section-head">
          <div>
            <h2>后续优化方向</h2>
            <p>适合继续完善为课程设计、毕业设计或竞赛展示平台。</p>
          </div>
        </div>

        <div class="todo-list">
          <div v-for="item in todoList" :key="item.title" class="todo-item">
            <div class="todo-dot"></div>
            <div>
              <h3>{{ item.title }}</h3>
              <p>{{ item.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'

const router = useRouter()

const coverageChartRef = ref<HTMLDivElement | null>(null)
const typeChartRef = ref<HTMLDivElement | null>(null)
const radarChartRef = ref<HTMLDivElement | null>(null)

let coverageChart: echarts.ECharts | null = null
let typeChart: echarts.ECharts | null = null
let radarChart: echarts.ECharts | null = null

const go = (path: string) => {
  router.push(path)
}
const openScreen = () => {
  const routeData = router.resolve({
    path: '/screen'
  })
  window.open(routeData.href, '_blank')
}
const abilityList = [
  { label: '数据主题', value: '7类' },
  { label: '分析页面', value: '7个' },
  { label: '图表模型', value: '30+' },
  { label: '接口服务', value: '/api/common' }
]

const statCards = [
  {
    label: '数据管理模块',
    value: '7',
    desc: '覆盖多主题数据表 CRUD',
    icon: '数',
    className: 'blue'
  },
  {
    label: '可视化分析页面',
    value: '7',
    desc: '每个数据源对应独立分析页',
    icon: '图',
    className: 'green'
  },
  {
    label: '统计分析接口',
    value: '7+',
    desc: '统一放置在 /api/common 下',
    icon: '接',
    className: 'orange'
  },
  {
    label: '分析图表类型',
    value: '8+',
    desc: '柱状图、饼图、雷达图、地图、散点图等',
    icon: '析',
    className: 'purple'
  }
]

const datasetList = [
  {
    name: '消费者购物趋势分析',
    table: 'consumer_shopping_trends',
    icon: '购',
    desc: '分析购物偏好、年龄层、收入层、配送售后、平台选择和消费画像。',
    tags: ['消费偏好', '收入分层', '雷达画像'],
    managePath: '/source/comsumer',
    analysisPath: '/source/comsumer-analysis'
  },
  {
    name: '孕妇 NIPT 检测数据',
    table: 'maternal_nipt_data',
    icon: '检',
    desc: '分析年龄、BMI、孕周、测序质量、Z 值和胎儿健康状态。',
    tags: ['BMI分层', '检测质量', '风险提示'],
    managePath: '/source/nipt',
    analysisPath: '/source/nipt-analysis'
  },
  {
    name: '北京房屋租赁数据',
    table: 'rental_house',
    icon: '房',
    desc: '结合北京地图分析区域租金、户型结构、地铁溢价和面积租金关系。',
    tags: ['北京地图', '区域租金', '交通溢价'],
    managePath: '/source/rental',
    analysisPath: '/source/rental-analysis'
  },
  {
    name: '学生考试表现数据',
    table: 'student_exam_performance',
    icon: '学',
    desc: '分析成绩等级、学习时长、家庭背景、资源条件与通过状态关系。',
    tags: ['成绩分析', '学习行为', '教育资源'],
    managePath: '/source/exam',
    analysisPath: '/source/exam-analysis'
  },
  {
    name: '青少年心理健康数据',
    table: 'teen_mental_health',
    icon: '心',
    desc: '分析社媒使用、睡眠、焦虑、压力、成瘾水平和抑郁风险。',
    tags: ['心理健康', '社媒影响', '风险画像'],
    managePath: '/source/teen',
    analysisPath: '/source/teen-analysis'
  },
  {
    name: '电视剧播放量数据',
    table: 'tv_drama_play_data',
    icon: '剧',
    desc: '分析播放热度、题材类型、评分口碑、互动反馈和爆款规律。',
    tags: ['播放热度', '题材分析', '口碑评分'],
    managePath: '/source/tvplay',
    analysisPath: '/source/tvplay-analysis'
  },
  {
    name: '世界幸福报告数据',
    table: 'world_happiness_report',
    icon: '幸',
    desc: '分析幸福分数、排名梯队、GDP、社会支持、健康寿命和自由度。',
    tags: ['幸福指数', '国家排名', '影响因子'],
    managePath: '/source/world',
    analysisPath: '/source/world-analysis'
  }
]

const featureList = [
  {
    title: '数据源管理',
    desc: '每张数据表提供分页查询、新增、编辑、删除和条件筛选能力，方便维护原始数据。'
  },
  {
    title: '统计挖掘接口',
    desc: '后端基于 MyBatis 聚合查询，输出分布、排行、分层、趋势、散点和热力矩阵数据。'
  },
  {
    title: 'ECharts 可视化',
    desc: '前端统一使用 ECharts 进行柱状图、饼图、雷达图、地图、散点图和热力图展示。'
  },
  {
    title: '规律价值解读',
    desc: '每个分析页面提供自动洞察文本，把图表结论转化为可读的数据分析说明。'
  }
]

const todoList = [
  {
    title: '汇总型数据大屏',
    desc: '可继续新增一个总览大屏，把所有主题数据的核心指标汇总展示。'
  },
  {
    title: '分析报告导出',
    desc: '支持将图表与洞察内容导出为 PDF 或 Word，便于课程设计和答辩展示。'
  },
  {
    title: '智能问答分析',
    desc: '结合大模型接口，让用户用自然语言询问某个数据集背后的规律。'
  }
]

const initCoverageChart = () => {
  if (!coverageChartRef.value) return

  coverageChart = echarts.init(coverageChartRef.value)

  coverageChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { top: 36, left: 36, right: 24, bottom: 56 },
    xAxis: {
      type: 'category',
      data: ['购物', 'NIPT', '租赁', '考试', '心理', '剧集', '幸福'],
      axisTick: { show: false },
      axisLine: { lineStyle: { color: '#d8e0ef' } },
      axisLabel: { color: '#64748b' }
    },
    yAxis: {
      type: 'value',
      name: '分析维度',
      nameTextStyle: { color: '#64748b' },
      splitLine: { lineStyle: { color: '#edf2f7' } },
      axisLabel: { color: '#64748b' }
    },
    series: [
      {
        name: '分析维度数量',
        type: 'bar',
        barWidth: 26,
        data: [8, 10, 10, 12, 10, 11, 10],
        itemStyle: {
          borderRadius: [8, 8, 0, 0],
          color: '#3b82f6'
        }
      },
      {
        name: '基础要求',
        type: 'line',
        data: [6, 6, 6, 6, 6, 6, 6],
        smooth: true,
        symbol: 'circle',
        symbolSize: 7,
        lineStyle: { width: 2, color: '#f59e0b' },
        itemStyle: { color: '#f59e0b' }
      }
    ]
  })
}

const initTypeChart = () => {
  if (!typeChartRef.value) return

  typeChart = echarts.init(typeChartRef.value)

  typeChart.setOption({
    tooltip: { trigger: 'item' },
    legend: {
      bottom: 0,
      itemWidth: 10,
      itemHeight: 10,
      textStyle: { color: '#64748b' }
    },
    series: [
      {
        name: '图表类型',
        type: 'pie',
        radius: ['42%', '68%'],
        center: ['50%', '43%'],
        avoidLabelOverlap: true,
        label: {
          formatter: '{b}\n{d}%',
          color: '#475569'
        },
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        data: [
          { name: '柱状图', value: 28 },
          { name: '饼图', value: 18 },
          { name: '雷达图', value: 14 },
          { name: '散点图', value: 12 },
          { name: '地图/热力图', value: 10 }
        ]
      }
    ]
  })
}

const initRadarChart = () => {
  if (!radarChartRef.value) return

  radarChart = echarts.init(radarChartRef.value)

  radarChart.setOption({
    tooltip: {},
    radar: {
      radius: '66%',
      indicator: [
        { name: '数据管理', max: 100 },
        { name: '统计挖掘', max: 100 },
        { name: '图表表达', max: 100 },
        { name: '页面体验', max: 100 },
        { name: '洞察解读', max: 100 },
        { name: '扩展能力', max: 100 }
      ],
      splitArea: {
        areaStyle: {
          color: ['#f8fafc', '#eef5ff']
        }
      },
      axisName: { color: '#64748b' },
      axisLine: { lineStyle: { color: '#d8e0ef' } },
      splitLine: { lineStyle: { color: '#d8e0ef' } }
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            name: '当前平台能力',
            value: [92, 88, 90, 86, 84, 80],
            areaStyle: { color: 'rgba(59, 130, 246, 0.18)' },
            lineStyle: { color: '#3b82f6', width: 2 },
            itemStyle: { color: '#3b82f6' }
          }
        ]
      }
    ]
  })
}

const resizeCharts = () => {
  coverageChart?.resize()
  typeChart?.resize()
  radarChart?.resize()
}

onMounted(() => {
  nextTick(() => {
    initCoverageChart()
    initTypeChart()
    initRadarChart()
    window.addEventListener('resize', resizeCharts)
  })
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  coverageChart?.dispose()
  typeChart?.dispose()
  radarChart?.dispose()
})
</script>

<style scoped lang="scss">
.console-page {
  min-height: 100%;
  // padding: 20px;
  background: #fafbfc;
  color: #1e293b;
}

.hero-card {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 340px;
  gap: 20px;
  padding: 28px;
  border: 1px solid rgba(148, 163, 184, 0.22);
  border-radius: 18px;
  background: #ffffff;
  box-shadow: 0 14px 40px rgba(15, 23, 42, 0.06);
  // box-sizing: border-box;
}

.hero-tag {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  margin-bottom: 14px;
  border-radius: 999px;
  color: #2563eb;
  font-size: 13px;
  font-weight: 600;
  background: rgba(37, 99, 235, 0.1);
}

.hero-card h1 {
  margin: 0;
  font-size: 30px;
  line-height: 1.25;
  font-weight: 800;
  color: #0f172a;
}

.hero-card p {
  max-width: 920px;
  margin: 14px 0 0;
  color: #64748b;
  line-height: 1.8;
  font-size: 15px;
}

.hero-actions {
  margin-top: 22px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.hero-card__panel {
  border-radius: 16px;
  padding: 18px;
  background: #f8fafc;
  border: 1px solid rgba(226, 232, 240, 0.9);
}

.panel-title {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 12px;
}

.panel-list {
  display: grid;
  gap: 10px;
}

.panel-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  border-radius: 12px;
  background: #ffffff;
  color: #64748b;
}

.panel-item strong {
  color: #2563eb;
  font-size: 18px;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-top: 18px;
}

.stat-card {
  display: flex;
  gap: 14px;
  align-items: flex-start;
  padding: 18px;
  border-radius: 16px;
  border: 1px solid rgba(226, 232, 240, 0.9);
  background: #ffffff;
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.04);
  margin-bottom: 0;
}

.stat-card__icon {
  width: 46px;
  height: 46px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  font-weight: 800;
  color: #fff;
}

.stat-card__icon.blue {
  background: #3b82f6;
}

.stat-card__icon.green {
  background: #10b981;
}

.stat-card__icon.orange {
  background: #f59e0b;
}

.stat-card__icon.purple {
  background: #8b5cf6;
}

.stat-card__value {
  font-size: 26px;
  line-height: 1;
  font-weight: 800;
  color: #0f172a;
}

.stat-card__label {
  margin-top: 7px;
  font-size: 15px;
  font-weight: 700;
  color: #334155;
}

.stat-card__desc {
  margin-top: 5px;
  font-size: 13px;
  color: #94a3b8;
}

.section-card,
.chart-card {
  margin-top: 18px;
  padding: 20px;
  border-radius: 18px;
  background: #ffffff;
  border: 1px solid rgba(226, 232, 240, 0.9);
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.04);
}

.section-head,
.chart-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.section-head h2,
.chart-head h2 {
  margin: 0;
  color: #0f172a;
  font-size: 18px;
  font-weight: 800;
}

.section-head p,
.chart-head p {
  margin: 6px 0 0;
  color: #94a3b8;
  font-size: 13px;
}

.dataset-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.dataset-card {
  padding: 16px;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  transition: all 0.2s ease;
}

.dataset-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 28px rgba(15, 23, 42, 0.08);
}

.dataset-card__top {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dataset-icon {
  width: 42px;
  height: 42px;
  flex: 0 0 42px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2563eb;
  background: rgba(37, 99, 235, 0.1);
  font-weight: 800;
}

.dataset-card h3 {
  margin: 0;
  font-size: 15px;
  color: #0f172a;
}

.dataset-card__top p {
  margin: 4px 0 0;
  font-size: 12px;
  color: #94a3b8;
}

.dataset-card__desc {
  min-height: 48px;
  margin-top: 12px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

.dataset-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 12px;
}

.dataset-tags span {
  padding: 4px 8px;
  border-radius: 999px;
  font-size: 12px;
  color: #475569;
  background: #eef2f7;
}

.dataset-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 14px;
}

.chart-layout {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.chart-card--wide {
  grid-column: 1 / -1;
}

.chart-box {
  width: 100%;
  height: 320px;
}

.bottom-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(0, 0.8fr);
  gap: 18px;
}

.compact-card {
  height: calc(100% - 18px);
}

.ability-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.ability-card {
  padding: 16px;
  border-radius: 14px;
  background: #f8fafc;
  border: 1px solid #edf2f7;
}

.ability-card h3,
.todo-item h3 {
  margin: 0;
  color: #0f172a;
  font-size: 15px;
}

.ability-card p,
.todo-item p {
  margin: 8px 0 0;
  color: #64748b;
  line-height: 1.7;
  font-size: 13px;
}

.todo-list {
  display: grid;
  gap: 14px;
}

.todo-item {
  display: flex;
  gap: 12px;
  padding: 14px;
  border-radius: 14px;
  background: #f8fafc;
  border: 1px solid #edf2f7;
}

.todo-dot {
  width: 10px;
  height: 10px;
  margin-top: 5px;
  flex: 0 0 10px;
  border-radius: 50%;
  background: #3b82f6;
  box-shadow: 0 0 0 5px rgba(59, 130, 246, 0.12);
}

@media (max-width: 1400px) {
  .dataset-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 1200px) {
  .hero-card,
  .bottom-layout {
    grid-template-columns: 1fr;
  }

  .stat-grid,
  .dataset-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .console-page {
    padding: 12px;
  }

  .hero-card {
    padding: 20px;
  }

  .hero-card h1 {
    font-size: 24px;
  }

  .stat-grid,
  .dataset-grid,
  .chart-layout,
  .ability-grid {
    grid-template-columns: 1fr;
  }

  .chart-card--wide {
    grid-column: auto;
  }
}
</style>