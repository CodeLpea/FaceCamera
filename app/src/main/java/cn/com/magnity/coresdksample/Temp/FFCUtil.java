package cn.com.magnity.coresdksample.Temp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static cn.com.magnity.coresdksample.Config.Temp_DIR;

/**
 *  获取到FFC
 *  存储FFC数据图
 *  读取FFC数据图
 *
 * */
public class FFCUtil {

    public static int m_FrameHeight=160;//高120
    public static int m_FrameWidth=120;//宽度160

    private static BufferedWriter bufferedWriter;
    private static BufferedReader bufferedReader;
    private static   String path = Temp_DIR + File.separator + "FFC.txt";

    public FFCUtil() {
    }

    /**
     * 获取到FFC
     * 原始数据，默认为拍摄一个均匀温度面
     * 算出平均值，并将每个店与平局值进行减
     * 并再减的基础上增加10度，也就是增加10000
     */
    public static int[] getFFC(int[] a) {
        int avg=intAvg(a);
        for(int i=0;i<a.length;i++){
            a[i]=a[i]-avg;
        }
        //保存
        //saveFFC(a);
        return a;
    }
    /**
     * 获取到FFC
     * 原始数据，默认为拍摄一个均匀温度面
     * 根据目标值，并将每个店与平局值进行减
     */
    public static int[] getFFC(int[] a,int targetTemp) {
        for(int i=0;i<a.length;i++){
            a[i]=a[i]-targetTemp;
        }
        return a;
    }

    /**
     * 算平均
     */
    public static int intAvg(int[] a) {
        int Total = 0;
        for(int i=0;i<a.length;i++){
            Total=Total+a[i];
        }
        Log.i("Total: ", String.valueOf((Total)));
        Log.i("a.length: ", String.valueOf((a.length)));
        Log.i("intAvg: ", String.valueOf((Total/a.length)));
        return Total/a.length;
    }





    public static void saveIntFfc(int[] a) {
        File file1 =new File(Temp_DIR);
        if (!file1.exists()) {
            file1.mkdirs();
        }

      /*  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
        String formatStr = formatter.format(new Date());*/

        String write = intToString(a);
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(path));
            bufferedWriter.write(write);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public static String intToString(int[] a){
        StringBuilder stringBuilder=new StringBuilder();
        int count=0;
        for(int i=0;i<a.length;i++){
          /*  if(i%160==0){
            stringBuilder.append("\r\n");
            //count=0;
            }*/
            stringBuilder.append(a[i]).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }


    public static int[] readFfc(){
        File file1 =new File(Temp_DIR);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        try {
            bufferedReader=new BufferedReader(new FileReader(path));
            String read=bufferedReader.readLine();
            int[] array=stringToInt(read);
            /*for(int i=0;i<array.length;i++){
                System.out.println(array[i]);
            }*/
            return array;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new int[1];
    }



    public static int[] stringToInt(String str){
        String[] strAry = str.split(",");
        int[] ary=new int[strAry.length];
        for(int i=0;i<strAry.length;i++){
            if(!strAry[i].equals("")){
                ary[i]=Integer.parseInt(strAry[i]);
            }
        }
        return ary;
    }


    /**
     * 输入最高温度的坐标 （x,y）x-120,y-160
     * 温度Temp
     * 和读取到的FFC矩阵
     * 换算出FFC校准后的温度
     * */
    public static int FfcCalibratTemps(int x, int y, int Temp, int[] FFCTemps){
        int maxTemps = 0;
        try{
            int FfcTemp=0;
            //将一维数组转换为二维数组，以便查询x,y对应的FFC补偿温度
            int j=0;
            int[][]b=new int[m_FrameWidth][m_FrameHeight];
            for(int i=0;i<FFCTemps.length;i++){
                b[j%m_FrameWidth][i/m_FrameWidth]=FFCTemps[i];
            }
            FfcTemp=b[x][y];//得到x,y点的FFC温度
            maxTemps=Temp-FfcTemp;//补偿该点温度
        }catch (Exception e){
            e.getMessage();
            Log.e("FfcCalibratTemps", "FfcCalibratTemps: " );
        }


        return maxTemps;
    }




}
