package cn.com.magnity.coresdksample.utils;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static cn.com.magnity.coresdksample.utils.Config.SavaRootDirPath;
import static cn.com.magnity.coresdksample.utils.FlieUtil.getFileName;

public class logSave {
    public logSave() {
    }

    /**
     * 温度照片
     */

    public static void saveLog(String str, String num) {
        String path=SavaRootDirPath+ File.separator + getFileName()+ File.separator+"TempPhotoName" + TimeUitl.currentDayTime() + ".txt";
        FlieUtil.isExistFlie(path);
        File files1 = new File(path);
        FileWriter file = null;
        if (str.equals("温度照片")) {
            try {
                file = new FileWriter(files1, true);
                file.write(num);
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    /**
     * 温度照片
     */
    public static void saveLog2(String str, String num) {
        String path=SavaRootDirPath+ File.separator + getFileName()+ File.separator+"PersonPhotoName" + TimeUitl.currentDayTime()+".txt";
        FlieUtil.isExistFlie(path);
       // File files2 = new File(getFolderPathToday(),"人脸照片日志" + TimeUitl.currentDayTime()+".txt");
        File files2 = new File(path);
        FileWriter file2 = null;
        if (str.equals("人脸照片")) {
            try {
                file2=new FileWriter(files2,true);
                file2.write(num);
                file2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
