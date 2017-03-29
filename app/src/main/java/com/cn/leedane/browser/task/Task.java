package com.cn.leedane.browser.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cn.leedane.browser.application.BaseApplication;
import com.cn.leedane.browser.bean.HttpRequestBean;
import com.cn.leedane.browser.util.ConstantsUtil;
import com.cn.leedane.browser.util.StringUtil;
import com.cn.leedane.browser.util.http.HttpConnectionUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 具体任务的执行类
 */
public class Task extends AsyncTask{

    public static final String TAG = "Task";
    /**
     * 上下文对象
     */
    private static Context mContext;
    /**
     * 任务容器
     */
    public ArrayList<Task> taskContainer;

    /**
     * 任务类型
     */
    public TaskType taskType = null;

    /**
     * 任务监听器
     */
    private TaskListener mListener;

    /**
     * 请求参数的实体类
     */
    private HttpRequestBean mRequestBean;

    /**
     * 请求参数的实体类
     */
    private Object mObjectBean;

    /**
     * 封装响应的对象
     */
    private Object mTaskResult = "连接不上服务器，请稍后重试";

    public Task(TaskType type, TaskListener listener, Object objectBean){
        mListener = listener;
        mObjectBean = objectBean;
        taskType = type;
        if(mObjectBean != null){
            //插入基本的参数
            //if(BaseApplication.newInstance().getBaseRequestParams() != null){
                // mDownloadBean.setParams(getBaseRequestParams());
            //}

        }
    }

    public Task(TaskType type, TaskListener listener, HttpRequestBean requestBean){
        taskType  = type;
        mListener = listener;
        mRequestBean = requestBean;

        if(mRequestBean != null){
            //插入基本的参数
            //if(getBaseRequestParams() != null)
                mRequestBean.getParams().putAll(BaseApplication.newInstance().getBaseRequestParams());
        }
    }
    /**
     * 设置应用上下文，在BaseApplication中进行设置
     */
    public static void setApplicationContext(Context context){
        mContext = context;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        //final String resultStr;
        Log.i(TAG, "任务类型:" +taskType);
        try {
            if(mRequestBean == null){
                Error error = new Error("请求参数不能为空");
                mTaskResult = error;
               return null;
            }


            //请求服务器的方法不能为空
            if(StringUtil.isNull(mRequestBean.getServerMethod())) {
                Error error = new Error("请求服务器的方法不能为空");
                mTaskResult = error;
                return null;
            }

            final String serverPath;
            String serverMethod = mRequestBean.getServerMethod();

            if(serverMethod.startsWith("/")){
                serverMethod = serverMethod.substring(1, serverMethod.length());
            }
            if(StringUtil.isNull(mRequestBean.getUrl())){
                serverPath = BaseApplication.getBaseServerUrl() + serverMethod;
            }else{
                String url = mRequestBean.getUrl();
                if(url.endsWith("/")){
                    url = url.substring(0, (url.length() -1));
                }
                serverPath = url + serverMethod;
            }


            if(taskType == TaskType.LOADNETWORK_BLOG_IMAGE){
                mTaskResult = HttpConnectionUtil.getPostRequestInputStream(mRequestBean.getUrl(), mRequestBean.getParams(), 0, 0);
                return null;
            }

            Log.i(TAG, "Task请求的路径:" +serverPath);

            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(mRequestBean.getRequestTimeOut()  > 0 ? mRequestBean.getRequestTimeOut() : ConstantsUtil.DEFAULT_REQUEST_TIME_OUT, TimeUnit.MILLISECONDS)
                    .readTimeout(mRequestBean.getResponseTimeOut()  > 0 ? mRequestBean.getResponseTimeOut() : ConstantsUtil.DEFAULT_RESPONSE_TIME_OUT, TimeUnit.MILLISECONDS)
                    .build();

            //由于okhttp是异步操作，在这里封装的时候不适合，加锁改成同步
            final CountDownLatch latch = new CountDownLatch(1);
            //执行get请求
            if(mRequestBean.getRequestMethod().equalsIgnoreCase(ConstantsUtil.REQUEST_METHOD_GET)){
                okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(serverPath)
                            .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mTaskResult = "{\"isSuccess\": false, \"message\":\"GET网络请求响应异常\"}";
                        latch.countDown();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        mTaskResult = response.body().toString();
                        latch.countDown();
                        //mTaskResult = HttpConnectionUtil.sendGetRequest(serverPath, mRequestBean.getRequestTimeOut(), mRequestBean.getResponseTimeOut());
                    }
                });
                //执行post请求
            } else {
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(serverPath)
                        .post(MapToRequestBody(mRequestBean.getParams()))
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mTaskResult = "{\"isSuccess\": false, \"message\":\"POST网络请求响应异常\"}";
                        latch.countDown();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                byte[] data = response.body().bytes();
                                mTaskResult = new String(data);
                                /*InputStream is = response.body().byteStream();
                                if (is != null) {
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                                    //存储单行文本的数据
                                    String line;
                                    //保存请求返回的所有文本数据
                                    StringBuffer buffer = new StringBuffer();
                                    while ((line = reader.readLine()) != null) {
                                        buffer.append(line);
                                    }
                                    //关闭相关的流
                                    if (is != null) is.close();
                                    if (reader != null) reader.close();
                                    mTaskResult = new String(buffer.toString().getBytes("utf-8"), "gb2312");
                                }*/
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        latch.countDown();
                        //mTaskResult = response.body().toString();
                        //mTaskResult = HttpConnectionUtil.sendPostRequest(serverPath, mRequestBean.getParams(), mRequestBean.getRequestTimeOut(), mRequestBean.getResponseTimeOut());
                    }
                });
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
            }
            Log.i(TAG, "任务完成，响应数据：" + mTaskResult);
        } catch (Exception e) {
            String msg = e.getMessage();
            if(StringUtil.isNull(msg)){
                msg = "连接服务器失败，请稍后重试";
            }
            mTaskResult = new Error(msg);
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 将map集合转成RequestBody
     * @param params
     */
    private RequestBody MapToRequestBody(Map<String, Object> params){
        FormBody.Builder builder = new FormBody.Builder();
        for(Map.Entry<String, Object> entry: params.entrySet()){
            builder.add(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return builder.build();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(mListener != null){
            //取消当前任务
            mListener.taskCanceled(taskType);
        }
        //把监听器设置为空
        mListener = null;
        if(taskContainer != null){
            taskContainer.remove(this);
        }
    }

    /**
     * 执行任务完成后
     * @param o
     */
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if(mListener != null){
            mListener.taskFinished(taskType, mTaskResult);
        }
        if(taskContainer != null){
            //从当前的任务容器移除当前任务对象
            taskContainer.remove(this);
        }
    }

    /**
     * 准备执行任务
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(mListener != null){
            //启动任务
            mListener.taskStarted(taskType);
        }
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
    }

    /**
     * 构建基本的请求参数
     * @return
     */
    /*private HashMap<String, Object> getBaseRequestParams(){
        HashMap<String, Object> params = new HashMap<>();
        params.put("login_mothod", "android");
        try{
            JSONObject userInfo = SharedPreferenceUtil.getUserInfo(mContext);
            //baseServerUrl = SharedPreferenceUtil.getSettingBean(mContext, ConstantsUtil.STRING_SETTING_BEAN_SERVER).getContent();

            if(userInfo != null){
                if(userInfo.has("no_login_code"))
                    params.put("no_login_code", userInfo.getString("no_login_code"));
                if(userInfo.has("account"))
                    params.put("account", userInfo.getString("account"));
                if(userInfo.has("id"))
                    params.put("id", userInfo.getString("id"));
            }
            TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String szImei = telephonyManager.getDeviceId();
            params.put("imei", szImei);
            return params;
        }catch (Exception e){
            return null;
        }
    }*/
}