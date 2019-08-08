package com.mrntlu.huaweiapplication.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.mrntlu.huaweiapplication.models.TodoItem;
import com.mrntlu.huaweiapplication.models.TodoList;
import com.mrntlu.huaweiapplication.repository.TodoRepository;
import java.util.List;
import io.reactivex.Completable;

public class TodoViewModel extends AndroidViewModel {

    private TodoRepository todoRepository;

    public TodoViewModel(@NonNull Application application) {
        super(application);
        todoRepository=new TodoRepository(application);
    }

    public LiveData<List<TodoList>> getTodoLists(){
        return todoRepository.getTodoLists();
    }

    public LiveData<List<TodoItem>> getTodoItems(String todoListId, TodoItem.TodoStatus todoStatus,String name,int filterby,int orderby){
        return todoRepository.getTodoItems(todoListId,todoStatus,name,filterby,orderby);
    }

    public LiveData<List<TodoItem>> getAllTodoItems(String todoListId){
        return todoRepository.getAllTodoItems(todoListId);
    }

    public Completable insertTodoList(TodoList todoList){
        return todoRepository.insertTodoList(todoList);
    }

    public Completable updateTodoList(TodoList todoList){
        return todoRepository.updateTodoList(todoList);
    }

    public Completable deleteTodoList(TodoList todoList){
        return todoRepository.deleteTodoList(todoList);
    }

    public Completable insertTodoItem(TodoList todoList,TodoItem todoItem){
        return todoRepository.insertTodoItem(todoList,todoItem);
    }

    public Completable updateTodoItem(TodoItem todoItem){
        return todoRepository.updateTodoItem(todoItem);
    }

    public Completable deleteTodoItem(TodoItem todoItem){
        return todoRepository.deleteTodoItem(todoItem);
    }
}
