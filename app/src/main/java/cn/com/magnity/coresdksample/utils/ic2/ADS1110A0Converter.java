package cn.com.magnity.coresdksample.utils.ic2;

import android.util.Log;

import com.didanuo.robot.libi2c.I2CInterface;

/**
 * Created by Long on 2018/1/25.
 */

public class ADS1110A0Converter{
    private static final String TAG = "ADS1110A0Converter";
    private static final String m_dev_path = "/dev/i2c-4";
    private static final byte  SLAVE_ADDRESS = 0x48;

    private static final int Vref = 4096;
    private static final int CONST1 = 50500;
    private static final int CONST2 = 5000;
    private static final int CONST3 = 253;
    private static final double V1 = 157.2390;
    private static final double PT100R_1 = 80.31;
    private static final double PT100R_2 = 100;
    private static final double PT100R_3 = 119.4;
    private static final double PT100R_4 = 138.51;
    private static int m_adsFd = -1;
    private I2CInterface m_i2cInterface = null;
    private int mI2COK = 0;

    public class ADTemperature{
        char   mAdc;
        float  mAdsTemperature;

        public char getmAdc() {
            return mAdc;
        }

        public float getmAdsTemperature() {
            return mAdsTemperature;
        }
    }

    private ADS1110A0Converter(){
        mI2COK = Open();
    }

    private static class ADS1110A0ConverterInner{
        private static ADS1110A0Converter mAds = new ADS1110A0Converter();

    }

    public static ADS1110A0Converter GetInstance(){
        return ADS1110A0ConverterInner.mAds;
    }
    public ADTemperature Temperature() {
        ADTemperature adc = new ADTemperature();
        byte[] buffer = new byte[3];
        int ret = m_i2cInterface.read((char)m_adsFd, buffer, 3);
        if(buffer[2] != 0x8c)
        {
            adc.mAdc = (char)buffer[0];
            adc.mAdc <<= 8;
            adc.mAdc |= (buffer[1] & 0x00FF);
            double vdelta = Vdelta((double)adc.mAdc);
            double R = PT100R(vdelta);
            adc.mAdsTemperature = (float) Temperature(R);
            Log.i(TAG, "ADC         : " + (int)adc.mAdc);
            Log.i(TAG, "Vdelta      : " + vdelta);
            Log.i(TAG, "R           : " + R);
            Log.i(TAG, "temperature : " + adc.mAdsTemperature);
        }

        return adc;
    }

    public boolean ModuleOk(){
        return mI2COK < 0 ? false : true;
    }

    public void Destory(){
        Close();
    }

    private int Open() {
        m_i2cInterface = new I2CInterface();
        m_adsFd = m_i2cInterface.open(m_dev_path);
        if(m_adsFd > 0) {
            m_i2cInterface.set_slave_addr((char) m_adsFd, SLAVE_ADDRESS);
            byte[] buffer = new byte[1];
            buffer[0] = (byte) 0x8f;
            int ret = m_i2cInterface.write((char) m_adsFd, buffer, 1);
            if(ret < 1){
//                ModuleSelfCheck selfCheck = new ModuleSelfCheck();
//                selfCheck.setLepton("ads i2c write error!code=" + ret);
//                selfCheck.upload();
            }
        }else{
//            ModuleSelfCheck selfCheck = new ModuleSelfCheck();
//            selfCheck.setLepton("ads i2c 模块打开失败，请检查是否是老温度模块");
//            selfCheck.upload();
        }
        return m_adsFd;
    }

    private void Close() {
        if(m_adsFd > 0) {
            m_i2cInterface.close((char) m_adsFd);
        }
    }

    private double Vout(double adc)
    {
        //  return ((adc / 32768) * 2048);
        return ((adc / 32768) * 2.048);
    }

    private double V2(double vout)
    {
        //return (V1 - ((101 * vout - 90 * V1) / (101 * 9.2)));
        return (V1 - ((101 * vout - 90 * V1) / (101 * 9.8)));
    }

    private double IR12(double v2)
    {
        //return ((V1 / 101) * 100 - v2);
        // return (((V1 / 101) * 100 - v2) / 1000);
        return ((100000 * V1 - 101000 * v2) / (101000 * 1000));
    }

    private double  PT100R(double v2, double ir12)
    {
        //return ((5 * v2) / (Vref - v2 + 5 * ir12));
        // return ((5000 * v2) / (Vref - v2 + 5000 * ir12));
        return ((2000 * v2) / (Vref - v2 + 2000 * ir12));
    }

    private double Temperature(double r)
    {
        double l_t = 0;
        if(PT100R_1 < r && r < PT100R_2)
        {
            l_t = (((r - PT100R_1) / (PT100R_2 - PT100R_1)) * 50 - 50);
        }
        else if(PT100R_2 < r && r < PT100R_3)
        {
            l_t = (((r - PT100R_2)/(PT100R_3 - PT100R_2)) * 50 );
        }
        else if(PT100R_3 < r && r < PT100R_4)
        {
            l_t = (((r - PT100R_3 ) / (PT100R_4 - PT100R_3)) * 50 + 50);
        }

        return l_t;
    }

    private double Vdelta(double adc)
    {
        return ((2048 * adc) / (8 * 32768));
    }

    private double PT100R(double vdelta)
    {
        return ((2000 * Vref - 22000 * vdelta) / (10 * Vref + 11 * vdelta));
    }
}
