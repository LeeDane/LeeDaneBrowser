package com.cn.leedane.browser.task;

/**
 * 任务枚举类
 */
public enum TaskType {
    LOGIN_DO,     //登录
    REGISTER_DO, //注册
    LOADNETWORK_BLOG_IMAGE, //加载博客的网络图片
    IS_SIGN_IN, //判断是否签到
    DO_SIGN_IN, //签到
    DO_GALLERY, //加载图库
    LOAD_CHAT_BG_SELECT_WEB,//获取聊天背景的选择图片
    DO_LOGIN_PHONE, //手机登录
    DO_GET_LOGIN_CODE, //获取手机登录的验证码
    GET_APP_VERSION, //检查APP版本
    LOAD_USER_INFO_DATA, //加载用户的基本数据
    LOAD_HEAD_PATH, //加载用户新头像
    FANYI, //翻译
    QINIU_TOKEN, //七牛云存储token
    SCAN_LOGIN, //扫码登陆
    CANCEL_SCAN_LOGIN, //取消二维码登录
}
