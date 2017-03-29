package com.cn.leedane.browser.task;

import android.os.AsyncTask;
import android.util.Log;

import com.cn.leedane.browser.bean.HttpRequestBean;
import com.cn.leedane.browser.util.AppUtil;

import java.util.ArrayList;

/**
 * 当前任务加载器(启动任务的入口)
 */
public class TaskLoader {

    public static final String TAG = "TaskLoader";
    private static TaskLoader taskLoader;

    /**
     * 当前任务容器
     */
    private ArrayList<Task> mTaskContainer;

    /**
     * 私有化构造方法
     */
    private TaskLoader(){
        mTaskContainer = new ArrayList<Task>();
    }

    /**
     * 获取任务加载器的实例
     * @return
     */
    public static synchronized TaskLoader getInstance(){
        if(taskLoader == null){
            taskLoader = new TaskLoader();
        }
        return taskLoader;
    }

    /**
     * 启动任务，接收返回信息
     * @param type
     * @param listener
     * @param requestBean
     */
    public void startTaskForResult(TaskType type, TaskListener listener, HttpRequestBean requestBean){
        try{
            Task task = new Task(type, listener, requestBean);
            task.taskContainer = mTaskContainer;

            //把当前的任务加进任务容器中
            mTaskContainer.add(task);
            if(AppUtil.getAndroidSDKVersion() > 11){
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }else{
                task.execute();
            }
        }catch (Exception e){
            Log.i(TAG, "启动任务出现异常");
            e.printStackTrace();
        }
    }

    /**
     * 启动任务(上传或者下载的任务)，接收返回信息
     * @param type
     * @param listener
     * @param objectBean
     */
    public void startTaskForResult(TaskType type, TaskListener listener, Object objectBean){
        try{
            Task task = new Task(type, listener, objectBean);
            task.taskContainer = mTaskContainer;

            //把当前的任务加进任务容器中
            mTaskContainer.add(task);
            if(AppUtil.getAndroidSDKVersion() > 11){
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }else{
                task.execute();
            }
        }catch (Exception e){
            Log.i(TAG, "启动任务出现异常");
            e.printStackTrace();
        }
    }

    /**
     * 根据任务类型取消单个任务
     * @param type
     */
    public void cancelOneTask(TaskType type){
        for(Task task: mTaskContainer){
            if(task.taskType == type){
                task.cancel(true);
                break;
            }
        }
    }

    /**
     * 取消全部的任务
     */
    public void cancelAllTasks(){
        for(Task task: mTaskContainer){
            task.cancel(true);
        }
    }

}
