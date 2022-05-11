package com.se390.thonk;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.se390.thonk.data.AppDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class MainApplication extends Application {
    public static MainApplication inst;
    public static AppDatabase db;

    private HashMap<String, AppInfoCache> appInfoCache = new HashMap<>();

    public void onCreate() {
        super.onCreate();
        if(db == null) {
            db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database").build();
        }
        if(inst == null) {
            inst = this;
        }
    }

    public static class AppInfoCache {
        public final String pkg;
        public final ApplicationInfo info;
        public final String name;
        public final Drawable icon;
        public AppInfoCache(String pkg, ApplicationInfo info, String name, Drawable icon) {
            this.pkg = pkg;
            this.info = info;
            this.name = name;
            this.icon = icon;
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof AppInfoCache)) {
                return false;
            }
            return Objects.equals(pkg, ((AppInfoCache) o).pkg);
        }

        @Override
        public int hashCode() {
            return pkg.hashCode();
        }

        @Override
        public String toString() {
            return AppInfoCache.class.getName() + "[" + pkg + "]";
        }
    }

    public List<AppInfoCache> listApps() {
        PackageManager pm = getApplicationContext().getPackageManager();
        List<ApplicationInfo> infos = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        HashSet<String> packagesToClear = new HashSet<>(appInfoCache.keySet());

        List<AppInfoCache> outputList = new ArrayList<>();
        for(ApplicationInfo info : infos) {
            packagesToClear.remove(info.packageName);
            outputList.add(getAppInfo(info));
        }

        for(String pkg : packagesToClear) {
            appInfoCache.remove(pkg);
        }

        return outputList;
    }

    public AppInfoCache getAppInfo(ApplicationInfo info) {
        AppInfoCache infoCache = appInfoCache.get(info.packageName);
        if(infoCache != null) {
            return infoCache;
        }
        PackageManager pm = getApplicationContext().getPackageManager();
        String name = pm.getApplicationLabel(info).toString();
        Drawable icon = pm.getApplicationIcon(info);
        infoCache = new AppInfoCache(info.packageName, info, name, icon);
        appInfoCache.put(info.packageName, infoCache);
        return infoCache;
    }

    public AppInfoCache getAppInfo(String packageName) {
        AppInfoCache infoCache = appInfoCache.get(packageName);
        if(infoCache != null) {
            return infoCache;
        }
        PackageManager pm = getApplicationContext().getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
            String name = pm.getApplicationLabel(info).toString();
            Drawable icon = pm.getApplicationIcon(info);
            infoCache = new AppInfoCache(packageName, info, name, icon);
            appInfoCache.put(packageName, infoCache);
            return infoCache;
        } catch(PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public String getAppName(String packageName) {
        AppInfoCache infoCache = getAppInfo(packageName);
        if(infoCache == null) {
            return packageName;
        }
        return infoCache.name;
    }
}
