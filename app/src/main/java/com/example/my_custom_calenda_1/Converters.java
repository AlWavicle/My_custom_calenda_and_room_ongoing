package com.example.my_custom_calenda_1;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class Converters {
    @TypeConverter
    public static LocalDate fromString(String value){
        return value == null ? null : LocalDate.parse(value);
    }

    @TypeConverter
    public static String dateToString(LocalDate date){
        return date == null ? null : date.toString();
    }

}
