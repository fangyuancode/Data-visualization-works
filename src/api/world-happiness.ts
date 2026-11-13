import request from '@/utils/http'

export interface WorldHappinessReportItem {
  id?: number

  year?: number
  rankInYear?: number
  country?: string

  happinessScore?: number
  lowerWhisker?: number
  upperWhisker?: number

  explainedLogGdpPerCapita?: number
  explainedSocialSupport?: number
  explainedHealthyLifeExpectancy?: number
  explainedFreedom?: number
  explainedGenerosity?: number
  explainedCorruption?: number
  dystopiaPlusResidual?: number

  completeData?: boolean
  createTime?: string
}

export interface WorldHappinessReportQuery {
  current?: number
  size?: number

  year?: number
  country?: string

  rankMin?: number
  rankMax?: number

  scoreMin?: number
  scoreMax?: number

  completeOnly?: boolean
}

export interface WorldHappinessReportOptions {
  years: number[]
  countries: string[]
}

export interface WorldHappinessAnalysisSummary {
  totalCount?: number
  completeCount?: number
  completeRate?: number
  avgScore?: number
  maxScore?: number
  minScore?: number
  avgRank?: number
  avgGdp?: number
  avgSocialSupport?: number
  avgHealthyLife?: number
  avgFreedom?: number
  avgGenerosity?: number
  avgCorruption?: number
  avgResidual?: number
}

export interface WorldHappinessAnalysisItem {
  [key: string]: any
  segmentName?: string
  factorName?: string
  country?: string
  year?: number
  yearName?: string | number
  rankInYear?: number
  totalCount?: number
  completeCount?: number
  happinessScore?: number
  avgScore?: number
  maxScore?: number
  minScore?: number
  avgRank?: number
  avgGdp?: number
  avgSocialSupport?: number
  avgHealthyLife?: number
  avgFreedom?: number
  avgGenerosity?: number
  avgCorruption?: number
  avgResidual?: number
  avgValue?: number
  ratio?: number
}

export interface WorldHappinessAnalysisResult {
  summary: WorldHappinessAnalysisSummary
  scoreSegmentStats: WorldHappinessAnalysisItem[]
  rankSegmentStats: WorldHappinessAnalysisItem[]
  yearTrend: WorldHappinessAnalysisItem[]
  factorContribution: WorldHappinessAnalysisItem[]
  topCountryRank: WorldHappinessAnalysisItem[]
  countryAverageRank: WorldHappinessAnalysisItem[]
  factorRadarByScoreSegment: WorldHappinessAnalysisItem[]
  scoreFactorScatter: WorldHappinessAnalysisItem[]
  yearScoreHeatmap: WorldHappinessAnalysisItem[]
  insights: string[]
}

export type WorldHappinessReportPage = Api.Common.PaginatedResponse<WorldHappinessReportItem>

export function fetchWorldHappinessPage (params: WorldHappinessReportQuery) {
  return request.get<WorldHappinessReportPage>({
    url: '/api/common/world-happiness/page',
    params
  })
}

export function fetchWorldHappinessOptions () {
  return request.get<WorldHappinessReportOptions>({
    url: '/api/common/world-happiness/options'
  })
}

export function fetchWorldHappinessAnalysis (params: WorldHappinessReportQuery) {
  return request.get<WorldHappinessAnalysisResult>({
    url: '/api/common/world-happiness/analysis',
    params
  })
}

export function fetchCreateWorldHappiness (data: WorldHappinessReportItem) {
  return request.post<string>({
    url: '/api/common/world-happiness',
    data,
    showSuccessMessage: true
  })
}

export function fetchUpdateWorldHappiness (id: number, data: WorldHappinessReportItem) {
  return request.put<string>({
    url: `/api/common/world-happiness/${id}`,
    data,
    showSuccessMessage: true
  })
}

export function fetchDeleteWorldHappiness (id: number) {
  return request.del<string>({
    url: `/api/common/world-happiness/${id}`,
    showSuccessMessage: true
  })
}
