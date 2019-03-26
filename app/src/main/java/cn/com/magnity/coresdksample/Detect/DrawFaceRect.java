package cn.com.magnity.coresdksample.Detect;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import cn.com.magnity.coresdksample.MyApplication;
import cn.com.magnity.coresdksample.utils.Config;

public class DrawFaceRect {
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
        Paint paint = new Paint(); //创建画笔对象
        paint.setColor(Color.GREEN);//设置画笔的颜色
        int len = (face.bound.bottom - face.bound.top) / 8;
        if (len / 8 >= 2) {
            paint.setStrokeWidth(len / 8);//设置画笔的粗度
        } else {
            paint.setStrokeWidth(2);//设置画笔的粗度
        }
        Rect rect = face.bound;//获取人脸区域
        if (frontCamera) {
            int top = rect.top;
            rect.top = width - rect.bottom;
            rect.bottom = width - top;
        }
        int drawL = rect.left - len;
        int drawR = rect.right + len;
        int drawU = rect.top - len;
        int drawD = rect.bottom + len;
    /*     Log.i("人脸框坐标", "len "+len);
         Log.i("人脸框坐标", "left "+drawL);
         Log.i("人脸框坐标", "right "+drawR);
         Log.i("人脸框坐标", "top "+drawU);
         Log.i("人脸框坐标", "bottom "+drawD);*/
         if(MyApplication.getInstance().mView!=null){
             MyApplication.getInstance().faceRect.bound=face.bound;
             MyApplication.getInstance().faceRect.point=face.point;
         MyApplication.getInstance().juGeFaceRect.setxStart(drawL);//设置区域
         MyApplication.getInstance().juGeFaceRect.setxStop(drawR);
         MyApplication.getInstance().juGeFaceRect.setyStart(drawU);
         MyApplication.getInstance().juGeFaceRect.setyStop(drawD);
         }
        //绘制人脸识别框，每两个一组
        canvas.drawLine(drawL, drawD, drawL, drawD - len, paint);
        canvas.drawLine(drawL, drawD, drawL + len, drawD, paint);
        canvas.drawLine(drawR, drawD, drawR, drawD - len, paint);
        canvas.drawLine(drawR, drawD, drawR - len, drawD, paint);
        canvas.drawLine(drawL, drawU, drawL, drawU + len, paint);
        canvas.drawLine(drawL, drawU, drawL + len, drawU, paint);
        canvas.drawLine(drawR, drawU, drawR, drawU + len, paint);
        canvas.drawLine(drawR, drawU, drawR - len, drawU, paint);
        /**
         * 绘制人脸监测点
         */
        if (face.point != null) {
            //遍历检测点，并绘制
            for (Point p : face.point) {
                if (frontCamera) {
                    p.y = width - p.y;
                }
                canvas.drawPoint(p.x, p.y, paint);
            }

          /**
           * face.point[20]右下角嘴巴
           * face.point[19]左下角嘴巴
           * face.point[18]鼻尖
           * face.point[17]右眼中间
           * face.point[16]左眼中间
           * face.point[15]下嘴唇中间
           * face.point[14]中间嘴唇
           * face.point[13]上嘴唇中间
           * 换算比例
           * (float)（下嘴唇-上嘴唇）/（右嘴唇-左边嘴唇）
           *
           *  Log.i(TAG, "下嘴唇face.point[15].y: "+face.point[15].y);
           Log.i(TAG, "上嘴唇face.point[13].y: "+face.point[13].y);
           Log.i(TAG, "右嘴角face.point[20].x: "+face.point[20].x);
           Log.i(TAG, "左嘴角face.point[19].x: "+face.point[19].x);
           int Ver=face.point[15].y-face.point[13].y;
           int Hor=face.point[20].x-face.point[19].x;
           float proportion=(float)((float)Ver/(float) Hor);

           Log.i(TAG, "嘴唇开口比例: "+proportion);
           * */
         /*   for(int i=0;i<face.point.length;i++){
                Point p=face.point[i];
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
        Log.i(TAG, "下嘴唇face.point[15].y: "+face.point[15].y);
        Log.i(TAG, "上嘴唇face.point[13].y: "+face.point[13].y);
        Log.i(TAG, "右嘴角face.point[20].x: "+face.point[20].x);
        Log.i(TAG, "左嘴角face.point[19].x: "+face.point[19].x);
        int Ver=face.point[15].y-face.point[13].y;
        int Hor=face.point[20].x-face.point[19].x;
        float proportion=(float)((float)Ver/(float) Hor);
        if(proportion>0.5){
            result=true;
            Log.i(TAG, "张嘴了！！！！:");
        }else {
            MyApplication.getInstance().ttsUtil.SpeechRepead("请张开嘴巴   ", Config.heightTempVoiceVolume);
        }
        Log.i(TAG, "嘴唇开口比例: "+proportion);

         return result;
    }

}
