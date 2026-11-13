package org.example.base_project.controller;

import jakarta.annotation.Resource;
import org.example.base_project.common.Result;
import org.example.base_project.entity.Role;
import org.example.base_project.entity.RoleQuery;
import org.example.base_project.service.RoleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @GetMapping("/list")
    public Result list(RoleQuery query) {
        return Result.success(roleService.page(query));
    }

    @GetMapping("/all")
    public Result all() {
        return Result.success(roleService.allEnabled());
    }

    @PostMapping
    public Result create(@RequestBody Role role) {
        roleService.create(role);
        return Result.success("新增角色成功");
    }

    @PutMapping("/{roleId}")
    public Result update(@PathVariable Integer roleId, @RequestBody Role role) {
        role.setRoleId(roleId);
        roleService.update(role);
        return Result.success("修改角色成功");
    }

    @DeleteMapping("/{roleId}")
    public Result delete(@PathVariable Integer roleId) {
        roleService.delete(roleId);
        return Result.success("删除角色成功");
    }
}
