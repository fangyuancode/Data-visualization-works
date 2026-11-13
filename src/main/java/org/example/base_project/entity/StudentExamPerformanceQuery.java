package org.example.base_project.entity;

import java.math.BigDecimal;

public class StudentExamPerformanceQuery {
    private Integer current = 1;
    private Integer size = 10;

    private String studentId;
    private String gender;
    private String parentalEducation;
    private String familyIncome;
    private String internetAccess;
    private String studyEnvironment;
    private String tutoring;
    private String passFail;
    private String gradeCategory;

    private Integer ageMin;
    private Integer ageMax;

    private BigDecimal finalScoreMin;
    private BigDecimal finalScoreMax;

    private BigDecimal studyHoursMin;
    private BigDecimal studyHoursMax;

    public Integer getOffset() {
        int page = current == null || current < 1 ? 1 : current;
        int pageSize = size == null || size < 1 ? 10 : size;
        return (page - 1) * pageSize;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getParentalEducation() {
        return parentalEducation;
    }

    public void setParentalEducation(String parentalEducation) {
        this.parentalEducation = parentalEducation;
    }

    public String getFamilyIncome() {
        return familyIncome;
    }

    public void setFamilyIncome(String familyIncome) {
        this.familyIncome = familyIncome;
    }

    public String getInternetAccess() {
        return internetAccess;
    }

    public void setInternetAccess(String internetAccess) {
        this.internetAccess = internetAccess;
    }

    public String getStudyEnvironment() {
        return studyEnvironment;
    }

    public void setStudyEnvironment(String studyEnvironment) {
        this.studyEnvironment = studyEnvironment;
    }

    public String getTutoring() {
        return tutoring;
    }

    public void setTutoring(String tutoring) {
        this.tutoring = tutoring;
    }

    public String getPassFail() {
        return passFail;
    }

    public void setPassFail(String passFail) {
        this.passFail = passFail;
    }

    public String getGradeCategory() {
        return gradeCategory;
    }

    public void setGradeCategory(String gradeCategory) {
        this.gradeCategory = gradeCategory;
    }

    public Integer getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(Integer ageMin) {
        this.ageMin = ageMin;
    }

    public Integer getAgeMax() {
        return ageMax;
    }

    public void setAgeMax(Integer ageMax) {
        this.ageMax = ageMax;
    }

    public BigDecimal getFinalScoreMin() {
        return finalScoreMin;
    }

    public void setFinalScoreMin(BigDecimal finalScoreMin) {
        this.finalScoreMin = finalScoreMin;
    }

    public BigDecimal getFinalScoreMax() {
        return finalScoreMax;
    }

    public void setFinalScoreMax(BigDecimal finalScoreMax) {
        this.finalScoreMax = finalScoreMax;
    }

    public BigDecimal getStudyHoursMin() {
        return studyHoursMin;
    }

    public void setStudyHoursMin(BigDecimal studyHoursMin) {
        this.studyHoursMin = studyHoursMin;
    }

    public BigDecimal getStudyHoursMax() {
        return studyHoursMax;
    }

    public void setStudyHoursMax(BigDecimal studyHoursMax) {
        this.studyHoursMax = studyHoursMax;
    }
}