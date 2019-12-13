/*
 * Copyright 2018 Zhenjie Yan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.com.magnity.coresdksample.ddnwebserver.controller;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.framework.body.StringBody;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.RequestBody;
import com.yanzhenjie.andserver.util.MediaType;

import java.io.IOException;

import cn.com.magnity.coresdksample.ddnwebserver.WebConfig;
import cn.com.magnity.coresdksample.ddnwebserver.model.CalibratPositionData;
import cn.com.magnity.coresdksample.ddnwebserver.model.CameraData;
import cn.com.magnity.coresdksample.ddnwebserver.model.CurrentSettingData;
import cn.com.magnity.coresdksample.ddnwebserver.model.FFCData;
import cn.com.magnity.coresdksample.ddnwebserver.model.PhotoDataRespons;
import cn.com.magnity.coresdksample.ddnwebserver.database.PictureData;
import cn.com.magnity.coresdksample.ddnwebserver.model.RecordQueryRequest;
import cn.com.magnity.coresdksample.ddnwebserver.model.ReturnData;
import cn.com.magnity.coresdksample.ddnwebserver.model.TemperCameraData;
import cn.com.magnity.coresdksample.ddnwebserver.model.TemperatureData;
import cn.com.magnity.coresdksample.ddnwebserver.model.ValidAreaData;
import cn.com.magnity.coresdksample.ddnwebserver.model.VoiceData;
import cn.com.magnity.coresdksample.ddnwebserver.model.WifiData;
import cn.com.magnity.coresdksample.usecache.CurrentConfig;
import cn.com.magnity.coresdksample.ddnwebserver.server.SetConfigServer;
import cn.com.magnity.coresdksample.ddnwebserver.util.JsonUtils;
import cn.com.magnity.coresdksample.utils.voice.TtsSpeak;

/**
 * 设置界面控制器
 * lp
 * 2019/07/30
 */
@RestController
@RequestMapping(path = "/setting")
public class SettingController {

    /**
     * 默认获取到当前的所有配置信息
     * 要通过MessageConverter自动处理一遍
     */
    @GetMapping(path = "/")
    public CurrentSettingData getSetting() {
        return CurrentConfig.getInstance().updateSetting();
    }

    /**
     * 手动刷新获取到当前的所有配置信息
     * 直接返回不经过MessageConverter
     */
    @GetMapping(path = "/refresh/setting")
    public void getSetting2(HttpResponse response) {
        CurrentSettingData currentSettingData = CurrentConfig.getInstance().updateSetting();
        String content = JsonUtils.successfulJson(currentSettingData);
        StringBody body = new StringBody(content);
        response.setBody(body);
    }

    /**
     * 设置wifi信息
     */
    @PostMapping(path = WebConfig.setWifiRequestPath, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public WifiData setWifiConfig(RequestBody request) {
        String content = null;
        try {
            content = request.string();
            Log.i("setWifiConfig", content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        WifiData wifiData = JSON.parseObject(content, WifiData.class);
        SetConfigServer.getInstance().setWifiData(wifiData);
        TtsSpeak.getInstance().SystemSpeech("设置wifi信息成功");
        return wifiData;

//HttpRequest request
//        //将请求转换为Json
//        JSONObject jsonObject = JsonUtils.request2Json(request);
//        Log.i("setWifiConfig", "jsonObject: "+JsonUtils.toJsonString(jsonObject));
//        WifiData wifiData = JSON.toJavaObject(jsonObject, WifiData.class);
//        SetConfigServer.getInstance().setWifiData(wifiData);
    }

    /**
     * 设置语音信息
     */
    @PostMapping(path = WebConfig.setVoiceRequestPath, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public VoiceData setVoiceConfig(RequestBody request) {
        String content = null;
        try {
            content = request.string();
            Log.i("setVoiceConfig", content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        VoiceData voiceData = JSON.parseObject(content, VoiceData.class);
        SetConfigServer.getInstance().setVoiceData(voiceData);
        TtsSpeak.getInstance().SystemSpeech("设置有效区域预览成功");
        return voiceData;

    }

    /***
     * 设置温度阀值
     * */
    @PostMapping(path = WebConfig.setTemperatureRequestPath, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public TemperatureData setTemperatureConfig(RequestBody request) {
        String content = null;
        try {
            content = request.string();
            Log.i("setTemperatureConfig", content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TemperatureData temperatureData = JSON.parseObject(content, TemperatureData.class);
        SetConfigServer.getInstance().setTemperatureData(temperatureData);
        TtsSpeak.getInstance().SystemSpeech("设置温度阀值"+temperatureData.getTemperature_threshold1()+"成功");
        return temperatureData;

    }

    /***
     * 设置曝光参数
     * */
    @PostMapping(path = WebConfig.setExploreRequestPath, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CameraData setCameraConfig(RequestBody request) {
        String content = null;
        try {
            content = request.string();
            Log.i("setCameraConfig", content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CameraData cameraData = JSON.parseObject(content, CameraData.class);
        SetConfigServer.getInstance().setCameraData(cameraData);
        TtsSpeak.getInstance().SystemSpeech("设置曝光参数"+cameraData.getExplorer()+"成功");
        return cameraData;
    }

    /***
     * 设置FFC补偿参数
     * 可为负数
     * */
    @PostMapping(path = WebConfig.setFFCCompensationRequestPath, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public FFCData setFFCcompensationConfig(RequestBody request) {
        String content = null;
        try {
            content = request.string();
            Log.i("setFFCcompensation", content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FFCData ffcData = JSON.parseObject(content, FFCData.class);
        SetConfigServer.getInstance().setFFCData(ffcData);
        TtsSpeak.getInstance().SystemSpeech("设置FFC补偿参数"+ffcData.getCompensation()+"成功");
        return ffcData;
    }

    /***
     * 黑体温度校准
     * 值为参考的目标黑体温度
     * */
    @PostMapping(path = WebConfig.setBlackCalibrationnRequestPath, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public FFCData setFFCalibrationConfig(RequestBody request) {
        String content = null;
        try {
            content = request.string();
            Log.i("setFFCalibrationConfig", content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FFCData ffcData = JSON.parseObject(content, FFCData.class);
        SetConfigServer.getInstance().setFFCData(ffcData);
        return ffcData;
    }

    /***
     * 平均温度校准
     * */
    @PostMapping(path = WebConfig.setAvgCalibrationnRequestPath, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public FFCData setFFCavgConfig(RequestBody request) {
        String content = null;
        try {
            content = request.string();
            Log.i("setFFCavgConfig", content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FFCData ffcData = JSON.parseObject(content, FFCData.class);
        SetConfigServer.getInstance().setFFCData(ffcData);
        //平均温度校准，没有参数传来，因此获得的为空，要给出返回值
        if (ffcData == null) {
            ffcData = new FFCData();
        }
        return ffcData;
    }

    /***
     * 设置目标距离
     * */
    @PostMapping(path = WebConfig.setDistanceRequestPath, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public TemperCameraData setTempCameraConfig(RequestBody request) {
        String content = null;
        try {
            content = request.string();
            Log.i("setTempCameraConfig", content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TemperCameraData temperCameraData = JSON.parseObject(content, TemperCameraData.class);
        SetConfigServer.getInstance().setTemperatureCameraData(temperCameraData);
        TtsSpeak.getInstance().SystemSpeech("设置目标距离"+temperCameraData.getDistance()+"成功");
        return temperCameraData;
    }

    /***
     * 获取预览图片路径
     * */
    @PostMapping(path = WebConfig.getPhotoPreviewRequestPath, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public PictureData previewTempPath(RequestBody request) {
        Log.i("previewTempPath", "获取预览图片路径: ");
        PictureData pictureData = SetConfigServer.getInstance().getPictureData();
        return pictureData;
    }

    /***
     * 校准红外定位框
     * */
    @PostMapping(path = WebConfig.setTemperLoactionRequestPath, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ReturnData calibrationLoaction(RequestBody request) {
        String content = null;
        try {
            content = request.string();
            Log.i("calibrationLoaction", content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CalibratPositionData calibratPositionData = JSON.parseObject(content, CalibratPositionData.class);
        if (calibratPositionData != null) {
            SetConfigServer.getInstance().setCalibratPosition(calibratPositionData);
        }
        TtsSpeak.getInstance().SystemSpeech("校准红外定位框成功");
        return new ReturnData();
    }

    /***
     * 获取有效区域预览
     * */
    @PostMapping(path = WebConfig.getValidAreaRequestPath, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ValidAreaData getValidArea(RequestBody request) {
        Log.i("getValidArea", "获取有效区域预览: ");
        ValidAreaData validAreaData = SetConfigServer.getInstance().getValidAreaData();
        return validAreaData;
    }

    /***
     * 设置有效区域预览
     * */
    @PostMapping(path = WebConfig.setValidAreaRequestPath, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ValidAreaData setValidArea(RequestBody request) {
        Log.i("setValidArea", "设置有效区域预览: ");
        String content = null;
        try {
            content = request.string();
            Log.i("setValidArea", content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ValidAreaData validAreaData = JSON.parseObject(content, ValidAreaData.class);
        if (validAreaData != null) {
            SetConfigServer.getInstance().setValidAreaData(validAreaData);
        }
        TtsSpeak.getInstance().SystemSpeech("设置有效区域预览成功");
        return validAreaData;
    }

    /***
     * 获取所有图片数据
     * */
    @PostMapping(path = WebConfig.queryDataRequestPath, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public PhotoDataRespons getPhotodata(RequestBody request) {
        Log.i("getPhotodata", "获取所有图片数据: ");
        String content = null;
        try {
            content = request.string();
            Log.i("getPhotodata", content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PhotoDataRespons respons = new PhotoDataRespons();
        RecordQueryRequest recordQueryRequest = JSON.parseObject(content, RecordQueryRequest.class);
        if (recordQueryRequest != null) {
            respons = SetConfigServer.getInstance().queryRecord(recordQueryRequest);
            return respons;
        }
        return respons;
    }

}