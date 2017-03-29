package com.cn.leedane.browser.task;

/**
 * 任务的监听器类
 */
public interface TaskListener {

    /**
     * 任务启动
     * @param type  任务的类型
     */
    public void taskStarted(TaskType type);


    /**
     * 任务的完成
     * @param type  任务类型
     * @param result  返回的结果集
     */
    public void taskFinished(TaskType type, Object result);

    /**
     * 任务的取消
     * @param type  任务的类型
     */
    public void taskCanceled(TaskType type);

}
