package org.example.base_project.service;

import jakarta.annotation.Resource;
import org.example.base_project.entity.PageResult;
import org.example.base_project.entity.TeenMentalHealth;
import org.example.base_project.entity.TeenMentalHealthQuery;
import org.example.base_project.exception.CustomException;
import org.example.base_project.mapper.TeenMentalHealthMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeenMentalHealthService {

    @Resource
    private TeenMentalHealthMapper teenMentalHealthMapper;

    public PageResult<TeenMentalHealth> page(TeenMentalHealthQuery query) {
        normalizePage(query);

        List<TeenMentalHealth> records = teenMentalHealthMapper.selectPage(query);
        Long total = teenMentalHealthMapper.selectCount(query);

        return new PageResult<>(records, query.getCurrent(), query.getSize(), total);
    }

    public TeenMentalHealth detail(Long id) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        TeenMentalHealth data = teenMentalHealthMapper.selectById(id);

        if (data == null) {
            throw new CustomException("404", "青少年心理健康数据不存在");
        }

        return data;
    }

    public void create(TeenMentalHealth data) {
        if (data == null) {
            throw new CustomException("400", "新增数据不能为空");
        }

        teenMentalHealthMapper.insert(data);
    }

    public void update(Long id, TeenMentalHealth data) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        TeenMentalHealth dbData = teenMentalHealthMapper.selectById(id);

        if (dbData == null) {
            throw new CustomException("404", "要修改的数据不存在");
        }

        data.id = id;
        teenMentalHealthMapper.update(data);
    }

    public void delete(Long id) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        TeenMentalHealth dbData = teenMentalHealthMapper.selectById(id);

        if (dbData == null) {
            throw new CustomException("404", "要删除的数据不存在");
        }

        teenMentalHealthMapper.deleteById(id);
    }

    public Map<String, Object> options() {
        Map<String, Object> result = new HashMap<>();

        result.put("genders", teenMentalHealthMapper.selectGenderOptions());
        result.put("platformUsages", teenMentalHealthMapper.selectPlatformUsageOptions());
        result.put("socialInteractionLevels", teenMentalHealthMapper.selectSocialInteractionLevelOptions());
        result.put("depressionLabels", teenMentalHealthMapper.selectDepressionLabelOptions());

        return result;
    }

    /**
     * 青少年心理健康数据挖掘：从社交媒体使用、睡眠、学业、运动、压力、焦虑、成瘾和风险标签等维度进行规律分析。
     */
    public Map<String, Object> analysis(TeenMentalHealthQuery query) {
        if (query == null) {
            query = new TeenMentalHealthQuery();
        }

        Map<String, Object> summary = safeMap(teenMentalHealthMapper.selectAnalysisSummary(query));
        List<Map<String, Object>> depressionDistribution = safeList(teenMentalHealthMapper.selectDepressionDistribution(query));
        List<Map<String, Object>> platformStats = safeList(teenMentalHealthMapper.selectPlatformStats(query));
        List<Map<String, Object>> ageSegmentStats = safeList(teenMentalHealthMapper.selectAgeSegmentStats(query));
        List<Map<String, Object>> socialMediaSegmentStats = safeList(teenMentalHealthMapper.selectSocialMediaSegmentStats(query));
        List<Map<String, Object>> sleepSegmentStats = safeList(teenMentalHealthMapper.selectSleepSegmentStats(query));
        List<Map<String, Object>> socialInteractionStats = safeList(teenMentalHealthMapper.selectSocialInteractionStats(query));
        List<Map<String, Object>> mentalRadarByRisk = safeList(teenMentalHealthMapper.selectMentalRadarByRisk(query));
        List<Map<String, Object>> platformRadarStats = safeList(teenMentalHealthMapper.selectPlatformRadarStats(query));

        Map<String, Object> result = new HashMap<>();
        result.put("summary", summary);
        result.put("depressionDistribution", depressionDistribution);
        result.put("genderDistribution", safeList(teenMentalHealthMapper.selectGenderDistribution(query)));
        result.put("platformStats", platformStats);
        result.put("ageSegmentStats", ageSegmentStats);
        result.put("socialMediaSegmentStats", socialMediaSegmentStats);
        result.put("sleepSegmentStats", sleepSegmentStats);
        result.put("socialInteractionStats", socialInteractionStats);
        result.put("mentalRadarByRisk", mentalRadarByRisk);
        result.put("platformRadarStats", platformRadarStats);
        result.put("socialAnxietyScatter", safeList(teenMentalHealthMapper.selectSocialAnxietyScatter(query)));
        result.put("screenSleepScatter", safeList(teenMentalHealthMapper.selectScreenSleepScatter(query)));
        result.put("platformRiskHeatmap", safeList(teenMentalHealthMapper.selectPlatformRiskHeatmap(query)));
        result.put("insights", buildInsights(summary, depressionDistribution, platformStats, ageSegmentStats,
                socialMediaSegmentStats, sleepSegmentStats, socialInteractionStats, mentalRadarByRisk, platformRadarStats));

        return result;
    }

    private List<String> buildInsights(Map<String, Object> summary,
                                       List<Map<String, Object>> depressionDistribution,
                                       List<Map<String, Object>> platformStats,
                                       List<Map<String, Object>> ageSegmentStats,
                                       List<Map<String, Object>> socialMediaSegmentStats,
                                       List<Map<String, Object>> sleepSegmentStats,
                                       List<Map<String, Object>> socialInteractionStats,
                                       List<Map<String, Object>> mentalRadarByRisk,
                                       List<Map<String, Object>> platformRadarStats) {
        List<String> insights = new ArrayList<>();

        long totalCount = toLong(summary.get("totalCount"));
        if (totalCount <= 0) {
            insights.add("当前筛选条件下暂无可分析的青少年心理健康数据，可以放宽性别、平台、年龄或风险标签条件后重新查看。");
            return insights;
        }

        insights.add("当前共纳入 " + totalCount + " 条青少年心理健康记录，平均每日社交媒体使用时长为 "
                + formatNumber(summary.get("avgSocialHours")) + " 小时，平均睡眠时长为 "
                + formatNumber(summary.get("avgSleepHours")) + " 小时，抑郁风险标签占比为 "
                + formatNumber(summary.get("riskRate")) + "% 。这些指标可作为整体心理健康风险识别的基础盘。 ");

        insights.add("从行为强度看，高社媒使用占比为 " + formatNumber(summary.get("highSocialUseRate"))
                + "% ，睡眠不足占比为 " + formatNumber(summary.get("sleepInsufficientRate"))
                + "% ，高压力占比为 " + formatNumber(summary.get("highStressRate"))
                + "% 。若三类比例同时偏高，说明该群体需要优先关注作息管理和数字媒介使用边界。 ");

        Map<String, Object> mainRisk = maxBy(depressionDistribution, "totalCount");
        if (mainRisk != null) {
            insights.add("抑郁风险结构中，“" + getText(mainRisk, "labelName") + "”样本最多，共 "
                    + toLong(mainRisk.get("totalCount")) + " 条，占比 " + formatNumber(mainRisk.get("ratio"))
                    + "% 。若风险样本占比较高，建议进一步联动压力、焦虑、成瘾和睡眠指标做分层预警。 ");
        }

        Map<String, Object> highRiskPlatform = maxBy(platformStats, "riskRate");
        if (highRiskPlatform != null) {
            insights.add("平台维度中，“" + getText(highRiskPlatform, "labelName") + "”对应的抑郁风险占比相对较高，达到 "
                    + formatNumber(highRiskPlatform.get("riskRate")) + "% ，平均社媒时长为 "
                    + formatNumber(highRiskPlatform.get("avgSocialHours")) + " 小时。该平台可作为后续分析平台内容机制、使用场景和沉迷风险的重点对象。 ");
        }

        Map<String, Object> highAgeRisk = maxBy(ageSegmentStats, "riskRate");
        if (highAgeRisk != null) {
            insights.add("年龄分层中，“" + getText(highAgeRisk, "segmentName") + "”风险占比最高，为 "
                    + formatNumber(highAgeRisk.get("riskRate")) + "% ，平均压力水平为 "
                    + formatNumber(highAgeRisk.get("avgStress")) + " 。这说明年龄阶段差异可能影响学习压力、社交需求和媒介依赖程度。 ");
        }

        Map<String, Object> highUseRisk = maxBy(socialMediaSegmentStats, "riskRate");
        if (highUseRisk != null) {
            insights.add("社媒使用时长分层中，“" + getText(highUseRisk, "segmentName") + "”的风险占比为 "
                    + formatNumber(highUseRisk.get("riskRate")) + "% ，平均焦虑水平为 "
                    + formatNumber(highUseRisk.get("avgAnxiety")) + " 。若高时长区间风险显著偏高，可将每日使用时长作为行为预警变量。 ");
        }

        Map<String, Object> shortSleepRisk = maxBy(sleepSegmentStats, "riskRate");
        if (shortSleepRisk != null) {
            insights.add("睡眠分层显示，“" + getText(shortSleepRisk, "segmentName") + "”群体风险占比最高，达到 "
                    + formatNumber(shortSleepRisk.get("riskRate")) + "% ，平均睡前屏幕时长为 "
                    + formatNumber(shortSleepRisk.get("avgScreenBeforeSleep")) + " 小时。睡前屏幕暴露可能通过影响睡眠质量间接放大心理压力。 ");
        }

        Map<String, Object> socialRisk = maxBy(socialInteractionStats, "riskRate");
        if (socialRisk != null) {
            insights.add("社交互动水平维度中，“" + getText(socialRisk, "labelName") + "”群体风险占比最高，为 "
                    + formatNumber(socialRisk.get("riskRate")) + "% 。这说明线下社交支持不足或社交互动质量偏低时，可能更容易形成压力累积。 ");
        }

        Map<String, Object> riskProfile = findContains(mentalRadarByRisk, "labelName", "风险");
        Map<String, Object> normalProfile = findContains(mentalRadarByRisk, "labelName", "暂无");
        if (riskProfile != null && normalProfile != null) {
            double riskStress = toDouble(riskProfile.get("stressLevel"));
            double normalStress = toDouble(normalProfile.get("stressLevel"));
            double riskAnxiety = toDouble(riskProfile.get("anxietyLevel"));
            double normalAnxiety = toDouble(normalProfile.get("anxietyLevel"));
            insights.add("雷达画像对比显示，风险群体的压力水平比非风险群体高 "
                    + formatNumber(riskStress - normalStress) + " ，焦虑水平高 "
                    + formatNumber(riskAnxiety - normalAnxiety)
                    + " 。这类差值适合用于构建可解释的心理健康预警规则。 ");
        }

        Map<String, Object> activePlatform = maxBy(platformRadarStats, "totalCount");
        if (activePlatform != null) {
            insights.add("从平台画像看，“" + getText(activePlatform, "labelName") + "”样本数量较多，平均压力为 "
                    + formatNumber(activePlatform.get("stressLevel")) + " ，平均焦虑为 "
                    + formatNumber(activePlatform.get("anxietyLevel")) + " 。若平台样本量足够，可进一步比较不同平台内容类型对心理状态的差异影响。 ");
        }

        return insights;
    }

    private void normalizePage(TeenMentalHealthQuery query) {
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

    private Map<String, Object> findContains(List<Map<String, Object>> list, String key, String keyword) {
        if (list == null || list.isEmpty() || keyword == null) {
            return null;
        }
        for (Map<String, Object> item : list) {
            String text = getText(item, key);
            if (text.contains(keyword)) {
                return item;
            }
        }
        return null;
    }

    private String getText(Map<String, Object> map, String key) {
        if (map == null || map.get(key) == null) {
            return "未知";
        }
        String text = String.valueOf(map.get(key));
        return text.trim().isEmpty() ? "未知" : text;
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
        } catch (Exception e) {
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
        } catch (Exception e) {
            return 0D;
        }
    }

    private String formatNumber(Object value) {
        try {
            return BigDecimal.valueOf(toDouble(value)).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
        } catch (Exception e) {
            return "0";
        }
    }
}
