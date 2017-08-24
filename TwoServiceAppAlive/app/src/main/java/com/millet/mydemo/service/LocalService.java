package com.millet.mydemo.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.millet.mydemo.IMyAidlInterface;

/**
 * Created by Administrator on 2017/8/25 0025.
 */

public class LocalService extends Service {

    public static final String TAG = LocalService.class.getSimpleName();

    private MyBinder mMyBinder;

    private MyServiceConnection mMyServiceConnection;

    @Override
    public void onCreate() {
        super.onCreate();
        if (null == mMyBinder)
            mMyBinder = new MyBinder();
        mMyServiceConnection = new MyServiceConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent _intent = new Intent(this, RemoteService.class);
        this.bindService(_intent, mMyServiceConnection, Context.BIND_IMPORTANT);
        Log.d(TAG, "LocalService onStartCommand");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent _intent) {
        return mMyBinder;
    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName _componentName, IBinder _iBinder) {
            Log.d(TAG, "LocalService onServiceConnected is connected");

        }

        @Override
        public void onServiceDisconnected(ComponentName _componentName) {
            Log.d(TAG, "LocalService onServiceDisconnected is not connected");
            // 连接出现了异常断开了，RemoteService被杀掉了
            Toast.makeText(LocalService.this, "远程服务Remote被干掉", Toast.LENGTH_LONG).show();
            // 启动RemoteService
            Intent _intent = new Intent(LocalService.this, RemoteService.class);
            startService(_intent);
            Intent _intent1 = new Intent(LocalService.this, RemoteService.class);
            bindService(_intent1, mMyServiceConnection, Context.BIND_IMPORTANT);
        }
    }

    class MyBinder extends IMyAidlInterface.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public String getProName() throws RemoteException {
            return "LocalService Millet";
        }
    }

}
