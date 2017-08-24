package com.millet.myapplication.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.millet.myapplication.SystemUtil;

/**
 * Created by Administrator on 2017/8/24 0024.
 */

public class MyService2 extends Service {

    private String mServiceName2 = "com.millet.myapplication.service1";

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context _context, Intent _intent) {
            // 调用系统广播，每一分钟回接收一次，如果service停止，在这里重启service
            if (_intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                keepService1();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mBroadcastReceiver, filter);
        keepService1();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        keepService1();
        return Service.START_STICKY;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i("MeiDeNi_PushService2", "this process is onTrimMemory...");
        keepService1();
    }

    /**
     * 判断服务是否还在运行，若是已经停止，则重启service
     */
    private void keepService1() {
        if (!SystemUtil.isServiceAlive(this, mServiceName2)) {
            Log.i("MeiDeNi_PushService2", "restart PushService...");
            Intent intent = new Intent(MyService2.this, MyService1.class);
            this.startService(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        startService(new Intent(MyService2.this, MyService1.class));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent _intent) {
        return null;
    }

}
