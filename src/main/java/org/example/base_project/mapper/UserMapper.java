package org.example.base_project.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.base_project.entity.Account;
import org.example.base_project.entity.LoginUserResult;
import org.example.base_project.entity.User;
import org.example.base_project.entity.UserListItem;
import org.example.base_project.entity.UserQuery;

import java.util.List;

public interface UserMapper {
    User selectByName(Account account);

    User selectById(@Param("id") Integer id);

    void insert(User user);

    void insertUser(User user);

    void updateUser(User user);

    void deleteById(@Param("id") Integer id);

    void updateStatus(@Param("id") Integer id, @Param("status") String status);

    void assignRole(@Param("id") Integer id, @Param("roleId") Integer roleId);

    void updateIp(LoginUserResult safeUser);

    List<UserListItem> selectPage(UserQuery query);

    Long selectCount(UserQuery query);
}
