import request from '@/utils/http'

export interface ConsumerShoppingTrendItem {
  id?: number

  age?: number
  monthlyIncome?: number
  dailyInternetHours?: number
  smartphoneUsageYears?: number
  socialMediaHours?: number

  onlinePaymentTrustScore?: number
  techSavvyScore?: number

  monthlyOnlineOrders?: number
  monthlyStoreVisits?: number

  avgOnlineSpend?: number
  avgStoreSpend?: number

  discountSensitivity?: number
  returnFrequency?: number
  avgDeliveryDays?: number
  deliveryFeeSensitivity?: number
  freeReturnImportance?: number
  productAvailabilityOnline?: number

  impulseBuyingScore?: number
  needTouchFeelScore?: number
  brandLoyaltyScore?: number
  environmentalAwareness?: number
  timePressureLevel?: number

  gender?: string
  cityTier?: string
  shoppingPreference?: string

  createTime?: string
}

export interface ConsumerShoppingTrendQuery {
  current?: number
  size?: number

  gender?: string
  cityTier?: string
  shoppingPreference?: string

  ageMin?: number
  ageMax?: number

  incomeMin?: number
  incomeMax?: number

  ordersMin?: number
  ordersMax?: number
}

export interface ConsumerShoppingTrendOptions {
  genders: string[]
  cityTiers: string[]
  shoppingPreferences: string[]
}

export interface NameValueItem {
  name: string
  value: number
  rate?: number
}

export interface ConsumerAnalysisSummary {
  totalCount?: number
  avgAge?: number
  avgIncome?: number
  avgDailyInternetHours?: number
  avgSocialMediaHours?: number
  avgOnlineOrders?: number
  avgStoreVisits?: number
  avgOnlineSpend?: number
  avgStoreSpend?: number
  avgPaymentTrustScore?: number
  avgTechSavvyScore?: number
  avgDiscountSensitivity?: number
  avgBrandLoyaltyScore?: number
  avgEnvironmentalAwareness?: number
  avgTimePressureLevel?: number
  onlinePreferenceRate?: number
  offlinePreferenceRate?: number
  bothPreferenceRate?: number
}

export interface AgeSegmentPreferenceItem {
  ageSegment: string
  totalCount: number
  onlineCount: number
  offlineCount: number
  bothCount: number
  onlineRate: number
  avgIncome: number
  avgOnlineOrders: number
  avgStoreVisits: number
}

export interface IncomeSegmentStatsItem {
  incomeSegment: string
  totalCount: number
  avgOnlineSpend: number
  avgStoreSpend: number
  avgOnlineOrders: number
  avgStoreVisits: number
  onlineRate: number
}

export interface CityTierPreferenceStatsItem {
  cityTier: string
  totalCount: number
  avgIncome: number
  avgOnlineOrders: number
  avgStoreVisits: number
  avgOnlineSpend: number
  avgStoreSpend: number
  avgTechSavvyScore: number
  avgDiscountSensitivity: number
  avgPaymentTrustScore: number
  onlineRate: number
}

export interface RadarByPreferenceItem {
  preference: string
  totalCount: number
  onlinePaymentTrustScore: number
  techSavvyScore: number
  discountSensitivity: number
  returnFrequency: number
  deliveryFeeSensitivity: number
  freeReturnImportance: number
  productAvailabilityOnline: number
  impulseBuyingScore: number
  needTouchFeelScore: number
  brandLoyaltyScore: number
  environmentalAwareness: number
  timePressureLevel: number
  avgOnlineOrders: number
  avgStoreVisits: number
}

export interface DeliveryExperienceStatsItem {
  preference: string
  totalCount: number
  avgDeliveryDays: number
  deliveryFeeSensitivity: number
  freeReturnImportance: number
  returnFrequency: number
  productAvailabilityOnline: number
}

export interface TechOrderScatterItem {
  techSavvyScore: number
  monthlyOnlineOrders: number
  monthlyIncome: number
  avgOnlineSpend: number
  cityTier: string
  shoppingPreference: string
}

export interface ConsumerShoppingAnalysisData {
  summary: ConsumerAnalysisSummary
  preferenceDistribution: NameValueItem[]
  genderDistribution: NameValueItem[]
  cityTierDistribution: NameValueItem[]
  ageSegmentPreference: AgeSegmentPreferenceItem[]
  incomeSegmentStats: IncomeSegmentStatsItem[]
  cityTierPreferenceStats: CityTierPreferenceStatsItem[]
  radarByPreference: RadarByPreferenceItem[]
  deliveryExperienceStats: DeliveryExperienceStatsItem[]
  techOrderScatter: TechOrderScatterItem[]
  insights: string[]
}

export type ConsumerShoppingTrendPage = Api.Common.PaginatedResponse<ConsumerShoppingTrendItem>

export function fetchConsumerShoppingPage (params: ConsumerShoppingTrendQuery) {
  return request.get<ConsumerShoppingTrendPage>({
    url: '/api/common/consumer-shopping/page',
    params
  })
}

export function fetchConsumerShoppingOptions () {
  return request.get<ConsumerShoppingTrendOptions>({
    url: '/api/common/consumer-shopping/options'
  })
}

export function fetchConsumerShoppingAnalysis (params: ConsumerShoppingTrendQuery) {
  return request.get<ConsumerShoppingAnalysisData>({
    url: '/api/common/consumer-shopping/analysis',
    params
  })
}

export function fetchCreateConsumerShopping (data: ConsumerShoppingTrendItem) {
  return request.post<string>({
    url: '/api/common/consumer-shopping',
    data,
    showSuccessMessage: true
  })
}

export function fetchUpdateConsumerShopping (id: number, data: ConsumerShoppingTrendItem) {
  return request.put<string>({
    url: `/api/common/consumer-shopping/${id}`,
    data,
    showSuccessMessage: true
  })
}

export function fetchDeleteConsumerShopping (id: number) {
  return request.del<string>({
    url: `/api/common/consumer-shopping/${id}`,
    showSuccessMessage: true
  })
}
