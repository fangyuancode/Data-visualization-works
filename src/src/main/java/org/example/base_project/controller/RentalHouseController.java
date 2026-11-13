package org.example.base_project.controller;

import jakarta.annotation.Resource;
import org.example.base_project.common.Result;
import org.example.base_project.entity.RentalHouseData;
import org.example.base_project.entity.RentalHouseQuery;
import org.example.base_project.service.RentalHouseService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common/rental")
public class RentalHouseController {

    @Resource
    private RentalHouseService rentalHouseService;

    @GetMapping("/page")
    public Result page(RentalHouseQuery query) {
        return Result.success(rentalHouseService.page(query));
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id) {
        return Result.success(rentalHouseService.detail(id));
    }

    @GetMapping("/options")
    public Result options() {
        return Result.success(rentalHouseService.options());
    }

    @GetMapping("/analysis")
    public Result analysis(RentalHouseQuery query) {
        return Result.success(rentalHouseService.analysis(query));
    }

    @PostMapping
    public Result create(@RequestBody RentalHouseData data) {
        rentalHouseService.create(data);
        return Result.success("新增租房数据成功");
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody RentalHouseData data) {
        rentalHouseService.update(id, data);
        return Result.success("修改租房数据成功");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        rentalHouseService.delete(id);
        return Result.success("删除租房数据成功");
    }
}
