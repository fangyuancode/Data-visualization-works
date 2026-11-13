package org.example.base_project.service;

import jakarta.annotation.Resource;
import org.example.base_project.entity.PageResult;
import org.example.base_project.entity.WorldHappinessReport;
import org.example.base_project.entity.WorldHappinessReportQuery;
import org.example.base_project.exception.CustomException;
import org.example.base_project.mapper.WorldHappinessReportMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorldHappinessReportService {

    @Resource
    private WorldHappinessReportMapper worldHappinessReportMapper;

    public PageResult<WorldHappinessReport> page(WorldHappinessReportQuery query) {
        normalizePage(query);

        List<WorldHappinessReport> records = worldHappinessReportMapper.selectPage(query);
        Long total = worldHappinessReportMapper.selectCount(query);

        return new PageResult<>(records, query.getCurrent(), query.getSize(), total);
    }

    public WorldHappinessReport detail(Long id) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        WorldHappinessReport data = worldHappinessReportMapper.selectById(id);

        if (data == null) {
            throw new CustomException("404", "世界幸福报告数据不存在");
        }

        return data;
    }

    public void create(WorldHappinessReport data) {
        if (data == null) {
            throw new CustomException("400", "新增数据不能为空");
        }

        worldHappinessReportMapper.insert(data);
    }

    public void update(Long id, WorldHappinessReport data) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        WorldHappinessReport dbData = worldHappinessReportMapper.selectById(id);

        if (dbData == null) {
            throw new CustomException("404", "要修改的数据不存在");
        }

        data.id = id;
        worldHappinessReportMapper.update(data);
    }

    public void delete(Long id) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        WorldHappinessReport dbData = worldHappinessReportMapper.selectById(id);

        if (dbData == null) {
            throw new CustomException("404", "要删除的数据不存在");
        }

        worldHappinessReportMapper.deleteById(id);
    }

    public Map<String, Object> options() {
        Map<String, Object> result = new HashMap<>();

        result.put("years", worldHappinessReportMapper.selectYearOptions());
        result.put("countries", worldHappinessReportMapper.selectCountryOptions());

        return result;
    }

    /**
     * 世界幸福报告数据挖掘：从幸福分数、年度排名、解释性指标、国家差异和时间趋势等维度分析数据规律。
     */
    public Map<String, Object> analysis(WorldHappinessReportQuery query) {
        if (query == null) {
            query = new WorldHappinessReportQuery();
        }

        Map<String, Object> summary = safeMap(worldHappinessReportMapper.selectAnalysisSummary(query));
        List<Map<String, Object>> scoreSegmentStats = safeList(worldHappinessReportMapper.selectScoreSegmentStats(query));
        List<Map<String, Object>> rankSegmentStats = safeList(worldHappinessReportMapper.selectRankSegmentStats(query));
        List<Map<String, Object>> yearTrend = safeList(worldHappinessReportMapper.selectYearTrend(query));
        List<Map<String, Object>> factorContribution = safeList(worldHappinessReportMapper.selectFactorContribution(query));
        List<Map<String, Object>> topCountryRank = safeList(worldHappinessReportMapper.selectTopCountryRank(query));
        List<Map<String, Object>> countryAverageRank = safeList(worldHappinessReportMapper.selectCountryAverageRank(query));
        List<Map<String, Object>> factorRadarByScoreSegment = safeList(worldHappinessReportMapper.selectFactorRadarByScoreSegment(query));

        Map<String, Object> result = new HashMap<>();
        result.put("summary", summary);
        result.put("scoreSegmentStats", scoreSegmentStats);
        result.put("rankSegmentStats", rankSegmentStats);
        result.put("yearTrend", yearTrend);
        result.put("factorContribution", factorContribution);
        result.put("topCountryRank", topCountryRank);
        result.put("countryAverageRank", countryAverageRank);
        result.put("factorRadarByScoreSegment", factorRadarByScoreSegment);
        result.put("scoreFactorScatter", safeList(worldHappinessReportMapper.selectScoreFactorScatter(query)));
        result.put("yearScoreHeatmap", safeList(worldHappinessReportMapper.selectYearScoreHeatmap(query)));
        result.put("insights", buildInsights(summary, scoreSegmentStats, rankSegmentStats, yearTrend,
                factorContribution, topCountryRank, countryAverageRank, factorRadarByScoreSegment));

        return result;
    }

    private List<String> buildInsights(Map<String, Object> summary,
                                       List<Map<String, Object>> scoreSegmentStats,
                                       List<Map<String, Object>> rankSegmentStats,
                                       List<Map<String, Object>> yearTrend,
                                       List<Map<String, Object>> factorContribution,
                                       List<Map<String, Object>> topCountryRank,
                                       List<Map<String, Object>> countryAverageRank,
                                       List<Map<String, Object>> factorRadarByScoreSegment) {
        List<String> insights = new ArrayList<>();

        long totalCount = toLong(summary.get("totalCount"));
        if (totalCount <= 0) {
            insights.add("当前筛选条件下暂无可分析的世界幸福报告数据，可以放宽年份、国家、排名、幸福分数或完整数据条件后重新查看。 ");
            return insights;
        }

        insights.add("当前共纳入 " + totalCount + " 条世界幸福报告记录，平均幸福分数为 "
                + formatNumber(summary.get("avgScore")) + " 分，最高分为 "
                + formatNumber(summary.get("maxScore")) + " 分，最低分为 "
                + formatNumber(summary.get("minScore")) + " 分。幸福分数能够综合反映经济、健康、社会关系和主观环境等因素。 ");

        insights.add("数据完整性方面，核心解释性指标完整的数据共有 " + toLong(summary.get("completeCount"))
                + " 条，完整率约为 " + formatNumber(summary.get("completeRate"))
                + "% 。进行因子贡献、雷达画像和散点关系分析时，建议优先关注完整数据，以减少缺失值对结论的影响。 ");

        Map<String, Object> mainScoreSegment = maxBy(scoreSegmentStats, "totalCount");
        if (mainScoreSegment != null) {
            insights.add("幸福分数分层中，“" + getText(mainScoreSegment, "segmentName") + "”的样本最多，共 "
                    + toLong(mainScoreSegment.get("totalCount")) + " 条，占比 "
                    + formatNumber(mainScoreSegment.get("ratio"))
                    + "% 。该区间可以视为当前数据集中国家幸福水平的主要集中带。 ");
        }

        Map<String, Object> topRankSegment = maxBy(rankSegmentStats, "avgScore");
        if (topRankSegment != null) {
            insights.add("排名梯队对比显示，“" + getText(topRankSegment, "segmentName") + "”的平均幸福分数最高，为 "
                    + formatNumber(topRankSegment.get("avgScore"))
                    + " 分。通常排名越靠前，GDP、社会支持和健康寿命等解释性指标也更稳定。 ");
        }

        Map<String, Object> strongestFactor = maxBy(factorContribution, "avgValue");
        if (strongestFactor != null) {
            insights.add("解释性指标贡献中，“" + getText(strongestFactor, "factorName") + "”平均解释值最高，为 "
                    + formatNumber(strongestFactor.get("avgValue"))
                    + "。该因素在当前筛选样本中对幸福分数具有较强支撑意义。 ");
        }

        Map<String, Object> weakestFactor = minBy(factorContribution, "avgValue");
        if (weakestFactor != null) {
            insights.add("相对薄弱的解释性指标是“" + getText(weakestFactor, "factorName") + "”，平均解释值为 "
                    + formatNumber(weakestFactor.get("avgValue"))
                    + "。如果用于公共政策分析，可将其作为进一步改善幸福感的潜在切入点。 ");
        }

        Map<String, Object> bestYear = maxBy(yearTrend, "avgScore");
        if (bestYear != null) {
            insights.add("年份趋势中，“" + getText(bestYear, "yearName") + "”的平均幸福分数最高，为 "
                    + formatNumber(bestYear.get("avgScore")) + " 分，样本量为 "
                    + toLong(bestYear.get("totalCount"))
                    + "。若某一年平均分明显高于其他年份，可以进一步结合全球经济、公共健康和社会支持变化进行解释。 ");
        }

        Map<String, Object> topCountry = maxBy(topCountryRank, "happinessScore");
        if (topCountry != null) {
            insights.add("国家排名中，“" + getText(topCountry, "country") + "”在 "
                    + getText(topCountry, "year") + " 年幸福分数最高，达到 "
                    + formatNumber(topCountry.get("happinessScore")) + " 分，年度排名为 "
                    + getText(topCountry, "rankInYear")
                    + "。该国家可作为高幸福水平样本，分析其经济、健康和社会支持结构。 ");
        }

        Map<String, Object> stableCountry = maxBy(countryAverageRank, "avgScore");
        if (stableCountry != null) {
            insights.add("按国家多年平均表现看，“" + getText(stableCountry, "country") + "”平均幸福分数较高，为 "
                    + formatNumber(stableCountry.get("avgScore")) + " 分，平均排名约为 "
                    + formatNumber(stableCountry.get("avgRank"))
                    + "。多年平均值更适合识别长期稳定的高幸福国家。 ");
        }

        Map<String, Object> highScoreRadar = maxBy(factorRadarByScoreSegment, "avgScore");
        if (highScoreRadar != null) {
            insights.add("雷达画像显示，高幸福分数组通常在 GDP、社会支持、健康寿命、自由度等维度形成较均衡的优势。这说明幸福水平并非由单一因素决定，而是经济基础、社会保障和个体自由共同作用的结果。 ");
        }

        return insights;
    }

    private void normalizePage(WorldHappinessReportQuery query) {
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

    private Map<String, Object> safeMap(Map<String, Object> value) {
        return value == null ? new HashMap<>() : value;
    }

    private List<Map<String, Object>> safeList(List<Map<String, Object>> value) {
        return value == null ? new ArrayList<>() : value;
    }

    private Map<String, Object> maxBy(List<Map<String, Object>> list, String key) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        Map<String, Object> target = null;
        double maxValue = -Double.MAX_VALUE;
        for (Map<String, Object> item : list) {
            double value = toDouble(item.get(key));
            if (target == null || value > maxValue) {
                target = item;
                maxValue = value;
            }
        }
        return target;
    }

    private Map<String, Object> minBy(List<Map<String, Object>> list, String key) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        Map<String, Object> target = null;
        double minValue = Double.MAX_VALUE;
        for (Map<String, Object> item : list) {
            double value = toDouble(item.get(key));
            if (target == null || value < minValue) {
                target = item;
                minValue = value;
            }
        }
        return target;
    }

    private String getText(Map<String, Object> map, String key) {
        Object value = map == null ? null : map.get(key);
        return value == null ? "未知" : String.valueOf(value);
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return new BigDecimal(String.valueOf(value)).longValue();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private double toDouble(Object value) {
        if (value == null) {
            return 0D;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return new BigDecimal(String.valueOf(value)).doubleValue();
        } catch (Exception ignored) {
            return 0D;
        }
    }

    private String formatNumber(Object value) {
        if (value == null) {
            return "0.00";
        }
        try {
            return new BigDecimal(String.valueOf(value)).setScale(2, RoundingMode.HALF_UP).toPlainString();
        } catch (Exception ignored) {
            return "0.00";
        }
    }
}
