package org.example.base_project.service;


import jakarta.annotation.Resource;
import org.example.base_project.entity.CommonUser;
import org.example.base_project.mapper.CommonMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommonService {
    @Resource
   private CommonMapper commonMapper;
    public List<CommonUser> selectAll() {
        List<CommonUser> list=commonMapper.selectAll();
        return list;
    }

}
