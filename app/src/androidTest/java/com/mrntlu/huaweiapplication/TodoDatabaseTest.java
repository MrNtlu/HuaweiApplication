package com.mrntlu.huaweiapplication;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import com.mrntlu.huaweiapplication.persistance.TodoDao;
import com.mrntlu.huaweiapplication.persistance.TodoDatabase;
import org.junit.After;
import org.junit.Before;

public abstract class TodoDatabaseTest {

    private TodoDatabase noteDatabase;


    public TodoDao getTodoDao(){
        return noteDatabase.getTodoDao();
    }

    @Before
    public void init(){
        noteDatabase = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                TodoDatabase.class
        ).build();
    }

    @After
    public void finish(){
        noteDatabase.close();
    }
}