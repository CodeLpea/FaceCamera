package cn.com.magnity.coresdksample.ddnwebserver.component;

import android.os.Environment;
import android.util.Log;

import com.yanzhenjie.andserver.annotation.Website;
import com.yanzhenjie.andserver.framework.website.FileBrowser;

import cn.com.magnity.coresdksample.utils.Config;

/**
 * 设置一个目录作为本地存储文件夹
 * 所有文件路径都是基于更目录后的，不是绝对路径
 * 比如/storage/emulated/0/DdnTempCamera/person.png
 * 根目录为/storage/emulated/0
 * 因此文件路径为/DdnTempCamera/person.png
 * */
@Website
public class SimpleBrowser extends FileBrowser {
    public SimpleBrowser() {
        //需要将根目录传递进去，往后所有文件路径都是直接基于根目录的
        super(Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.e("PATH", Environment.getExternalStorageDirectory().getAbsolutePath());
    }
}