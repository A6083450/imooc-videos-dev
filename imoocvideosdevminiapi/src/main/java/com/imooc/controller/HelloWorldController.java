package com.imooc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author erpljq
 * @date 2018/9/12
 */
@RestController
public class HelloWorldController {

    @RequestMapping("/")
    public String Hello(){
        return "hello spring boot~";
    }
}
