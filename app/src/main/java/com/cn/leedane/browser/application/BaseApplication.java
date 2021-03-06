package com.cn.leedane.browser.application;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cn.leedane.browser.R;
import com.cn.leedane.browser.util.ConstantsUtil;
import com.cn.leedane.browser.util.SharedPreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * android启动加载的上下文类(需要在AndroidManifest.xml中注册)
 */
public class BaseApplication extends Application {
    public static final String TAG = "BaseApplication";
    public static final int TASK_FINISH_CODE = 190;
    public static RequestQueue queue;
    private static BaseApplication mBaseApplication ;

    private static Bitmap getDefaultImage;
    private static Bitmap getErrorImage;
    private static boolean isLogin;
    public Vibrator mVibrator;

    @Override
    public void onCreate() {
        long start = System.currentTimeMillis();
        super.onCreate();
        mBaseApplication = this;
        Log.i(TAG, "正在启动创建上下文信息");
        long end = System.currentTimeMillis();
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        queue = Volley.newRequestQueue(getApplicationContext());//使用全局上下文
        Log.i(TAG, "创建上下文信息完成，耗时：" + (end - start));
    }

    /**
     * 获取Application实例
     * @return
     */
    public static BaseApplication newInstance(){
        return mBaseApplication;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static RequestQueue getVollyQueue() {
        return queue;
    }

    /**
     * 获取默认图像的bitmap
     * @return
     */
    public static Bitmap getDefaultImage(){
        return getBitmapFromRes(R.drawable.default_loading);

    }

    /**
     * 获取默认图像的bitmap
     * @return
     */
    public static Bitmap getNotPicImage(){
        return getBitmapFromRes(R.drawable.no_pic);

    }

    /**
     * 获取默认的字体
     * @return
     */
    public static Typeface getDefaultTypeface(){
        return Typeface.createFromAsset(BaseApplication.newInstance().getAssets(), "fangzhengliuxingti.ttf");
    }


    /**
     * 获取当图像获取失败后显示的错误提示图像的Bitmap
     * @return
     */
    public static Bitmap getErrorImage() {
        return getBitmapFromRes(R.drawable.no_pic);
    }
    public static Bitmap getBitmapFromRes(int resId) {
        Resources res = BaseApplication.newInstance().getResources();
        return BitmapFactory.decodeResource(res, resId);
    }

    /**
     * 获取默认的请求服务器的地址
     * @return
     */
    public static String getBaseServerUrl(){
        return SharedPreferenceUtil.getSettingBean(newInstance(), ConstantsUtil.STRING_SETTING_BEAN_SERVER).getContent();
    }
    /**
     * 构建基本的请求参数
     * @return
     */
    public HashMap<String, Object> getBaseRequestParams(){
        HashMap<String, Object> params = new HashMap<>();
        params.put("login_mothod", "android");
        params.put("froms", Build.MODEL);//手机型号
        params.put("producer", Build.MANUFACTURER);//手机厂家
        try{
            JSONObject userInfo = getLoginUserInfo();
            if(userInfo != null){
                if(userInfo.has("no_login_code"))
                    params.put("no_login_code", userInfo.getString("no_login_code"));
                if(userInfo.has("account"))
                    params.put("account", userInfo.getString("account"));
                if(userInfo.has("id"))
                    params.put("id", userInfo.getString("id"));
            }
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String szImei = telephonyManager.getDeviceId();
            params.put("imei", szImei);
            return params;
        }catch (Exception e){
            return null;
        }
    }

    public int[] getScreenWidthAndHeight(){
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        int[] widthAndHeight = new int[2];
        //获得当前设备的屏幕宽度
        widthAndHeight[0] = dm.widthPixels;
        widthAndHeight[1] = dm.heightPixels;
        //Toast.makeText(getApplicationContext(), "屏幕的宽度是:"+screenWidth, Toast.LENGTH_LONG).show();
        return widthAndHeight;
    }

    /**
     * 获取系统包信息
     * @return
     */
    public static PackageInfo getPackageInfo(){
        PackageInfo packageInfo = null;
        try{
            packageInfo = newInstance().getPackageManager().getPackageInfo(newInstance().getPackageName(), 0);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return packageInfo;
    }

    /**
     * 判断用户是否登录
     * @return
     */
    public static boolean isLogin(){
        return getLoginUserId() > 0;
    }

    /**
     * 获取登录的用户信息
     * @return
     */
    public static JSONObject getLoginUserInfo(){
        return SharedPreferenceUtil.getUserInfo(BaseApplication.newInstance());
    }

    /**
     * 获取登录的用户ID
     * @return
     */
    public static int getLoginUserId(){

        int userId = 0;
        try {
            JSONObject jsonObject = getLoginUserInfo();
            if(jsonObject != null && jsonObject.has("id")) {
                userId = jsonObject.getInt("id");
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return userId;
    }

    /**
     * 获取登录的用户性别
     * @return
     */
    public static String getLoginUserSex(){
        String sex = "";
        try {
            JSONObject jsonObject = getLoginUserInfo();
            if(jsonObject != null && jsonObject.has("id")) {
                sex = jsonObject.getString("sex");
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return sex;
    }

    /**
     * 获取登录的用户头像路径
     * @return
     */
    public static String getLoginUserPicPath(){

        String picPath = null;
        try {
            JSONObject jsonObject = getLoginUserInfo();
            if(jsonObject != null && jsonObject.has("user_pic_path")){
                picPath = jsonObject.getString("user_pic_path");
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return picPath;
    }

    /**
     * 获取登录的用户名称
     * @return
     */
    public static String getLoginUserName(){

        String userName = null;
        try {
            JSONObject jsonObject = getLoginUserInfo();
            if(jsonObject != null && jsonObject.has("account")){
                userName = jsonObject.getString("account");
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return userName;
    }

}
