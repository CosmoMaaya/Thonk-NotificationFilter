package com.se390.thonk.data;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Parcel;
import android.service.notification.StatusBarNotification;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;

@Entity(tableName = "history")
public class NotificationEntry {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long timestamp;
    public String title;
    public String subtext;
    public String text;
    public String bigtext;
    public String app;
    public String template;
    public int color;
    public byte[] largeIcon;
    public byte[] smallIcon;
    public byte[] picture;

    public NotificationEntry() {
    }

//    @TypeConverter
//    public static Icon bytesToIcon(byte[] b) {
//        Parcel parcel = Parcel.obtain();
//        parcel.unmarshall(b, 0, b.length);
//        Icon icon = Icon.CREATOR.createFromParcel(parcel);
//        parcel.recycle();
//        return icon;
//    }
//
//    @TypeConverter
//    public static byte[] bytesToIcon(Icon icon) {
//        icon.loadDrawable
//        System.out.println(icon);
//        Parcel parcel = Parcel.obtain();
//        icon.writeToParcel(parcel, 0);
//        byte[] b = parcel.marshall();
//        parcel.recycle();
//        return b;
//    }

    private String getBundleString(Bundle bundle, String key) {
        Object obj = bundle.get(key);
        if(obj == null) {
            return null;
        }
        return obj.toString();
    }

    private byte[] iconToBytes(Context context, Icon icon) {
        if(icon == null) {
            return null;
        }
        System.out.println(icon);
        Drawable drawable = icon.loadDrawable(context);
        Bitmap bitmap;
        System.out.println(drawable);
        if(drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return bitmapToBytes(bitmap);
    }

    private byte[] bitmapToBytes(Bitmap bitmap) {
        if(bitmap == null) {
            return null;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, output);
        return output.toByteArray();
    }

    public NotificationEntry(Context context, StatusBarNotification sbn) {
        Notification notif = sbn.getNotification();
        System.out.println(notif);
        System.out.println(notif.extras);
        title = getBundleString(notif.extras, Notification.EXTRA_TITLE);
        text = getBundleString(notif.extras, Notification.EXTRA_TEXT);
        subtext = getBundleString(notif.extras, Notification.EXTRA_SUB_TEXT);
        bigtext = getBundleString(notif.extras, Notification.EXTRA_BIG_TEXT);
        template = getBundleString(notif.extras, Notification.EXTRA_TEMPLATE);
        largeIcon = iconToBytes(context, notif.getLargeIcon());
        color = notif.color;
        smallIcon = iconToBytes(context, notif.getSmallIcon());
        picture = bitmapToBytes((Bitmap) notif.extras.get(Notification.EXTRA_PICTURE));
//        if(picture == null) {
//            picture = iconToBytes(context, (Icon) notif.extras.get(Notification.EXTRA_PICTURE_ICON));
//        }
        app = sbn.getPackageName();
        timestamp = sbn.getPostTime();
    }
}
