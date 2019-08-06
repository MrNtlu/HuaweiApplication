package com.mrntlu.huaweiapplication.persistance;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.mrntlu.huaweiapplication.models.TodoItem;
import com.mrntlu.huaweiapplication.models.TodoList;
import com.mrntlu.huaweiapplication.models.converters.TodoStatusConverter;

@Database(entities = {TodoList.class, TodoItem.class}, version = 1)
@TypeConverters({TodoStatusConverter.class})
public abstract class TodoDatabase extends RoomDatabase {

    public static final String DATABASE_NAME="todo_db";

    private static TodoDatabase instance;

    public static TodoDatabase provideLeagueDatabase(Application application){
        if (instance==null) {
            instance= Room.databaseBuilder(
                    application,
                    TodoDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract TodoDao getTodoDao();
}
