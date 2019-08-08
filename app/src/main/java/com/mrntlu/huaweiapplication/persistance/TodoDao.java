package com.mrntlu.huaweiapplication.persistance;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.mrntlu.huaweiapplication.models.TodoItem;
import com.mrntlu.huaweiapplication.models.TodoList;

import java.util.List;

@Dao
public abstract class TodoDao {
//GET
    @Query("SELECT * FROM todoList")
    public abstract LiveData<List<TodoList>> getTodoLists();

    @Query("SELECT * FROM todoItem WHERE todoList_id LIKE :todoListId AND " +
            "CASE :filterby " +
            "WHEN 1 THEN status LIKE :status " +
            "ELSE name LIKE '%' || :name || '%' END " +
            "ORDER BY CASE :orderby " +
            "WHEN 1 THEN created_at " +
            "WHEN 2 THEN deadline " +
            "WHEN 3 THEN name " +
            "WHEN 4 THEN status END ASC")
    public abstract LiveData<List<TodoItem>> getTodoItems(String todoListId,TodoItem.TodoStatus status,String name,int filterby,int orderby);

    @Query("SELECT * FROM todoItem WHERE todoList_id LIKE :todoListId")
    public abstract LiveData<List<TodoItem>> getAllTodoItems(String todoListId);

//INSERT
    @Transaction
    public void insertTodoItem(TodoList todoList,TodoItem todoItem){
        todoItem.setTodoListId(todoList.getId());
        insertTodoItem(todoItem);
    }

    @Insert
    public abstract void insertTodoList(TodoList todoList);

    @Insert
    public abstract void insertTodoItem(TodoItem todoItem);

//DELETE
    @Delete
    public abstract void deleteTodoList(TodoList todoList);

    @Delete
    public abstract void deleteTodoItem(TodoItem todoItem);

//UPDATE
    @Update
    public abstract void updateTodoList(TodoList todoList);

    @Update
    public abstract void updateTodoItem(TodoItem todoItem);
}
