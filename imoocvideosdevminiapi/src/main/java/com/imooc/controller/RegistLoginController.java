package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author erpljq
 * @date 2018/9/12
 */
@RestController
@Api(value = "用户注册登录的接口", tags = {"注册和登录的controller"})
public class RegistLoginController extends BasicController{

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册", notes = "用户注册的接口")
    @PostMapping("/regist")
    public IMoocJSONResult regist(@RequestBody Users user) throws Exception {
        // 1. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return IMoocJSONResult.errorMsg("用户名和密码不能为空");
        }
        // 2. 判断用户名是否存在
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
        // 3. 保存用户, 注册信息
        if(!usernameIsExist){
            user.setNickname(user.getUsername());
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            user.setFansCounts(0);
            user.setReceiveLikeCounts(0);
            user.setFollowCounts(0);
            userService.saveUser(user);
        } else {
            return IMoocJSONResult.errorMsg("用户名已经存在, 请换一个再试");
        }
        user.setPassword("");

//        String uniqueToken = UUID.randomUUID().toString();
//        redis.set(USER_REDIS_SESSION + ":" + user.getId(), uniqueToken, 1000 * 60 * 30);
//
//        UsersVO usersVO = new UsersVO();
//        BeanUtils.copyProperties(user, usersVO);
//        usersVO.setUserToken(uniqueToken);

        UsersVO usersVO = serUserRedisSessionToken(user);

        return IMoocJSONResult.ok(usersVO);
    }

    public UsersVO serUserRedisSessionToken(Users userModel){
        String uniqueToken = UUID.randomUUID().toString();
        redis.set(USER_REDIS_SESSION + ":" + userModel.getId(), uniqueToken, 1000 * 60 * 30);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userModel, usersVO);
        usersVO.setUserToken(uniqueToken);
        return usersVO;
    }

    @ApiOperation(value = "用户登录", notes = "用户登录的接口")
    @PostMapping("/login")
    public IMoocJSONResult login(@RequestBody Users user) throws Exception {
        // 1. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){
            return IMoocJSONResult.errorMsg("用户名和密码不能为空");
        }
        //查询用户是否存在
        Users result = userService.queryUserForLogin(user.getUsername(),MD5Utils.getMD5Str(user.getPassword()));
        // 2. 判断用户名是否存在
        if (ObjectUtils.isEmpty(result)){
            return IMoocJSONResult.errorMsg("用户名或密码不正确, 请重试...");
        }
        result.setPassword("");
        UsersVO usersVO = serUserRedisSessionToken(result);
        return IMoocJSONResult.ok(usersVO);
    }

    @ApiOperation(value = "用户注销", notes = "用户注销的接口")
    @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query")
    @PostMapping("/logout")
    public IMoocJSONResult logout(String userId) throws Exception {
        //清除redis缓存中的key
        redis.del(USER_REDIS_SESSION + ":" + userId);
        return IMoocJSONResult.ok();
    }

}
