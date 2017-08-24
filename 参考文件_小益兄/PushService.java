package com.imacco.mup004.thirdparty.jpush;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.imacco.mup004.activity.fitting.Fitting_DetailActivity;
import com.imacco.mup004.application.DataDict;
import com.imacco.mup004.util.system.SystemUtil;
import com.imacco.mup004.view.impl.beauty.Information_DetailActivity;
import com.imacco.mup004.view.impl.home.HomeActivity;
import com.imacco.mup004.view.impl.welfare.Welfare_DetailActivity;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 用于接收消息推送的服务
 */
public class PushService extends Service {

    private String mType;
    private String mPackageName;
    private String mServiceName = "com.imacco.mup004:service2";
    private BroadcastReceiver mMyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            // 调用系统广播，每一分钟回接收一次，如果service停止，在这里重启service
            if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                keepService();
            }
            if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                // 在这里可以自己写代码去定义用户点击后的行为
                mType = bundle.getString(JPushInterface.EXTRA_EXTRA);
                mPackageName = SystemUtil.getPackageName(context);
                // 如果app还存活
                if (SystemUtil.isAppAlive(context, mPackageName)) {
                    // app是否在后台运行
                    if (SystemUtil.isAppOnBackground(context)) {
                        // 根据包名打开app
                        Intent i = new Intent(context, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        i.putExtra(DataDict.IntentInfo.PUSHMESS, mType);
                        context.startActivity(i);
                    } else {
                        try {
                            JSONObject json = new JSONObject(mType);
                            if (json.getString("Type").equals("Info")) {
                                Intent i = new Intent(context, Information_DetailActivity.class);
                                i.putExtra("id", json.getString("RelateID"));
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                context.startActivity(i);
                            } else if (json.getString("Type").equals("Activity")) {
                                Intent i = new Intent(context, Welfare_DetailActivity.class);
                                i.putExtra("WelfareID", json.getString("RelateID"));
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                context.startActivity(i);
                            } else if (json.getString("Type").equals("Makeup")) {
                                Intent i = new Intent(context, Fitting_DetailActivity.class);
                                i.putExtra("fragment2_item_id", json.getString("RelateID"));
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                context.startActivity(i);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                // 如果app已经被杀死
                else {
                    Log.i("MeiDeNi_MyReceiver", "this process is dead");
                    // 根据包名打开app
                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(mPackageName);
                    launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    launchIntent.putExtra(DataDict.IntentInfo.PUSHMESS, mType);
                    context.startActivity(launchIntent);
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction("cn.jpush.android.intent.REGISTRATION");
        filter.addAction("cn.jpush.android.intent.MESSAGE_RECEIVED");
        filter.addAction("cn.jpush.android.intent.NOTIFICATION_RECEIVED");
        filter.addAction("cn.jpush.android.intent.NOTIFICATION_OPENED");
        filter.addAction("cn.jpush.android.intent.ACTION_RICHPUSH_CALLBACK");
        filter.addAction("cn.jpush.android.intent.CONNECTION");
        filter.addCategory("com.imacco.mup004");
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mMyReceiver, filter);
        keepService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        JPushInterface.init(getBaseContext());
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
        Log.i("MeiDeNi_PushService", "this process is onTrimMemory...");
        keepService();
    }

    /**
     * 监听服务是否停止，若停止，则重启service
     */
    private void keepService() {
        if (!SystemUtil.isServiceAlive(this, mServiceName)) {
            Log.i("MeiDeNi_PushService", "restart PushService2...");
            Intent intent = new Intent(PushService.this, PushService2.class);
            this.startService(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMyReceiver);
        startService(new Intent(PushService.this, PushService.class));
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 使用startService()方法启动service时，方法体内只需要return null
        return null;
    }
}
