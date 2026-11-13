package org.example.base_project.controller;

import jakarta.annotation.Resource;
import org.example.base_project.common.Result;
import org.example.base_project.entity.StudentExamPerformance;
import org.example.base_project.entity.StudentExamPerformanceQuery;
import org.example.base_project.service.StudentExamPerformanceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common/exam")
public class StudentExamPerformanceController {

    @Resource
    private StudentExamPerformanceService studentExamPerformanceService;

    @GetMapping("/page")
    public Result page(StudentExamPerformanceQuery query) {
        return Result.success(studentExamPerformanceService.page(query));
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id) {
        return Result.success(studentExamPerformanceService.detail(id));
    }

    @GetMapping("/options")
    public Result options() {
        return Result.success(studentExamPerformanceService.options());
    }

    /**
     * 学生考试表现数据挖掘与可视化分析接口
     */
    @GetMapping("/analysis")
    public Result analysis(StudentExamPerformanceQuery query) {
        return Result.success(studentExamPerformanceService.analysis(query));
    }

    @PostMapping
    public Result create(@RequestBody StudentExamPerformance data) {
        studentExamPerformanceService.create(data);
        return Result.success("新增学生考试表现数据成功");
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody StudentExamPerformance data) {
        studentExamPerformanceService.update(id, data);
        return Result.success("修改学生考试表现数据成功");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        studentExamPerformanceService.delete(id);
        return Result.success("删除学生考试表现数据成功");
    }
}
