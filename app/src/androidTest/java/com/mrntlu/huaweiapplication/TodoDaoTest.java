package com.mrntlu.huaweiapplication;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.mrntlu.huaweiapplication.models.TodoItem;
import com.mrntlu.huaweiapplication.models.TodoList;
import com.mrntlu.huaweiapplication.util.LiveDataTestUtil;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class TodoDaoTest extends TodoDatabaseTest{
    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    public static final TodoList TEST_TODOLIST_1=new TodoList("Daily Todo List");
    public static final TodoList TEST_TODOLIST_2=new TodoList("Shopping List");
    public static final TodoItem TEST_TODOITEM_1=new TodoItem("Work","Work on Android Project",new Date(),new Date());
    public static final TodoItem TEST_TODOITEM_2=new TodoItem("Food","Buy some random food",new Date(),new Date());

    @Test
    public void insertReadDelete() throws InterruptedException {
        getTodoDao().insertTodoList(TEST_TODOLIST_1);

        LiveDataTestUtil<List<TodoList>> liveDataTestUtil=new LiveDataTestUtil<>();
        List<TodoList> insertedTodoLists=liveDataTestUtil.getValue(getTodoDao().getTodoLists());

        assertNotNull(insertedTodoLists);

        assertEquals(TEST_TODOLIST_1.getName(),insertedTodoLists.get(0).getName());
        assertEquals(TEST_TODOLIST_1.getId(),insertedTodoLists.get(0).getId());

        getTodoDao().deleteTodoList(TEST_TODOLIST_1);
        insertedTodoLists=liveDataTestUtil.getValue(getTodoDao().getTodoLists());
        assertEquals(0,insertedTodoLists.size());
    }
}
