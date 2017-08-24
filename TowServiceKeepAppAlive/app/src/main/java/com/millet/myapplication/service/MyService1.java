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

public class MyService1 extends Service {

    private String mPackageName;

    private String mServiceName2 = "com.millet.myapplication.service2";

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context _context, Intent _intent) {
            // 调用系统广播，每一分钟回接收一次，如果service停止，在这里重启service
            if (_intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                keepService2();
            }
            mPackageName = SystemUtil.getPackageName(_context);
            if (!SystemUtil.isAppAlive(_context, mPackageName)) {
                // 根据包名打开app
                Intent launchIntent = _context.getPackageManager().getLaunchIntentForPackage(mPackageName);
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                _context.startActivity(launchIntent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter _intentFilter = new IntentFilter();
        _intentFilter.setPriority(1000);
        _intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mBroadcastReceiver, _intentFilter);
        keepService2();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    /**
     * 清理内存时，调用的方法
     *
     * @param level
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i("MeiDeNi_PushService1", "this process is onTrimMemory...");
        keepService2();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent _intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        startService(new Intent(MyService1.this, MyService2.class));
    }

    /**
     * 监听服务是否停止，若停止，则重启service
     */
    private void keepService2() {
        if (!SystemUtil.isServiceAlive(this, mServiceName2)) {
            Log.i("MeiDeNi_PushService1", "restart PushService2...");
            Intent intent = new Intent(MyService1.this, MyService2.class);
            this.startService(intent);
        }
    }

}
