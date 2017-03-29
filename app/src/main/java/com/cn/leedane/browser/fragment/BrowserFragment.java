package com.cn.leedane.browser.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cn.leedane.browser.R;
import com.cn.leedane.browser.bean.ImageDetailBean;
import com.cn.leedane.browser.handler.CommonHandler;

import java.util.ArrayList;
import java.util.List;

import lib.homhomlib.design.SlidingLayout;

/**
 *
 */
public class BrowserFragment extends Fragment {

    public static final String TAG = "BrowserFragment";
    private Context mContext;
    private int mIndex;
    private View mRootView;
    private boolean isFirstLoading = true; //是否是第一次加载
    private WebSettings mSettings;
    private String mLink;

    private SlidingLayout mSlidingLayout;
    /**
     * 内置浏览器webview对象
     */
    private WebView mWebView;
    /**
     * 加载进度的进度条
     */
    private ProgressBar mProgressBar;

    public BrowserFragment(){

    }
    public static final BrowserFragment newInstance(Bundle bundle){
        BrowserFragment fragment = new BrowserFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_browser, container,
                    false);
        Bundle bundle = getArguments();
        if(bundle != null){
            mLink = bundle.getString("link");
            mIndex = bundle.getInt("index");
        }
        if(mContext == null)
            mContext = getActivity();

        if(isFirstLoading){
            mWebView = (WebView)mRootView.findViewById(R.id.detail_webview);
            mSlidingLayout = (SlidingLayout)mRootView.findViewById(R.id.webview_background_view);
            mProgressBar = (ProgressBar)mRootView.findViewById(R.id.detail_progressbar);

            if(mSlidingLayout.getBackgroundView() != null){
                ((TextView)mSlidingLayout.getBackgroundView().findViewById(R.id.webview_backgroup_textview)).setText("网页由 "+ mLink +"提供\n\r   LeeDane官方提供技术支持");
            }

            mWebView.loadUrl(mLink);
            //启用支持javascript
            mSettings = mWebView.getSettings();
            mSettings.setJavaScriptEnabled(true);
            mSettings.setSupportZoom(false);
            mWebView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
                        handler.sendEmptyMessage(1);
                        return true;
                    }
                    return false;
                }
            });

//            LayoutAlgorithm是一个枚举，用来控制html的布局，总共有三种类型：
//            NORMAL：正常显示，没有渲染变化。
//            SINGLE_COLUMN：把所有内容放到WebView组件等宽的一列中。
//            NARROW_COLUMNS：可能的话，使所有列的宽度不超过屏幕宽度。
            mSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

            mSettings.setUseWideViewPort(true);
            //mSettings.setLoadWithOverviewMode(true);


            /** todo mWebView.addJavascriptInterface(this, "webview");//对应js中的test.xxx**/
            //使用缓存模式缓存加载过的数据
//            缓存模式(5种)
//            LOAD_CACHE_ONLY:  不使用网络，只读取本地缓存数据
//            LOAD_DEFAULT:  根据cache-control决定是否从网络上取数据。
//            LOAD_CACHE_NORMAL: API level 17中已经废弃, 从API level 11开始作用同LOAD_DEFAULT模式
//            LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
//            LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
            if (Build.VERSION.SDK_INT >= 19) {
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            }
            //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
            mWebView.setWebViewClient(new LocalWebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                    view.loadUrl(url);
                    return true;
                }
            });

            mWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress == 100) {
                        mProgressBar.setVisibility(View.GONE);
                        //Toast.makeText(DetailActivity.this, "网页加载完成", Toast.LENGTH_SHORT).show();
                    } else {
                        // 加载中
                        mProgressBar.setProgress(newProgress);
                    }
                }
            });
        }else{
            mWebView.loadUrl(mLink);
        }
        return mRootView;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1: {
                    webViewGoBack();
                }
                break;
            }
        }
    };

    private void webViewGoBack() {
        mWebView.goBack();
    }

    /**
     * 图片的点击事件
     * @param imgs
     * @param index
     * @param maxWidth
     * @param maxHeight
     */
    @JavascriptInterface
    public void clickImg(String imgs, int index, int maxWidth, int maxHeight) {//对应js中xxx.hello("")
        Log.e("webview", "hello");
        String[] arrayImg = imgs.split(";");

        if (arrayImg.length > 0 && index >= 0 && arrayImg.length > index) {
            List<ImageDetailBean> list = new ArrayList<>();
            ImageDetailBean imageDetailBean = null;
            for (int i = 0; i < arrayImg.length; i++) {
                imageDetailBean = new ImageDetailBean();
                imageDetailBean.setPath(arrayImg[i]);
                /*if(maxWidth > 0 && maxHeight > 0){
                    imageDetailBean.setWidth(maxWidth);
                    imageDetailBean.setHeight(maxHeight);
                }*/
                list.add(imageDetailBean);
            }
            CommonHandler.startImageDetailActivity(mContext, list, index);
        }


    }

    @Override
    public void onDestroy() {
        if(mWebView != null)
            mWebView.destroy();
        if(handler != null)
            handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    class LocalWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.startsWith("http:") || url.startsWith("https:") ) {
                view.loadUrl(url);
                return false;
            }else{
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        }
    }
}
