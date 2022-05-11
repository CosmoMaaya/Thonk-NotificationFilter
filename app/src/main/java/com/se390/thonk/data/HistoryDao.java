package com.se390.thonk.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY id DESC")
    LiveData<List<NotificationEntry>> getAll();

    @Insert
    void insert(NotificationEntry... entries);
}
