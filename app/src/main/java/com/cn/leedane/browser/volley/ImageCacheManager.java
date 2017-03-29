package com.cn.leedane.browser.volley;

import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cn.leedane.browser.application.BaseApplication;
import com.cn.leedane.browser.util.MySettingConfigUtil;
import com.cn.leedane.browser.util.StringUtil;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * 图片缓存管理类， 获取ImageLoader对象
 */
public class ImageCacheManager {
    private static final String TAG = ImageCacheManager.class.getSimpleName();

    //获取图片缓存对象
    private static ImageLoader.ImageCache mImageCache = new ImageCacheUtil();

    //获取ImageLoader对象
    public static ImageLoader mImageLoader = new ImageLoader(RequestQueueManager.mRequestQueue, mImageCache);


    public static ImageLoader.ImageListener getImageListener(final ImageView view, final Bitmap defaultImage, final Bitmap errorImage){
        return new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                //回调成功
                if(imageContainer.getBitmap() != null && view != null){
                    view.setImageBitmap(imageContainer.getBitmap());
                }else if(defaultImage != null && view != null){
                    view.setImageBitmap(defaultImage);
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //回调失败
                if(errorImage != null && view != null)
                    view.setImageBitmap(errorImage);
            }
        };
    }

    /**
     * 加载网络动态图片
     * @param view
     * @param defaultImage
     * @param errorImage
     * @return
     */
    public static ImageLoader.ImageListener getGifImageListener(final GifImageView view, final Bitmap defaultImage, final Bitmap errorImage){
        return new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                //回调成功
                if(imageContainer.getBitmap() != null && view != null){
                    view.setImageBitmap(imageContainer.getBitmap());
                }else if(defaultImage != null && view != null){
                    view.setImageBitmap(defaultImage);
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //回调失败
                if(errorImage != null && view != null)
                    view.setImageBitmap(errorImage);
            }
        };
    }

    public static ImageLoader.ImageListener getSubsamplingScaleImageListener(final SubsamplingScaleImageView view, final Bitmap defaultImage, final Bitmap errorImage){
        return new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                //回调成功
                if(imageContainer.getBitmap() != null && view != null){
                    view.setImage(ImageSource.bitmap(imageContainer.getBitmap()));
                }else if(defaultImage != null && view != null){
                    view.setImage(ImageSource.bitmap(defaultImage));
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //回调失败
                if(errorImage != null && view != null){
                    view.setImage(ImageSource.bitmap(errorImage));
                }
            }
        };
    }

    /**
     * 提供给外部调用的方法
     * @param url
     * @param view
     */
    public static void loadImage(String url, ImageView view){
        if(StringUtil.isNull(url) || !MySettingConfigUtil.getLoadImage()){
            return;
        }
        mImageLoader.get(url, ImageCacheManager.getImageListener(view, BaseApplication.getDefaultImage(), BaseApplication.getErrorImage()));
    }

    /**
     * 提供给外部调用的方法
     * @param url
     * @param view
     * @param defaultImage
     * @param errorImage
     */
    public static void loadImage(String url, ImageView view, Bitmap defaultImage, Bitmap errorImage){
        if(StringUtil.isNull(url) || !MySettingConfigUtil.getLoadImage()){
            return;
        }
        mImageLoader.get(url, ImageCacheManager.getImageListener(view, defaultImage, errorImage));
    }

    /**
     * 提供给外部调用的方法
     * @param url
     * @param view
     * @param defaultImage
     * @param errorImage
     * @param maxWidth
     * @param maxHeight
     */
    public static void loadImage(String url, ImageView view, Bitmap defaultImage, Bitmap errorImage, int maxWidth, int maxHeight ) {
        if(StringUtil.isNull(url) || !MySettingConfigUtil.getLoadImage()){
            return;
        }
        mImageLoader.get(url, ImageCacheManager.getImageListener(view, defaultImage, errorImage), maxWidth, maxHeight);
    }

    /**
     * 提供给外部使用的，加载网络图片，返回BitMap
     * @param url
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static void loadImage(String url, SubsamplingScaleImageView view, int maxWidth, int maxHeight){
        if(StringUtil.isNull(url) || !MySettingConfigUtil.getLoadImage()){
            return;
        }
        mImageLoader.get(url, ImageCacheManager.getSubsamplingScaleImageListener(view, BaseApplication.getDefaultImage(), BaseApplication.getErrorImage()), maxWidth, maxHeight).getBitmap();
    }

    /**
     * 提供给外部使用的，加载网络图片，返回BitMap
     * @param url
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static void loadImage(String url, ImageView view, int maxWidth, int maxHeight){
        if(StringUtil.isNull(url) || !MySettingConfigUtil.getLoadImage()){
            return;
        }
        mImageLoader.get(url, ImageCacheManager.getImageListener(view, BaseApplication.getDefaultImage(), BaseApplication.getErrorImage()), maxWidth, maxHeight).getBitmap();
    }

    /**
     * 提供给外部使用的，加载网络动态图片，返回BitMap
     * @param url
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static void loadGifImage(String url, GifImageView view, int maxWidth, int maxHeight){
        if(StringUtil.isNull(url) || !MySettingConfigUtil.getLoadImage()){
            return;
        }
        mImageLoader.get(url, ImageCacheManager.getGifImageListener(view, BaseApplication.getDefaultImage(), BaseApplication.getErrorImage()), maxWidth, maxHeight).getBitmap();
    }

    /**
     * 提供给外部使用的，加载网络图片，返回BitMap
     * @param url
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static Bitmap loadImage(String url, int maxWidth, int maxHeight){
        if(StringUtil.isNull(url) || !MySettingConfigUtil.getLoadImage()){
            return null;
        }
        return mImageLoader.get(url, ImageCacheManager.getImageListener(null, BaseApplication.getDefaultImage(), BaseApplication.getErrorImage()), maxWidth, maxHeight).getBitmap();
    }

    /**
     * 取消请求
     * @param tag
     */
    public static void cancelRequest(Object tag){
        RequestQueueManager.cancelRequest(tag);
    }
}
