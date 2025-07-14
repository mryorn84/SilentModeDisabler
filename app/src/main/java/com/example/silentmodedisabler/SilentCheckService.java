
package com.example.silentmodedisabler;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class SilentCheckService extends Service {
    private static final int NOTIF_ID = 1;
    private static final long INTERVAL = 5 * 60 * 1000;
    private Handler handler;
    private Runnable checker;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIF_ID, buildNotification());

        handler = new Handler(Looper.getMainLooper());
        checker = () -> {
            checkAndUnmute();
            handler.postDelayed(checker, INTERVAL);
        };
        handler.post(checker);
    }

    private void checkAndUnmute() {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        int mode = am.getRingerMode();
        if (mode != AudioManager.RINGER_MODE_NORMAL) {
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (nm.isNotificationPolicyAccessGranted()) {
                nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            }
        }
    }

    private Notification buildNotification() {
        return new NotificationCompat.Builder(this, "silent_check_channel")
            .setContentTitle("SilentModeDisabler")
            .setContentText("Monitoring silent and DND mode")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                "silent_check_channel",
                "Silent Mode Monitor",
                NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(checker);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
