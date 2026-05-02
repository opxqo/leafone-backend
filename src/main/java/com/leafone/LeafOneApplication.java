package com.leafone;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.leafone.*.mapper")
public class LeafOneApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeafOneApplication.class, args);
    }
}
