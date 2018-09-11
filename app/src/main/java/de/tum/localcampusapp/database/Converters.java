package de.tum.localcampusapp.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
