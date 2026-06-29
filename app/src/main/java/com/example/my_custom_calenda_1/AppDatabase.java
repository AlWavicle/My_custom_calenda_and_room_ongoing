package com.example.my_custom_calenda_1;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Event.class, SavedQuery.class}, version = 6) // 버전 상향 (5 -> 6)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventDao eventDao();
    public abstract SavedQueryDao savedQueryDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "calendar_database")
                            .fallbackToDestructiveMigration() // 스키마 변경 시 기존 데이터 삭제 및 재생성
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
