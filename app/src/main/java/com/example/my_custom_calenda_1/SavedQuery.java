package com.example.my_custom_calenda_1;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_queries")
public class SavedQuery {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String query;
    private long timestamp;

    public SavedQuery(String query, long timestamp) {
        this.query = query;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
