package org.example.base_project.controller;

import jakarta.annotation.Resource;
import org.example.base_project.common.Result;
import org.example.base_project.entity.WorldHappinessReport;
import org.example.base_project.entity.WorldHappinessReportQuery;
import org.example.base_project.service.WorldHappinessReportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common/world-happiness")
public class WorldHappinessReportController {

    @Resource
    private WorldHappinessReportService worldHappinessReportService;

    @GetMapping("/page")
    public Result page(WorldHappinessReportQuery query) {
        return Result.success(worldHappinessReportService.page(query));
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id) {
        return Result.success(worldHappinessReportService.detail(id));
    }

    @GetMapping("/options")
    public Result options() {
        return Result.success(worldHappinessReportService.options());
    }

    /**
     * 世界幸福报告数据挖掘与可视化分析接口。
     */
    @GetMapping("/analysis")
    public Result analysis(WorldHappinessReportQuery query) {
        return Result.success(worldHappinessReportService.analysis(query));
    }

    @PostMapping
    public Result create(@RequestBody WorldHappinessReport data) {
        worldHappinessReportService.create(data);
        return Result.success("新增世界幸福报告数据成功");
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody WorldHappinessReport data) {
        worldHappinessReportService.update(id, data);
        return Result.success("修改世界幸福报告数据成功");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        worldHappinessReportService.delete(id);
        return Result.success("删除世界幸福报告数据成功");
    }
}
