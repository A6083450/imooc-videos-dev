package com.imooc.mapper;

import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideosMapperCustom extends MyMapper<Videos> {

    public List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc, @Param("userId") String userId);

    /**
     * 对视频喜欢的数量进行累计
     * @param videoId
     */
    public void addVideoLikeCount(String videoId);

    /**
     * 对视频喜欢的数量进行累减
     * @param videoId
     */
    public void reduceVideoLikeCount(String videoId);

    /**
     * 查询我的喜欢视频作品
     * @param userId
     * @return
     */
    List<VideosVO> queryMyLikeVideos(String userId);

    /**
     * 查询我关注的人发的视频
     * @param userId
     * @return
     */
    List<VideosVO> queryMyFollowVideos(String userId);
}