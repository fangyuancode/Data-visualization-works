package org.example.base_project.service;

import jakarta.annotation.Resource;
import org.example.base_project.entity.PageResult;
import org.example.base_project.entity.RentalHouseData;
import org.example.base_project.entity.RentalHouseQuery;
import org.example.base_project.exception.CustomException;
import org.example.base_project.mapper.RentalHouseMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RentalHouseService {

    @Resource
    private RentalHouseMapper rentalHouseMapper;

    public PageResult<RentalHouseData> page(RentalHouseQuery query) {
        normalizePage(query);

        List<RentalHouseData> records = rentalHouseMapper.selectPage(query);
        Long total = rentalHouseMapper.selectCount(query);

        return new PageResult<>(records, query.getCurrent(), query.getSize(), total);
    }

    public RentalHouseData detail(Long id) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        RentalHouseData data = rentalHouseMapper.selectById(id);

        if (data == null) {
            throw new CustomException("404", "租房数据不存在");
        }

        return data;
    }

    public void create(RentalHouseData data) {
        if (data == null) {
            throw new CustomException("400", "新增数据不能为空");
        }

        rentalHouseMapper.insert(data);
    }

    public void update(Long id, RentalHouseData data) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        RentalHouseData dbData = rentalHouseMapper.selectById(id);
        if (dbData == null) {
            throw new CustomException("404", "要修改的数据不存在");
        }

        data.id = id;
        rentalHouseMapper.update(data);
    }

    public void delete(Long id) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        RentalHouseData dbData = rentalHouseMapper.selectById(id);
        if (dbData == null) {
            throw new CustomException("404", "要删除的数据不存在");
        }

        rentalHouseMapper.deleteById(id);
    }

    public Map<String, Object> options() {
        Map<String, Object> result = new HashMap<>();

        result.put("rooms", rentalHouseMapper.selectRoomOptions());
        result.put("floorGrps", rentalHouseMapper.selectFloorGrpOptions());
        result.put("subways", rentalHouseMapper.selectSubwayOptions());
        result.put("regions", rentalHouseMapper.selectRegionOptions());
        result.put("heatings", rentalHouseMapper.selectHeatingOptions());

        return result;
    }

    /**
     * 北京房屋租赁数据挖掘与可视化分析。
     * 支持沿用列表页筛选条件，不影响原有CRUD能力。
     */
    public Map<String, Object> analysis(RentalHouseQuery query) {
        RentalHouseQuery safeQuery = query == null ? new RentalHouseQuery() : query;

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> summary = rentalHouseMapper.selectAnalysisSummary(safeQuery);
        List<Map<String, Object>> regionStats = rentalHouseMapper.selectRegionStats(safeQuery);
        List<Map<String, Object>> rentSegmentStats = rentalHouseMapper.selectRentSegmentStats(safeQuery);
        List<Map<String, Object>> areaSegmentStats = rentalHouseMapper.selectAreaSegmentStats(safeQuery);
        List<Map<String, Object>> roomDistribution = rentalHouseMapper.selectRoomDistribution(safeQuery);
        List<Map<String, Object>> bedroomStats = rentalHouseMapper.selectBedroomStats(safeQuery);
        List<Map<String, Object>> subwayStats = rentalHouseMapper.selectSubwayStats(safeQuery);
        List<Map<String, Object>> floorStats = rentalHouseMapper.selectFloorStats(safeQuery);
        List<Map<String, Object>> heatingStats = rentalHouseMapper.selectHeatingStats(safeQuery);
        List<Map<String, Object>> regionBedroomHeatmap = rentalHouseMapper.selectRegionBedroomHeatmap(safeQuery);
        List<Map<String, Object>> rentAreaScatter = rentalHouseMapper.selectRentAreaScatter(safeQuery);
        List<Map<String, Object>> featureRadarStats = rentalHouseMapper.selectFeatureRadarStats(safeQuery);

        result.put("summary", summary == null ? new HashMap<>() : summary);
        result.put("regionStats", regionStats);
        result.put("rentSegmentStats", rentSegmentStats);
        result.put("areaSegmentStats", areaSegmentStats);
        result.put("roomDistribution", roomDistribution);
        result.put("bedroomStats", bedroomStats);
        result.put("subwayStats", subwayStats);
        result.put("floorStats", floorStats);
        result.put("heatingStats", heatingStats);
        result.put("regionBedroomHeatmap", regionBedroomHeatmap);
        result.put("rentAreaScatter", rentAreaScatter);
        result.put("featureRadarStats", featureRadarStats);
        result.put("insights", buildInsights(summary, regionStats, rentSegmentStats, areaSegmentStats, roomDistribution, bedroomStats, subwayStats, heatingStats));

        return result;
    }

    private List<String> buildInsights(Map<String, Object> summary,
                                       List<Map<String, Object>> regionStats,
                                       List<Map<String, Object>> rentSegmentStats,
                                       List<Map<String, Object>> areaSegmentStats,
                                       List<Map<String, Object>> roomDistribution,
                                       List<Map<String, Object>> bedroomStats,
                                       List<Map<String, Object>> subwayStats,
                                       List<Map<String, Object>> heatingStats) {
        List<String> insights = new ArrayList<>();
        if (summary == null || toLong(summary.get("totalCount")) <= 0) {
            insights.add("当前筛选条件下暂无可分析的北京房屋租赁数据，请调整区域、租金、面积或户型条件后重新分析。");
            return insights;
        }

        long total = toLong(summary.get("totalCount"));
        long regionCount = toLong(summary.get("regionCount"));
        double avgRent = toDouble(summary.get("avgRent"));
        double avgArea = toDouble(summary.get("avgArea"));
        double avgUnitRent = toDouble(summary.get("avgUnitRent"));
        double subwayRate = toDouble(summary.get("subwayRate"));
        double heatingRate = toDouble(summary.get("centralizedHeatingRate"));

        insights.add("当前共纳入 " + total + " 条北京租房记录，覆盖 " + regionCount + " 个区域，平均租金约为 " + formatNumber(avgRent, 0) + " 元/月，平均面积约为 " + formatNumber(avgArea, 1) + "㎡，平均单位租金约为 " + formatNumber(avgUnitRent, 1) + " 元/㎡。这些指标可用于快速判断整体租赁市场的价格中枢与房源供给水平。");

        Map<String, Object> supplyTopRegion = maxBy(regionStats, "totalCount");
        if (supplyTopRegion != null) {
            insights.add("从区域供给看，" + safeName(supplyTopRegion.get("regionName")) + " 的房源数量最多，说明该区域在样本中供给活跃度较高，适合结合地图热力图进一步观察租赁需求与价格集中情况。");
        }

        Map<String, Object> priceTopRegion = maxBy(regionStats, "avgUnitRent");
        if (priceTopRegion != null) {
            insights.add("从单位租金看，" + safeName(priceTopRegion.get("regionName")) + " 的平均单位租金相对更高，说明该区域可能具有更强的区位溢价、通勤价值或居住配套优势。对于租客而言，该区域需要重点比较预算与居住便利性的平衡。");
        }

        Map<String, Object> hotRentSegment = maxBy(rentSegmentStats, "totalCount");
        if (hotRentSegment != null) {
            insights.add("租金区间分布中，“" + safeName(hotRentSegment.get("rentSegment")) + "”房源数量最多，代表当前市场中更主流的预算区间。平台可基于该区间设计默认筛选项，提升用户找房效率。");
        }

        Map<String, Object> hotAreaSegment = maxBy(areaSegmentStats, "totalCount");
        if (hotAreaSegment != null) {
            insights.add("面积结构上，“" + safeName(hotAreaSegment.get("areaSegment")) + "”房源占比较高，说明北京租赁市场中该面积段更常见，适合与户型结构一起分析实际居住舒适度和价格承受能力。");
        }

        if (roomDistribution != null && !roomDistribution.isEmpty()) {
            Map<String, Object> topRoom = roomDistribution.get(0);
            insights.add("房间类型中，“" + safeName(topRoom.get("name")) + "”出现频率最高，说明样本房源在居住形态上存在明显集中。该规律可为租赁平台推荐、房源标签建设和租客画像分析提供依据。");
        }

        Map<String, Object> hotBedroom = maxBy(bedroomStats, "totalCount");
        if (hotBedroom != null) {
            insights.add("卧室数量结构中，“" + safeName(hotBedroom.get("bedroomSegment")) + "”更常见。若该户型同时具备较高租金和较大供给量，通常说明它处在家庭租住、合租或刚需租赁的核心需求带上。");
        }

        Map<String, Object> nearSubway = findByName(subwayStats, "subwayFlag", "近地铁");
        Map<String, Object> farSubway = findByName(subwayStats, "subwayFlag", "非近地铁");
        if (nearSubway != null && farSubway != null) {
            double nearRent = toDouble(nearSubway.get("avgRent"));
            double farRent = toDouble(farSubway.get("avgRent"));
            double premium = farRent > 0 ? (nearRent - farRent) * 100.0 / farRent : 0;
            insights.add("交通便利性方面，近地铁房源占比约为 " + formatNumber(subwayRate, 1) + "% 。近地铁房源平均租金约为 " + formatNumber(nearRent, 0) + " 元/月，相比非近地铁房源约 " + (premium >= 0 ? "高" : "低") + formatNumber(Math.abs(premium), 1) + "% ，体现出轨道交通对租金的影响价值。");
        } else {
            insights.add("交通便利性方面，近地铁房源占比约为 " + formatNumber(subwayRate, 1) + "% 。该指标可用于评估通勤便利程度对房源租金、区域热度和用户选择偏好的影响。");
        }

        if (heatingStats != null && !heatingStats.isEmpty()) {
            insights.add("供暖配套方面，集中供暖房源占比约为 " + formatNumber(heatingRate, 1) + "% 。北京租房场景中，供暖方式会影响冬季居住体验，也会对租金接受度和房源竞争力产生影响。");
        }

        insights.add("综合来看，房屋租赁数据适合从“区域位置、租金水平、面积户型、交通配套、供暖条件”五个维度构建租房价值画像。通过地图、柱状图、饼图、雷达图和散点图联合分析，可以为租客选房、平台推荐、房源定价和区域市场研究提供支撑。" );

        return insights;
    }

    private Map<String, Object> maxBy(List<Map<String, Object>> list, String key) {
        if (list == null || list.isEmpty()) return null;
        Map<String, Object> max = null;
        double maxValue = -Double.MAX_VALUE;
        for (Map<String, Object> item : list) {
            double current = toDouble(item.get(key));
            if (max == null || current > maxValue) {
                max = item;
                maxValue = current;
            }
        }
        return max;
    }

    private Map<String, Object> findByName(List<Map<String, Object>> list, String key, String value) {
        if (list == null || list.isEmpty()) return null;
        for (Map<String, Object> item : list) {
            if (value.equals(safeName(item.get(key)))) {
                return item;
            }
        }
        return null;
    }

    private String safeName(Object value) {
        if (value == null) return "未知";
        String text = String.valueOf(value);
        return text == null || text.trim().isEmpty() ? "未知" : text;
    }

    private double toDouble(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (Exception e) {
            return 0;
        }
    }

    private long toLong(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (Exception e) {
            return 0;
        }
    }

    private String formatNumber(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    private void normalizePage(RentalHouseQuery query) {
        if (query == null) {
            throw new CustomException("400", "分页参数不能为空");
        }

        if (query.getCurrent() == null || query.getCurrent() < 1) {
            query.setCurrent(1);
        }

        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(10);
        }
    }
}
