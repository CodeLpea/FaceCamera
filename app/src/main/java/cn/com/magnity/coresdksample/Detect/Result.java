package cn.com.magnity.coresdksample.Detect;

import android.graphics.Point;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Result {
    /**
     * 解析人脸返回结果
     * @param json
     * @return
     */
    public static FaceRect result(String json) {
        FaceRect rect = null;
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject joResult = new JSONObject(json);
            JSONArray items = joResult.getJSONArray("face");//解析face数组
            JSONObject position = items.getJSONObject(0).getJSONObject("position");
            //获取人脸检测框数据
            rect = new FaceRect();
            rect.bound.left = position.getInt("left");//左边起始位置点
            rect.bound.top = position.getInt("top");//上端起始位置点
            rect.bound.right = position.getInt("right");//右边结束位置点
            rect.bound.bottom = position.getInt("bottom");//下端结束位置点
            // 提取关键点数据
            JSONObject landmark = items.getJSONObject(0).getJSONObject("landmark");
            int keyPoint = landmark.length();//人脸检测点数据
            rect.point = new Point[keyPoint];//人脸检测点数组
            Iterator it = landmark.keys();//使用迭代器解析人脸监测点数据
            int point = 0;
            while (it.hasNext()) {//检测点，的坐标
                String key = (String) it.next();
                JSONObject object = landmark.getJSONObject(key);
                rect.point[point] = new Point(object.getInt("x"), object.getInt("y"));
                point++;
            }
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return rect;
    }


//    private void get(String json) throws JSONException {
//        JSONObject jsonObject = new JSONObject(json);
//        Object obj = jsonObject.get("obj");
//    }
}

/**
 * JSON数据
 */
//        {
//        "face": [
//        {
//        "position": {
//        "top": 87,
//        "left": 161,
//        "right": 404,
//        "bottom": 330
//        },
//        "landmark": {
//        "left_eyebrow_left_corner": {
//        "x": 381,
//        "y": 177
//        },
//        "left_eyebrow_middle": {
//        "x": 385,
//        "y": 197
//        },
//        "left_eyebrow_right_corner": {
//        "x": 377,
//        "y": 212
//        },
//        "right_eyebrow_left_corner": {
//        "x": 369,
//        "y": 248
//        },
//        "right_eyebrow_middle": {
//        "x": 367,
//        "y": 275
//        },
//        "right_eyebrow_right_corner": {
//        "x": 351,
//        "y": 299
//        },
//        "left_eye_left_corner": {
//        "x": 354,
//        "y": 173
//        },
//        "left_eye_right_corner": {
//        "x": 349,
//        "y": 201
//        },
//        "right_eye_left_corner": {
//        "x": 338,
//        "y": 248
//        },
//        "right_eye_right_corner": {
//        "x": 328,
//        "y": 283
//        },
//        "nose_left": {
//        "x": 289,
//        "y": 178
//        },
//        "nose_bottom": {
//        "x": 277,
//        "y": 194
//        },
//        "nose_right": {
//        "x": 277,
//        "y": 222
//        },
//        "mouth_upper_lip_top": {
//        "x": 243,
//        "y": 192
//        },
//        "mouth_middle": {
//        "x": 230,
//        "y": 189
//        },
//        "mouth_lower_lip_bottom": {
//        "x": 215,
//        "y": 188
//        },
//        "left_eye_center": {
//        "x": 354,
//        "y": 184
//        },
//        "right_eye_center": {
//        "x": 336,
//        "y": 266
//        },
//        "nose_top": {
//        "x": 305,
//        "y": 195
//        },
//        "mouth_left_corner": {
//        "x": 221,
//        "y": 162
//        },
//        "mouth_right_corner": {
//        "x": 203,
//        "y": 226
//        }
//        }
//        }
//        ],
//        "ret": 0
//        }