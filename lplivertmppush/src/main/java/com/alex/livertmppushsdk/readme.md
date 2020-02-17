###LpRTMP
使用：
实例化LpRTMP

        public LpRtmp mLpRtmp;
        mLpRtmp=new LpRtmp();
        mLpRtmp.setFormat(_iCameraCodecType).startRtmp(_rtmpUrl);
        mLpRtmp.startRtmp("rtmp://192.168.8.120:1935/live/camera");
开始传输数据

 
         mLpRtmp.inputData(data);
  
 使用结束或者暂停时，一定要使用stop，否则下次启动可能会无法连接服务器
 
         mLpRtmp.stopRtmp();

    