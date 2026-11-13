import request from '@/utils/http'

export interface RentalHouseDataItem {
  id?: number

  sourceId?: number

  rent?: number
  bedroom?: number
  livingroom?: number
  bathroom?: number
  area?: number

  room?: string
  floorGrp?: string
  subway?: string
  region?: string
  heating?: string

  createTime?: string
}

export interface RentalHouseQuery {
  current?: number
  size?: number

  room?: string
  floorGrp?: string
  subway?: string
  region?: string
  heating?: string

  bedroom?: number
  livingroom?: number
  bathroom?: number

  rentMin?: number
  rentMax?: number

  areaMin?: number
  areaMax?: number
}

export interface RentalHouseOptions {
  rooms: string[]
  floorGrps: string[]
  subways: string[]
  regions: string[]
  heatings: string[]
}

export interface NameValueItem {
  name: string
  value: number
  avgRent?: number
  avgArea?: number
  avgUnitRent?: number
}

export interface RentalAnalysisSummary {
  totalCount?: number
  regionCount?: number
  avgRent?: number
  minRent?: number
  maxRent?: number
  avgArea?: number
  avgUnitRent?: number
  avgBedroom?: number
  subwayCount?: number
  subwayRate?: number
  centralizedHeatingCount?: number
  centralizedHeatingRate?: number
}

export interface RentalRegionStat {
  regionName: string
  totalCount: number
  avgRent: number
  minRent: number
  maxRent: number
  avgArea: number
  avgUnitRent: number
  avgBedroom: number
  subwayRate: number
  centralizedHeatingRate: number
}

export interface RentalRentSegmentStat {
  rentSegment: string
  totalCount: number
  avgRent: number
  avgArea: number
  avgUnitRent: number
}

export interface RentalAreaSegmentStat {
  areaSegment: string
  totalCount: number
  avgRent: number
  avgArea: number
  avgUnitRent: number
}

export interface RentalBedroomStat {
  bedroomSegment: string
  totalCount: number
  avgRent: number
  avgArea: number
  avgUnitRent: number
}

export interface RentalSubwayStat {
  subwayFlag: string
  totalCount: number
  avgRent: number
  avgArea: number
  avgUnitRent: number
}

export interface RentalFloorStat {
  floorGrp: string
  totalCount: number
  avgRent: number
  avgArea: number
  avgUnitRent: number
}

export interface RentalHeatingStat {
  heatingFlag: string
  totalCount: number
  avgRent: number
  avgArea: number
  avgUnitRent: number
}

export interface RentalRegionBedroomHeatmapItem {
  regionName: string
  bedroomSegment: string
  totalCount: number
  avgRent: number
  avgUnitRent: number
}

export interface RentalRentAreaScatterItem {
  id: number
  regionName: string
  rent: number
  area: number
  unitRent: number
  bedroom: number
  room: string
}

export interface RentalFeatureRadarItem {
  subwayFlag: string
  totalCount: number
  avgRent: number
  avgArea: number
  avgUnitRent: number
  avgBedroom: number
  centralizedHeatingRate: number
}

export interface RentalAnalysisData {
  summary: RentalAnalysisSummary
  regionStats: RentalRegionStat[]
  rentSegmentStats: RentalRentSegmentStat[]
  areaSegmentStats: RentalAreaSegmentStat[]
  roomDistribution: NameValueItem[]
  bedroomStats: RentalBedroomStat[]
  subwayStats: RentalSubwayStat[]
  floorStats: RentalFloorStat[]
  heatingStats: RentalHeatingStat[]
  regionBedroomHeatmap: RentalRegionBedroomHeatmapItem[]
  rentAreaScatter: RentalRentAreaScatterItem[]
  featureRadarStats: RentalFeatureRadarItem[]
  insights: string[]
}

export type RentalHousePage = Api.Common.PaginatedResponse<RentalHouseDataItem>

export function fetchRentalPage (params: RentalHouseQuery) {
  return request.get<RentalHousePage>({
    url: '/api/common/rental/page',
    params
  })
}

export function fetchRentalOptions () {
  return request.get<RentalHouseOptions>({
    url: '/api/common/rental/options'
  })
}

export function fetchRentalAnalysis (params: RentalHouseQuery) {
  return request.get<RentalAnalysisData>({
    url: '/api/common/rental/analysis',
    params
  })
}

export function fetchCreateRental (data: RentalHouseDataItem) {
  return request.post<string>({
    url: '/api/common/rental',
    data,
    showSuccessMessage: true
  })
}

export function fetchUpdateRental (id: number, data: RentalHouseDataItem) {
  return request.put<string>({
    url: `/api/common/rental/${id}`,
    data,
    showSuccessMessage: true
  })
}

export function fetchDeleteRental (id: number) {
  return request.del<string>({
    url: `/api/common/rental/${id}`,
    showSuccessMessage: true
  })
}
