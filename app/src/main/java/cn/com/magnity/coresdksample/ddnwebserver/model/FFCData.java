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
    private String compensation;
    //FFC黑体校准参考值
    @JSONField(name =  WebConfig.FFC_CALIBRATION_PARAMETER)
    private String calibration;

    public String getCompensation() {
        return compensation;
    }

    public void setCompensation(String compensation) {
        this.compensation = compensation;
    }

    public String getCalibration() {
        return calibration;
    }

    public void setCalibration(String calibration) {
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
