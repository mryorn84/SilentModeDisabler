package com.example.silentmodedisabler;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // مخفی کردن آیکون بعد از 5 دقیقه + نمایش "..."
        new android.os.Handler().postDelayed(() -> {
            android.content.pm.PackageManager pm = getPackageManager();
            android.content.ComponentName componentName = new android.content.ComponentName(
                    getApplicationContext(), MainActivity.class);

            pm.setComponentEnabledSetting(
                    componentName,
                    android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    android.content.pm.PackageManager.DONT_KILL_APP
            );

            // نمایش پیام فقط شامل چند نقطه
            Toast.makeText(getApplicationContext(), "...", Toast.LENGTH_SHORT).show();

        }, 5 * 60 * 1000); // 5 دقیقه

        // چک کردن اجازهٔ دسترسی به Do Not Disturb
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && !notificationManager.isNotificationPolicyAccessGranted()) {
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
    }
}
