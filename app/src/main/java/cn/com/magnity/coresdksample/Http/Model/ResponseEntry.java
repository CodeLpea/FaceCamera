package cn.com.magnity.coresdksample.Http.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Long on 2018/7/24.
 */

public class ResponseEntry<T> {
    public transient static final int SUCCESS = 0;
    public transient static final int FAILED = 1;
    @SerializedName("code")
    private int    code;
    @SerializedName("message")
    private String message;
    @SerializedName("data_len")
    private long   data_len;
    @SerializedName("success")
    private boolean mSuccess;
    @SerializedName("data")
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getData_len() {
        return data_len;
    }

    public void setData_len(long data_len) {
        this.data_len = data_len;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public void setmSuccess(boolean success) {
        this.mSuccess = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseEntry{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data_len=" + data_len +
                ", mSuccess=" + mSuccess +
                ", data=" + data +
                '}';
    }
}
