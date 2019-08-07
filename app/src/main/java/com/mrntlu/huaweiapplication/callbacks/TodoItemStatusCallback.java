package com.mrntlu.huaweiapplication.callbacks;

import com.mrntlu.huaweiapplication.models.TodoItem;

public interface TodoItemStatusCallback {

    void onTodoItemStatusChanged(TodoItem todoItem);
}
