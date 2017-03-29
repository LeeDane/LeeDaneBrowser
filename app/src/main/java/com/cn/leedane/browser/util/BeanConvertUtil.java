package com.cn.leedane.browser.util;

import android.util.Log;

import com.cn.leedane.browser.bean.HttpResponseCommonBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Bean转化工具类
 */
public class BeanConvertUtil {
    public static final String TAG = "BeanConvertUtil";
    private static Gson gson = new GsonBuilder()
   // .excludeFieldsWithoutExposeAnnotation() //不导出实体中没有用@Expose注解的属性
     //.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
    .enableComplexMapKeySerialization() //支持Map的key为复杂对象的形式
    //.setPrettyPrinting() //对json结果格式化

    //.setVersion(1.0)    //有的字段不是一开始就有的,会随着版本的升级添加进来,那么在进行序列化和返序列化的时候就会根据版本号来选择是否要序列化.
    //@Since(版本号)能完美地实现这个功能.还的字段可能,随着版本的升级而删除,那么
    //@Until(版本号)也能实现这个功能,GsonBuilder.setVersion(double)方法需要调用.
    .create();

    /**
     * 将响应请求的字符串转化成HttpResponseCommonBean对象
     * @param str
     * @return
     */
    public static HttpResponseCommonBean strConvertToCommonBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseCommonBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseCommonBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseCommonBean对象转换失败");
        }
        return null;
    }
}
