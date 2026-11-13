package org.example.base_project.service;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.example.base_project.entity.PageResult;
import org.example.base_project.entity.Role;
import org.example.base_project.entity.RoleQuery;
import org.example.base_project.exception.CustomException;
import org.example.base_project.mapper.RoleMapper;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class RoleService {

    @Resource
    private RoleMapper roleMapper;

    public PageResult<Role> page(RoleQuery query) {
        normalizePage(query);
        List<Role> records = roleMapper.selectPage(query);
        Long total = roleMapper.selectCount(query);
        return new PageResult<>(records, query.getCurrent(), query.getSize(), total);
    }

    public List<Role> allEnabled() {
        return roleMapper.selectAllEnabled();
    }

    public Role selectById(Integer roleId) {
        if (roleId == null) {
            return null;
        }
        return roleMapper.selectById(roleId);
    }

    public void create(Role role) {
        validateRole(role, false);
        Role exists = roleMapper.selectByCode(role.getRoleCode());
        if (exists != null) {
            throw new CustomException("500", "角色编码已存在");
        }
        String now = now();
        role.setCreateTime(now);
        role.setUpdateTime(now);
        if (role.getEnabled() == null) {
            role.setEnabled(true);
        }
        roleMapper.insert(role);
    }

    public void update(Role role) {
        validateRole(role, true);
        Role dbRole = roleMapper.selectById(role.getRoleId());
        if (dbRole == null) {
            throw new CustomException("500", "角色不存在");
        }

        Role sameCodeRole = roleMapper.selectByCode(role.getRoleCode());
        if (sameCodeRole != null && !sameCodeRole.getRoleId().equals(role.getRoleId())) {
            throw new CustomException("500", "角色编码已存在");
        }

        role.setUpdateTime(now());
        if (role.getEnabled() == null) {
            role.setEnabled(true);
        }
        roleMapper.update(role);
    }

    public void delete(Integer roleId) {
        if (roleId == null) {
            throw new CustomException("400", "请选择要删除的角色");
        }
        if (roleId == 1 || roleId == 2 || roleId == 3) {
            throw new CustomException("500", "系统内置角色不允许删除");
        }
        Integer userCount = roleMapper.countUserByRoleId(roleId);
        if (userCount != null && userCount > 0) {
            throw new CustomException("500", "该角色已分配给用户，请先调整用户角色后再删除");
        }
        roleMapper.deleteById(roleId);
    }

    private void validateRole(Role role, boolean update) {
        if (role == null) {
            throw new CustomException("400", "角色信息不能为空");
        }
        if (update && role.getRoleId() == null) {
            throw new CustomException("400", "角色ID不能为空");
        }
        if (StrUtil.isBlank(role.getRoleName())) {
            throw new CustomException("400", "请输入角色名称");
        }
        if (StrUtil.isBlank(role.getRoleCode())) {
            throw new CustomException("400", "请输入角色编码");
        }
    }

    private void normalizePage(RoleQuery query) {
        if (query.getCurrent() == null || query.getCurrent() < 1) {
            query.setCurrent(1);
        }
        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(10);
        }
    }

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
