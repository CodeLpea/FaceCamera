package cn.com.magnity.coresdksample.service.update;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import cn.com.magnity.coresdksample.R;
import cn.com.magnity.coresdksample.utils.FlieUtil;
import cn.com.magnity.coresdksample.utils.ShellUtils;
import cn.com.magnity.coresdksample.utils.TimeUitl;

import static android.content.Context.NOTIFICATION_SERVICE;
import static cn.com.magnity.coresdksample.Config.DdnDownLoadApkPath;


public class SoftWareUpgradeInstaller {
    private static final String TAG = "UpgradeInstaller";
    private Context mContext;
    /**
     * 下载状态通知栏设置
     */
    private NotificationManager mNotificationManager = null;
    private NotificationCompat.Builder mDownloadNotifBuilder = null;
    private static final int NOTIFICATION_ID = UUID.randomUUID().hashCode();

    public SoftWareUpgradeInstaller(){

    }

    public SoftWareUpgradeInstaller(Context context){
        mContext = context;
    }

    /**
     * 初始化通知栏
     */
    public void initNotify(){
        Log.i(TAG, "初始化通知栏: ");
        //初始化下载状态通知对象
        mNotificationManager = (NotificationManager)mContext.getSystemService(NOTIFICATION_SERVICE);
        mDownloadNotifBuilder = new NotificationCompat.Builder(mContext);
        //android5.1之后为了统一通知栏小图标的风格，google统一限制成全白色透明图标，彩色图标会显示纯白色
        mDownloadNotifBuilder.setSmallIcon(R.drawable.point2)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.point2))
                .setContentTitle(mContext.getString(R.string.app_name))
                .setAutoCancel(true);//点击该通知时会清除它
    }

    /**
     * 不带进度条
     * @param msg
     */
    private void updateProgress(String msg){
        if(mDownloadNotifBuilder != null) {
            mDownloadNotifBuilder.setContentText(msg);
            PendingIntent pendingintent = PendingIntent.getActivity(mContext, 0, new Intent(), PendingIntent
                    .FLAG_UPDATE_CURRENT);
            mDownloadNotifBuilder.setContentIntent(pendingintent);
            mNotificationManager.notify(NOTIFICATION_ID, mDownloadNotifBuilder.build());
        }
    }

    /**
     * 带进度条
     * @param msg
     * @param offset
     */
    private void updateProgress(String msg, int offset){
        if(mDownloadNotifBuilder != null) {
            mDownloadNotifBuilder.setProgress(100, offset, false);
            mDownloadNotifBuilder.setContentText(msg);
            PendingIntent pendingintent = PendingIntent.getActivity(mContext, 0, new Intent(), PendingIntent
                    .FLAG_UPDATE_CURRENT);
            mDownloadNotifBuilder.setContentIntent(pendingintent);
            mNotificationManager.notify(NOTIFICATION_ID, mDownloadNotifBuilder.build());
        }
    }
    /**
     * 文件下载
     * @param url
     */

    public boolean startDownload(String url){
        FlieUtil.initFile(DdnDownLoadApkPath);

        //初始化下载任务
        DownloadTask task = new DownloadTask.Builder(url, new File(DdnDownLoadApkPath))
                // .setFilename("upgrade.zip")
                .setAutoCallbackToUIThread(true)
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(30)
                // ignore the same task has already completed in the past.
                .setPassIfAlreadyCompleted(true)
                .build();

        //开始下载任务
        task.enqueue(new DownloadListener4WithSpeed() {
            private long totalLength;
            private String humanReadableTotalLength;
            @Override
            public void taskStart(@NonNull DownloadTask task) {
                Log.i(TAG, "taskStart...");
            }

            @Override
            public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {
                //Log.i(TAG, "connectStart-->blockIndex=" + blockIndex);
            }

            @Override
            public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
                //Log.i(TAG, "connectEnd-->blockIndex=" + blockIndex + " responseCode=" + responseCode);
            }

            @Override
            public void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4SpeedAssistExtend.Listener4SpeedModel model) {
                //Log.i(TAG, "infoReady-->BreakpointInfo=" + info.toString() + " fromBreakpoint=" + fromBreakpoint);
                totalLength = info.getTotalLength();
                humanReadableTotalLength = humanReadableBytes(totalLength, true);
                //Log.i(TAG, "infoReady-->totalLength=" + totalLength + " humanReadableTotalLength=" + humanReadableTotalLength);
            }

            @Override
            public void progressBlock(@NonNull DownloadTask task, int blockIndex, long currentBlockOffset, @NonNull SpeedCalculator blockSpeed) {
               // Log.i(TAG, "progressBlock-->blockIndex=" + blockIndex + " currentBlockOffset=" + currentBlockOffset + " blockSpeed=" + blockSpeed.speed());

            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, @NonNull SpeedCalculator taskSpeed) {
                //Log.i(TAG, "progress-->currentOffset=" + currentOffset + " taskSpeed=" + taskSpeed.speed());
                final String readableOffset = humanReadableBytes(currentOffset, true);
                final String progressStatus = readableOffset + "/" + humanReadableTotalLength;
                final String speed = taskSpeed.speed();
                final String progressStatusWithSpeed = progressStatus + "(" + speed + ")";
               // Log.i(TAG, "progress-->progressStatusWithSpeed=" + progressStatusWithSpeed);
                final int offsetPercent = (int)(currentOffset * 100 / totalLength);
                updateProgress(progressStatusWithSpeed, offsetPercent);
            }

            @Override
            public void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info, @NonNull SpeedCalculator blockSpeed) {
               // Log.i(TAG, "blockEnd-->blockIndex=" + blockIndex + " BlockInfo=" + info.toString() + " blockSpeed=" + blockSpeed.speed());
            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull SpeedCalculator taskSpeed) {
               // Log.i(TAG, "taskEnd-->EndCause=" + cause.name() + " Exception=" + (realCause != null ? realCause.getMessage() : null) + " taskSpeed=" + taskSpeed.speed());
                if(cause == EndCause.ERROR){
                    task.enqueue(this);
                }else if (cause == EndCause.COMPLETED){
                    task.setTag(null);
                    task.cancel();
                    updateProgress("下载完成", 100);
                }
            }
        });

        task.setTag("mark-task-started");

        //异步操作，在此等待下载完成
        while(task.getTag() != null){
            TimeUitl.delayMs(5 * 1000);
        }

        return true;
    }


    /**
     * 静默安装升级包，需要root权限。这种安装方式会导致当前运行的APP死掉
     * @param context
     * @param file apk绝对路径下的File对象
     */
    public int silentInstall(Context context, File file){
        int exec_result = 0;
        String exec_result_msg = null;
        String filePath = file.getAbsoluteFile().toString();
        String fileName = file.getName();

        updateProgress("正在安装，请稍等....");
        //检查MD5值是否一致
     //   String downloadFileMD5Str = Md5Utils.getFileMD5(filePath);
        String[] fileMD5Str = fileName.split("\\.");
        if(fileMD5Str != null){
                ShellUtils.CommandResult result = ShellUtils.execCommand("pm install -r " + filePath, true, true);
                 Log.i(TAG, "silentInstall:result "+result.result);
                 Log.i(TAG, "silentInstall:successMsg "+result.successMsg.trim());
                 Log.i(TAG, "silentInstall:errorMsg "+result.errorMsg.trim());
               if(result.errorMsg.trim().contains("apkFailure")){
                    updateProgress("安装失败");
                }
        }else{
            exec_result = -1;
        }

        return exec_result;
    }

    /**
     * 获取下载的升级包包名
     * @return
     */
    public File getDownloadPackage(){
        File upgradePackage = null;

        List<File> packages = new ArrayList<>();
        FlieUtil.getFiles(new File(DdnDownLoadApkPath), packages, new FileFilter() {
            @Override
            public boolean accept(File file) {
                if(file.getName().contains(".apk")){
                    return true;
                }else {
                    return false;
                }
            }
        });

        if(!packages.isEmpty()){
            upgradePackage = packages.get(0);
        }

        return upgradePackage;
    }

    private String humanReadableBytes(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format(Locale.ENGLISH, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
