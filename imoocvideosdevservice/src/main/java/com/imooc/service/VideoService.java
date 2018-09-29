package com.imooc.service;

import com.imooc.pojo.Comments;
import com.imooc.pojo.Videos;
import com.imooc.utils.PagedResult;

import java.util.List;

/**
 * @author erpljq
 * @date 2018/9/19
 */
public interface VideoService {

    /**
     * 保存视频
     * @param video
     */
    String saveVideo(Videos video);

    /**
     * 修改视频的封面
     * @param videoId
     * @param coverPath
     */
    void updateVideo(String videoId, String coverPath);

    /**
     * 分页查询视频列表
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult getAllVideos(Videos videos, Integer isSaveRecord, Integer page, Integer pageSize);

    /**
     * 获取热搜词列表
     * @return
     */
    List<String> getHotwords();

    /**
     * 用户喜欢视频/用户点赞视频
     * @param userId
     * @param VideoId
     * @param videoCreateId
     */
    void userLikeVideo(String userId, String VideoId, String videoCreateId);

    /**
     * 用户不喜欢/取消点赞
     * @param userId
     * @param VideoId
     * @param videoCreateId
     */
    void userUnLikeVideo(String userId, String VideoId, String videoCreateId);

    /**
     * 查询我的喜欢视频作品
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize);

    /**
     * 查询我关注的人发的视频
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize);

    /**
     * 保存留言
     * @param comment
     */
    void saveComment(Comments comment);

    /**
     * 留言分页
     * @param videoId
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult getAllComments(String videoId, Integer page, Integer pageSize);
}
