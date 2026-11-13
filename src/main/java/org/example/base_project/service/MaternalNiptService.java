package org.example.base_project.service;

import jakarta.annotation.Resource;
import org.example.base_project.entity.MaternalNiptData;
import org.example.base_project.entity.MaternalNiptQuery;
import org.example.base_project.entity.PageResult;
import org.example.base_project.exception.CustomException;
import org.example.base_project.mapper.MaternalNiptMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MaternalNiptService {

    @Resource
    private MaternalNiptMapper maternalNiptMapper;

    public PageResult<MaternalNiptData> page(MaternalNiptQuery query) {
        normalizePage(query);

        List<MaternalNiptData> records = maternalNiptMapper.selectPage(query);
        Long total = maternalNiptMapper.selectCount(query);

        return new PageResult<>(records, query.getCurrent(), query.getSize(), total);
    }

    public MaternalNiptData detail(Long id) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        MaternalNiptData data = maternalNiptMapper.selectById(id);
        if (data == null) {
            throw new CustomException("404", "NIPT检测数据不存在");
        }

        return data;
    }

    public void create(MaternalNiptData data) {
        if (data == null) {
            throw new CustomException("400", "新增数据不能为空");
        }

        maternalNiptMapper.insert(data);
    }

    public void update(Long id, MaternalNiptData data) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        MaternalNiptData dbData = maternalNiptMapper.selectById(id);
        if (dbData == null) {
            throw new CustomException("404", "要修改的数据不存在");
        }

        data.id = id;
        maternalNiptMapper.update(data);
    }

    public void delete(Long id) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        MaternalNiptData dbData = maternalNiptMapper.selectById(id);
        if (dbData == null) {
            throw new CustomException("404", "要删除的数据不存在");
        }

        maternalNiptMapper.deleteById(id);
    }

    public Map<String, Object> options() {
        Map<String, Object> result = new HashMap<>();

        result.put("fetusGenders", maternalNiptMapper.selectFetusGenderOptions());
        result.put("fetusHealthyOptions", maternalNiptMapper.selectFetusHealthyOptions());
        result.put("chromosomeAneuploidyOptions", maternalNiptMapper.selectChromosomeAneuploidyOptions());

        return result;
    }

    /**
     * NIPT检测数据挖掘与可视化分析。
     * 说明：该接口只做统计分析，不改变原有CRUD逻辑；支持沿用列表页的筛选条件。
     */
    public Map<String, Object> analysis(MaternalNiptQuery query) {
        MaternalNiptQuery safeQuery = query == null ? new MaternalNiptQuery() : query;

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> summary = maternalNiptMapper.selectAnalysisSummary(safeQuery);
        List<Map<String, Object>> fetusGenderDistribution = maternalNiptMapper.selectFetusGenderDistribution(safeQuery);
        List<Map<String, Object>> healthyDistribution = maternalNiptMapper.selectHealthyDistribution(safeQuery);
        List<Map<String, Object>> aneuploidyDistribution = maternalNiptMapper.selectAneuploidyDistribution(safeQuery);
        List<Map<String, Object>> ageSegmentStats = maternalNiptMapper.selectAgeSegmentStats(safeQuery);
        List<Map<String, Object>> bmiSegmentStats = maternalNiptMapper.selectBmiSegmentStats(safeQuery);
        List<Map<String, Object>> gestationalSegmentStats = maternalNiptMapper.selectGestationalSegmentStats(safeQuery);
        List<Map<String, Object>> zValueByAgeSegment = maternalNiptMapper.selectZValueByAgeSegment(safeQuery);
        List<Map<String, Object>> qualityRadarByFetusGender = maternalNiptMapper.selectQualityRadarByFetusGender(safeQuery);
        List<Map<String, Object>> bmiYConcentrationScatter = maternalNiptMapper.selectBmiYConcentrationScatter(safeQuery);
        List<Map<String, Object>> testMonthTrend = maternalNiptMapper.selectTestMonthTrend(safeQuery);

        result.put("summary", summary == null ? new HashMap<>() : summary);
        result.put("fetusGenderDistribution", fetusGenderDistribution);
        result.put("healthyDistribution", healthyDistribution);
        result.put("aneuploidyDistribution", aneuploidyDistribution);
        result.put("ageSegmentStats", ageSegmentStats);
        result.put("bmiSegmentStats", bmiSegmentStats);
        result.put("gestationalSegmentStats", gestationalSegmentStats);
        result.put("zValueByAgeSegment", zValueByAgeSegment);
        result.put("qualityRadarByFetusGender", qualityRadarByFetusGender);
        result.put("bmiYConcentrationScatter", bmiYConcentrationScatter);
        result.put("testMonthTrend", testMonthTrend);
        result.put("insights", buildInsights(summary, fetusGenderDistribution, healthyDistribution, aneuploidyDistribution, ageSegmentStats, bmiSegmentStats, gestationalSegmentStats));

        return result;
    }

    private List<String> buildInsights(Map<String, Object> summary,
                                       List<Map<String, Object>> fetusGenderDistribution,
                                       List<Map<String, Object>> healthyDistribution,
                                       List<Map<String, Object>> aneuploidyDistribution,
                                       List<Map<String, Object>> ageSegmentStats,
                                       List<Map<String, Object>> bmiSegmentStats,
                                       List<Map<String, Object>> gestationalSegmentStats) {
        List<String> insights = new ArrayList<>();
        if (summary == null || toLong(summary.get("totalCount")) <= 0) {
            insights.add("当前筛选条件下暂无可分析的NIPT检测数据，请调整筛选条件后重新分析。");
            return insights;
        }

        long total = toLong(summary.get("totalCount"));
        double abnormalRate = toDouble(summary.get("abnormalRate"));
        double healthyRate = toDouble(summary.get("healthyRate"));
        double avgBmi = toDouble(summary.get("avgBmi"));
        double avgGestationalDays = toDouble(summary.get("avgGestationalDays"));
        double avgMappingRatio = toDouble(summary.get("avgMappingRatio"));
        double avgGcContent = toDouble(summary.get("avgGcContent"));

        insights.add("当前共纳入 " + total + " 条NIPT检测记录，平均BMI约为 " + formatNumber(avgBmi, 2) + "，平均检测孕周约为 " + formatNumber(avgGestationalDays / 7.0, 1) + " 周，可用于观察孕妇体征、检测孕周与检测结果之间的关系。");

        if (!fetusGenderDistribution.isEmpty()) {
            Map<String, Object> topGender = fetusGenderDistribution.get(0);
            insights.add("胎儿类型样本中，" + safeName(topGender.get("name")) + "记录最多，占当前样本的主要部分。该指标可辅助判断样本结构是否均衡，避免后续分析被单一胎儿类型样本影响。");
        }

        insights.add("当前筛选样本中胎儿健康率约为 " + formatNumber(healthyRate, 2) + "% ，染色体非整倍体提示率约为 " + formatNumber(abnormalRate, 2) + "% 。若异常率在某些年龄段、BMI区间或孕周区间明显升高，应进一步关注该人群的风险筛查价值。");

        if (!aneuploidyDistribution.isEmpty()) {
            Map<String, Object> topAneuploidy = aneuploidyDistribution.get(0);
            insights.add("染色体非整倍体结果中，出现最多的类型为“" + safeName(topAneuploidy.get("name")) + "”。该分布可用于识别主要异常类型，为后续重点关注13、18、21号染色体Z值变化提供方向。");
        }

        Map<String, Object> ageRisk = maxBy(ageSegmentStats, "abnormalRate");
        if (ageRisk != null) {
            insights.add("按年龄层分析，" + safeName(ageRisk.get("ageSegment")) + " 的非整倍体提示率相对更高，提示年龄分层对风险识别具有参考意义，可在该年龄段结合Z值、BMI和孕周指标进一步分析。");
        }

        Map<String, Object> bmiRisk = maxBy(bmiSegmentStats, "abnormalRate");
        if (bmiRisk != null) {
            insights.add("按BMI分层分析，" + safeName(bmiRisk.get("bmiSegment")) + " 的异常提示率相对突出。BMI可能影响胎儿游离DNA浓度和检测质量，适合与Y染色体浓度、读段过滤比例一起综合观察。");
        }

        Map<String, Object> bestGestational = maxBy(gestationalSegmentStats, "avgYConcentration");
        if (bestGestational != null) {
            insights.add("从孕周分布看，" + safeName(bestGestational.get("gestationalSegment")) + " 的平均Y染色体浓度相对更高，说明该孕周区间的胎儿DNA信号可能更稳定，可作为检测时机分析的参考。");
        }

        insights.add("测序质量方面，平均比对比例约为 " + formatNumber(avgMappingRatio * 100, 2) + "% ，平均GC含量约为 " + formatNumber(avgGcContent * 100, 2) + "% 。若某些分组出现低比对率、高重复率或高过滤比例，应重点检查样本质量和测序稳定性。");

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

    private void normalizePage(MaternalNiptQuery query) {
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
