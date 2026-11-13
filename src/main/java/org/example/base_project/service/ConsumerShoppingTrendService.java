package org.example.base_project.service;

import jakarta.annotation.Resource;
import org.example.base_project.entity.ConsumerShoppingTrend;
import org.example.base_project.entity.ConsumerShoppingTrendQuery;
import org.example.base_project.entity.PageResult;
import org.example.base_project.exception.CustomException;
import org.example.base_project.mapper.ConsumerShoppingTrendMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConsumerShoppingTrendService {

    @Resource
    private ConsumerShoppingTrendMapper consumerShoppingTrendMapper;

    public PageResult<ConsumerShoppingTrend> page(ConsumerShoppingTrendQuery query) {
        normalizePage(query);

        List<ConsumerShoppingTrend> records = consumerShoppingTrendMapper.selectPage(query);
        Long total = consumerShoppingTrendMapper.selectCount(query);

        return new PageResult<>(records, query.getCurrent(), query.getSize(), total);
    }

    public ConsumerShoppingTrend detail(Long id) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        ConsumerShoppingTrend data = consumerShoppingTrendMapper.selectById(id);

        if (data == null) {
            throw new CustomException("404", "数据不存在");
        }

        return data;
    }

    public void create(ConsumerShoppingTrend item) {
        if (item == null) {
            throw new CustomException("400", "新增数据不能为空");
        }

        consumerShoppingTrendMapper.insert(item);
    }

    public void update(Long id, ConsumerShoppingTrend item) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        ConsumerShoppingTrend dbData = consumerShoppingTrendMapper.selectById(id);
        if (dbData == null) {
            throw new CustomException("404", "要修改的数据不存在");
        }

        item.id = id;
        consumerShoppingTrendMapper.update(item);
    }

    public void delete(Long id) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        ConsumerShoppingTrend dbData = consumerShoppingTrendMapper.selectById(id);
        if (dbData == null) {
            throw new CustomException("404", "要删除的数据不存在");
        }

        consumerShoppingTrendMapper.deleteById(id);
    }

    public Map<String, Object> options() {
        Map<String, Object> result = new HashMap<>();

        result.put("genders", consumerShoppingTrendMapper.selectGenderOptions());
        result.put("cityTiers", consumerShoppingTrendMapper.selectCityTierOptions());
        result.put("shoppingPreferences", consumerShoppingTrendMapper.selectShoppingPreferenceOptions());

        return result;
    }

    /**
     * 消费者购物趋势数据挖掘结果。
     * 返回结构直接服务前端 ECharts，可减少前端二次聚合压力。
     */
    public Map<String, Object> analysis(ConsumerShoppingTrendQuery query) {
        if (query == null) {
            query = new ConsumerShoppingTrendQuery();
        }

        Map<String, Object> result = new HashMap<>();

        Map<String, Object> summary = consumerShoppingTrendMapper.selectAnalysisSummary(query);
        List<Map<String, Object>> preferenceDistribution = consumerShoppingTrendMapper.selectPreferenceDistribution(query);
        List<Map<String, Object>> genderDistribution = consumerShoppingTrendMapper.selectGenderDistribution(query);
        List<Map<String, Object>> cityTierDistribution = consumerShoppingTrendMapper.selectCityTierDistribution(query);
        List<Map<String, Object>> ageSegmentPreference = consumerShoppingTrendMapper.selectAgeSegmentPreference(query);
        List<Map<String, Object>> incomeSegmentStats = consumerShoppingTrendMapper.selectIncomeSegmentStats(query);
        List<Map<String, Object>> cityTierPreferenceStats = consumerShoppingTrendMapper.selectCityTierPreferenceStats(query);
        List<Map<String, Object>> radarByPreference = consumerShoppingTrendMapper.selectRadarByPreference(query);
        List<Map<String, Object>> deliveryExperienceStats = consumerShoppingTrendMapper.selectDeliveryExperienceStats(query);
        List<Map<String, Object>> techOrderScatter = consumerShoppingTrendMapper.selectTechOrderScatter(query);

        result.put("summary", summary == null ? new HashMap<>() : summary);
        result.put("preferenceDistribution", preferenceDistribution);
        result.put("genderDistribution", genderDistribution);
        result.put("cityTierDistribution", cityTierDistribution);
        result.put("ageSegmentPreference", ageSegmentPreference);
        result.put("incomeSegmentStats", incomeSegmentStats);
        result.put("cityTierPreferenceStats", cityTierPreferenceStats);
        result.put("radarByPreference", radarByPreference);
        result.put("deliveryExperienceStats", deliveryExperienceStats);
        result.put("techOrderScatter", techOrderScatter);
        result.put("insights", buildInsights(summary, preferenceDistribution, cityTierPreferenceStats, incomeSegmentStats, radarByPreference));

        return result;
    }

    private List<String> buildInsights(
            Map<String, Object> summary,
            List<Map<String, Object>> preferenceDistribution,
            List<Map<String, Object>> cityTierPreferenceStats,
            List<Map<String, Object>> incomeSegmentStats,
            List<Map<String, Object>> radarByPreference
    ) {
        List<String> insights = new ArrayList<>();

        long totalCount = longValue(summary, "totalCount");
        if (totalCount <= 0) {
            insights.add("当前筛选条件下暂无有效数据，建议放宽筛选条件后重新分析。");
            return insights;
        }

        BigDecimal onlinePreferenceRate = decimalValue(summary, "onlinePreferenceRate");
        BigDecimal avgOnlineOrders = decimalValue(summary, "avgOnlineOrders");
        BigDecimal avgStoreVisits = decimalValue(summary, "avgStoreVisits");
        BigDecimal avgOnlineSpend = decimalValue(summary, "avgOnlineSpend");
        BigDecimal avgStoreSpend = decimalValue(summary, "avgStoreSpend");
        BigDecimal avgTechSavvyScore = decimalValue(summary, "avgTechSavvyScore");
        BigDecimal avgPaymentTrustScore = decimalValue(summary, "avgPaymentTrustScore");

        if (onlinePreferenceRate.compareTo(new BigDecimal("60")) >= 0) {
            insights.add("线上或混合购物偏好占比达到 " + formatDecimal(onlinePreferenceRate) + "% ，说明该客群已经明显具备线上转化基础，适合加强内容推荐、会员复购和精准优惠券投放。");
        } else if (onlinePreferenceRate.compareTo(new BigDecimal("40")) >= 0) {
            insights.add("线上或混合购物偏好占比为 " + formatDecimal(onlinePreferenceRate) + "% ，线上线下渠道较为均衡，适合采用到店体验 + 线上复购的全渠道运营策略。");
        } else {
            insights.add("线上或混合购物偏好占比仅为 " + formatDecimal(onlinePreferenceRate) + "% ，线下触点仍然重要，应优先优化门店体验、导购服务与线下会员沉淀。");
        }

        if (avgOnlineOrders.compareTo(avgStoreVisits) >= 0) {
            insights.add("平均线上订单数为 " + formatDecimal(avgOnlineOrders) + "，高于平均门店访问数 " + formatDecimal(avgStoreVisits) + "，说明线上购买频次更强，可重点挖掘高频低客单与订阅式复购场景。");
        } else {
            insights.add("平均门店访问数为 " + formatDecimal(avgStoreVisits) + "，高于平均线上订单数 " + formatDecimal(avgOnlineOrders) + "，说明线下体验仍能影响购买决策，应强化试用、触摸体验和门店导购价值。");
        }

        if (avgOnlineSpend.compareTo(avgStoreSpend) >= 0) {
            insights.add("线上平均消费金额为 " + formatDecimal(avgOnlineSpend) + "，高于线下均消 " + formatDecimal(avgStoreSpend) + "，可进一步通过组合推荐、满减门槛和会员权益提升线上客单价。");
        } else {
            insights.add("线下平均消费金额为 " + formatDecimal(avgStoreSpend) + "，高于线上均消 " + formatDecimal(avgOnlineSpend) + "，说明高价值商品更依赖体验信任，可尝试直播讲解、AR 展示或门店到家服务降低线上决策成本。");
        }

        Map<String, Object> topPreference = maxByValue(preferenceDistribution, "value");
        if (topPreference != null) {
            insights.add("当前占比最高的购物偏好为“" + preferenceName(String.valueOf(topPreference.get("name"))) + "”，企业可以围绕该偏好人群设计首页入口、优惠机制和售后承诺。");
        }

        Map<String, Object> topCityTier = maxByValue(cityTierPreferenceStats, "avgOnlineOrders");
        if (topCityTier != null) {
            insights.add("线上订单表现最突出的城市等级为“" + topCityTier.get("cityTier") + "”，平均线上订单数约 " + formatDecimal(decimalValue(topCityTier, "avgOnlineOrders")) + "，可作为线上投放和渠道扩张的优先区域。");
        }

        Map<String, Object> bestIncomeSegment = maxByValue(incomeSegmentStats, "avgOnlineSpend");
        if (bestIncomeSegment != null) {
            insights.add("线上客单价最高的收入段为“" + bestIncomeSegment.get("incomeSegment") + "”，平均线上消费约 " + formatDecimal(decimalValue(bestIncomeSegment, "avgOnlineSpend")) + "，适合进行高价值商品、品质型商品和会员升级推荐。");
        }

        Map<String, Object> onlineRadar = findByName(radarByPreference, "preference", "Online");
        if (onlineRadar != null) {
            BigDecimal tech = decimalValue(onlineRadar, "techSavvyScore");
            BigDecimal trust = decimalValue(onlineRadar, "onlinePaymentTrustScore");
            if (tech.compareTo(avgTechSavvyScore) >= 0 && trust.compareTo(avgPaymentTrustScore) >= 0) {
                insights.add("线上偏好人群的技术熟练度和支付信任度高于整体均值，说明技术接受度与支付信任是线上购物转化的重要驱动因素。");
            }
        }

        return insights;
    }

    private Map<String, Object> maxByValue(List<Map<String, Object>> list, String key) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        Map<String, Object> best = null;
        BigDecimal bestValue = null;

        for (Map<String, Object> item : list) {
            BigDecimal value = decimalValue(item, key);
            if (best == null || value.compareTo(bestValue) > 0) {
                best = item;
                bestValue = value;
            }
        }

        return best;
    }

    private Map<String, Object> findByName(List<Map<String, Object>> list, String key, String value) {
        if (list == null) {
            return null;
        }

        for (Map<String, Object> item : list) {
            Object current = item.get(key);
            if (current != null && value.equalsIgnoreCase(String.valueOf(current))) {
                return item;
            }
        }

        return null;
    }

    private String preferenceName(String value) {
        if ("Online".equalsIgnoreCase(value)) {
            return "线上购物";
        }
        if ("Offline".equalsIgnoreCase(value)) {
            return "线下购物";
        }
        if ("Both".equalsIgnoreCase(value)) {
            return "混合购物";
        }
        return value == null ? "未知" : value;
    }

    private BigDecimal decimalValue(Map<String, Object> map, String key) {
        if (map == null || map.get(key) == null) {
            return BigDecimal.ZERO;
        }

        Object value = map.get(key);
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue()).setScale(2, RoundingMode.HALF_UP);
        }

        try {
            return new BigDecimal(String.valueOf(value)).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private long longValue(Map<String, Object> map, String key) {
        if (map == null || map.get(key) == null) {
            return 0L;
        }

        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        try {
            return Long.parseLong(String.valueOf(value));
        } catch (Exception e) {
            return 0L;
        }
    }

    private String formatDecimal(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return value.stripTrailingZeros().toPlainString();
    }

    private void normalizePage(ConsumerShoppingTrendQuery query) {
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
