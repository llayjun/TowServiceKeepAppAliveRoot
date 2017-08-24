package com.imacco.mup004.thirdparty.jpush;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.support.annotation.Nullable;
import android.util.Log;

import com.imacco.mup004.util.system.SystemUtil;

/**
 * PushService的守护进程，当PushService停止时，会重启PushService
 */
public class PushService2 extends Service {

    private String mServiceName = "com.imacco.mup004:service1";
    private BroadcastReceiver mMyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 调用系统广播，每一分钟回接收一次，如果service停止，在这里重启service
            if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                keepService();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mMyReceiver, filter);
        keepService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        keepService();
        return Service.START_STICKY;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i("MeiDeNi_PushService2", "this process is onTrimMemory...");
        keepService();
    }

    /**
     * 判断服务是否还在运行，若是已经停止，则重启service
     */
    private void keepService() {
        if (!SystemUtil.isServiceAlive(this, mServiceName)) {
            Log.i("MeiDeNi_PushService2", "restart PushService...");
            Intent intent = new Intent(PushService2.this, PushService.class);
            this.startService(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(PushService2.this, PushService.class));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
