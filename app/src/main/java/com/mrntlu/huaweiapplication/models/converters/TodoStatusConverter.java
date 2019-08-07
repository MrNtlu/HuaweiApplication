package com.mrntlu.huaweiapplication.models.converters;

import androidx.room.TypeConverter;

import com.mrntlu.huaweiapplication.models.TodoItem;

import static com.mrntlu.huaweiapplication.models.TodoItem.TodoStatus.EXPIRED;
import static com.mrntlu.huaweiapplication.models.TodoItem.TodoStatus.FINISHED;
import static com.mrntlu.huaweiapplication.models.TodoItem.TodoStatus.ONGOING;

public class TodoStatusConverter {
    @TypeConverter
    public static TodoItem.TodoStatus toStatus(int status) {
        if (status == ONGOING.getCode()) {
            return ONGOING;
        } else if (status == FINISHED.getCode()) {
            return FINISHED;
        } else if (status == EXPIRED.getCode()){
            return EXPIRED;
        }else{
            throw new IllegalArgumentException("Couldn't recognize status");
        }
    }

    @TypeConverter
    public static int toInteger(TodoItem.TodoStatus status) {
        return status.getCode();
    }
}
