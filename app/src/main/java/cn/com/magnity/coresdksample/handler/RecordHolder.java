package cn.com.magnity.coresdksample.handler;

import android.graphics.Bitmap;

import cn.com.magnity.coresdksample.detect.FaceRect;

public class RecordHolder {
    private Bitmap personBitmap;
    private Bitmap tempBitmap;
    private String temp;
    private FaceRect faceRect;

    public Bitmap getPersonBitmap() {
        return personBitmap;
    }

    public void setPersonBitmap(Bitmap personBitmap) {
        this.personBitmap = personBitmap;
    }

    public Bitmap getTempBitmap() {
        return tempBitmap;
    }

    public void setTempBitmap(Bitmap tempBitmap) {
        this.tempBitmap = tempBitmap;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public FaceRect getFaceRect() {
        return faceRect;
    }

    public void setFaceRect(FaceRect faceRect) {
        this.faceRect = faceRect;
    }
}
