package com.imooc.service;

import com.imooc.pojo.Bgm;

import java.util.List;

/**
 * @author erpljq
 * @date 2018/9/18
 */
public interface BgmService {

    /**
     * 查询背景音乐列表
     * @return
     */
    List<Bgm> queryBgmList();

    Bgm queryBgmById(String bgmId);
}
