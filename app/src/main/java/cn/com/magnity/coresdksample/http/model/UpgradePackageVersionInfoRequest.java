package cn.com.magnity.coresdksample.http.model;

import com.google.gson.annotations.SerializedName;

/**
 * 上传当前版本信息，查看是否有最新版本
 * Created by Long on 2018/10/8.
 */

public class UpgradePackageVersionInfoRequest {
    @SerializedName("model")
    private String mHardWareDeviceType;
    @SerializedName("system_type")
    private int mSoftWareType;//软件类型，比如4表示小诺合一版本， 5表示手足口软件，6表示电子班牌软件，7表示android版刷一刷软件
    @SerializedName("version")
    private String mCurrentApkVersion;
    @SerializedName("device_no")
    private String mHardWareDeviceNumber;

    public String getHardWareDeviceType() {
        return mHardWareDeviceType;
    }

    public void setHardWareDeviceType(String hardWareDeviceType) {
        this.mHardWareDeviceType = hardWareDeviceType;
    }

    public int getSoftWareType() {
        return mSoftWareType;
    }

    public void setSoftWareType(int softWareType) {
        this.mSoftWareType = softWareType;
    }

    public String getCurrentApkVersion() {
        return mCurrentApkVersion;
    }

    public void setCurrentApkVersion(String currentApkVersion) {
        this.mCurrentApkVersion = currentApkVersion;
    }

    public String getHardWareDeviceNumber() {
        return mHardWareDeviceNumber;
    }

    public void setHardWareDeviceNumber(String hardWareDeviceNumber) {
        this.mHardWareDeviceNumber = hardWareDeviceNumber;
    }

    @Override
    public String toString() {
        return "UpgradePackageVersionInfoRequest{" +
                "mHardWareDeviceType='" + mHardWareDeviceType + '\'' +
                ", mSoftWareType=" + mSoftWareType +
                ", mCurrentApkVersion='" + mCurrentApkVersion + '\'' +
                ", mHardWareDeviceNumber='" + mHardWareDeviceNumber + '\'' +
                '}';
    }
}
