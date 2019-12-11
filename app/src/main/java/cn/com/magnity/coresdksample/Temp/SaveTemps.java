package cn.com.magnity.coresdksample.Temp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static cn.com.magnity.coresdksample.Temp.FFCUtil.m_FrameWidth;
import static cn.com.magnity.coresdksample.Config.Temp_DIR;

public class SaveTemps {
    /*private static int m_FrameHeight=160;//高120
    private static int m_FrameWidth=120;//宽度160*/
    private static BufferedWriter bufferedWriter;
    private static   String pathTemps =null;



    public static void saveIntTemps(int[] a,String tempName) {
        File file1 = new File(Temp_DIR);
        if (!file1.exists()) {
            file1.mkdirs();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
        SimpleDateFormat pathName = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        String formatStr = formatter.format(new Date());
        String pathNames = pathName.format(new Date());
        pathTemps=Temp_DIR+ File.separator + pathNames+"FFC校准数据.txt";
        String write = intToString(a);
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(pathTemps,true));
            switch (tempName){
                case "Origin":
                    bufferedWriter.write("\r\n"+formatStr+" 原始数据  : "+"\r\n"+write+"\r\n");
                    break;
                case "FFC":
                    bufferedWriter.write("\r\n"+formatStr+" FFC数据  : "+"\r\n"+write+"\r\n");
                    break;
                case "After":
                    bufferedWriter.write("\r\n"+formatStr+" 校准后的数据  : "+"\r\n"+write+"\r\n");
                    break;
            }

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }



    public static String intToString(int[] a){
        StringBuilder stringBuilder=new StringBuilder();
        int count=0;
        for(int i=0;i<a.length;i++){
            if(i%m_FrameWidth==0){
            stringBuilder.append("\r\n");
            //count=0;
            }
            stringBuilder.append(a[i]).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }





}
