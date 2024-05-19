package com.example.final_blackjack.converters;

import android.os.Build;

import androidx.room.TypeConverter;
import java.time.LocalDate;

public class DateConverter{
    @TypeConverter
    public static LocalDate fromTimestamp(Long value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return value == null ? null : LocalDate.ofEpochDay(value);
        }
        return null;
    }

    @TypeConverter
    public static Long dateToTimestamp(LocalDate date) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return date == null ? null : date.toEpochDay();
        }
        return null;
    }
}