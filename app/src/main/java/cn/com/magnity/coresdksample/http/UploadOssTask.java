package cn.com.magnity.coresdksample.http;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import cn.com.magnity.coresdksample.MyApplication;
import cn.com.magnity.coresdksample.utils.AppUtils;
import cn.com.magnity.coresdksample.utils.FileUtil;
import cn.com.magnity.coresdksample.utils.FlieUtil;
import cn.com.magnity.coresdksample.utils.TimeUitl;


/**
 * Created by Long on 2018/7/23.
 */

public class UploadOssTask extends Thread {
    private static final String TAG = "UploadOssTask";
    private String objectKey;
    private String filePath;
    private static OSS oss = null;
    private Context mContext;

    /**
     * @param objectKey 在OSS服务器上的objectKey
     * @param filePath 需要上传的文件路径，包括文件名
     */
    public UploadOssTask(Context context, String objectKey, String filePath) {
        this.mContext = context;
        this.objectKey = objectKey;
        this.filePath = filePath;
    }
    
    public void run(){
        int serverErrors = 0;
        Log.i(TAG, "=====0===" + currentThread().getId() + " filePath : " + filePath + " " + FileUtil.isFileExist(filePath));
        while(filePath != null && FileUtil.isFileExist(filePath)){
            Log.i(TAG, "=====1===" + currentThread().getId());
            int ret = uploadAliYunOSSBaseStsServer(objectKey, filePath);
            Log.i(TAG, "=====2===" + currentThread().getId() + " ret = " + ret);
            if (ret == 0) {
                //上传成功，删除本地的文件
//                FileUtil.deleteFile(filePath);
//                Log.i(TAG, "delete pic : " + filePath);
                Log.i(TAG, "上传成功 : " + objectKey);
                break;
            }else if(ret == -2){
                serverErrors++;
                Log.i(TAG, "serverErrors++: ");
                if(serverErrors > 3){
                    FileUtil.deleteFile(filePath);
                    Log.i(TAG, "delete pic : " + filePath + " serverErrors = " + serverErrors);
                    break;
                }
            }
            TimeUitl.delayMs(5000);

        }
    }

    private OSS initOSS(){
        //初始化
        Log.i(TAG,"initOSS..... ");
        String aliyun_oss_url="/api/service/ossSts";
        String endpoint = "oss-cn-shenzhen.aliyuncs.com";
        String stsServer = "http://120.77.237.242:8081" +aliyun_oss_url;
       // String stsServer = InvisibleConfigUtil.readString(InvisibleConfigKey.KINDERGARTEN_SERVER_DOMAIN) + InvisibleConfigUtil.readString(InvisibleConfigKey.ALIYUN_OSS_URL);
        //推荐使用OSSAuthCredentialsProvider。token过期可以及时更新
        OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(stsServer);
        //该配置类如果不设置，会有默认配置，具体可看该类
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次

        return new OSSClient(mContext, endpoint, credentialProvider);
    }

    /**
     * 上传至阿里云的OSS服务器
     * @param objectKey 在OSS服务器上的objectKey
     * @param filePath  需要上传的文件路径，包括文件名
     * @return 0表示上传成功， -1表示客户端的网络异常， -2表示服务端异常
     */
    private int uploadAliYunOSSBaseStsServer(String objectKey, String filePath) {
        synchronized (this) {
            if (oss == null) {
                //初始化
                oss = initOSS();
            }
        }

        String bucketName =  "image-didanuo";
        // String ossPath = Md5Utils.getMD5(TimeUtil.getYMDHMSDate() + stuID + DeviceUtil.getNumber());
        //String objectKey  = "detection/"+ TimeUtil.getYMDDate() + "/" + DeviceUtil.getNumber() + "_" + stuID + "_" + pic_path.substring(pic_path.lastIndexOf("/") + 1);
      //  String objectKey = "detection/"+ TimeUtil.getYMDDate() + "/" + ossFileName;
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, filePath);
        try {
            PutObjectResult putResult = oss.putObject(put);
            Log.i("PutObject", "UploadSuccess");
            Log.i("ETag", putResult.getETag());
            Log.i("RequestId", putResult.getRequestId());
        } catch (ClientException e) {
            Log.i("upBaseStsServer", e.getMessage());
            // 本地异常如网络异常等
            return -1;
        } catch (ServiceException e) {
            // 服务异常
            Log.i("RequestId", e.getRequestId());
            Log.i("ErrorCode", e.getErrorCode());
            Log.i("HostId", e.getHostId());
            Log.i("RawMessage", e.getRawMessage());
            Log.i("ErrorCode",e.getErrorCode());
            Log.i("StatusCode", "status code = " + e.getStatusCode());
            return -2;
        }

        return 0;
    }

    /**
     * 根据上传文件名和学生ID来构造OSS的ObjectKey
     * @param filePath
     * @param stuID
     * @return
     */
    public static String getObjectKey(String filePath, long stuID){
        String objectKey  = "detection/"+ FlieUtil.getTodayFileName() + "/" + AppUtils.getLocalMacAddressFromWifiInfo(MyApplication.getInstance()) + "_" + stuID + "_" + filePath.substring(filePath.lastIndexOf("/") + 1);

        return objectKey;
    }
}
