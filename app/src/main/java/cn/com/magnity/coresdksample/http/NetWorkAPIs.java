package cn.com.magnity.coresdksample.http;


import cn.com.magnity.coresdksample.http.model.ResponseEntry;
import cn.com.magnity.coresdksample.http.model.SchoolKeyEntry;
import cn.com.magnity.coresdksample.http.model.UpgradePackageVersionInfoEntry;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface NetWorkAPIs {
    /**
     * 获取学校Key
     */
    @GET
    Call<ResponseEntry<SchoolKeyEntry>> getSchoolKey(@Url String url);

    /**
     * 获取服务器最新升级包版本信息
     */
    @POST
    Call<ResponseEntry<UpgradePackageVersionInfoEntry>> getUpgradePackageVersionInfo(@Url String url, @Body RequestBody body);


    /**
     * 上传未识别的红外记录信息
     * */
    @POST
    Call<ResponseEntry> upLoadUnKonwRecord(@Url String url, @Body RequestBody body);
}
