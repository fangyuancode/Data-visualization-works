package org.example.base_project.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.base_project.entity.TeenMentalHealth;
import org.example.base_project.entity.TeenMentalHealthQuery;

import java.util.List;
import java.util.Map;

public interface TeenMentalHealthMapper {
    List<TeenMentalHealth> selectPage(TeenMentalHealthQuery query);

    Long selectCount(TeenMentalHealthQuery query);

    TeenMentalHealth selectById(@Param("id") Long id);

    void insert(TeenMentalHealth data);

    void update(TeenMentalHealth data);

    void deleteById(@Param("id") Long id);

    List<String> selectGenderOptions();

    List<String> selectPlatformUsageOptions();

    List<String> selectSocialInteractionLevelOptions();

    List<Integer> selectDepressionLabelOptions();

    Map<String, Object> selectAnalysisSummary(TeenMentalHealthQuery query);

    List<Map<String, Object>> selectDepressionDistribution(TeenMentalHealthQuery query);

    List<Map<String, Object>> selectGenderDistribution(TeenMentalHealthQuery query);

    List<Map<String, Object>> selectPlatformStats(TeenMentalHealthQuery query);

    List<Map<String, Object>> selectAgeSegmentStats(TeenMentalHealthQuery query);

    List<Map<String, Object>> selectSocialMediaSegmentStats(TeenMentalHealthQuery query);

    List<Map<String, Object>> selectSleepSegmentStats(TeenMentalHealthQuery query);

    List<Map<String, Object>> selectSocialInteractionStats(TeenMentalHealthQuery query);

    List<Map<String, Object>> selectMentalRadarByRisk(TeenMentalHealthQuery query);

    List<Map<String, Object>> selectPlatformRadarStats(TeenMentalHealthQuery query);

    List<Map<String, Object>> selectSocialAnxietyScatter(TeenMentalHealthQuery query);

    List<Map<String, Object>> selectScreenSleepScatter(TeenMentalHealthQuery query);

    List<Map<String, Object>> selectPlatformRiskHeatmap(TeenMentalHealthQuery query);
}