package org.example.base_project.controller;

import jakarta.annotation.Resource;
import org.example.base_project.common.Result;
import org.example.base_project.entity.TeenMentalHealth;
import org.example.base_project.entity.TeenMentalHealthQuery;
import org.example.base_project.service.TeenMentalHealthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common/teen")
public class TeenMentalHealthController {

    @Resource
    private TeenMentalHealthService teenMentalHealthService;

    @GetMapping("/page")
    public Result page(TeenMentalHealthQuery query) {
        return Result.success(teenMentalHealthService.page(query));
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id) {
        return Result.success(teenMentalHealthService.detail(id));
    }

    @GetMapping("/options")
    public Result options() {
        return Result.success(teenMentalHealthService.options());
    }

    @GetMapping("/analysis")
    public Result analysis(TeenMentalHealthQuery query) {
        return Result.success(teenMentalHealthService.analysis(query));
    }

    @PostMapping
    public Result create(@RequestBody TeenMentalHealth data) {
        teenMentalHealthService.create(data);
        return Result.success("新增青少年心理健康数据成功");
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody TeenMentalHealth data) {
        teenMentalHealthService.update(id, data);
        return Result.success("修改青少年心理健康数据成功");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        teenMentalHealthService.delete(id);
        return Result.success("删除青少年心理健康数据成功");
    }
}