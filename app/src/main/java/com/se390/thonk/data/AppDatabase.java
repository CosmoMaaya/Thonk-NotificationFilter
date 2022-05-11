package com.se390.thonk.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

@Database(entities = {NotificationEntry.class}, version = 1)
//@TypeConverters({NotificationEntry.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract HistoryDao historyDao();
}
