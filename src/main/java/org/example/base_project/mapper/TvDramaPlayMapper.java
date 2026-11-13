package org.example.base_project.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.base_project.entity.TvDramaPlayData;
import org.example.base_project.entity.TvDramaPlayQuery;

import java.util.List;
import java.util.Map;

public interface TvDramaPlayMapper {
    List<TvDramaPlayData> selectPage(TvDramaPlayQuery query);

    Long selectCount(TvDramaPlayQuery query);

    TvDramaPlayData selectById(@Param("id") Long id);

    void insert(TvDramaPlayData data);

    void update(TvDramaPlayData data);

    void deleteById(@Param("id") Long id);

    List<String> selectDramaTypeOptions();

    Map<String, Object> selectAnalysisSummary(TvDramaPlayQuery query);

    List<Map<String, Object>> selectTypeDistribution(TvDramaPlayQuery query);

    List<Map<String, Object>> selectScoreSegmentStats(TvDramaPlayQuery query);

    List<Map<String, Object>> selectPlaySegmentStats(TvDramaPlayQuery query);

    List<Map<String, Object>> selectMonthTrend(TvDramaPlayQuery query);

    List<Map<String, Object>> selectTypeRadarStats(TvDramaPlayQuery query);

    List<Map<String, Object>> selectTopDramaRank(TvDramaPlayQuery query);

    List<Map<String, Object>> selectScorePlayScatter(TvDramaPlayQuery query);

    List<Map<String, Object>> selectTypeScoreHeatmap(TvDramaPlayQuery query);

    List<Map<String, Object>> selectInteractionByScoreSegment(TvDramaPlayQuery query);
}
