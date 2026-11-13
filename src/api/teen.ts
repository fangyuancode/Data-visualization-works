import request from '@/utils/http'

export interface TeenMentalHealthItem {
  id?: number

  age?: number
  gender?: string

  dailySocialMediaHours?: number
  platformUsage?: string

  sleepHours?: number
  screenTimeBeforeSleep?: number

  academicPerformance?: number
  physicalActivity?: number

  socialInteractionLevel?: string

  stressLevel?: number
  anxietyLevel?: number
  addictionLevel?: number

  depressionLabel?: number

  createTime?: string
}

export interface TeenMentalHealthQuery {
  current?: number
  size?: number

  gender?: string
  platformUsage?: string
  socialInteractionLevel?: string
  depressionLabel?: number

  ageMin?: number
  ageMax?: number

  dailySocialMediaHoursMin?: number
  dailySocialMediaHoursMax?: number

  sleepHoursMin?: number
  sleepHoursMax?: number

  academicPerformanceMin?: number
  academicPerformanceMax?: number

  stressLevel?: number
  anxietyLevel?: number
  addictionLevel?: number
}

export interface TeenMentalHealthOptions {
  genders: string[]
  platformUsages: string[]
  socialInteractionLevels: string[]
  depressionLabels: number[]
}

export type TeenMentalHealthPage = Api.Common.PaginatedResponse<TeenMentalHealthItem>

export function fetchTeenPage (params: TeenMentalHealthQuery) {
  return request.get<TeenMentalHealthPage>({
    url: '/api/common/teen/page',
    params
  })
}

export function fetchTeenOptions () {
  return request.get<TeenMentalHealthOptions>({
    url: '/api/common/teen/options'
  })
}

export function fetchCreateTeen (data: TeenMentalHealthItem) {
  return request.post<string>({
    url: '/api/common/teen',
    data,
    showSuccessMessage: true
  })
}

export function fetchUpdateTeen (id: number, data: TeenMentalHealthItem) {
  return request.put<string>({
    url: `/api/common/teen/${id}`,
    data,
    showSuccessMessage: true
  })
}

export function fetchDeleteTeen (id: number) {
  return request.del<string>({
    url: `/api/common/teen/${id}`,
    showSuccessMessage: true
  })
}
export interface TeenMentalHealthAnalysisResult {
  summary: Record<string, any>
  depressionDistribution: Record<string, any>[]
  genderDistribution: Record<string, any>[]
  platformStats: Record<string, any>[]
  ageSegmentStats: Record<string, any>[]
  socialMediaSegmentStats: Record<string, any>[]
  sleepSegmentStats: Record<string, any>[]
  socialInteractionStats: Record<string, any>[]
  mentalRadarByRisk: Record<string, any>[]
  platformRadarStats: Record<string, any>[]
  socialAnxietyScatter: Record<string, any>[]
  screenSleepScatter: Record<string, any>[]
  platformRiskHeatmap: Record<string, any>[]
  insights: string[]
}

export function fetchTeenAnalysis (params: TeenMentalHealthQuery) {
  return request.get<TeenMentalHealthAnalysisResult>({
    url: '/api/common/teen/analysis',
    params
  })
}
