package com.example.my_custom_calenda_1;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface SavedQueryDao {
    @Insert
    void insert(SavedQuery savedQuery);

    @Query("SELECT * FROM saved_queries ORDER BY timestamp DESC")
    List<SavedQuery> getAllSavedQueries();

    @Query("DELETE FROM saved_queries WHERE id = :id")
    void deleteById(int id);
}
