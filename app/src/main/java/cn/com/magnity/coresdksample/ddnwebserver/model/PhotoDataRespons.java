package cn.com.magnity.coresdksample.ddnwebserver.model;

import com.alibaba.fastjson.annotation.JSONField;
import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;
import cn.com.magnity.coresdksample.ddnwebserver.database.PhotoRecordDb;

import java.util.List;

public class PhotoDataRespons {
    @JSONField(name = WebConfig.DATALIST)
    private List<PhotoRecordDb> photoRecordDbList;
    @JSONField(name = WebConfig.ALLSIZE)
    private int allSize;

    public List<PhotoRecordDb> getPhotoRecordDbList() {
        return photoRecordDbList;
    }

    public void setPhotoRecordDbList(List<PhotoRecordDb> photoRecordDbList) {
        this.photoRecordDbList = photoRecordDbList;
    }

    public int getAllSize() {
        return allSize;
    }

    public void setAllSize(int allSize) {
        this.allSize = allSize;
    }

    @Override
    public String toString() {
        return "PhotoDataRespons{" +
                "photoRecordDbList=" + photoRecordDbList +
                ", allSize=" + allSize +
                '}';
    }
}
