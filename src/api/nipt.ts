import request from '@/utils/http'

export interface MaternalNiptDataItem {
  id?: number

  fetusGender?: string
  serialNo?: number
  pregnantWomanCode?: string

  age?: number
  height?: number
  weight?: number

  lastMenstrualPeriod?: string
  ivfPregnancy?: string
  testDate?: string
  bloodDrawCount?: number

  gestationalWeek?: string
  gestationalDays?: number
  bmi?: number

  rawReadCount?: number
  mappingRatio?: number
  duplicateReadRatio?: number
  uniqueMappedReads?: number
  gcContent?: number

  chr13ZValue?: number
  chr18ZValue?: number
  chr21ZValue?: number
  chrXZValue?: number
  chrYZValue?: number

  chrYConcentration?: number
  chrXConcentration?: number

  chr13GcContent?: number
  chr18GcContent?: number
  chr21GcContent?: number

  filteredReadRatio?: number
  chromosomeAneuploidy?: string

  pregnancyCount?: string
  productionCount?: number
  fetusHealthy?: string

  createTime?: string
}

export interface MaternalNiptQuery {
  current?: number
  size?: number

  fetusGender?: string
  pregnantWomanCode?: string
  fetusHealthy?: string
  chromosomeAneuploidy?: string

  ageMin?: number
  ageMax?: number

  bmiMin?: number
  bmiMax?: number

  gestationalDaysMin?: number
  gestationalDaysMax?: number

  testStartDate?: string
  testEndDate?: string
}

export interface MaternalNiptOptions {
  fetusGenders: string[]
  fetusHealthyOptions: string[]
  chromosomeAneuploidyOptions: string[]
}

export interface NameValueItem {
  name: string
  value: number
}

export interface NiptAnalysisSummary {
  totalCount?: number
  avgAge?: number
  avgBmi?: number
  avgGestationalDays?: number
  avgRawReadCount?: number
  avgMappingRatio?: number
  avgDuplicateReadRatio?: number
  avgFilteredReadRatio?: number
  avgGcContent?: number
  avgYConcentration?: number
  avgXConcentration?: number
  abnormalCount?: number
  abnormalRate?: number
  healthyCount?: number
  healthyRate?: number
  maleFetusCount?: number
  femaleFetusCount?: number
}

export interface NiptAgeSegmentStat {
  ageSegment: string
  totalCount: number
  avgBmi: number
  avgGestationalDays: number
  avgYConcentration: number
  avgChr21ZValue: number
  abnormalCount: number
  abnormalRate: number
  healthyRate: number
}

export interface NiptBmiSegmentStat {
  bmiSegment: string
  totalCount: number
  avgAge: number
  avgGestationalDays: number
  avgYConcentration: number
  avgFilteredReadRatio: number
  avgMappingRatio: number
  abnormalCount: number
  abnormalRate: number
  healthyRate: number
}

export interface NiptGestationalSegmentStat {
  gestationalSegment: string
  totalCount: number
  avgBmi: number
  avgMappingRatio: number
  avgDuplicateReadRatio: number
  avgFilteredReadRatio: number
  avgGcContent: number
  avgYConcentration: number
  abnormalCount: number
  abnormalRate: number
}

export interface NiptZValueByAgeSegment {
  ageSegment: string
  avgChr13ZValue: number
  avgChr18ZValue: number
  avgChr21ZValue: number
  avgChrXZValue: number
  avgChrYZValue: number
}

export interface NiptQualityRadarItem {
  fetusGender: string
  mappingScore: number
  nonDuplicateScore: number
  cleanReadScore: number
  gcScore: number
  yConcentrationScore: number
  xConcentrationScore: number
}

export interface NiptScatterItem {
  id: number
  age: number
  bmi: number
  gestationalDays: number
  fetusGender: string
  fetusHealthy: string
  yConcentration: number
  xConcentration: number
  chr21ZValue: number
}

export interface NiptMonthTrendItem {
  month: string
  totalCount: number
  avgBmi: number
  avgGestationalWeek: number
  abnormalRate: number
}

export interface MaternalNiptAnalysisData {
  summary: NiptAnalysisSummary
  fetusGenderDistribution: NameValueItem[]
  healthyDistribution: NameValueItem[]
  aneuploidyDistribution: NameValueItem[]
  ageSegmentStats: NiptAgeSegmentStat[]
  bmiSegmentStats: NiptBmiSegmentStat[]
  gestationalSegmentStats: NiptGestationalSegmentStat[]
  zValueByAgeSegment: NiptZValueByAgeSegment[]
  qualityRadarByFetusGender: NiptQualityRadarItem[]
  bmiYConcentrationScatter: NiptScatterItem[]
  testMonthTrend: NiptMonthTrendItem[]
  insights: string[]
}

export type MaternalNiptPage = Api.Common.PaginatedResponse<MaternalNiptDataItem>

export function fetchNiptPage (params: MaternalNiptQuery) {
  return request.get<MaternalNiptPage>({
    url: '/api/common/nipt/page',
    params
  })
}

export function fetchNiptOptions () {
  return request.get<MaternalNiptOptions>({
    url: '/api/common/nipt/options'
  })
}

export function fetchNiptAnalysis (params: MaternalNiptQuery) {
  return request.get<MaternalNiptAnalysisData>({
    url: '/api/common/nipt/analysis',
    params
  })
}

export function fetchCreateNipt (data: MaternalNiptDataItem) {
  return request.post<string>({
    url: '/api/common/nipt',
    data,
    showSuccessMessage: true
  })
}

export function fetchUpdateNipt (id: number, data: MaternalNiptDataItem) {
  return request.put<string>({
    url: `/api/common/nipt/${id}`,
    data,
    showSuccessMessage: true
  })
}

export function fetchDeleteNipt (id: number) {
  return request.del<string>({
    url: `/api/common/nipt/${id}`,
    showSuccessMessage: true
  })
}
