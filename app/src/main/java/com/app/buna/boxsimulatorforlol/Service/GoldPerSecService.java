package com.app.buna.boxsimulatorforlol.Service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.app.buna.boxsimulatorforlol.Activity.LoginActivity;
import com.app.buna.boxsimulatorforlol.Activity.MainActivity;
import com.app.buna.boxsimulatorforlol.Broadcast.BgmRestartBroadcast;
import com.app.buna.boxsimulatorforlol.Manager.GoldManager;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.Util.LangUtil;

public class GoldPerSecService extends Service {

    private GoldManager goldManager;
    private String channelId = "1592";
    private Thread goldSecThread;

    @Override
    public void onCreate() {
        unregisterRestartAlarm();
        super.onCreate();
        goldManager = new GoldManager(this);
        startGPC();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createChannel();
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = createNotification();
        builder.setCustomContentView(new RemoteViews(this.getPackageName(), R.layout.custom_mini_notification));

        startForeground(1, builder.build());
        nm.notify(startId, builder.build());
        nm.cancel(startId);

        return super.onStartCommand(intent, flags, startId);
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, "Background Service", NotificationManager.IMPORTANCE_NONE);
            notificationChannel.enableVibration(false);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @SuppressLint("WrongConstant")
    private NotificationCompat.Builder createNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("")
                .setSmallIcon(R.drawable.rounded_app_icon)
                .setContentText("")
                .setPriority(Notification.PRIORITY_MIN)
                .setDefaults(Notification.FLAG_FOREGROUND_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        return builder;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        registerRestartAlarm();
    }




    public void startGPC(){
        goldSecThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(1000);
                        goldManager.setGold(goldManager.getGold() + goldManager.getGoldPerSec());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        goldSecThread.start();
    }

    private void registerRestartAlarm(){
        Log.i("000 PersistentService" , "registerRestartAlarm" );
        Intent intent = new Intent(GoldPerSecService.this, BgmRestartBroadcast.class);
        intent.setAction("ACTION.RESTART.GoldPerSecService");
        PendingIntent sender = PendingIntent.getBroadcast(GoldPerSecService.this,0, intent,0);

        long firstTime = SystemClock.elapsedRealtime();

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        /**
         * 알람 등록
         */
        alarmManager.set(AlarmManager.RTC, firstTime, sender);

    }


    private void unregisterRestartAlarm(){

        Log.i("000 PersistentService" , "unregisterRestartAlarm" );

        Intent intent = new Intent(GoldPerSecService.this, BgmRestartBroadcast.class);
        intent.setAction("ACTION.RESTART.GoldPerSecService");
        PendingIntent sender = PendingIntent.getBroadcast(GoldPerSecService.this,0,intent,0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        /**
         * 알람 취소
         */
        alarmManager.cancel(sender);

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
