package org.example.base_project.mapper;

import org.apache.ibatis.annotations.Select;
import org.example.base_project.entity.CommonUser;

import java.util.List;

public interface CommonMapper {
    @Select("select  * from sys_user")
    List<CommonUser> selectAll();
}
