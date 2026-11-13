import request from '@/utils/http'

export interface TvDramaPlayDataItem {
  id?: number

  dramaName?: string
  dramaType?: string

  playCountText?: string
  playCount?: number

  likeCount?: number
  negativeCount?: number

  score?: number
  collectTime?: string

  createTime?: string
}

export interface TvDramaPlayQuery {
  current?: number
  size?: number

  dramaName?: string
  dramaType?: string

  playCountMin?: number
  playCountMax?: number

  likeCountMin?: number
  likeCountMax?: number

  negativeCountMin?: number
  negativeCountMax?: number

  scoreMin?: number
  scoreMax?: number

  collectStartTime?: string
  collectEndTime?: string
}

export interface TvDramaPlayOptions {
  dramaTypes: string[]
}

export interface TvDramaAnalysisSummary {
  totalCount?: number
  totalPlayCount?: number
  avgPlayCount?: number
  maxPlayCount?: number
  avgScore?: number
  avgLikeCount?: number
  avgNegativeCount?: number
  totalLikeCount?: number
  totalNegativeCount?: number
  positiveRate?: number
  highScoreRate?: number
  billionDramaRate?: number
}

export interface TvDramaAnalysisItem {
  [key: string]: any
  labelName?: string
  segmentName?: string
  monthName?: string
  dramaName?: string
  dramaType?: string
  scoreSegment?: string
  totalCount?: number
  totalPlayCount?: number
  avgPlayCount?: number
  playCount?: number
  avgScore?: number
  score?: number
  likeCount?: number
  negativeCount?: number
  totalLikeCount?: number
  totalNegativeCount?: number
  positiveRate?: number
  negativeRate?: number
  controversyRate?: number
  ratio?: number
}

export interface TvDramaPlayAnalysisResult {
  summary: TvDramaAnalysisSummary
  typeDistribution: TvDramaAnalysisItem[]
  scoreSegmentStats: TvDramaAnalysisItem[]
  playSegmentStats: TvDramaAnalysisItem[]
  monthTrend: TvDramaAnalysisItem[]
  typeRadarStats: TvDramaAnalysisItem[]
  topDramaRank: TvDramaAnalysisItem[]
  scorePlayScatter: TvDramaAnalysisItem[]
  typeScoreHeatmap: TvDramaAnalysisItem[]
  interactionByScoreSegment: TvDramaAnalysisItem[]
  insights: string[]
}

export type TvDramaPlayPage = Api.Common.PaginatedResponse<TvDramaPlayDataItem>

export function fetchTvPlayPage (params: TvDramaPlayQuery) {
  return request.get<TvDramaPlayPage>({
    url: '/api/common/tvplay/page',
    params
  })
}

export function fetchTvPlayOptions () {
  return request.get<TvDramaPlayOptions>({
    url: '/api/common/tvplay/options'
  })
}

export function fetchTvPlayAnalysis (params: TvDramaPlayQuery) {
  return request.get<TvDramaPlayAnalysisResult>({
    url: '/api/common/tvplay/analysis',
    params
  })
}

export function fetchCreateTvPlay (data: TvDramaPlayDataItem) {
  return request.post<string>({
    url: '/api/common/tvplay',
    data,
    showSuccessMessage: true
  })
}

export function fetchUpdateTvPlay (id: number, data: TvDramaPlayDataItem) {
  return request.put<string>({
    url: `/api/common/tvplay/${id}`,
    data,
    showSuccessMessage: true
  })
}

export function fetchDeleteTvPlay (id: number) {
  return request.del<string>({
    url: `/api/common/tvplay/${id}`,
    showSuccessMessage: true
  })
}
