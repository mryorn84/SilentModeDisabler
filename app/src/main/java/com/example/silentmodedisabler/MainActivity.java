package com.example.silentmodedisabler;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ اجرای HideIconWorker با تاخیر 5 دقیقه‌ای
        OneTimeWorkRequest hideIconRequest =
                new OneTimeWorkRequest.Builder(HideIconWorker.class)
                        .setInitialDelay(5, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this).enqueueUniqueWork(
                "hide_icon_work",
                ExistingWorkPolicy.REPLACE,
                hideIconRequest
        );

        // 🟡 بررسی دسترسی Do Not Disturb
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && !notificationManager.isNotificationPolicyAccessGranted()) {
            Toast.makeText(this, "لطفاً اجازه دسترسی به Do Not Disturb را بدهید", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }

        // ⏱ اجرای DisableSilentWorker هر 15 دقیقه
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(DisableSilentWorker.class, 15, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "disable_silent_mode_worker",
                androidx.work.ExistingPeriodicWorkPolicy.REPLACE,
                periodicWorkRequest
        );
    }
}
