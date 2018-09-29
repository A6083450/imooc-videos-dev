package com.imooc.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author erpljq
 * @date 2018/9/14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisOperatorTest {

    @Autowired
    private RedisOperator redis;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void test(){
        redisTemplate.opsForValue().set("sb","bs");
    }

}