package com.imooc.mapper;

import com.imooc.pojo.Comments;
import com.imooc.pojo.vo.CommentsVO;
import com.imooc.utils.MyMapper;

import java.util.List;

/**
 * @author erpljq
 * @date 2018/9/27
 */
public interface CommentsMapperCustom extends MyMapper<Comments> {

    public List<CommentsVO> queryComments(String videoId);
}
