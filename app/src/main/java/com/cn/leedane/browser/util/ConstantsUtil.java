package com.cn.leedane.browser.util;

/**
 * 常量类
 */
public class ConstantsUtil {

    private ConstantsUtil(){};

    /**
     * 默认请求的服务器地址
     */
    //public static final String DEFAULT_SERVER_URL = "http://leedane.tunnel.mobi/";
    //public static final String DEFAULT_SERVER_URL = "http://192.168.16.118:8080/";
    public static final String DEFAULT_SERVER_URL = "http://leedanetf.tunnel.2bdata.com/leedaneMVC/";
    //public static final String DEFAULT_SERVER_URL = "http://192.168.1.105:8080/";

    /**
     * 服务器上的app下载地址
     */
    public static final String WEB_APP_DOWNLOAD_PATH = "page/download.jsp";
    /**
     * 七牛云存储的服务器域名
     */
    public static final String QINIU_CLOUD_SERVER = "http://7xnv8i.com1.z0.glb.clouddn.com/";

    /**
     * 草稿状态
     */
    public static final int STATUS_DRAFT = -1;

    /**
     * 禁用状态
     */
    public static final int STATUS_DISABLE = 0;

    /**
     * 正常状态
     */
    public static final int STATUS_NORMAL = 1;

    /**
     * 删除状态
     */
    public static final int STATUS_DELETE = 2;

    /**
     * 默认的请求超时时间10秒
     */
    public static final int DEFAULT_REQUEST_TIME_OUT = 15000;

    /**
     * 默认的响应超时时间10秒
     */
    public static final int DEFAULT_RESPONSE_TIME_OUT = 15000;
    /**
     * 响应成功的状态码200
     */
    public static final int RESPONSE_CODE_SUCCESS = 200;

    /**
     * 响应成功的状态码206(下载返回的成功码)
     */
    public static final int RESPONSE_CODE_DOWNLOAD_SUCCESS = 206;
    /**
     * GET请求方法
     */
    public static final String REQUEST_METHOD_GET = "GET";

    /**
     * POST请求方法
     */
    public static final String REQUEST_METHOD_POST = "POST";

    //SharedPreferenceUtil相关的常量
    /**
     * 心情草稿信息
     */
    public static final String MOOD_DRAFT = "mood_draft";

    /**
     * 心情草稿的内容
     */
    public static final String MOOD_DRAFT_CONTENT = "mood_draft_content";

    /**
     * 心情草稿的图片
     */
    public static final String MOOD_DRAFT_IMGS = "mood_draft_imgs";

    /**
     * 用户的基本信息
     */
    public static final String STRING_USERINFO = "string_userinfo";
    /**
     * 用户的基本信息
     */
    public static final String USERINFO = "userinfo";

    /**
     * 用户的数据信息
     */
    public static final String STRING_USERINFODATA = "string_userinfodata";
    /**
     * 用户的数据信息
     */
    public static final String USERINFODATA = "userinfodata";

    /**
     * 登录用户好友列表
     */
    public static final String STRING_FRIENDS = "string_friends";
    /**
     * 登录用户好友列表
     */
    public static final String FRIENDS = "friends";
    /**
     * 用户名称的历史
     */
    public static final String STRING_USERNAME_HISTORY = "string_username_history";
    /**
     * 用户名称的历史
     */
    public static final String USERNAME_HISTORY = "username_history";

    /**
     * 设置服务器地址
     */
    public static final String STRING_SETTING_BEAN_SERVER = "string_setting_bean";

    /**
     * 设置手机的型号
     */
    public static final String STRING_SETTING_BEAN_PHONE = "string_setting_phone_bean";

    /**
     * 设置实体的uuid
     */
    public static final String SETTING_BEAN_UUID = "setting_bean_uuid";
    /**
     * 设置实体的title
     */
    public static final String SETTING_BEAN_TITLE = "setting_bean_title";

    /**
     * 设置实体的content
     */
    public static final String SETTING_BEAN_CONTENT = "setting_bean_content";

    /**
     * 设置实体的hint
     */
    public static final String SETTING_BEAN_HINT = "setting_bean_hint";

    /**
     * 加载每个心情的用户头像
     */
    public static final int LOAD_MOOD_USER_PIC = 101;

    /**
     * 加载心情图像列表
     */
    public static final int LOAD_MOOD_PIC_LIST = 102;

    /**
     * 加载每个心情的单张图片
     */
    public static  final int LOAD_MOOD_PIC = 103;

    /**
     * 本地SQLite数据库的名称
     */
    public static final String DB_NAME = "LEEDANEDB";

    /**
     * 数据库的版本
     */
    public static final int DB_VERSION = 12;
    /**
     * 上传标记名称
     */
    public static final String UPLOAD_FILE_TABLE_NAME = "t_app_upload";

    /**
     * 上传app版本标记名称
     */
    public static final String UPLOAD_APP_VERSION_TABLE_NAME = "app_version";

    /**
     * 上传用户头像
     */
    public static final String UPLOAD_UPDATE_HEADER_TABLE_NAME = "t_user";

    /**
     * 点赞的handler，what值
     */
    public static final int ZAN_MESSAGE_WHAT = 106;

    /**
     * 支持的图片类型
     */
    public final static String[] SUPPORTIMAGESUFFIXS = {"png","jpg","bmp","gif","psd","jpeg"};

    /**
     * DES加密的密钥
     */
    public final static String DES_SECRET_KEY = "leedaneofficial";

    /**
     * 搜索类型--博客
     */
    public final static int SEARCH_TYPE_BLOG = 1;

    /**
     * 搜索类型--心情
     */
    public final static int SEARCH_TYPE_MOOD = 2;

    /**
     * 搜索类型--用户
     */
    public final static int SEARCH_TYPE_USER = 3;
}
