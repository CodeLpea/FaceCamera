package cn.com.magnity.coresdksample.utils;

import android.os.Environment;

import java.io.File;

public class FlieUtil {
    /**
     * 初始化文件夹
     * 检查，生成
     * */
    public static void initFile(String filePath) {
        File file1 = Environment.getExternalStorageDirectory();
        if (null != file1) {
            file1 = new File(file1, filePath);//file1位根目录，filePath为文件夹：名称
            if (!file1.exists()) {
                file1.mkdirs();
            }
        }
        }

/**
 * 检查，生成文件
 * */
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        initFile(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
