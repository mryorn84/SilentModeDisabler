package com.example.silentmodedisabler;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // چک کردن اجازهٔ دسترسی به Do Not Disturb
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && !notificationManager.isNotificationPolicyAccessGranted()) {
            Toast.makeText(this, "لطفاً اجازه دسترسی به Do Not Disturb را بدهید", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }

        // اجرای DisableSilentWorker هر 15 دقیقه
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(DisableSilentWorker.class, 15, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "disable_silent_mode_worker",
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicWorkRequest
        );

        // اجرای Worker برای پنهان‌کردن آیکون برنامه بعد از 1 دقیقه
        OneTimeWorkRequest hideAppWork = new OneTimeWorkRequest.Builder(HideAppWorker.class)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueue(hideAppWork);
    }
}
