package cn.com.magnity.coresdksample.http.model;

import com.google.gson.annotations.SerializedName;

public class UpRecordEntry {
    /**
     * raw_temperature : 16.9
     * create_time : 20180329071049
     * state_type : 9
     * weight : 5019
     * img_id : 123
     * device_no : 68c7da9e
     * recognition_type : 1
     * temperature : 36.5
     * env_temperature : 15.9
     * temperature_threshold : 38.1
     * height : 2017
     * imgUrl : detection/20180328/925bd45b9ccc60106a52c80205917c26.jpg
     * redImgUrl : detection/20180328/925bd45b9ccc60106a52c80205917c26.jpg
     */

    @SerializedName("raw_temperature")
    private String raw_temperature;
    @SerializedName("create_time")
    private String create_time;
    @SerializedName("state_type")
    private String state_type;
    @SerializedName("weight")
    private String weight;
    @SerializedName("img_id")
    private String img_id;
    @SerializedName("device_no")
    private String device_no;
    @SerializedName("recognition_type")
    private String recognition_type;
    @SerializedName("temperature")
    private String temperature;
    @SerializedName("env_temperature")
    private String env_temperature;
    @SerializedName("temperature_threshold")
    private String temperature_threshold;
    @SerializedName("height")
    private String height;
    @SerializedName("imgUrl")
    private String imgUrl;
    @SerializedName("redImgUrl")
    private String redImgUrl;

    public String getRaw_temperature() {
        return raw_temperature;
    }

    public void setRaw_temperature(String raw_temperature) {
        this.raw_temperature = raw_temperature;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getState_type() {
        return state_type;
    }

    public void setState_type(String state_type) {
        this.state_type = state_type;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getImg_id() {
        return img_id;
    }

    public void setImg_id(String img_id) {
        this.img_id = img_id;
    }

    public String getDevice_no() {
        return device_no;
    }

    public void setDevice_no(String device_no) {
        this.device_no = device_no;
    }

    public String getRecognition_type() {
        return recognition_type;
    }

    public void setRecognition_type(String recognition_type) {
        this.recognition_type = recognition_type;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getEnv_temperature() {
        return env_temperature;
    }

    public void setEnv_temperature(String env_temperature) {
        this.env_temperature = env_temperature;
    }

    public String getTemperature_threshold() {
        return temperature_threshold;
    }

    public void setTemperature_threshold(String temperature_threshold) {
        this.temperature_threshold = temperature_threshold;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getRedImgUrl() {
        return redImgUrl;
    }

    public void setRedImgUrl(String redImgUrl) {
        this.redImgUrl = redImgUrl;
    }

    @Override
    public String toString() {
        return "ExamResultUnknown{" +
                "raw_temperature='" + raw_temperature + '\'' +
                ", create_time='" + create_time + '\'' +
                ", state_type='" + state_type + '\'' +
                ", weight='" + weight + '\'' +
                ", img_id='" + img_id + '\'' +
                ", device_no='" + device_no + '\'' +
                ", recognition_type='" + recognition_type + '\'' +
                ", temperature='" + temperature + '\'' +
                ", env_temperature='" + env_temperature + '\'' +
                ", temperature_threshold='" + temperature_threshold + '\'' +
                ", height='" + height + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", redImgUrl='" + redImgUrl + '\'' +
                '}';
    }
}
