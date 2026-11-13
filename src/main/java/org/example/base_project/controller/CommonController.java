package org.example.base_project.controller;

import jakarta.annotation.Resource;
import org.example.base_project.common.Result;
import org.example.base_project.entity.CommonUser;
import org.example.base_project.service.CommonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//添加注解，RestController 对外提供数据接口的类 标注该类是一个接口，返回json数据，
@RestController
@RequestMapping("/api/common")
public class CommonController {
    @Resource
    private CommonService commonService;

    @GetMapping("/selectAll")
    public Result selectAll() {
        List<CommonUser> list = commonService.selectAll();
        return Result.success(list);
    }
}
