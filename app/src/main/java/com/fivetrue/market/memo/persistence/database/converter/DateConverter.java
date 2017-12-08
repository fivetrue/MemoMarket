package com.fivetrue.market.memo.persistence.database.converter;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by kwonojin on 2017. 11. 16..
 */

public class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
