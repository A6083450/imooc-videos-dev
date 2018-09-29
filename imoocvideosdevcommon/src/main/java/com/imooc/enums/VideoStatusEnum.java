package com.imooc.enums;

/**
 * @author erpljq
 * @date 2018/9/19
 */
public enum VideoStatusEnum {

    SUCCESS(1),     //发布成功
    FORBIN(2);      //禁止发布, 管理员操作

    public final int value;

    VideoStatusEnum(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
