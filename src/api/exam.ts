import request from '@/utils/http'

export interface StudentExamPerformanceItem {
  id?: number

  studentId?: string
  gender?: string
  age?: number

  parentalEducation?: string
  familyIncome?: string
  internetAccess?: string
  studyEnvironment?: string

  studyHoursPerDay?: number
  attendanceRate?: number
  sleepHours?: number
  socialMediaHours?: number
  assignmentCompletionRate?: number
  participationScore?: number

  onlineCoursesCompleted?: number
  tutoring?: string

  mathScore?: number
  readingScore?: number
  writingScore?: number
  scienceScore?: number
  finalExamScore?: number
  previousGpa?: number

  passFail?: string
  gradeCategory?: string

  createTime?: string
}

export interface StudentExamPerformanceQuery {
  current?: number
  size?: number

  studentId?: string
  gender?: string
  parentalEducation?: string
  familyIncome?: string
  internetAccess?: string
  studyEnvironment?: string
  tutoring?: string
  passFail?: string
  gradeCategory?: string

  ageMin?: number
  ageMax?: number

  finalScoreMin?: number
  finalScoreMax?: number

  studyHoursMin?: number
  studyHoursMax?: number
}

export interface StudentExamPerformanceOptions {
  genders: string[]
  parentalEducations: string[]
  familyIncomes: string[]
  internetAccessOptions: string[]
  studyEnvironments: string[]
  tutoringOptions: string[]
  passFailOptions: string[]
  gradeCategories: string[]
}

export interface ExamAnalysisData {
  summary: Record<string, any>
  passFailDistribution: Record<string, any>[]
  gradeDistribution: Record<string, any>[]
  genderDistribution: Record<string, any>[]
  subjectAverage: Record<string, any>[]
  ageSegmentStats: Record<string, any>[]
  studyHourSegmentStats: Record<string, any>[]
  familyIncomeStats: Record<string, any>[]
  parentalEducationStats: Record<string, any>[]
  internetAccessStats: Record<string, any>[]
  studyEnvironmentStats: Record<string, any>[]
  subjectRadarByGrade: Record<string, any>[]
  behaviorRadarByPass: Record<string, any>[]
  behaviorScoreScatter: Record<string, any>[]
  gradeStudyHourHeatmap: Record<string, any>[]
  insights: string[]
}

export type StudentExamPerformancePage = Api.Common.PaginatedResponse<StudentExamPerformanceItem>

export function fetchExamPage (params: StudentExamPerformanceQuery) {
  return request.get<StudentExamPerformancePage>({
    url: '/api/common/exam/page',
    params
  })
}

export function fetchExamOptions () {
  return request.get<StudentExamPerformanceOptions>({
    url: '/api/common/exam/options'
  })
}

export function fetchExamAnalysis (params?: StudentExamPerformanceQuery) {
  return request.get<ExamAnalysisData>({
    url: '/api/common/exam/analysis',
    params
  })
}

export function fetchCreateExam (data: StudentExamPerformanceItem) {
  return request.post<string>({
    url: '/api/common/exam',
    data,
    showSuccessMessage: true
  })
}

export function fetchUpdateExam (id: number, data: StudentExamPerformanceItem) {
  return request.put<string>({
    url: `/api/common/exam/${id}`,
    data,
    showSuccessMessage: true
  })
}

export function fetchDeleteExam (id: number) {
  return request.del<string>({
    url: `/api/common/exam/${id}`,
    showSuccessMessage: true
  })
}
