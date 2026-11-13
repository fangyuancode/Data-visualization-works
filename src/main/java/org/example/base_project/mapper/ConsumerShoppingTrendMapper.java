package org.example.base_project.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.base_project.entity.ConsumerShoppingTrend;
import org.example.base_project.entity.ConsumerShoppingTrendQuery;

import java.util.List;
import java.util.Map;

public interface ConsumerShoppingTrendMapper {
    List<ConsumerShoppingTrend> selectPage(ConsumerShoppingTrendQuery query);

    Long selectCount(ConsumerShoppingTrendQuery query);

    ConsumerShoppingTrend selectById(@Param("id") Long id);

    void insert(ConsumerShoppingTrend item);

    void update(ConsumerShoppingTrend item);

    void deleteById(@Param("id") Long id);

    List<String> selectGenderOptions();

    List<String> selectCityTierOptions();

    List<String> selectShoppingPreferenceOptions();

    Map<String, Object> selectAnalysisSummary(ConsumerShoppingTrendQuery query);

    List<Map<String, Object>> selectPreferenceDistribution(ConsumerShoppingTrendQuery query);

    List<Map<String, Object>> selectGenderDistribution(ConsumerShoppingTrendQuery query);

    List<Map<String, Object>> selectCityTierDistribution(ConsumerShoppingTrendQuery query);

    List<Map<String, Object>> selectAgeSegmentPreference(ConsumerShoppingTrendQuery query);

    List<Map<String, Object>> selectIncomeSegmentStats(ConsumerShoppingTrendQuery query);

    List<Map<String, Object>> selectCityTierPreferenceStats(ConsumerShoppingTrendQuery query);

    List<Map<String, Object>> selectRadarByPreference(ConsumerShoppingTrendQuery query);

    List<Map<String, Object>> selectDeliveryExperienceStats(ConsumerShoppingTrendQuery query);

    List<Map<String, Object>> selectTechOrderScatter(ConsumerShoppingTrendQuery query);
}
