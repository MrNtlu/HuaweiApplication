package com.mrntlu.huaweiapplication.repository;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.mrntlu.huaweiapplication.models.TodoItem;
import com.mrntlu.huaweiapplication.models.TodoList;
import com.mrntlu.huaweiapplication.persistance.TodoDao;
import com.mrntlu.huaweiapplication.persistance.TodoDatabase;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class TodoRepository {

    private TodoDatabase todoDatabase;
    private TodoDao todoDao;

    public TodoRepository(Application application) {
        todoDao=TodoDatabase.provideLeagueDatabase(application).getTodoDao();
    }

    public LiveData<List<TodoList>> getTodoLists(){
        return todoDao.getTodoLists();
    }

    public LiveData<List<TodoItem>> getTodoItems(String todoListId){
        return todoDao.getTodoItems(todoListId);
    }

    public Completable insertTodoList(TodoList todoList){
        return Completable.fromRunnable(()-> todoDao.insertTodoList(todoList)).subscribeOn(Schedulers.io());
    }

    public Completable deleteTodoList(TodoList todoList){
        return Completable.fromRunnable(()-> todoDao.deleteTodoList(todoList)).subscribeOn(Schedulers.io());
    }

    public Completable updateTodoList(TodoList todoList){
        return Completable.fromRunnable(()-> todoDao.updateTodoList(todoList)).subscribeOn(Schedulers.io());
    }

    public Completable insertTodoItem(TodoList todoList,TodoItem todoItem){
        return Completable.fromRunnable(()-> todoDao.insertTodoItem(todoList,todoItem)).subscribeOn(Schedulers.io());
    }

    public Completable updateTodoItem(TodoItem todoItem){
        return Completable.fromRunnable(()-> todoDao.updateTodoItem(todoItem)).subscribeOn(Schedulers.io());
    }

    public Completable deleteTodoItem(TodoItem todoItem){
        return Completable.fromRunnable(()-> todoDao.deleteTodoItem(todoItem)).subscribeOn(Schedulers.io());
    }
}
