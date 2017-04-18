package com.cn.leedane.browser.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cn.leedane.browser.R;
import com.cn.leedane.browser.adapter.BrowserTitlePagerAdapter;
import com.cn.leedane.browser.adapter.FragmentPagerAdapter;
import com.cn.leedane.browser.application.BaseApplication;
import com.cn.leedane.browser.customview.ScaleTransitionPagerTitleView;
import com.cn.leedane.browser.fragment.BrowserFragment;
import com.cn.leedane.browser.util.ToastUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.BezierPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BrowserActivity extends BaseActivity{
    private static final String[] CHANNELS = new String[]{"京东", "好123", "百度首页", "神马搜索", "顺丰", "开源中国osc", "新浪微博", "tmall"};
    private static final String[] links = new String[]{"https://item.jd.com/1925170.html#none",
            "https://www.hao123.com/?tn=39005028_243_hao_pg",
            "https://www.baidu.com/?tn=sitehao123_15",
            "https://so.m.sm.cn/?uc_param_str=dnntnwvepffrgibijbprsvdsme&dn=4274309449-9b8ecbd0&nt=2&nw=WIFI&ve=11.4.2.936&pf=145&fr=android&gi=bTkwBPds7xDRty9P38RTCCRLRYP32DEFWRCny3xGaXd5NA%3D%3D&bi=34668&pr=UCMobile&sv=ucrelease&ds=bTkwBMGPeC7Ry4Dd%2FmKifPiI4LBusyAanMgRGuKXt%2Bei4A%3D%3D&me=AATCJJdeZZFVgIdnV%2B7CY9HC&from=ucframe",
            "http://www.sf-express.com/cn/sc/dynamic_function/price/",
            "http://www.oschina.net/",
            "http://weibo.com/leedane/home?topnav=1&wvr=5",
            "https://www.tmall.com/"};
    private int mCurrentTab = 0;

    private ViewPager mViewPager;
    private List<String> mDataList = Arrays.asList(CHANNELS);
    private BrowserTitlePagerAdapter titlePagerAdapter = new BrowserTitlePagerAdapter(mDataList);

    private List<Fragment> mFragments = new ArrayList<>();

    private int mTitleAlpha = 100;

    private float mTitleHeight = 0f;

    private View mTitleView;

    private View mRootView;

    /**
     * 获取状态栏的高度
     */
    private int mBarHeight;

    /**
     * 容器的高度
     */
    private int mContraierHeight;

    /**
     * 距离顶部的距离
     */
    private float marginTop = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_browser);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(getStringResource(R.string.transmit));
        backLayoutVisible();

        mTitleView =  findViewById(R.id.baeselayout_navbar);
        mTitleView.setOnClickListener(this);

        //mTitleHeight = mTitleView.getHeight(); 获取到的高度是0
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mTitleView.measure(w, h);
        mTitleHeight = mTitleView.getMeasuredHeight();
        marginTop = mTitleHeight;

        mViewPager = (ViewPager) findViewById(R.id.browser_viewpager);
        mViewPager.setAdapter(titlePagerAdapter);

        mRootView = findViewById(R.id.root_layout);

        for(int i = 0 ; i <links.length; i++){
            Bundle bundle = new Bundle();
            bundle.putString("link", links[i]);
            bundle.putInt("index", i);
            mFragments.add(BrowserFragment.newInstance(bundle));
        }

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            mBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        /*int w1 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h1 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        findViewById(R.id.root_layout).measure(w1, h1);
        mRootHeight = findViewById(R.id.root_layout).getMeasuredHeight();*/

        initMagicIndicator6();

        WindowManager wm = (WindowManager) BrowserActivity.this
                .getSystemService(Context.WINDOW_SERVICE);

        //int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        mContraierHeight = height - (int)mTitleHeight - mBarHeight;
    }

    private void initMagicIndicator6() {
        MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.setNormalColor(Color.GRAY);
                simplePagerTitleView.setSelectedColor(Color.BLACK);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                BezierPagerIndicator indicator = new BezierPagerIndicator(context);
                indicator.setColors(Color.parseColor("#ff4a42"), Color.parseColor("#fcde64"), Color.parseColor("#73e8f4"), Color.parseColor("#76b0ff"), Color.parseColor("#c683fe"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), getBaseContext(), mFragments));
        mViewPager.setCurrentItem(mCurrentTab);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    @Override
    public void onBackPressed() {
       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.baeselayout_navbar:
                ToastUtil.success(getBaseContext());
                break;
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        //ToastUtil.success(BrowserActivity.this, "gggdd", Toast.LENGTH_SHORT);
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        }

        if(event.getAction() == MotionEvent.ACTION_UP) {
            //当手指移动的时候的时候
            x2 = event.getX();
            y2 = event.getY();
            int he = mTitleView.getHeight();


            int[] local = new int[2];
            mTitleView.getLocationOnScreen(local);
            if(y1 > y2 && y1 - y2 > 100) { //上滑
                // ObjectAnimator.ofFloat(mTitleView, "translationY", 100, 210 - (y1 - y2));
                //ToastUtil.success(BrowserActivity.this, "向上滑动", Toast.LENGTH_SHORT);
                //mTitleView.getBackground().setAlpha(mTitleAlpha --);
                ObjectAnimator anim =ObjectAnimator.ofFloat(mTitleView, "translationY", 0, -mTitleHeight);
                anim.setDuration(5);
                anim.start();
                Log.i("browserMove", "he="+ he +", y1="+ y1 +", y2="+ y2+ ", result="+ -mTitleHeight);
                LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) mRootView.getLayoutParams(); //取控件textView当前的布局参数
                linearParams.height = mContraierHeight + (int)mTitleHeight;// 控件的高强制设成20
                mRootView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                findViewById(R.id.root_layout).setY(mBarHeight);
            } else if(y2 > y1 && y2 - y1 > 100) {
                //ObjectAnimator.ofFloat(mTitleView, "translationY", 100, 210 + (y2 - y1));
                //ToastUtil.success(BrowserActivity.this, "向下滑动", Toast.LENGTH_SHORT);
                //mTitleView.getBackground().setAlpha(mTitleAlpha ++);
                ObjectAnimator anim =ObjectAnimator.ofFloat(mTitleView, "translationY", -mTitleHeight, 0);
                anim.setDuration(5);
                anim.start();
                //Log.i("browserMove", "he="+ he +", y1="+ y1 +", y2="+ y2+ ", result="+ top);
                LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) mRootView.getLayoutParams(); //取控件textView当前的布局参数
                linearParams.height = mContraierHeight - (int)mTitleHeight;// 控件的高强制设成20
                mRootView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                findViewById(R.id.root_layout).setY(mTitleHeight);

            } else if(x1 - x2 > 100) {
                ToastUtil.success(BrowserActivity.this, "向左滑动", Toast.LENGTH_SHORT);
                ObjectAnimator anim =ObjectAnimator.ofFloat(mTitleView, "translationY", 0, -mTitleHeight);
                anim.setDuration(5);
                anim.start();

                LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) mRootView.getLayoutParams(); //取控件textView当前的布局参数
                linearParams.height = mContraierHeight + (int)mTitleHeight;// 控件的高强制设成20
                mRootView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                findViewById(R.id.root_layout).setY(mBarHeight);
            } else if(x2 - x1 > 100) {
                ToastUtil.success(BrowserActivity.this, "向右滑动", Toast.LENGTH_SHORT);
                ObjectAnimator anim =ObjectAnimator.ofFloat(mTitleView, "translationY", 0, -mTitleHeight);
                anim.setDuration(5);
                anim.start();
                //mRootView.setY(mBarHeight);
                LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) mRootView.getLayoutParams(); //取控件textView当前的布局参数
                linearParams.height = mContraierHeight + (int)mTitleHeight;// 控件的高强制设成20
                mRootView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                findViewById(R.id.root_layout).setY(mBarHeight);
            }
        }
        /*if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        }

        if(event.getAction() == MotionEvent.ACTION_MOVE) {
            //当手指移动的时候的时候
            x2 = event.getX();
            y2 = event.getY();
        }

        if(event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
            if(y1 - y2 > 50) {
                ToastUtil.success(BrowserActivity.this, "向上滑动", Toast.LENGTH_SHORT);
            } else if(y2 - y1 > 50) {
                ToastUtil.success(BrowserActivity.this, "向下滑动", Toast.LENGTH_SHORT);
            } else if(x1 - x2 > 50) {
                ToastUtil.success(BrowserActivity.this, "向左滑动", Toast.LENGTH_SHORT);
            } else if(x2 - x1 > 50) {
                ToastUtil.success(BrowserActivity.this, "向右滑动", Toast.LENGTH_SHORT);
            }
        }*/
        return super.dispatchTouchEvent(event);
    }

    private float x1, y1, x2, y2;
}
