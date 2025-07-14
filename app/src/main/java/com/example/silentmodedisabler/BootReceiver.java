package com.example.silentmodedisabler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.ExistingPeriodicWorkPolicy;

import java.util.concurrent.TimeUnit;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            PeriodicWorkRequest workRequest =
                new PeriodicWorkRequest.Builder(DisableSilentWorker.class, 15, TimeUnit.MINUTES)
                    .build();

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "DisableSilentWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            );
        }
    }
}