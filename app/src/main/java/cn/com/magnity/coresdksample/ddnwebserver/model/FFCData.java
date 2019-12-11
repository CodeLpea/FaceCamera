package cn.com.magnity.coresdksample.ddnwebserver.model;

import com.alibaba.fastjson.annotation.JSONField;

import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;

/**
 * FFC参数信息
 * FFC补偿参数，可为负数
 * FFC黑体校准参考值
 * */
public class FFCData {
    //FFC补偿参数，可为负数
    @JSONField(name = WebConfig.FFC_COMPENSATION_PARAMETER)
    private float compensation;
    //FFC黑体校准参考值
    @JSONField(name =  WebConfig.FFC_CALIBRATION_PARAMETER)
    private float calibration;

    public float getCompensation() {
        return compensation;
    }

    public void setCompensation(float compensation) {
        this.compensation = compensation;
    }

    public float getCalibration() {
        return calibration;
    }

    public void setCalibration(float calibration) {
        this.calibration = calibration;
    }

    @Override
    public String toString() {
        return "FFCData{" +
                "compensation='" + compensation + '\'' +
                ", calibration='" + calibration + '\'' +
                '}';
    }
}
