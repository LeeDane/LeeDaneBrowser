package com.cn.leedane.browser.util;

import android.content.Context;
import android.os.Environment;

import com.cn.leedane.browser.R;
import com.cn.leedane.browser.application.BaseApplication;
import com.cn.leedane.browser.util.http.HttpConnectionUtil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class FileUtil {

    private static FileUtil fileUtil;
    //默认的每次上传的数据最大的值
    public static final long MAX_EACH_UPLOAD_SIZE = 1024*200;

    private FileUtil(){}
    public static synchronized FileUtil getInstance(){
        if(fileUtil == null)
            fileUtil = new FileUtil();

        return fileUtil;
    }


    public File getCacheDir(Context context){
        File sdDir = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            sdDir = Environment.getExternalStorageDirectory();
        }
        else{
            sdDir = context.getCacheDir();
        }
        File cacheDir = new File(sdDir, context.getResources().getString(R.string.app_dirsname));
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    /**
     * 获取临时文件的文件夹
     *
     * @param context
     * @return
     */
    public static File getTempDir(Context context) {
        File sdDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            sdDir = Environment.getExternalStorageDirectory();
        } else {
            sdDir = context.getCacheDir();
        }
        File cacheDir = new File(sdDir, context.getResources().getString(R.string.app_dirsname) + File.separator + context.getResources().getString(R.string.temp_filepath));
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }


    /**
     * 读取文件(文件夹)大小
     * @param file
     * @return KB
     */
    public static Long getFileSizeFormatKB(File file) {
        return getFileSize(file) / 1024;
    }

    /**
     * 读取文件(文件夹)大小
     * @param file
     * @return MB
     */
    public static Long getFileSizeFormatMB(File file) {
        return getFileSizeFormatKB(file) / 1024;
    }

    /**
     * 读取文件(文件夹)大小
     * @param file
     * @return Byte
     */
    public static Long getFileSize(File file) {
        long size = 0;
        try {
            if (!file.exists()) {
                return size;
            }

            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    size += getFileSize(files[i]);
                }
            } else {
                size = file.length();
            }
        } catch (Exception ex) {

        }
        return new Long(size);
    }

    /**
     * 读取文件(文件夹)文件数
     * @param file
     * @return
     */
    public static int getFileNumber(File file) {
        int size = 0;
        try {
            if (!file.exists()) {
                return size;
            }

            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    size += getFileNumber(files[i]);
                }
            } else {
                size = 1;
            }
        } catch (Exception ex) {

        }
        return size;
    }

    /**
     * 判断文件是否存在
     * @param fileName
     * @return
     */
    public static boolean isExist(String fileName) {
        try {
            if (fileName == null || fileName.equals("")) {
                return false;
            }
            File file = new File(fileName);
            if (file.exists() && file.isFile()) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 从指定的文件路径中加载字符串文件返回
     * @param filePath 文件的路径
     * @return
     * @throws IOException
     */
    public static String getStringFromPath(String filePath) throws IOException{
        StringBuffer bf = new StringBuffer();
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath));
        BufferedReader bufferedReader = new BufferedReader(reader);
        String text = "";
        while( (text = bufferedReader.readLine()) != null){
            bf.append(text);
        }
        reader.close();
        return bf.toString();
    }

    /**
     * 读取断点文件进流中
     * @param filePath
     * @param out
     * @throws IOException
     */
    public static boolean readFile(String filePath, FileOutputStream out) throws IOException {
        boolean result = false;
        try{
            DataInputStream in = new DataInputStream(new FileInputStream(filePath));
            int bytes = 0;
            byte[] buffer = new byte[1024];
            while ((bytes = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytes);
            }
            out.flush();
            in.close();

	        /*//删除文件
	        File file = new File(filePath);
	        if(file.exists())
	        	file.deleteOnExit();*/

            result = true;
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取网络图片的宽和高以及大小
     * @param imgUrl
     * @return 返回数组，依次是：宽，高，大小
     */
    public static Map<String, Object> saveNetWorkLinkToFile(Context context, String imgUrl) {

        Map<String, Object> b = new HashMap<>();
        b.put("isSuccess", false);
        if(StringUtil.isNotNull(imgUrl) && ImageUtil.isSupportType(StringUtil.getFileName(imgUrl))){

            StringBuffer saveFilePath = new StringBuffer();
            File ff = FileUtil.getInstance().getCacheDir(BaseApplication.newInstance());
            ff = new File(ff.getAbsolutePath() + File.separator + "file");
            if(!ff.exists())
                ff.mkdirs();

            saveFilePath.append(ff.getAbsolutePath());
            saveFilePath.append(File.separator);
            saveFilePath.append(StringUtil.getFileName(imgUrl));
            File saveFile = new File(saveFilePath.toString());
            if(!saveFile.exists()){
                try {
                    InputStream inputStream = HttpConnectionUtil.getInputStream(imgUrl);
                    if(inputStream != null){
                        //载入图片到输入流
                        BufferedInputStream bis = new BufferedInputStream(inputStream);
                        //实例化存储字节数组
                        byte[] bytes = new byte[1024];
                        //设置写入路径以及图片名称
                        OutputStream bos = new FileOutputStream(saveFile);
                        int len;
                        while ((len = bis.read(bytes)) > 0) {
                            bos.write(bytes, 0, len);
                        }
                        inputStream.close();
                        bis.close();
                        bos.flush();
                        bos.close();
                        //关闭输出流
                        b.put("message", "文件保存成功，路径是:"+StringUtil.getFileName(imgUrl));
                        b.put("isSuccess", true);
                        b.put("path", saveFilePath.toString());
                    }else{
                        b.put("message", "文件下载异常");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //如果图片未找到
                    b.put("message", "该地址不是正确的图片路径");
                }
            }else{
                b.put("message", "文件已经存在");
                b.put("isSuccess", true);
                b.put("path", saveFilePath.toString());
            }
        }else{
            b.put("message", "文件链接为空");
        }
        return b;

    }
}
