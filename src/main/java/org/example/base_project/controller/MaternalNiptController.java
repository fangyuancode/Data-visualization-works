package org.example.base_project.controller;

import jakarta.annotation.Resource;
import org.example.base_project.common.Result;
import org.example.base_project.entity.MaternalNiptData;
import org.example.base_project.entity.MaternalNiptQuery;
import org.example.base_project.service.MaternalNiptService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common/nipt")
public class MaternalNiptController {

    @Resource
    private MaternalNiptService maternalNiptService;

    @GetMapping("/page")
    public Result page(MaternalNiptQuery query) {
        return Result.success(maternalNiptService.page(query));
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id) {
        return Result.success(maternalNiptService.detail(id));
    }

    @GetMapping("/options")
    public Result options() {
        return Result.success(maternalNiptService.options());
    }

    /**
     * NIPT检测数据可视化分析接口
     */
    @GetMapping("/analysis")
    public Result analysis(MaternalNiptQuery query) {
        return Result.success(maternalNiptService.analysis(query));
    }

    @PostMapping
    public Result create(@RequestBody MaternalNiptData data) {
        maternalNiptService.create(data);
        return Result.success("新增NIPT检测数据成功");
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody MaternalNiptData data) {
        maternalNiptService.update(id, data);
        return Result.success("修改NIPT检测数据成功");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        maternalNiptService.delete(id);
        return Result.success("删除NIPT检测数据成功");
    }
}