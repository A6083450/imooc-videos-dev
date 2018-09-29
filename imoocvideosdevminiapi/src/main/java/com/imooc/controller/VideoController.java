package com.imooc.controller;

import com.imooc.enums.VideoStatusEnum;
import com.imooc.pojo.Bgm;
import com.imooc.pojo.Comments;
import com.imooc.pojo.Videos;
import com.imooc.service.BgmService;
import com.imooc.service.VideoService;
import com.imooc.utils.FetchVideoCover;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MergeVideoMp3;
import com.imooc.utils.PagedResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * @author erpljq
 * @date 2018/9/18
 */
@Api(value = "视频相关业务的接口", tags = {"视频相关业务的controller"})
@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;

    @Value("${save-user-video-path}")
    private String saveUserVideoPath;

    @Value("${ffmpeg-path}")
    private String ffmpegPath;

    @ApiOperation(value = "上传视频", notes = "上传视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "bgmId", value = "背景音乐id", required = false, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds", value = "背景音乐的播放长度",
                    required = true, dataType = "double", paramType = "from"),
            @ApiImplicitParam(name = "videoWidth", value = "视频的宽度", required = true, dataType = "int", paramType = "form"),
            @ApiImplicitParam(name = "videoHeight", value = "视频的高度", required = true, dataType = "int", paramType = "form"),
            @ApiImplicitParam(name = "desc", value = "视频的描述", required = false, dataType = "String", paramType = "form"),
    })
    @PostMapping(value ="/upload", headers = {"content-type=multipart/form-data"})
    public IMoocJSONResult upload(String userId, String bgmId, double videoSeconds, int videoWidth,
                                  int videoHeight, String desc,
                                  @ApiParam(value = "短视频", required = true) MultipartFile file) throws Exception{

        if (StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id不能为空");
        }
        // 文件保存的命名空间
        String fileSpace = saveUserVideoPath;
        // 保存到数据库中的相对路径
        String uploadPathDb = "/" + userId + "/video";
        // 保存到数据库中的相对路径
        String coverPathDb = "/" + userId + "/video";
        // 声明一个文件输出流
        FileOutputStream fileOutputStream = null;
        // 声明一个文件输入流
        InputStream inputStream = null;
        // 文件上传的最终保存路径
        String finalVideoPath = "";
        try {
            //判断上传的文件组不等空, 并且文件组的长度大于0
            if (file != null) {
                //获取上传的文件名
                String filename = file.getOriginalFilename();
                //根据上传的文件名以"."分割获取文件名的前缀
                String fileNamePrefix = filename.split("\\.")[0];
                if (StringUtils.isNotBlank(filename)){
                    //文件上传的最终保存路径
                    finalVideoPath = fileSpace + uploadPathDb + "/" + filename;
                    //设置数据库保存的路径
                    uploadPathDb += ("/" + filename);
                    //设置视频封面相对路径
                    coverPathDb = coverPathDb + "/" + fileNamePrefix + ".jpg";
                    //创建该文件
                    File outFile = new File(finalVideoPath);
                    //判断该文件上一个目录存不存在, 并且上一个文件不等于文件, 如果是文件则创建该目录
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()){
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    // 设置输出到该文件
                    fileOutputStream = new FileOutputStream(outFile);
                    // 在上传的文件中读入到输入流
                    inputStream = file.getInputStream();
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

        //判断bgmId是否为空, 如果不为空, 那就查询bgm的信息, 并且合并视频, 生成新的视频
        if (StringUtils.isNotBlank(bgmId)){
            //查询用户所选的背景音乐
            Bgm bgm = bgmService.queryBgmById(bgmId);
            //获取用户所选背景音乐所在磁盘的路径
            String mp3InputPath = fileSpace + bgm.getPath();
            //获取FFMpeg操作视频音频文件的路径
            String ffmpegEXE = ffmpegPath;
            //调用工具类合并视频和背景音乐
            MergeVideoMp3 tool = new MergeVideoMp3(ffmpegEXE);
            //获取上传视频的磁盘文件路径
            String videoInputPath = finalVideoPath;
            //新视频的文件名
            String videoOutPutName = UUID.randomUUID().toString() + ".mp4";
            //新视频保存到数据库的相对路径
            uploadPathDb = "/" + userId + "/video" + "/" + videoOutPutName;
            //新视频最终的保存路径
            finalVideoPath = fileSpace + uploadPathDb;
            //执行该方法合并视频
            tool.converter(videoInputPath, mp3InputPath, videoSeconds,finalVideoPath);
        }

        //对视频进行截图
        FetchVideoCover videoCover = new FetchVideoCover(ffmpegPath);
        videoCover.getCover(finalVideoPath, saveUserVideoPath + coverPathDb);

        //保存视频信息到数据库
        Videos videos = new Videos();
        //设置用户选择背景音乐的id
        videos.setAudioId(bgmId);
        //设置用户id
        videos.setUserId(userId);
        //设置视频的长度
        videos.setVideoSeconds((float) videoSeconds);
        //设置视频高度
        videos.setVideoHeight(videoHeight);
        //设置视频宽度
        videos.setVideoWidth(videoWidth);
        //设置视频的描述
        videos.setVideoDesc(desc);
        //设置视频最终的保存路径
        videos.setVideoPath(uploadPathDb);
        //设置视频封面的保存路径
        videos.setCoverPath(coverPathDb);
        //设置视频的状态
        videos.setStatus(VideoStatusEnum.SUCCESS.value);
        //设置视频的创建时间
        videos.setCreateTime(new Date());
        //执行保存视频对象方法并获取返回的主键
        String videoId = videoService.saveVideo(videos);

        return IMoocJSONResult.ok(videoId);
    }

    @ApiOperation(value = "上传封面", notes = "上传封面的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "视频主键id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form")
    })
    @PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
    public IMoocJSONResult uploadCover(String videoId, String userId,
                                       @ApiParam(value = "视频封面", required = true) MultipartFile file) throws Exception{

        if (StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("视频id不能为空...");
        }
        // 文件保存的命名空间
        String fileSpace = saveUserVideoPath;
        // 保存到数据库中的相对路径
        String uploadPathDb = "/" + userId + "/video";
        // 声明一个文件输出流
        FileOutputStream fileOutputStream = null;
        // 声明一个文件输入流
        InputStream inputStream = null;
        // 文件上传的最终保存路径
        String finalCoverPath = "";
        try {
            //判断上传的文件组不等空, 并且文件组的长度大于0
            if (file != null) {
                //获取上传的文件名
                String filename = file.getOriginalFilename();
                if (StringUtils.isNotBlank(filename)){
                    //文件上传的最终保存路径
                    finalCoverPath = fileSpace + uploadPathDb + "/" + filename;
                    //设置数据库保存的路径
                    uploadPathDb += ("/" + filename);
                    //创建该文件
                    File outFile = new File(finalCoverPath);
                    //判断该文件上一个目录存不存在, 并且上一个文件不等于文件, 如果是文件则创建该目录
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()){
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    // 设置输出到该文件
                    fileOutputStream = new FileOutputStream(outFile);
                    // 在上传的文件中读入到输入流
                    inputStream = file.getInputStream();
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
        //更新视频的封面路径
        videoService.updateVideo(videoId,uploadPathDb);

        return IMoocJSONResult.ok();
    }

    /**
     * 分页和搜索查询视频列表
     * 1 -  需要保持
     * 0 -  不需要保持, 或者为空的时候
     *
     */
    @ApiOperation(value = "查询视频", notes = "查询视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", required = false, dataType = "Int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示的数量", required = false, dataType = "Int", paramType = "query")
    })
    @PostMapping(value = "/showAll")
    public IMoocJSONResult showAll(@RequestBody Videos videos, Integer isSaveRecord,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "5") Integer pageSize) throws Exception{

        //根据分页查询所有视频
        PagedResult result = videoService.getAllVideos(videos, isSaveRecord, page, pageSize);

        return IMoocJSONResult.ok(result);
    }

    @ApiOperation(value = "热搜关键字", notes = "热搜关键字查询的接口")
    @PostMapping(value = "/hot")
    public IMoocJSONResult hot(){
        return IMoocJSONResult.ok(videoService.getHotwords());
    }

    @ApiOperation(value = "用户点赞", notes = "用户点赞的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "videoId", value = "视频Id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "videoCreaterId", value = "视频创建Id", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping(value = "/userLike")
    public IMoocJSONResult userLike(String userId, String videoId, String videoCreaterId) throws Exception{
        //调用视频点赞方法
        videoService.userLikeVideo(userId, videoId, videoCreaterId);
        return IMoocJSONResult.ok();
    }


    @ApiOperation(value = "用户取消点赞", notes = "用户取消点赞的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "videoId", value = "视频Id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "videoCreaterId", value = "视频创建Id", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping(value = "/userUnLike")
    public IMoocJSONResult userUnLike(String userId, String videoId, String videoCreaterId){
        //调用视频点赞方法
        videoService.userUnLikeVideo(userId, videoId, videoCreaterId);
        return IMoocJSONResult.ok();
    }

    @ApiOperation(value = "分页和搜索查询视频列表", notes = "分页和搜索查询视频列表的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "第几页", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示多少条", required = false, dataType = "String", paramType = "query")
    })
    @PostMapping("/showMyLike")
    public IMoocJSONResult showMyLike(String userId,
                                      @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "5") Integer pageSize){
        //判断用户Id
        if (StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户Id不能为空");
        }
        PagedResult videoList = videoService.queryMyLikeVideos(userId, page, pageSize);
        return IMoocJSONResult.ok(videoList);
    }

    @ApiOperation(value = "我关注的人发的视频", notes = "我关注的人发的视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "第几页", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示多少条", required = false, dataType = "String", paramType = "query")
    })
    @PostMapping("/showMyFollow")
    public IMoocJSONResult showMyFollow(String userId,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "5") Integer pageSize) throws Exception{
        //判断用户Id
        if (StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户Id不能为空");
        }
        PagedResult videoList = videoService.queryMyFollowVideos(userId, page, pageSize);
        return IMoocJSONResult.ok(videoList);
    }

    @ApiOperation(value = "保存留言", notes = "保存留言的接口")
    @PostMapping("/saveComment")
    public IMoocJSONResult saveComment(@RequestBody Comments comment, String fatherCommentId, String toUserId) throws Exception {
        comment.setFatherCommentId(fatherCommentId);
        comment.setToUserId(toUserId);
        videoService.saveComment(comment);
        return IMoocJSONResult.ok();
    }

    @PostMapping("/getVideoComments")
    public IMoocJSONResult getVideoComment(String videoId,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize) throws Exception{
        if (StringUtils.isBlank(videoId)){
            return IMoocJSONResult.errorMsg("视频id不能为空");
        }
        PagedResult list = videoService.getAllComments(videoId, page, pageSize);
        return IMoocJSONResult.ok(list);
    }
}
