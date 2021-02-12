package com.example.timetablefragment;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * from event_table ORDER BY startYear, startMonth, startDay, startHour, startMin ASC")
    LiveData<List<Events>> getAllEvents();

    @Query("SELECT * FROM event_table WHERE startYear = :startYr AND startMonth = :startMon AND startDay = :startDay")
    public Events[] getCriteria(int startYr, int startMon, int startDay);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Events event);

    @Query("DELETE FROM event_table")
    void deleteAll();
}
