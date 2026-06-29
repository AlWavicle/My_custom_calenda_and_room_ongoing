package com.example.my_custom_calenda_1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EventDao {
    @Insert
    void insert(Event event); //일정 추가

    @Delete
    void delete(Event event);//일정 삭제

    @Query("SELECT * FROM events")
    List<Event> getAllEvents();//저장된 모든 일정 불러오기

    @Query("DELETE FROM events WHERE id = :id")
    void deleteById(int id);

    @Query("UPDATE events SET isChecked = :isChecked WHERE id = :id")
    void updateCheckStatus(int id, boolean isChecked);

    @Query("SELECT * FROM events WHERE startDate IS NULL AND endDate IS NULL")
    List<Event> getNullDateEvents();

    @androidx.room.Update
    void update(Event event);
}
