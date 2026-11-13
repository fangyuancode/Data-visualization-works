<template>
  <div class="exam-page">
    <div class="art-card page-header">
      <div>
        <div class="page-title">学生考试表现数据管理</div>
        <div class="page-desc">
          支持学生基础信息、家庭背景、学习行为、成绩指标、通过状态和成绩等级的分页展示、条件查询、新增、编辑和删除。
        </div>
      </div>

      <ElButton v-if="canAdd" type="primary" @click="openDialog('add')">新增学生数据</ElButton>
    </div>

    <ElRow :gutter="20" class="mb-20">
      <ElCol v-for="item in statCards" :key="item.label" :xs="24" :sm="12" :lg="6">
        <div class="art-card stat-card">
          <div class="stat-label">{{ item.label }}</div>
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-extra">{{ item.extra }}</div>
        </div>
      </ElCol>
    </ElRow>

    <div class="art-card search-card mb-20">
      <ElForm :model="searchForm" inline label-width="90px" class="search-form">
        <ElFormItem label="学生编号">
          <ElInput v-model="searchForm.studentId" placeholder="请输入学生编号" clearable style="width: 170px" />
        </ElFormItem>

        <ElFormItem label="性别">
          <ElSelect v-model="searchForm.gender" placeholder="请选择" clearable style="width: 120px">
            <ElOption v-for="item in options.genders" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="父母教育">
          <ElSelect v-model="searchForm.parentalEducation" placeholder="请选择" clearable filterable style="width: 160px">
            <ElOption v-for="item in options.parentalEducations" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="家庭收入">
          <ElSelect v-model="searchForm.familyIncome" placeholder="请选择" clearable filterable style="width: 150px">
            <ElOption v-for="item in options.familyIncomes" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="网络条件">
          <ElSelect v-model="searchForm.internetAccess" placeholder="请选择" clearable filterable style="width: 140px">
            <ElOption v-for="item in options.internetAccessOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="学习环境">
          <ElSelect v-model="searchForm.studyEnvironment" placeholder="请选择" clearable filterable style="width: 150px">
            <ElOption v-for="item in options.studyEnvironments" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="是否辅导">
          <ElSelect v-model="searchForm.tutoring" placeholder="请选择" clearable style="width: 120px">
            <ElOption v-for="item in options.tutoringOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="通过状态">
          <ElSelect v-model="searchForm.passFail" placeholder="请选择" clearable style="width: 120px">
            <ElOption v-for="item in options.passFailOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="成绩等级">
          <ElSelect v-model="searchForm.gradeCategory" placeholder="请选择" clearable style="width: 120px">
            <ElOption v-for="item in options.gradeCategories" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="年龄">
          <div class="range-input">
            <ElInputNumber v-model="searchForm.ageMin" :min="0" :controls="false" placeholder="最小" />
            <span>-</span>
            <ElInputNumber v-model="searchForm.ageMax" :min="0" :controls="false" placeholder="最大" />
          </div>
        </ElFormItem>

        <ElFormItem label="期末成绩">
          <div class="range-input">
            <ElInputNumber v-model="searchForm.finalScoreMin" :min="0" :controls="false" placeholder="最低" />
            <span>-</span>
            <ElInputNumber v-model="searchForm.finalScoreMax" :min="0" :controls="false" placeholder="最高" />
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
        <ElSpace wrap>
          <ElButton v-if="canAdd" type="primary" plain @click="openDialog('add')">新增</ElButton>
          <ElButton v-if="canEdit" :disabled="selectedRows.length !== 1" @click="handleBatchEdit">编辑</ElButton>
          <ElButton type="success" plain @click="handleOpenAnalysis">可视化分析</ElButton>
        </ElSpace>

        <div class="toolbar-tip">当前共 {{ pagination.total }} 条学生考试表现数据</div>
      </div>

      <ElTable v-loading="loading" :data="tableData" height="560" style="width: 100%" @selection-change="handleSelectionChange">
        <ElTableColumn v-if="canWrite" type="selection" width="55" fixed="left" />
        <ElTableColumn prop="id" label="ID" width="80" fixed="left" />

        <ElTableColumn label="学生信息" min-width="190" fixed="left">
          <template #default="{ row }">
            <div class="student-cell">
              <div class="student-main">{{ row.studentId || '-' }}</div>
              <div class="student-sub">
                {{ row.gender || '-' }} / {{ row.age || '-' }}岁 / {{ row.gradeCategory || '-' }}
              </div>
            </div>
          </template>
        </ElTableColumn>

        <ElTableColumn label="通过状态" width="110" align="center">
          <template #default="{ row }">
            <ElTag :type="getPassTagType(row.passFail)" effect="light" round>
              {{ row.passFail || '-' }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn label="期末成绩" width="110" align="center">
          <template #default="{ row }">
            <span class="score-text">{{ formatNumber(row.finalExamScore, 1) }}</span>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="mathScore" label="数学" width="90" align="center" />
        <ElTableColumn prop="readingScore" label="阅读" width="90" align="center" />
        <ElTableColumn prop="writingScore" label="写作" width="90" align="center" />
        <ElTableColumn prop="scienceScore" label="科学" width="90" align="center" />
        <ElTableColumn prop="previousGpa" label="历史GPA" width="100" align="center" />

        <ElTableColumn prop="studyHoursPerDay" label="每日学习/h" width="120" align="center" />
        <ElTableColumn label="出勤率" width="100" align="center">
          <template #default="{ row }">{{ formatPercent(row.attendanceRate) }}</template>
        </ElTableColumn>
        <ElTableColumn prop="sleepHours" label="睡眠/h" width="100" align="center" />
        <ElTableColumn prop="socialMediaHours" label="社媒/h" width="100" align="center" />
        <ElTableColumn label="作业完成率" width="120" align="center">
          <template #default="{ row }">{{ formatPercent(row.assignmentCompletionRate) }}</template>
        </ElTableColumn>
        <ElTableColumn prop="participationScore" label="参与得分" width="110" align="center" />
        <ElTableColumn prop="onlineCoursesCompleted" label="在线课程数" width="120" align="center" />

        <ElTableColumn prop="parentalEducation" label="父母教育" width="150" />
        <ElTableColumn prop="familyIncome" label="家庭收入" width="130" />
        <ElTableColumn prop="internetAccess" label="网络条件" width="110" />
        <ElTableColumn prop="studyEnvironment" label="学习环境" width="130" />
        <ElTableColumn prop="tutoring" label="是否辅导" width="110" />
        <ElTableColumn prop="createTime" label="导入时间" min-width="180" />

        <ElTableColumn v-if="canWrite" label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <ElSpace>
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

    <ElDialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增学生考试表现数据' : '编辑学生考试表现数据'" width="980px" destroy-on-close>
      <ElForm ref="dialogFormRef" :model="dialogForm" label-width="120px">
        <ElDivider content-position="left">学生基础信息</ElDivider>

        <ElRow :gutter="16">
          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="学生编号">
              <ElInput v-model="dialogForm.studentId" placeholder="请输入学生编号" clearable />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="性别">
              <ElSelect v-model="dialogForm.gender" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.genders" :key="item" :label="item" :value="item" />
                <ElOption label="Male" value="Male" />
                <ElOption label="Female" value="Female" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="年龄">
              <ElInputNumber v-model="dialogForm.age" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="父母教育">
              <ElSelect v-model="dialogForm.parentalEducation" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.parentalEducations" :key="item" :label="item" :value="item" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="家庭收入">
              <ElSelect v-model="dialogForm.familyIncome" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.familyIncomes" :key="item" :label="item" :value="item" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="网络条件">
              <ElSelect v-model="dialogForm.internetAccess" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.internetAccessOptions" :key="item" :label="item" :value="item" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="学习环境">
              <ElSelect v-model="dialogForm.studyEnvironment" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.studyEnvironments" :key="item" :label="item" :value="item" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="是否辅导">
              <ElSelect v-model="dialogForm.tutoring" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.tutoringOptions" :key="item" :label="item" :value="item" />
                <ElOption label="Yes" value="Yes" />
                <ElOption label="No" value="No" />
              </ElSelect>
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElDivider content-position="left">学习行为</ElDivider>

        <ElRow :gutter="16">
          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="每日学习时长">
              <ElInputNumber v-model="dialogForm.studyHoursPerDay" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="出勤率">
              <ElInputNumber v-model="dialogForm.attendanceRate" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="睡眠时长">
              <ElInputNumber v-model="dialogForm.sleepHours" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="社媒时长">
              <ElInputNumber v-model="dialogForm.socialMediaHours" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="作业完成率">
              <ElInputNumber v-model="dialogForm.assignmentCompletionRate" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="课堂参与得分">
              <ElInputNumber v-model="dialogForm.participationScore" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="在线课程数">
              <ElInputNumber v-model="dialogForm.onlineCoursesCompleted" :min="0" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElDivider content-position="left">成绩信息</ElDivider>

        <ElRow :gutter="16">
          <ElCol v-for="field in scoreFields" :key="field.prop" :xs="24" :sm="12" :lg="8">
            <ElFormItem :label="field.label">
              <ElInputNumber v-model="dialogForm[field.prop]" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="通过状态">
              <ElSelect v-model="dialogForm.passFail" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.passFailOptions" :key="item" :label="item" :value="item" />
                <ElOption label="Pass" value="Pass" />
                <ElOption label="Fail" value="Fail" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :xs="24" :sm="12" :lg="8">
            <ElFormItem label="成绩等级">
              <ElSelect v-model="dialogForm.gradeCategory" clearable filterable allow-create style="width: 100%">
                <ElOption v-for="item in options.gradeCategories" :key="item" :label="item" :value="item" />
              </ElSelect>
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
  fetchCreateExam,
  fetchDeleteExam,
  fetchExamOptions,
  fetchExamPage,
  fetchUpdateExam
} from '@/api/exam'
import type { StudentExamPerformanceItem, StudentExamPerformanceOptions } from '@/api/exam'

import { usePagePermission } from '@/hooks/core/usePagePermission'

defineOptions({ name: 'StudentExamPerformanceManage' })

type DialogType = 'add' | 'edit'

type ScoreField = {
  label: string
  prop: keyof StudentExamPerformanceItem
}

const router = useRouter()
const { canWrite, canAdd, canEdit, canDelete, checkWritePermission } = usePagePermission()

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref<DialogType>('add')
const dialogFormRef = ref<FormInstance>()

const tableData = ref<StudentExamPerformanceItem[]>([])
const selectedRows = ref<StudentExamPerformanceItem[]>([])

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

const searchForm = reactive({
  studentId: '',
  gender: '',
  parentalEducation: '',
  familyIncome: '',
  internetAccess: '',
  studyEnvironment: '',
  tutoring: '',
  passFail: '',
  gradeCategory: '',
  ageMin: undefined as number | undefined,
  ageMax: undefined as number | undefined,
  finalScoreMin: undefined as number | undefined,
  finalScoreMax: undefined as number | undefined,
  studyHoursMin: undefined as number | undefined,
  studyHoursMax: undefined as number | undefined
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const scoreFields: ScoreField[] = [
  { label: '数学成绩', prop: 'mathScore' },
  { label: '阅读成绩', prop: 'readingScore' },
  { label: '写作成绩', prop: 'writingScore' },
  { label: '科学成绩', prop: 'scienceScore' },
  { label: '期末成绩', prop: 'finalExamScore' },
  { label: '历史GPA', prop: 'previousGpa' }
]

const getDefaultForm = (): StudentExamPerformanceItem => ({
  studentId: '',
  gender: '',
  age: undefined,
  parentalEducation: '',
  familyIncome: '',
  internetAccess: '',
  studyEnvironment: '',
  studyHoursPerDay: undefined,
  attendanceRate: undefined,
  sleepHours: undefined,
  socialMediaHours: undefined,
  assignmentCompletionRate: undefined,
  participationScore: undefined,
  onlineCoursesCompleted: undefined,
  tutoring: '',
  mathScore: undefined,
  readingScore: undefined,
  writingScore: undefined,
  scienceScore: undefined,
  finalExamScore: undefined,
  previousGpa: undefined,
  passFail: '',
  gradeCategory: ''
})

const dialogForm = reactive<StudentExamPerformanceItem>(getDefaultForm())

const statCards = computed(() => {
  const currentPageTotal = tableData.value.length

  const avgScore = currentPageTotal
    ? tableData.value.reduce((sum, item) => sum + Number(item.finalExamScore || 0), 0) /
      currentPageTotal
    : 0

  const passCount = tableData.value.filter((item) =>
    String(item.passFail || '')
      .toLowerCase()
      .includes('pass')
  ).length

  const avgStudyHours = currentPageTotal
    ? tableData.value.reduce((sum, item) => sum + Number(item.studyHoursPerDay || 0), 0) /
      currentPageTotal
    : 0

  return [
    {
      label: '数据总量',
      value: pagination.total,
      extra: '学生考试表现记录总数'
    },
    {
      label: '当前页平均分',
      value: formatNumber(avgScore, 1),
      extra: '基于当前分页数据计算'
    },
    {
      label: '当前页通过人数',
      value: passCount,
      extra: '通过状态为 Pass 的记录'
    },
    {
      label: '当前页平均学习时长',
      value: `${formatNumber(avgStudyHours, 1)}h`,
      extra: '每日学习时长均值'
    }
  ]
})

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

const loadTableData = async () => {
  loading.value = true

  try {
    const res = await fetchExamPage({
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


const handleOpenAnalysis = () => {
  router.push('/source/exam-analysis')
}

const handleReset = () => {
  searchForm.studentId = ''
  searchForm.gender = ''
  searchForm.parentalEducation = ''
  searchForm.familyIncome = ''
  searchForm.internetAccess = ''
  searchForm.studyEnvironment = ''
  searchForm.tutoring = ''
  searchForm.passFail = ''
  searchForm.gradeCategory = ''
  searchForm.ageMin = undefined
  searchForm.ageMax = undefined
  searchForm.finalScoreMin = undefined
  searchForm.finalScoreMax = undefined
  searchForm.studyHoursMin = undefined
  searchForm.studyHoursMax = undefined

  pagination.current = 1
  loadTableData()
}

const handleSelectionChange = (rows: StudentExamPerformanceItem[]) => {
  selectedRows.value = rows
}

const resetDialogForm = () => {
  Object.assign(dialogForm, getDefaultForm())
}

const openDialog = (type: DialogType, row?: StudentExamPerformanceItem) => {
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
      await fetchCreateExam(payload)
    } else {
      await fetchUpdateExam(Number(dialogForm.id), payload)
    }

    dialogVisible.value = false
    await loadTableData()
    await loadOptions()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: StudentExamPerformanceItem) => {
  if (!checkWritePermission()) {
    return
  }

  await ElMessageBox.confirm(
    `确定删除学生编号为「${row.studentId || row.id}」的考试表现数据吗？`,
    '删除确认',
    {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }
  )

  await fetchDeleteExam(Number(row.id))
  await loadTableData()
}

const getPassTagType = (value?: string) => {
  const text = String(value || '').toLowerCase()

  if (text.includes('pass')) {
    return 'success'
  }

  if (text.includes('fail')) {
    return 'danger'
  }

  return 'info'
}

const formatNumber = (value?: number | string, precision = 2) => {
  if (value === null || value === undefined || value === '') return '-'

  const num = Number(value)
  if (!Number.isFinite(num)) return '-'

  return num.toFixed(precision)
}

const formatPercent = (value?: number | string) => {
  if (value === null || value === undefined || value === '') return '-'

  const num = Number(value)
  if (!Number.isFinite(num)) return '-'

  if (num <= 1) {
    return `${(num * 100).toFixed(1)}%`
  }

  return `${num.toFixed(1)}%`
}

onMounted(async () => {
  await loadOptions()
  await loadTableData()
})
</script>

<style scoped lang="scss">
.exam-page {
  .mb-20 {
    margin-bottom: 20px;
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

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--art-text-gray-900, #111827);
  line-height: 1.2;
}

.page-desc {
  margin-top: 8px;
  font-size: 14px;
  color: var(--art-text-gray-600, #667085);
  line-height: 1.8;
}

.stat-card {
  height: 126px;
  padding: 20px;
  margin-bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-label {
  font-size: 14px;
  color: var(--art-text-gray-600, #667085);
}

.stat-value {
  margin-top: 12px;
  font-size: 28px;
  font-weight: 700;
  color: var(--art-text-gray-900, #111827);
  line-height: 1;
}

.stat-extra {
  margin-top: 12px;
  font-size: 13px;
  color: var(--art-text-gray-500, #98a2b3);
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
  gap: 8px;
  width: 210px;

  :deep(.el-input-number) {
    width: 96px;
  }
}

.table-card {
  padding: 0;
  overflow: hidden;
}

.table-toolbar {
  padding: 18px 20px;
  border-bottom: 1px solid var(--art-border-color, #eaecf0);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.toolbar-tip {
  font-size: 13px;
  color: var(--art-text-gray-500, #98a2b3);
}

.student-cell {
  min-width: 0;
}

.student-main {
  font-size: 14px;
  font-weight: 600;
  color: var(--art-text-gray-900, #111827);
  line-height: 1.5;
}

.student-sub {
  margin-top: 2px;
  font-size: 12px;
  color: var(--art-text-gray-500, #98a2b3);
  line-height: 1.5;
}

.score-text {
  font-weight: 600;
  color: #2563eb;
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
  padding: 18px 20px 20px;
  border-top: 1px solid var(--art-border-color, #eaecf0);
  background: #fff;
}

:deep(.el-table) {
  --el-table-header-bg-color: #fafafa;
}

:deep(.el-table th.el-table__cell) {
  font-weight: 600;
  color: var(--art-text-gray-700, #344054);
}

:deep(.el-dialog__body) {
  padding-top: 10px;
  padding-bottom: 10px;
}

:deep(.el-divider__text) {
  font-weight: 600;
  color: var(--art-text-gray-700, #344054);
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