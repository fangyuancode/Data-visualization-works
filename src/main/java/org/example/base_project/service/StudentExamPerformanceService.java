package org.example.base_project.service;

import jakarta.annotation.Resource;
import org.example.base_project.entity.PageResult;
import org.example.base_project.entity.StudentExamPerformance;
import org.example.base_project.entity.StudentExamPerformanceQuery;
import org.example.base_project.exception.CustomException;
import org.example.base_project.mapper.StudentExamPerformanceMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentExamPerformanceService {

    @Resource
    private StudentExamPerformanceMapper studentExamPerformanceMapper;

    public PageResult<StudentExamPerformance> page(StudentExamPerformanceQuery query) {
        normalizePage(query);

        List<StudentExamPerformance> records = studentExamPerformanceMapper.selectPage(query);
        Long total = studentExamPerformanceMapper.selectCount(query);

        return new PageResult<>(records, query.getCurrent(), query.getSize(), total);
    }

    public StudentExamPerformance detail(Long id) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        StudentExamPerformance data = studentExamPerformanceMapper.selectById(id);

        if (data == null) {
            throw new CustomException("404", "学生考试表现数据不存在");
        }

        return data;
    }

    public void create(StudentExamPerformance data) {
        if (data == null) {
            throw new CustomException("400", "新增数据不能为空");
        }

        studentExamPerformanceMapper.insert(data);
    }

    public void update(Long id, StudentExamPerformance data) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        StudentExamPerformance dbData = studentExamPerformanceMapper.selectById(id);

        if (dbData == null) {
            throw new CustomException("404", "要修改的数据不存在");
        }

        data.id = id;
        studentExamPerformanceMapper.update(data);
    }

    public void delete(Long id) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        StudentExamPerformance dbData = studentExamPerformanceMapper.selectById(id);

        if (dbData == null) {
            throw new CustomException("404", "要删除的数据不存在");
        }

        studentExamPerformanceMapper.deleteById(id);
    }

    public Map<String, Object> options() {
        Map<String, Object> result = new HashMap<>();

        result.put("genders", studentExamPerformanceMapper.selectGenderOptions());
        result.put("parentalEducations", studentExamPerformanceMapper.selectParentalEducationOptions());
        result.put("familyIncomes", studentExamPerformanceMapper.selectFamilyIncomeOptions());
        result.put("internetAccessOptions", studentExamPerformanceMapper.selectInternetAccessOptions());
        result.put("studyEnvironments", studentExamPerformanceMapper.selectStudyEnvironmentOptions());
        result.put("tutoringOptions", studentExamPerformanceMapper.selectTutoringOptions());
        result.put("passFailOptions", studentExamPerformanceMapper.selectPassFailOptions());
        result.put("gradeCategories", studentExamPerformanceMapper.selectGradeCategoryOptions());

        return result;
    }

    /**
     * 学生考试表现数据挖掘：成绩、学习行为、家庭背景、资源条件、等级结构和规律解读。
     */
    public Map<String, Object> analysis(StudentExamPerformanceQuery query) {
        if (query == null) {
            query = new StudentExamPerformanceQuery();
        }

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> summary = safeMap(studentExamPerformanceMapper.selectAnalysisSummary(query));
        List<Map<String, Object>> passFailDistribution = safeList(studentExamPerformanceMapper.selectPassFailDistribution(query));
        List<Map<String, Object>> gradeDistribution = safeList(studentExamPerformanceMapper.selectGradeDistribution(query));
        List<Map<String, Object>> ageSegmentStats = safeList(studentExamPerformanceMapper.selectAgeSegmentStats(query));
        List<Map<String, Object>> studyHourSegmentStats = safeList(studentExamPerformanceMapper.selectStudyHourSegmentStats(query));
        List<Map<String, Object>> familyIncomeStats = safeList(studentExamPerformanceMapper.selectFamilyIncomeStats(query));
        List<Map<String, Object>> parentalEducationStats = safeList(studentExamPerformanceMapper.selectParentalEducationStats(query));
        List<Map<String, Object>> internetAccessStats = safeList(studentExamPerformanceMapper.selectInternetAccessStats(query));
        List<Map<String, Object>> studyEnvironmentStats = safeList(studentExamPerformanceMapper.selectStudyEnvironmentStats(query));
        List<Map<String, Object>> subjectRadarByGrade = safeList(studentExamPerformanceMapper.selectSubjectRadarByGrade(query));
        List<Map<String, Object>> behaviorRadarByPass = safeList(studentExamPerformanceMapper.selectBehaviorRadarByPass(query));

        result.put("summary", summary);
        result.put("passFailDistribution", passFailDistribution);
        result.put("gradeDistribution", gradeDistribution);
        result.put("genderDistribution", safeList(studentExamPerformanceMapper.selectGenderDistribution(query)));
        result.put("subjectAverage", safeList(studentExamPerformanceMapper.selectSubjectAverage(query)));
        result.put("ageSegmentStats", ageSegmentStats);
        result.put("studyHourSegmentStats", studyHourSegmentStats);
        result.put("familyIncomeStats", familyIncomeStats);
        result.put("parentalEducationStats", parentalEducationStats);
        result.put("internetAccessStats", internetAccessStats);
        result.put("studyEnvironmentStats", studyEnvironmentStats);
        result.put("subjectRadarByGrade", subjectRadarByGrade);
        result.put("behaviorRadarByPass", behaviorRadarByPass);
        result.put("behaviorScoreScatter", safeList(studentExamPerformanceMapper.selectBehaviorScoreScatter(query)));
        result.put("gradeStudyHourHeatmap", safeList(studentExamPerformanceMapper.selectGradeStudyHourHeatmap(query)));
        result.put("insights", buildInsights(summary, passFailDistribution, gradeDistribution, ageSegmentStats,
                studyHourSegmentStats, familyIncomeStats, parentalEducationStats, internetAccessStats,
                studyEnvironmentStats, subjectRadarByGrade, behaviorRadarByPass));

        return result;
    }

    private List<String> buildInsights(Map<String, Object> summary,
                                       List<Map<String, Object>> passFailDistribution,
                                       List<Map<String, Object>> gradeDistribution,
                                       List<Map<String, Object>> ageSegmentStats,
                                       List<Map<String, Object>> studyHourSegmentStats,
                                       List<Map<String, Object>> familyIncomeStats,
                                       List<Map<String, Object>> parentalEducationStats,
                                       List<Map<String, Object>> internetAccessStats,
                                       List<Map<String, Object>> studyEnvironmentStats,
                                       List<Map<String, Object>> subjectRadarByGrade,
                                       List<Map<String, Object>> behaviorRadarByPass) {
        List<String> insights = new ArrayList<>();

        long totalCount = toLong(summary.get("totalCount"));
        if (totalCount <= 0) {
            insights.add("当前筛选条件下暂无可分析的学生考试表现数据，可以放宽筛选条件后重新查看。 ");
            return insights;
        }

        insights.add("当前共纳入 " + totalCount + " 条学生考试表现记录，平均期末成绩为 "
                + formatNumber(summary.get("avgFinalScore")) + " 分，通过率为 "
                + formatNumber(summary.get("passRate")) + "% ，可作为整体学业表现的基准线。 ");

        insights.add("从学习行为看，平均每日学习时长为 " + formatNumber(summary.get("avgStudyHours"))
                + " 小时，平均出勤率为 " + formatNumber(summary.get("avgAttendanceRate"))
                + "% ，平均作业完成率为 " + formatNumber(summary.get("avgAssignmentRate"))
                + "% 。这些过程性指标通常比单一考试分数更适合用于学习预警和干预。 ");

        Map<String, Object> mainGrade = maxBy(gradeDistribution, "totalCount");
        if (mainGrade != null) {
            insights.add("成绩等级结构中，“" + getText(mainGrade, "labelName") + "”占比最高，共 "
                    + toLong(mainGrade.get("totalCount")) + " 人，占 " + formatNumber(mainGrade.get("ratio"))
                    + "% 。该等级是当前学生群体的主力分布，应重点观察其向上提升空间。 ");
        }

        Map<String, Object> bestAge = maxBy(ageSegmentStats, "avgFinalScore");
        if (bestAge != null) {
            insights.add("年龄分层中，“" + getText(bestAge, "segmentName") + "”平均期末成绩最高，达到 "
                    + formatNumber(bestAge.get("avgFinalScore")) + " 分，通过率为 "
                    + formatNumber(bestAge.get("passRate")) + "% ，说明该年龄段的学习成熟度或课程适配度相对更好。 ");
        }

        Map<String, Object> bestStudy = maxBy(studyHourSegmentStats, "avgFinalScore");
        if (bestStudy != null) {
            insights.add("学习时长分层中，“" + getText(bestStudy, "segmentName") + "”平均成绩最高，为 "
                    + formatNumber(bestStudy.get("avgFinalScore")) + " 分。若该区间通过率也较高，可将其作为合理学习投入的参考范围。 ");
        }

        Map<String, Object> highIncome = maxBy(familyIncomeStats, "avgFinalScore");
        Map<String, Object> highEducation = maxBy(parentalEducationStats, "avgFinalScore");
        if (highIncome != null || highEducation != null) {
            StringBuilder builder = new StringBuilder("家庭背景维度显示");
            if (highIncome != null) {
                builder.append("，“").append(getText(highIncome, "labelName")).append("”收入层学生平均成绩相对较高");
            }
            if (highEducation != null) {
                builder.append("，“").append(getText(highEducation, "labelName")).append("”父母教育背景对应的平均成绩表现更突出");
            }
            builder.append("，说明家庭资源与学习支持可能对学生成绩存在间接影响。 ");
            insights.add(builder.toString());
        }

        Map<String, Object> bestInternet = maxBy(internetAccessStats, "avgFinalScore");
        Map<String, Object> bestEnvironment = maxBy(studyEnvironmentStats, "avgFinalScore");
        if (bestInternet != null || bestEnvironment != null) {
            StringBuilder builder = new StringBuilder("资源条件方面");
            if (bestInternet != null) {
                builder.append("，网络条件为“").append(getText(bestInternet, "labelName")).append("”的学生平均成绩为 ")
                        .append(formatNumber(bestInternet.get("avgFinalScore"))).append(" 分");
            }
            if (bestEnvironment != null) {
                builder.append("，学习环境为“").append(getText(bestEnvironment, "labelName")).append("”的学生平均成绩为 ")
                        .append(formatNumber(bestEnvironment.get("avgFinalScore"))).append(" 分");
            }
            builder.append("，可用于识别需要改善学习条件的学生群体。 ");
            insights.add(builder.toString());
        }

        Map<String, Object> passProfile = findContains(behaviorRadarByPass, "labelName", "pass");
        Map<String, Object> failProfile = findContains(behaviorRadarByPass, "labelName", "fail");
        if (passProfile != null && failProfile != null) {
            double studyGap = toDouble(passProfile.get("avgStudyHours")) - toDouble(failProfile.get("avgStudyHours"));
            double attendanceGap = toDouble(passProfile.get("avgAttendanceRate")) - toDouble(failProfile.get("avgAttendanceRate"));
            double assignmentGap = toDouble(passProfile.get("avgAssignmentRate")) - toDouble(failProfile.get("avgAssignmentRate"));
            insights.add("通过学生与未通过学生相比，每日学习时长差值约为 " + formatNumber(studyGap)
                    + " 小时，出勤率差值约为 " + formatNumber(attendanceGap)
                    + " 个百分点，作业完成率差值约为 " + formatNumber(assignmentGap)
                    + " 个百分点。这些差异可以转化为学习预警规则。 ");
        }

        Map<String, Object> bestRadar = maxBy(subjectRadarByGrade, "avgFinalScore");
        if (bestRadar != null) {
            insights.add("从学科能力雷达看，“" + getText(bestRadar, "gradeCategory") + "”等级学生在数学、阅读、写作、科学和期末综合分上整体更均衡，可作为优秀学生画像参考。 ");
        }

        return insights;
    }

    private void normalizePage(StudentExamPerformanceQuery query) {
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

    private Map<String, Object> safeMap(Map<String, Object> map) {
        return map == null ? new HashMap<>() : map;
    }

    private List<Map<String, Object>> safeList(List<Map<String, Object>> list) {
        return list == null ? new ArrayList<>() : list;
    }

    private Map<String, Object> maxBy(List<Map<String, Object>> list, String key) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        Map<String, Object> result = null;
        double max = -Double.MAX_VALUE;
        for (Map<String, Object> item : list) {
            double value = toDouble(item.get(key));
            if (result == null || value > max) {
                result = item;
                max = value;
            }
        }
        return result;
    }

    private Map<String, Object> findContains(List<Map<String, Object>> list, String key, String keyword) {
        if (list == null || keyword == null) {
            return null;
        }
        String lowerKeyword = keyword.toLowerCase();
        for (Map<String, Object> item : list) {
            String value = getText(item, key).toLowerCase();
            if (value.contains(lowerKeyword)) {
                return item;
            }
        }
        return null;
    }

    private String getText(Map<String, Object> map, String key) {
        if (map == null || map.get(key) == null) {
            return "未知";
        }
        return String.valueOf(map.get(key));
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (Exception e) {
            return 0L;
        }
    }

    private double toDouble(Object value) {
        if (value == null) {
            return 0D;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (Exception e) {
            return 0D;
        }
    }

    private String formatNumber(Object value) {
        return String.format("%.2f", toDouble(value));
    }
}
