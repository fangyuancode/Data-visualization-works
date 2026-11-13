package org.example.base_project.entity;

import java.math.BigDecimal;

/**
 * 学生考试表现数据实体
 */
public class StudentExamPerformance {
    public Long id;

    public String studentId;
    public String gender;
    public Integer age;

    public String parentalEducation;
    public String familyIncome;
    public String internetAccess;
    public String studyEnvironment;

    public BigDecimal studyHoursPerDay;
    public BigDecimal attendanceRate;
    public BigDecimal sleepHours;
    public BigDecimal socialMediaHours;
    public BigDecimal assignmentCompletionRate;
    public BigDecimal participationScore;

    public Integer onlineCoursesCompleted;
    public String tutoring;

    public BigDecimal mathScore;
    public BigDecimal readingScore;
    public BigDecimal writingScore;
    public BigDecimal scienceScore;
    public BigDecimal finalExamScore;
    public BigDecimal previousGpa;

    public String passFail;
    public String gradeCategory;

    public String createTime;
}