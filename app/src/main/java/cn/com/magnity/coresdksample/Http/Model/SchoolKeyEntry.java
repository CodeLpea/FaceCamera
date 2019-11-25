package cn.com.magnity.coresdksample.Http.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Long on 2018/7/24.
 */

public class SchoolKeyEntry {
    @SerializedName("key")
    private String mKey; //学校的唯一Key
    @SerializedName("key_time")
    private String mKeyTime; //key的创建时间
    @SerializedName("school_id")
    private int mSchoolId;
    @SerializedName("school_title")
    private String mSchoolName;

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public String getKeyTime() {
        return mKeyTime;
    }

    public void setKeyTime(String keyTime) {
        this.mKeyTime = keyTime;
    }

    public int getSchoolId() {
        return mSchoolId;
    }

    public void setSchoolId(int schoolId) {
        this.mSchoolId = schoolId;
    }

    public String getSchoolName() {
        return mSchoolName;
    }

    public void setSchoolName(String schoolName) {
        this.mSchoolName = schoolName;
    }

    @Override
    public String toString() {
        return "SchoolKeyEntry{" +
                "mKey='" + mKey + '\'' +
                ", mKeyTime='" + mKeyTime + '\'' +
                ", mSchoolId='" + mSchoolId + '\'' +
                ", mSchoolName='" + mSchoolName + '\'' +
                '}';
    }
}
