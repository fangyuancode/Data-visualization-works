package org.example.base_project.controller;

import jakarta.annotation.Resource;
import org.example.base_project.common.Result;
import org.example.base_project.entity.ConsumerShoppingTrend;
import org.example.base_project.entity.ConsumerShoppingTrendQuery;
import org.example.base_project.service.ConsumerShoppingTrendService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common/consumer-shopping")
public class ConsumerShoppingTrendController {

    @Resource
    private ConsumerShoppingTrendService consumerShoppingTrendService;

    @GetMapping("/page")
    public Result page(ConsumerShoppingTrendQuery query) {
        return Result.success(consumerShoppingTrendService.page(query));
    }

    @GetMapping("/options")
    public Result options() {
        return Result.success(consumerShoppingTrendService.options());
    }

    /**
     * 消费者购物趋势可视化分析接口。
     * 复用列表页的筛选条件，支持按性别、城市等级、购物偏好、年龄、收入、线上订单数过滤。
     */
    @GetMapping("/analysis")
    public Result analysis(ConsumerShoppingTrendQuery query) {
        return Result.success(consumerShoppingTrendService.analysis(query));
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id) {
        return Result.success(consumerShoppingTrendService.detail(id));
    }

    @PostMapping
    public Result create(@RequestBody ConsumerShoppingTrend item) {
        consumerShoppingTrendService.create(item);
        return Result.success("新增消费者购物数据成功");
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody ConsumerShoppingTrend item) {
        consumerShoppingTrendService.update(id, item);
        return Result.success("修改消费者购物数据成功");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        consumerShoppingTrendService.delete(id);
        return Result.success("删除消费者购物数据成功");
    }
}
