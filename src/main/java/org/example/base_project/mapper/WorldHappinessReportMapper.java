package org.example.base_project.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.base_project.entity.WorldHappinessReport;
import org.example.base_project.entity.WorldHappinessReportQuery;

import java.util.List;
import java.util.Map;

public interface WorldHappinessReportMapper {
    List<WorldHappinessReport> selectPage(WorldHappinessReportQuery query);

    Long selectCount(WorldHappinessReportQuery query);

    WorldHappinessReport selectById(@Param("id") Long id);

    void insert(WorldHappinessReport data);

    void update(WorldHappinessReport data);

    void deleteById(@Param("id") Long id);

    List<Integer> selectYearOptions();

    List<String> selectCountryOptions();

    Map<String, Object> selectAnalysisSummary(WorldHappinessReportQuery query);

    List<Map<String, Object>> selectScoreSegmentStats(WorldHappinessReportQuery query);

    List<Map<String, Object>> selectRankSegmentStats(WorldHappinessReportQuery query);

    List<Map<String, Object>> selectYearTrend(WorldHappinessReportQuery query);

    List<Map<String, Object>> selectFactorContribution(WorldHappinessReportQuery query);

    List<Map<String, Object>> selectTopCountryRank(WorldHappinessReportQuery query);

    List<Map<String, Object>> selectCountryAverageRank(WorldHappinessReportQuery query);

    List<Map<String, Object>> selectFactorRadarByScoreSegment(WorldHappinessReportQuery query);

    List<Map<String, Object>> selectScoreFactorScatter(WorldHappinessReportQuery query);

    List<Map<String, Object>> selectYearScoreHeatmap(WorldHappinessReportQuery query);
}
