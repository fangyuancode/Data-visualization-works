package org.example.base_project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//告诉springboot 如何扫描包,注册为springboot容器里的某一个bean
@MapperScan("org.example.base_project.mapper")
public class BaseProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseProjectApplication.class, args);
    }

}
