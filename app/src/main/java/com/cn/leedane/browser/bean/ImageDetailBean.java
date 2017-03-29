package com.cn.leedane.browser.bean;

import com.cn.leedane.browser.bean.base.IdBean;

/**
 * 图库查看器的实体类
 */
public class ImageDetailBean extends IdBean {

    /**
     * 图片的路径
     */
    private String path;

    private int width;

    private int height;

    private long lenght;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getLenght() {
        return lenght;
    }

    public void setLenght(long lenght) {
        this.lenght = lenght;
    }
}
