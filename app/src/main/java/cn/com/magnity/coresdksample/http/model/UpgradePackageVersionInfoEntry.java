package cn.com.magnity.coresdksample.http.model;

import com.google.gson.annotations.SerializedName;

/**
 * 描述当前服务器上最新升级包的版本信息
 * Created by Long on 2018/10/8.
 */

public class UpgradePackageVersionInfoEntry {
    @SerializedName("version")
    private String mVersion;
    @SerializedName("version_details")
    private String mVersionDetails;
    @SerializedName("version_type")
    private String mVersionType;
    @SerializedName("version_url")
    private String mVersionUrl;

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String mVersion) {
        this.mVersion = mVersion;
    }

    public String getVersionDetails() {
        return mVersionDetails;
    }

    public void setVersionDetails(String mVersionDetails) {
        this.mVersionDetails = mVersionDetails;
    }

    public String getVersionType() {
        return mVersionType;
    }

    public void setVersionType(String mVersionType) {
        this.mVersionType = mVersionType;
    }

    public String getVersionUrl() {
        return mVersionUrl;
    }

    public void setVersionUrl(String mVersionUrl) {
        this.mVersionUrl = mVersionUrl;
    }

    @Override
    public String toString() {
        return "UpgradePackageVersionInfoEntry{" +
                "mVersion='" + mVersion + '\'' +
                ", mVersionDetails='" + mVersionDetails + '\'' +
                ", mVersionType='" + mVersionType + '\'' +
                ", mVersionUrl='" + mVersionUrl + '\'' +
                '}';
    }
}
