package org.example.base_project.controller;

import jakarta.annotation.Resource;
import org.example.base_project.common.Result;
import org.example.base_project.entity.TvDramaPlayData;
import org.example.base_project.entity.TvDramaPlayQuery;
import org.example.base_project.service.TvDramaPlayService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common/tvplay")
public class TvDramaPlayController {

    @Resource
    private TvDramaPlayService tvDramaPlayService;

    @GetMapping("/page")
    public Result page(TvDramaPlayQuery query) {
        return Result.success(tvDramaPlayService.page(query));
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id) {
        return Result.success(tvDramaPlayService.detail(id));
    }

    @GetMapping("/options")
    public Result options() {
        return Result.success(tvDramaPlayService.options());
    }

    /**
     * 电视剧播放量数据挖掘与可视化分析接口。
     */
    @GetMapping("/analysis")
    public Result analysis(TvDramaPlayQuery query) {
        return Result.success(tvDramaPlayService.analysis(query));
    }

    @PostMapping
    public Result create(@RequestBody TvDramaPlayData data) {
        tvDramaPlayService.create(data);
        return Result.success("新增电视剧播放量数据成功");
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody TvDramaPlayData data) {
        tvDramaPlayService.update(id, data);
        return Result.success("修改电视剧播放量数据成功");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        tvDramaPlayService.delete(id);
        return Result.success("删除电视剧播放量数据成功");
    }
}
