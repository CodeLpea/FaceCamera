package cn.com.magnity.coresdksample.Detect;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import cn.com.magnity.coresdksample.MyApplication;
import cn.com.magnity.coresdksample.usecache.CurrentConfig;

public class DrawFaceRect {

    public static Paint paint =new Paint();

    /**
     * 绘制人脸检测框
     *
     * @param canvas      给定的画布
     * @param face        需要绘制的人脸信息
     * @param width       原图宽
     * @param frontCamera 是否为前置摄像头，如为前置摄像头需左右对称
     */
    private static final  String  TAG="DrawFaceRect";
     public static void drawFaceRect(Canvas canvas, FaceRect face, int width, boolean frontCamera) {
        if (canvas == null) {
            return;
        }
         //设置画笔的颜色
         paint.setColor(Color.GREEN);
         paint.setStrokeWidth(2);//设置画笔的粗度

         Rect rect = face.faceRect;//获取人脸区域
         //绘制人脸识别框
         //左
         canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, paint);
         //右
         canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint);
         //上
         canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
         //下
         canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
        /**
         * 绘制人脸监测点
         */
        if (face.facePoints != null) {
            //遍历检测点，并绘制
            for (Point p : face.facePoints) {
                if (frontCamera) {
                    p.y = width - p.y;
                }
                canvas.drawPoint(p.x, p.y, paint);
            }

          /**
           * face.facePoints[20]右下角嘴巴
           * face.facePoints[19]左下角嘴巴
           * face.facePoints[18]鼻尖
           * face.facePoints[17]右眼中间
           * face.facePoints[16]左眼中间
           * face.facePoints[15]下嘴唇中间
           * face.facePoints[14]中间嘴唇
           * face.facePoints[13]上嘴唇中间
           * 换算比例7
           * (float)（下嘴唇-上嘴唇）/（右嘴唇-左边嘴唇）
           *
           *  Log.i(TAG, "下嘴唇face.facePoints[15].y: "+face.facePoints[15].y);
           Log.i(TAG, "上嘴唇face.facePoints[13].y: "+face.facePoints[13].y);
           Log.i(TAG, "右嘴角face.facePoints[20].x: "+face.facePoints[20].x);
           Log.i(TAG, "左嘴角face.facePoints[19].x: "+face.facePoints[19].x);
           int Ver=face.facePoints[15].y-face.facePoints[13].y;
           int Hor=face.facePoints[20].x-face.facePoints[19].x;
           float proportion=(float)((float)Ver/(float) Hor);

           Log.i(TAG, "嘴唇开口比例: "+proportion);
           * */
         /*   for(int i=0;i<face.facePoints.length;i++){
                Point p=face.facePoints[i];
                if(i>12){
                    if (frontCamera) {
                        p.y = width - p.y;
                    }
                    canvas.drawPoint(p.x, p.y, paint);
                }
            }*/
        }
    }

    /**
     * 旋转人脸识别框
     * @param r
     * @param height
     * @return
     */
     public static Rect RotateDeg90(Rect r, int height) {
        int left = r.left;
        r.left = height - r.bottom;
        r.bottom = r.right;
        r.right = height - r.top;
        r.top = left;
        return r;
    }

    /**
     * 旋转人脸识别点
     * @param p
     * @param height
     * @return
     */
     public static Point RotateDeg90(Point p, int height) {
        int x = p.x;
        p.x = height - p.y;
        p.y = x;
        return p;
    }

    public  static boolean MouthDetection(FaceRect face){
         boolean result=false;
     /*   Log.i(TAG, "下嘴唇face.facePoints[15].y: "+face.facePoints[15].y);
        Log.i(TAG, "上嘴唇face.facePoints[13].y: "+face.facePoints[13].y);
        Log.i(TAG, "右嘴角face.facePoints[20].x: "+face.facePoints[20].x);
        Log.i(TAG, "左嘴角face.facePoints[19].x: "+face.facePoints[19].x);*/
        int Ver=face.facePoints[15].y-face.facePoints[13].y;
        int Hor=face.facePoints[20].x-face.facePoints[19].x;
        float proportion=(float)((float)Ver/(float) Hor);
        if(proportion>0.5){
            result=true;
            //Log.i(TAG, "张嘴了！！！！:");
        }else {
            Log.i(TAG, "暂时注释张嘴提醒，医院测试中提出 ");
//            TtsSpeak.getInstance().SpeechRepead("请张开嘴巴   ", CurrentConfig.getInstance().getCurrentData().getError_voice());
        }
       // Log.i(TAG, "嘴唇开口比例: "+proportion);
         return result;
    }
    public  static boolean scopeDetection(FaceRect face){//这个宽和高是反的。
        boolean result=false;
        int xPlace=face.facePoints[18].x;//中间嘴唇的x轴位置
        int yPlace=face.facePoints[18].y;//中间嘴唇的y轴位置

        if(CurrentConfig.getInstance().getCurrentData().getLineLeft()<=xPlace&&xPlace<=
                CurrentConfig.getInstance().getCurrentData().getLineRight()&&
                CurrentConfig.getInstance().getCurrentData().getLineDown()>=yPlace&&
                CurrentConfig.getInstance().getCurrentData().getLineUp()<=yPlace){
           //左侧小于x
           //右侧大于x
           //上方小于x
           //下方大于x
            result=true;
        }
        return result;
    }
    public  static void DrawScopeDetection(int PREVIEW__WIDTH,int PREVIEW_HEIGHT,Canvas canvas) {//这个宽和高是反的。
        Paint paint = new Paint(); //创建画笔对象
        paint.setColor(Color.RED);//设置画笔的颜色
        paint.setStrokeWidth(3);//设置画笔的粗度
        //画上方的一条线
        canvas.drawLine(0, CurrentConfig.getInstance().getCurrentData().getLineUp(),PREVIEW__WIDTH, CurrentConfig.getInstance().getCurrentData().getLineUp(),paint);
        //画左侧竖的一条线
        canvas.drawLine(CurrentConfig.getInstance().getCurrentData().getLineLeft(),0, CurrentConfig.getInstance().getCurrentData().getLineLeft(),PREVIEW_HEIGHT,paint);
        //画右侧竖的一条线
        canvas.drawLine(CurrentConfig.getInstance().getCurrentData().getLineRight(),0, CurrentConfig.getInstance().getCurrentData().getLineRight(),PREVIEW_HEIGHT,paint);
        //画下方的一条线
        canvas.drawLine(0,CurrentConfig.getInstance().getCurrentData().getLineDown(),PREVIEW__WIDTH, CurrentConfig.getInstance().getCurrentData().getLineDown(),paint);
    }

}
