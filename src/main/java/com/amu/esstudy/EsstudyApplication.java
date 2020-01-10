package com.amu.esstudy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.amu.esstudy.mapper")
public class EsstudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsstudyApplication.class, args);
    }

}
