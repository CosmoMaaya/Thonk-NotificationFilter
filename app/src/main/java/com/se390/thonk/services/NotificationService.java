package com.se390.thonk.services;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.se390.thonk.MainApplication;
import com.se390.thonk.data.HistoryDao;
import com.se390.thonk.data.NotificationEntry;
import com.se390.thonk.tasks.AsyncRunnableWrapper;

public class NotificationService extends NotificationListenerService {
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

//        Vibrator vibrator = getSystemService(Vibrator.class);
//        vibrator.vibrate(new long[]{0, 100, 100, 100, 100, 100, 100, 100, 100}, 0);

        HistoryDao history = MainApplication.db.historyDao();
        Context context = getApplicationContext();
        new AsyncRunnableWrapper(() -> {
            history.insert(new NotificationEntry(context, sbn));
        }).start();
    }
}
