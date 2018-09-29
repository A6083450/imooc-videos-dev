package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mapper.*;
import com.imooc.pojo.Comments;
import com.imooc.pojo.SearchRecords;
import com.imooc.pojo.UsersLikeVideos;
import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.CommentsVO;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.service.VideoService;
import com.imooc.utils.PagedResult;
import com.imooc.utils.TimeAgoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.Date;
import java.util.List;

/**
 * @author erpljq
 * @date 2018/9/19
 */
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private CommentsMapperCustom commentsMapperCustom;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveVideo(Videos video) {
        String id = sid.nextShort();
        //设置主键
        video.setId(id);
        //保存视频信息到数据库
        videosMapper.insertSelective(video);
        return id;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateVideo(String videoId, String coverPath) {
        Videos videos = new Videos();
        videos.setId(videoId);
        videos.setCoverPath(coverPath);
        videosMapper.updateByPrimaryKeySelective(videos);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult getAllVideos(Videos videos, Integer isSaveRecord, Integer page, Integer pageSize) {
        //获取热搜值
        String desc = videos.getVideoDesc();
        //获取用户的Id
        String userId = videos.getUserId();
        //判断是否需要保存
        if (isSaveRecord != null && isSaveRecord == 1){
            SearchRecords records = new SearchRecords();
            //设置主键
            records.setId(sid.nextShort());
            //设置热搜的内容
            records.setContent(desc);
            searchRecordsMapper.insert(records);
        }
        //使用pageHelper分页
        PageHelper.startPage(page, pageSize);
        //如果没有PageHelper.startPage(page, pageSize)则下面的查询为全部查询
        List<VideosVO> queryAllVideos = videosMapperCustom.queryAllVideos(desc, userId);
        //获取分页的详细信息
        PageInfo<VideosVO> pageInfo = new PageInfo<>(queryAllVideos);
        //创建返回的分页对象
        PagedResult pagedResult = new PagedResult();
        //设置第几页
        pagedResult.setPage(page);
        //设置总页数
        pagedResult.setTotal(pageInfo.getPages());
        //设置返回的数据
        pagedResult.setRow(queryAllVideos);
        //设置数据总记录数
        pagedResult.setRecords(pageInfo.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotwords() {
        //获取热搜词
        return searchRecordsMapper.getHotwords();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideo(String userId, String VideoId, String videoCreateId) {
        //1.保存用户和视频的喜欢点赞关联关系表
        String likeId = sid.nextShort();
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
        //设置主键
        usersLikeVideos.setId(likeId);
        //设置用户ID
        usersLikeVideos.setUserId(userId);
        //设置视频ID
        usersLikeVideos.setVideoId(VideoId);
        int insert = usersLikeVideosMapper.insert(usersLikeVideos);

        //2.视频喜欢数量累加
        videosMapperCustom.addVideoLikeCount(userId);

        //3.用户受喜欢数量的累加
        usersMapper.addReceiveLikeCount(userId);
    }

    @Override
    public void userUnLikeVideo(String userId, String VideoId, String videoCreateId) {
        //1.删除用户和视频的喜欢点赞关联关系表

        Example example = new Example(UsersLikeVideos.class);
        Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",VideoId);

        usersLikeVideosMapper.deleteByExample(example);

        //2.视频喜欢数量累加
        videosMapperCustom.reduceVideoLikeCount(userId);

        //3.用户受喜欢数量的累加
        usersMapper.reduceReceiveLikeCount(userId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {
        //使用pageHelper分页
        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyLikeVideos(userId);

        PageInfo<VideosVO> pageList = new PageInfo<>(list);
        PagedResult pagedResult = new PagedResult();
        //设置总页数
        pagedResult.setTotal(pageList.getPages());
        //设置查询的数据
        pagedResult.setRow(list);
        //设置当前页
        pagedResult.setPage(page);
        //设置总记录数
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize) {
        //使用pageHelper分页
        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyFollowVideos(userId);

        PageInfo<VideosVO> pageList = new PageInfo<>(list);
        PagedResult pagedResult = new PagedResult();
        //设置总页数
        pagedResult.setTotal(pageList.getPages());
        //设置查询的数据
        pagedResult.setRow(list);
        //设置当前页
        pagedResult.setPage(page);
        //设置总记录数
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComment(Comments comment) {
        String id = this.sid.nextShort();
        //设置id
        comment.setId(id);
        //设置创建时间
        comment.setCreateTime(new Date());
        //保存到数据库
        commentsMapper.insert(comment);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {
        //使用pageHelper分页
        PageHelper.startPage(page, pageSize);
        //进行查询返回list数据
        List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);
        //将每个时间格式化
        for (CommentsVO c : list){
            String timeAgo = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(timeAgo);
        }
        //获取pageHelper分页对象
        PageInfo<CommentsVO> pageList = new PageInfo<>(list);
        PagedResult grid = new PagedResult();
        //设置总页数
        grid.setTotal(pageList.getPages());
        //设置返回的数据
        grid.setRow(list);
        //设置总记录数
        grid.setRecords(pageList.getTotal());

        return grid;
    }
}
