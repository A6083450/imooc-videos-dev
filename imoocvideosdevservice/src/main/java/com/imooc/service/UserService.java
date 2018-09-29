package com.imooc.service;

import com.imooc.pojo.Users;
import com.imooc.pojo.UsersReport;

/**
 * @author erpljq
 * @date 2018/9/12
 */
public interface UserService {

    /**
     * @param username 判断用户名是否存在
     * @return
     */
    boolean queryUsernameIsExist(String username);

    /**
     * @param user  保存用户(用户注册)
     */
    void saveUser(Users user);

    /**
     * @param username    用户登录, 根据用户名和密码查询用户
     * @param password
     * @return
     */
    Users queryUserForLogin(String username,String password) throws Exception;

    /**
     * 用户修改信息
     * @param user
     */
    void updateUserInfo(Users user);

    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    Users queryUserInfo(String userId);

    /**
     * 查询用户是否喜欢点赞视频
     * @param userId
     * @param videoId
     * @return
     */
    public boolean isUserLikeVideo(String userId, String videoId);

    /**
     * 增加用户和粉丝的关系
     * @param userId
     * @param fanId
     */
    public void saveUserFansRelation(String userId, String fanId);

    /**
     * 删除用户和粉丝的关系
     * @param userId
     * @param fanId
     */
    public void deleteUserFansRelation(String userId, String fanId);

    /**
     * 查询用户是否关注
     * @param userId
     * @param fanId
     * @return
     */
    public boolean queryIfFollow(String userId, String fanId);

    /**
     * 举报用户
     * @param usersReport
     */
    void reportUser(UsersReport usersReport);
}
