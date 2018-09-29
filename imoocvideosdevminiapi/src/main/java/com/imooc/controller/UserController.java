package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.UsersReport;
import com.imooc.pojo.vo.PublisherVideo;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author erpljq
 * @date 2018/9/14
 */
@RestController
@Api(value = "用户相关业务的接口", tags = {"用户相关业务的controller"})
@RequestMapping("/user")
public class UserController {

    @Value("${save-user-video-path}")
    private String saveUserVideoPath;

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户上传头像", notes = "用户上传头像接口")
    @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query")
    @PostMapping("/uploadFace")
    public IMoocJSONResult uploadFace(String userId,@RequestParam("file") MultipartFile[] files) throws Exception{

        if (StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id不能为空...");
        }

        // 文件保存的命名空间
        String fileSpace = saveUserVideoPath;
        // 保存到数据库中的相对路径
        String uploadPathDb = "/" + userId + "/face";
        // 声明一个文件输出流
        FileOutputStream fileOutputStream = null;
        // 声明一个文件输入流
        InputStream inputStream = null;
        try {
            //判断上传的文件组不等空, 并且文件组的长度大于0
            if (files != null && files.length > 0) {
                //获取上传的文件名
                String filename = files[0].getOriginalFilename();
                if (StringUtils.isNotBlank(filename)){
                    // 文件上传的最终保存路径
                    String finalFacePath = fileSpace + uploadPathDb + "/" + filename;
                    //设置数据库保存的路径
                    uploadPathDb += ("/" + filename);
                    //创建该文件
                    File outFile = new File(finalFacePath);
                    //判断该文件上一个目录存不存在, 并且上一个文件不等于文件, 如果是文件则创建该目录
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()){
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    // 设置输出到该文件
                    fileOutputStream = new FileOutputStream(outFile);
                    // 在上传的文件中读入到输入流
                    inputStream = files[0].getInputStream();
                    // 使用工具类把输入流传到输出流变为文件
                    IOUtils.copy(inputStream,fileOutputStream);
                }
            } else {
                return IMoocJSONResult.errorMsg("上传出错...");
            }
        }catch (Exception e){
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错...");
        } finally {
            //关闭流
            if (fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }

        Users user = new Users();
        //设置要更新用户图片的id
        user.setId(userId);
        //设置更新的图片路径
        user.setFaceImage(uploadPathDb);
        //执行更新的方法
        userService.updateUserInfo(user);

        return IMoocJSONResult.ok(uploadPathDb);
    }

    @ApiOperation(value = "查询用户信息", notes = "查询用户信息的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "视频用户的Id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "fanId", value = "用户的Id", required = true, dataType = "String", paramType = "query")
    })

    @PostMapping("/query")
    public IMoocJSONResult query(String userId, String fanId) throws Exception{
        //判断userId是不是为空
        if (StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id不能为空");
        }
        //根据userId查询
        Users user = userService.queryUserInfo(userId);
        //创建userVO的返回对象
        UsersVO usersVO = new UsersVO();
        //使用工具类将user对象的属性复制到userVO
        BeanUtils.copyProperties(user,usersVO);
        //是否已关注我
        usersVO.setFollow(userService.queryIfFollow(userId, fanId));
        //将usersVO以json方式返回
        return IMoocJSONResult.ok(usersVO);
    }

    @ApiOperation(value = "查询用户是否点赞视频", notes = "查询用户是否点赞视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginUserId", value = "用户的Id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "videoId", value = "视频的Id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "publishUserId", value = "视频发布者的Id", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/queryPublisher")
    public IMoocJSONResult queryPublisher(String loginUserId, String videoId, String publishUserId) throws Exception{
        //判断参数不能为空
        if (StringUtils.isBlank(publishUserId)){
            return IMoocJSONResult.errorMsg("");
        }
        //查询视频发布者的信息
        Users userInfo = userService.queryUserInfo(publishUserId);
        UsersVO publisher = new UsersVO();
        //使用bean工具类拷贝属性值
        BeanUtils.copyProperties(userInfo, publisher);
        //查询当前登录者和视频的点赞关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);

        PublisherVideo bean = new PublisherVideo();
        bean.setPublisher(publisher);
        bean.setUserLikeVideo(userLikeVideo);

        return IMoocJSONResult.ok(bean);

    }

    @ApiOperation(value = "用户关注", notes = "用户关注的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "被关注的用户Id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "fanId", value = "粉丝的Id", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/beyourfans")
    public IMoocJSONResult beyoufans(String userId, String fanId) throws Exception{
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)){
            return IMoocJSONResult.errorMsg("");
        }
        userService.saveUserFansRelation(userId, fanId);
        return IMoocJSONResult.ok("关注成功");
    }

    @ApiOperation(value = "用户取消关注", notes = "用户取消关注的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "被取消关注的用户Id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "fanId", value = "粉丝的Id", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/dontbeyourfans")
    public IMoocJSONResult dontbeyoufans(String userId, String fanId) throws Exception{
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)){
            return IMoocJSONResult.errorMsg("");
        }
        userService.deleteUserFansRelation(userId, fanId);
        return IMoocJSONResult.ok("取消关注成功");
    }

    @ApiOperation(value = "举报用户", notes = "举报用户的接口")
    @PostMapping("/reportUser")
    public IMoocJSONResult reportUser(@RequestBody UsersReport usersReport) throws Exception{

        //保存举报信息
        userService.reportUser(usersReport);
        return IMoocJSONResult.ok("举报成功...有你平台变得更美好");
    }

}
