package cn.com.magnity.coresdksample.detect;

public class FaceRectCollect {

    //原始数据，用于在红外判断处保存图片
    public byte[] Nv21;
    public FaceRect faceRect;
    public volatile boolean ifTaken=false;
    private static FaceRectCollect Instance;

    public static FaceRectCollect getInstance() {
        if (Instance == null) {
            synchronized (FaceRectCollect.class) {
                Instance = new FaceRectCollect();
            }
        }
        return Instance;
    }

    public byte[] getNv21() {
        return Nv21;
    }

    public void setNv21(byte[] nv21) {
        Nv21 = nv21;
    }

    public  FaceRect getFaceRect() {
        return faceRect;
    }

    public void setFaceRect(FaceRect faceRect) {
        this.faceRect = faceRect;
    }

    public void  clearFaceRect(){
        faceRect=null;
    }
}
