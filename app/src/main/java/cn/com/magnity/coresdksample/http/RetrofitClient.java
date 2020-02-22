package cn.com.magnity.coresdksample.http;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.com.magnity.coresdksample.http.model.ResponseEntry;
import cn.com.magnity.coresdksample.http.model.UpgradePackageVersionInfoEntry;
import cn.com.magnity.coresdksample.http.model.UpgradePackageVersionInfoRequest;
import cn.com.magnity.coresdksample.MyApplication;
import cn.com.magnity.coresdksample.utils.AppUtils;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cn.com.magnity.coresdksample.Config.API_XIAONUO_BASE;
import static cn.com.magnity.coresdksample.Config.SOFTWARE_SYSTEM_TYTE;
import static cn.com.magnity.coresdksample.Config.VERSIONURL;

public class RetrofitClient {
    private static String TAG="RetrofitClient";
    private Retrofit mRetrofit;
    private NetWorkAPIs mNetWorkAPIs;
    private Gson mGson;

    private static class  Instance{
       public static RetrofitClient retrofitClient=new RetrofitClient();
    }

    public static RetrofitClient getInstance(){
        return Instance.retrofitClient;
    }

    public RetrofitClient() {
        init();
    }

    //初始化Retofit
    private void init() {
        //创建一个拦截器：
        OkHttpClient.Builder client=new OkHttpClient.Builder();
        client.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request=chain.request().newBuilder().
                        addHeader("device_no", AppUtils.getLocalMacAddressFromWifiInfo(MyApplication.getInstance()))
                        .build();
                return chain.proceed(request);
            }
        }).connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);

        mRetrofit=new Retrofit.Builder().
                baseUrl(API_XIAONUO_BASE)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mNetWorkAPIs=mRetrofit.create(NetWorkAPIs.class);
        mGson=new Gson();

    }


    /**
     * 获取当前服务器上最新升级包版本信息
     *
     * @return 成功返回 true, 失败返回false
     */
    public UpgradePackageVersionInfoEntry getUpgradePackageVersionInfo(String appVersion) {
        Log.e(TAG, "获取当前服务器上最新升级包版本信息getUpgradePackageVersionInfo: " + VERSIONURL);
        UpgradePackageVersionInfoEntry versionInfo = null;
        UpgradePackageVersionInfoRequest upgradePackageVersionInfoRequest = new UpgradePackageVersionInfoRequest();
        upgradePackageVersionInfoRequest.setHardWareDeviceType("巨哥温度摄像头");
        upgradePackageVersionInfoRequest.setHardWareDeviceNumber(AppUtils.getLocalMacAddressFromWifiInfo(MyApplication.getInstance()));
        upgradePackageVersionInfoRequest.setCurrentApkVersion(appVersion);
        upgradePackageVersionInfoRequest.setSoftWareType(SOFTWARE_SYSTEM_TYTE);
        String requestStr = mGson.toJson(upgradePackageVersionInfoRequest);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestStr);
        Call<ResponseEntry<UpgradePackageVersionInfoEntry>> call = mNetWorkAPIs.getUpgradePackageVersionInfo(VERSIONURL, body);
        ResponseEntry<UpgradePackageVersionInfoEntry> response;
        try {
            response = call.execute().body();
            if (response != null) {
                int code = response.getCode();
                Log.i(TAG, "response : " + mGson.toJson(response));
                if (code == ResponseEntry.SUCCESS) {
                    versionInfo = response.getData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return versionInfo;
    }
}
