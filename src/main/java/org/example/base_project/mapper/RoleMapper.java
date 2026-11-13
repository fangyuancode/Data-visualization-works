package org.example.base_project.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.base_project.entity.Role;
import org.example.base_project.entity.RoleQuery;

import java.util.List;

public interface RoleMapper {
    List<Role> selectPage(RoleQuery query);

    Long selectCount(RoleQuery query);

    List<Role> selectAllEnabled();

    Role selectById(@Param("roleId") Integer roleId);

    Role selectByCode(@Param("roleCode") String roleCode);

    void insert(Role role);

    void update(Role role);

    void deleteById(@Param("roleId") Integer roleId);

    Integer countUserByRoleId(@Param("roleId") Integer roleId);
}
