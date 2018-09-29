package com.imooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author erpljq
 * @date 2018/9/11
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.imooc", "org.n3r.idworker"})
@MapperScan(basePackages = {"com.imooc.mapper"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }
}
