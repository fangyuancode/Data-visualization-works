package org.example.base_project.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.base_project.entity.StudentExamPerformance;
import org.example.base_project.entity.StudentExamPerformanceQuery;

import java.util.List;
import java.util.Map;

public interface StudentExamPerformanceMapper {
    List<StudentExamPerformance> selectPage(StudentExamPerformanceQuery query);

    Long selectCount(StudentExamPerformanceQuery query);

    StudentExamPerformance selectById(@Param("id") Long id);

    void insert(StudentExamPerformance data);

    void update(StudentExamPerformance data);

    void deleteById(@Param("id") Long id);

    List<String> selectGenderOptions();

    List<String> selectParentalEducationOptions();

    List<String> selectFamilyIncomeOptions();

    List<String> selectInternetAccessOptions();

    List<String> selectStudyEnvironmentOptions();

    List<String> selectTutoringOptions();

    List<String> selectPassFailOptions();

    List<String> selectGradeCategoryOptions();

    Map<String, Object> selectAnalysisSummary(StudentExamPerformanceQuery query);

    List<Map<String, Object>> selectPassFailDistribution(StudentExamPerformanceQuery query);

    List<Map<String, Object>> selectGradeDistribution(StudentExamPerformanceQuery query);

    List<Map<String, Object>> selectGenderDistribution(StudentExamPerformanceQuery query);

    List<Map<String, Object>> selectSubjectAverage(StudentExamPerformanceQuery query);

    List<Map<String, Object>> selectAgeSegmentStats(StudentExamPerformanceQuery query);

    List<Map<String, Object>> selectStudyHourSegmentStats(StudentExamPerformanceQuery query);

    List<Map<String, Object>> selectFamilyIncomeStats(StudentExamPerformanceQuery query);

    List<Map<String, Object>> selectParentalEducationStats(StudentExamPerformanceQuery query);

    List<Map<String, Object>> selectInternetAccessStats(StudentExamPerformanceQuery query);

    List<Map<String, Object>> selectStudyEnvironmentStats(StudentExamPerformanceQuery query);

    List<Map<String, Object>> selectSubjectRadarByGrade(StudentExamPerformanceQuery query);

    List<Map<String, Object>> selectBehaviorRadarByPass(StudentExamPerformanceQuery query);

    List<Map<String, Object>> selectBehaviorScoreScatter(StudentExamPerformanceQuery query);

    List<Map<String, Object>> selectGradeStudyHourHeatmap(StudentExamPerformanceQuery query);
}
