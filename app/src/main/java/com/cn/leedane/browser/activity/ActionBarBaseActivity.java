package com.cn.leedane.browser.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.cn.leedane.browser.application.BaseApplication;
import com.cn.leedane.browser.util.ToastUtil;

import java.io.Serializable;

/**
 * 基本的activity
 * Created by LeeDane on 2015/10/17.
 */
public abstract class ActionBarBaseActivity extends AppCompatActivity implements View.OnClickListener, Serializable, SwipeRefreshLayout.OnRefreshListener{

    protected String mPreLoadMethod = "firstloading";//当前的操作方式
    protected boolean isLoading; //标记当前是否在加载数据
    protected int mFirstId;  //页面上第一条数据的ID
    protected int mLastId; //页面上第一条数据的ID

    protected TextView mRecyclerViewFooter;
    protected View mFooterView;

    protected SwipeRefreshLayout mSwipeLayout;

    /**
     * 检查是否登录
     */
    protected boolean checkedIsLogin() {
        //判断是否有缓存用户信息
        if(BaseApplication.getLoginUserId() < 1){
            return false;
        }
        return true;
    }
    /**
     * 弹出加载ProgressDiaLog
     */
    protected ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(getLabel());
    }

    /**
     * 获取主视图容器的ID
     * @return
     */
    protected abstract int getContentViewId();

    /**
     * 获取主视图的标题名称
     * @return
     */
    protected abstract String getLabel();

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case 0:

                break;
            default:
                break;
        }
    }

    public void onGoBack(View v){
        //Toast.makeText(BaseActivity.this, "点击返回", Toast.LENGTH_SHORT).show();
        ActionBarBaseActivity.this.finish();
    }

    /**
     * 显示加载Dialog
     * @param title  标题
     * @param main  内容
     */
    protected void showLoadingDialog(String title, String main){
        showLoadingDialog(title, main, false);
    }
    /**
     * 显示加载Dialog
     * @param title  标题
     * @param main  内容
     * @param cancelable 是否可以取消
     */
    protected void showLoadingDialog(String title, String main, boolean cancelable){
        dismissLoadingDialog();
        mProgressDialog = ProgressDialog.show(ActionBarBaseActivity.this, title, main, true, cancelable);
    }

    /**
     * 隐藏加载Dialog
     */
    protected void dismissLoadingDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    /**
     * 获取字符串资源
     * @param resourseId
     * @return
     */
    protected String getStringResource(int resourseId){
        return getResources().getString(resourseId);
    }

    /**
     *
     * 返回菜单的关闭操作
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
