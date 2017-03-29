package com.cn.leedane.browser.handler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.cn.leedane.browser.activity.ImageDetailActivity;
import com.cn.leedane.browser.activity.MipcaActivityCapture;
import com.cn.leedane.browser.application.BaseApplication;
import com.cn.leedane.browser.bean.HttpRequestBean;
import com.cn.leedane.browser.bean.ImageDetailBean;
import com.cn.leedane.browser.task.TaskListener;
import com.cn.leedane.browser.task.TaskLoader;
import com.cn.leedane.browser.task.TaskType;
import com.cn.leedane.browser.util.ConstantsUtil;
import com.cn.leedane.browser.util.DesUtils;
import com.cn.leedane.browser.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 相同操作相关的处理类
 */
public class CommonHandler {


    /**
     * 触发图片列表详情的activity
     * @param context
     * @param list
     * @param showCurrent 展示当前的位置索引
     */
    public static void startImageDetailActivity(Context context, List<ImageDetailBean> list, int showCurrent){
        Intent it_detail = new Intent(context, ImageDetailActivity.class);
        Type type = new TypeToken<ArrayList<ImageDetailBean>>(){}.getType();
        String json = new Gson().toJson(list,type);
        it_detail.putExtra("ImageDetailBeans", json);
        it_detail.putExtra("current", showCurrent);
        context.startActivity(it_detail);
    }

    /**
     * 触发图片列表详情的activity
     * @param context
     * @param imgs
     */
    public static void startImageDetailActivity(Context context, String imgs){
        List<ImageDetailBean> list = new ArrayList<>();
        String[] showImages = imgs.split(",");
        ImageDetailBean imageDetailBean;
        for(String img: showImages){
            imageDetailBean = new ImageDetailBean();
            imageDetailBean.setPath(img);
            list.add(imageDetailBean);
        }
        startImageDetailActivity(context, list, 0);
    }

    /**
     * 触发扫一扫的activity
     * @param context
     */
    public static void startMipcaActivityCapture(Context context){
        Intent intent = new Intent();
        intent.setClass(context, MipcaActivityCapture.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    /**
     * 调用系统浏览器打开网络链接
     * @param context
     * @param networkLink
     */
    public static void openLink(Context context, String networkLink){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(networkLink));
        context.startActivity(intent);
    }

    /**
     * 对生成二维码的字符串进行二次编码
     * @param sourceStr
     * @return
     */
    public static String encodeQrCodeStr(String sourceStr){
        if(StringUtil.isNotNull(sourceStr)){
            try {
                DesUtils des = new DesUtils();
                sourceStr = des.encrypt(sourceStr);
            }catch (Exception e){
                Log.i("CommonHandler", "字符串转成DES码失败，字符串是："+sourceStr);
            }

            //sourceStr = new String(Base64Util.encode(sourceStr.getBytes()));
        }
        return sourceStr;
    }

    /**
     * 获取翻译的请求
     * @param listener
     * @param content
     */
    public static void getFanYiRequest(TaskListener listener, String content){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("content", content);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        requestBean.setServerMethod("leedane/tool/fanyi.action");
        TaskLoader.getInstance().startTaskForResult(TaskType.FANYI, listener, requestBean);
    }

    /**
     * 获取七牛云存储token的请求
     * @param listener
     */
    public static void getQiniuTokenRequest(TaskListener listener){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        requestBean.setServerMethod("leedane/tool/getQiNiuToken.action");
        TaskLoader.getInstance().startTaskForResult(TaskType.QINIU_TOKEN, listener, requestBean);
    }

    /**
     * 二维码登录
     * @param listener
     * @param cid
     */
    public static void loginByQrCode(TaskListener listener, String cid){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("cid", cid);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/user/scan/login.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.SCAN_LOGIN, listener, requestBean);
    }

    /**
     * 取消二维码登录
     * @param listener
     * @param cid
     */
    public static void CancelLoginByQrCode(TaskListener listener, String cid){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("cid", cid);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/user/scan/cancel.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.CANCEL_SCAN_LOGIN, listener, requestBean);
    }
}
