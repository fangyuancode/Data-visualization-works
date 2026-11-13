package org.example.base_project.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.base_project.entity.MaternalNiptData;
import org.example.base_project.entity.MaternalNiptQuery;

import java.util.List;
import java.util.Map;

public interface MaternalNiptMapper {
    List<MaternalNiptData> selectPage(MaternalNiptQuery query);

    Long selectCount(MaternalNiptQuery query);

    MaternalNiptData selectById(@Param("id") Long id);

    void insert(MaternalNiptData data);

    void update(MaternalNiptData data);

    void deleteById(@Param("id") Long id);

    List<String> selectFetusGenderOptions();

    List<String> selectFetusHealthyOptions();

    List<String> selectChromosomeAneuploidyOptions();

    Map<String, Object> selectAnalysisSummary(MaternalNiptQuery query);

    List<Map<String, Object>> selectFetusGenderDistribution(MaternalNiptQuery query);

    List<Map<String, Object>> selectHealthyDistribution(MaternalNiptQuery query);

    List<Map<String, Object>> selectAneuploidyDistribution(MaternalNiptQuery query);

    List<Map<String, Object>> selectAgeSegmentStats(MaternalNiptQuery query);

    List<Map<String, Object>> selectBmiSegmentStats(MaternalNiptQuery query);

    List<Map<String, Object>> selectGestationalSegmentStats(MaternalNiptQuery query);

    List<Map<String, Object>> selectZValueByAgeSegment(MaternalNiptQuery query);

    List<Map<String, Object>> selectQualityRadarByFetusGender(MaternalNiptQuery query);

    List<Map<String, Object>> selectBmiYConcentrationScatter(MaternalNiptQuery query);

    List<Map<String, Object>> selectTestMonthTrend(MaternalNiptQuery query);
}
