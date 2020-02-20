package cn.com.magnity.coresdksample.Detect;

public class FaceRectCollect {

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

    public FaceRect getFaceRect() {
        return faceRect;
    }

    public void setFaceRect(FaceRect faceRect) {
        this.faceRect = faceRect;
    }

    public void  clearFaceRect(){
        faceRect=null;
    }
}
