package org.example.base_project.controller;

import jakarta.annotation.Resource;
import org.example.base_project.common.Result;
import org.example.base_project.entity.User;
import org.example.base_project.entity.UserQuery;
import org.example.base_project.entity.UserSaveRequest;
import org.example.base_project.exception.CustomException;
import org.example.base_project.service.UserService;
import org.example.base_project.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/info")
    public Result info() {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null || currentUser.getId() == null) {
            throw new CustomException("401", "登录状态已失效，请重新登录");
        }
        return Result.success(userService.buildUserInfo(currentUser));
    }

    @GetMapping("/list")
    public Result list(UserQuery query) {
        return Result.success(userService.page(query));
    }

    @PostMapping
    public Result create(@RequestBody UserSaveRequest request) {
        userService.createUser(request);
        return Result.success("新增用户成功");
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Integer id, @RequestBody UserSaveRequest request) {
        userService.updateUser(id, request);
        return Result.success("修改用户成功");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        userService.deleteUser(id);
        return Result.success("删除用户成功");
    }

    @PutMapping("/{id}/status")
    public Result updateStatus(@PathVariable Integer id, @RequestParam String status) {
        userService.updateStatus(id, status);
        return Result.success("用户状态修改成功");
    }

    @PutMapping("/{id}/role")
    public Result assignRole(@PathVariable Integer id, @RequestParam Integer roleId) {
        userService.assignRole(id, roleId);
        return Result.success("角色分配成功");
    }
}
