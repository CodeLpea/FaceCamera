package cn.com.magnity.coresdksample.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by xiaoyuren on 2018/2/1.
 * 项目名称：didano-robot
 * 类描述：文件操作工具类
 * company：www.didano.cn
 * email：vin.qin@didano.cn
 * 创建时间：2018/2/1 17:00
 */

public class FileUtil {
    private static final String TAG = "FileUtil";
    /**
     * 判断文件/目录是否存在
     *
     * @param fileName 文件名
     * @return 存在true 不存在false
     */
    public static boolean isFileExist(String fileName) {
        boolean ret = false;
        try {
            File file = new File(fileName);
            ret = file.exists();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * 创建目录，多层级创建
     *
     * @param dirName 目录名
     * @return
     */
    public static boolean createDirs(String dirName) {
        boolean ret = false;
        try {
            File file = new File(dirName);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
        }

        return ret;
    }


    /**
     * 创建文件
     *
     * @param fileName 文件名
     * @return
     */
    public static boolean createFile(String fileName) {
        boolean ret = false;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {

        }
        return ret;
    }

    /**
     * 删除指定的文件或文件夹(包括自身也会被删除)
     * @param fileName 文件的绝对路径
     * @return 删除成功返回true 删除失败返回false
     */
    public static boolean deleteFile(String fileName) {
        boolean ret = false;
        try {
            File file = new File(fileName);
            if (file.exists()) {
                if(file.isDirectory()){
                    File listFile[] = file.listFiles();
                    for(int i = 0; i < listFile.length; i++)
                        deleteFile(listFile[i].getAbsolutePath());
                }
                file.delete();
            }
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     *删除指定目下的文件或文件夹，不包括自身
     * @param rootDir 指定目录
     */
    public static void deleteFilesOfDir(File rootDir){
        File files[] = rootDir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteFilesOfDir(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteFilesOfDir(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

	 /**
     * 拷贝目录
     * @param srcDir 源目录
     * @param dstDir 目标目录
     * @return true 成功 false 失败
     */
    public static boolean copyDir(String srcDir, String dstDir) {
        boolean ret = false;
        try {
            /**
             * 确保为源目录、目标目录
             */
            File rootDir = new File(srcDir);
            if (!rootDir.exists() || rootDir.isFile())
                return ret;
            File targetDir = new File(dstDir);
            if (!targetDir.exists())
                targetDir.mkdir();

            /**
             * 目录下所有文件/子目录
             */
            File[] listFile = rootDir.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    /**
                     * 如果是目录，则递归
                     */
                    copyDir(listFile[i].getPath() + "/", dstDir + "/" + listFile[i].getName() + "/");
                } else {
                    /**
                     * 文件则直接拷贝
                     */
                    copyFile(listFile[i].getPath(), dstDir + "/" + listFile[i].getName());
                }
            }
            ret = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 文件拷贝
     *
     * @param srcFile 源文件
     * @param dstFile 目标文件
     * @return true 成功 false 失败
     */
    public static boolean copyFile(String srcFile, String dstFile) {
        boolean ret = false;
        try {
            InputStream is = new FileInputStream(srcFile);
            OutputStream os = new FileOutputStream(dstFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = is.read(bt)) > 0) {
                os.write(bt, 0, c);
            }
            is.close();
            os.close();
            ret = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * 移动文件
     * @param src
     * @param dest
     */
    public static void moveFile(String src, String dest){
        File file = new File(src);
        if(file.exists()){
            file.renameTo(new File(dest));
        }
    }

    /**
     * 读取文件中的全部内容
     * @param fileName 要读取的文件, 绝对路径
     * @return 错误返回null
     */
    public static String readAll(String fileName){
        String context = null;
        BufferedInputStream bufferInput = null;
        try{
            bufferInput = new BufferedInputStream(new FileInputStream(fileName));
            int bytesRead = 0;
            Log.i(TAG, "file size : " + bufferInput.available());
            byte[] buffer = new byte[bufferInput.available()];
            bytesRead = bufferInput.read(buffer, 0, buffer.length);
            //将读取的字节转为字符串对象
            context = new String(buffer, 0, bytesRead);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(bufferInput != null){
                    bufferInput.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return context;
    }

    /**
     * 写入字符串到文件中，覆盖以前的内容
     * @param fileName 要写入数据的文件，绝对路径, 文件不存在则自动创建
     * @param context 字符串
     */
    public static void write(String fileName, String context){
        BufferedOutputStream bufferOutput = null;
        File file = null;
        try{
            file = new File(fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            bufferOutput = new BufferedOutputStream(new FileOutputStream(file));
            bufferOutput.write(context.getBytes(), 0, context.length());
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(bufferOutput != null){
                    bufferOutput.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 将char型数据保存在文件中
     * @param fileName 要保存的文件名
     * @param context 要保存的数据
     * @param lineLength 文件中每一行的长度
     */
    public static void write(String fileName, char[] context, int lineLength){
        File file = new File(fileName);
        try{
            file.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(fileName)));
            for(int i = 0; i < context.length; i++){
               // Log.i(TAG, "bufferedWriter write : " + String.valueOf((int)context[i]) + " " + Integer.valueOf(context[i]).toString());
                //不能使用String.valueOf(context[i])，输出会是乱码
                //可以使用String.valueOf((int)context[i]) Integer.valueOf(context[i]).toString()
                bufferedWriter.write(Integer.valueOf(context[i]).toString() + " ");
                if(((i + 1) % lineLength) == 0){
                    bufferedWriter.newLine();
                }
            }
            bufferedWriter.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 检查文件是否在指定一段时间内有更新
     * @param path 需要检查的文件，绝对路径
     * @param currentDate 当前的时间，单位ms
     * @param diff 当前时间与文件最后修改时间的差值 单位ms
     * @return 有更新返回true， 没有更新或文件不存在返回false
     */
    public static boolean isModified(String path, int currentDate, int diff){
        File file = new File(path);
        if(file.exists()){
            long lastModify = file.lastModified();
             return ((Math.abs(currentDate - lastModify) < diff) ? true : false);
        }

        return false;
    }

    /**
     * 获取指定文件夹下的所有文件包括子目录下的文件
     * @param file  指定文件夹的File对象
     * @param fileList 保存指定文件夹下的所有文件File对象
     */
    public static void getFiles(File file, List<File> fileList, FileFilter fileFilter){
        if(file.isDirectory()) {
            File[] files = file.listFiles(fileFilter);
            for (File f:
                    files) {
                getFiles(f, fileList, fileFilter);
            }
        }else{
            fileList.add(file);
        }
    }
    /**
     * 获取指定文件夹下所有的文件并按最近修改时间进行排序
     * @param file 要获取的文件夹File对象
     * @return 返回经过排序后的文件对象
     */
    public static List<File> listFilesByLastModified(File file, FileFilter fileFilter){
        List<File> allFiles = new ArrayList<>();
        //先获取file对象下的所有文件，报错子目录下的文件
        getFiles(file, allFiles, fileFilter);
        //然后根据文件更新时间，从最新到最旧排序
        if(allFiles.size() > 1) {
            Collections.sort(allFiles, new FileLastModifiedComparator());
        }

        return allFiles;
    }

    private static class FileLastModifiedComparator implements Comparator<File> {

        @Override
        public int compare(File file, File t1) {
            return file.lastModified() > t1.lastModified() ? -1 : 1;
        }
    }
}
