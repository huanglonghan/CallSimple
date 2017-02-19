package pw.bmyo.www.bmyobaselibrary.config;

import pw.bmyo.www.bmyobaselibrary.BuildConfig;

/**
 * Created by huang on 2016/10/7.
 */

public class Config {

    static {
        if (BuildConfig.DEBUG) {
            API_HTTP_HOST = "http://api.zhiliao.science";
            OP_HTTP_HOST = "http://op-api.zhiliao.science";
            SEND_MSG_INTERVAL = 5;
        } else {
            API_HTTP_HOST = "http://api.moplus.live";
            OP_HTTP_HOST = "http://op-api.moplus.live";
            SEND_MSG_INTERVAL = 60;
        }
    }

    //=================== 地址 =================

    //api服务器地址
    public static final String API_HTTP_HOST;

    //运营服务器地址
    public static final String OP_HTTP_HOST;


    //=================== 链接 ==================

    //个人详情页基本链接
    public static final String USER_URL = "mo+://user?user_id=%d";

    //关于我们链接
    public static final String ABOUT_URL = "http://moplus.live/about/about.html";


    //=================== 延时 ==================

    //发送短信接口间隔(单位:秒)
    public static final int SEND_MSG_INTERVAL;

    //http连接超时(单位:秒)
    public static final int HTTP_CONNECT_TIMEOUT = 60;

    //http读超时(单位:秒)
    public static final int HTTP_READ_TIMEOUT = 4;

    //事件防抖动延时(单位:毫秒)
    public static final int EVENT_THROTTLE_DELAY = 300;


    //================= 缓存保存时间 ===================

    //缓存保存时间(单位:秒)
    public static final int CACHE_SAVE_TIME = 5 * 60 * 60;


    //==================== ID ==================

    //我们的id
    public static final long SELF_USER_ID = 1;


    //================= 数量默认值 ===================

    //手势滑动
    public static final int SLIDE_COUNT = 15;


    //=================== http返回码 ==================
    //http返回码
    public static final int HTTP_STATUS_SUCCEED = 200;

    //没有数据
    //没有数据
    public static final int HTTP_NO_MORE_A = 201;
    public static final int HTTP_NO_MORE = 202;
    //输入校验类错误
    public static final int HTTP_CHECK_ERROR = 203;
    // 弹窗提示
    public static final int HTTP_DIALOG_TIP = 204;

    public static final int HTTP_DIALOG_TIMEOUT = 205;
    //未登录
    public static final int HTTP_STATUS_NOT_LOGIN = 208;


}