package cn.com.magnity.coresdksample.Detect;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * 实体类，用于保存人脸识别的数据
 */
public class FaceRect {
	public Rect bound = new Rect();//人脸识别框数据
	public Point point[];//人脸识别点数据
}
