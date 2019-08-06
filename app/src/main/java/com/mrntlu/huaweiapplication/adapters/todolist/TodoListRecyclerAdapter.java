package com.mrntlu.huaweiapplication.adapters.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mrntlu.huaweiapplication.R;
import com.mrntlu.huaweiapplication.adapters.viewholders.LoadingItemViewHolder;
import com.mrntlu.huaweiapplication.adapters.viewholders.NoItemViewHolder;
import com.mrntlu.huaweiapplication.callbacks.TodoItemClickedCallback;
import com.mrntlu.huaweiapplication.models.TodoList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TODO_LIST_HOLDER=0,LOADING_HOLDER=1,NO_LIST_HOLDER=2;

    private boolean isAdapterSet=false;
    private List<TodoList> todoLists;
    private TodoItemClickedCallback todoItemClickedCallback;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==LOADING_HOLDER){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_loading_item,parent,false);
            return new LoadingItemViewHolder(view);
        }else if (viewType==TODO_LIST_HOLDER){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_todo_list,parent,false);
            return new TodoListViewHolder(view);
        }else {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_no_item,parent,false);
            return new NoItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TodoListViewHolder){
            TodoList todoList=todoLists.get(position);

            ((TodoListViewHolder) holder).todoListNameText.setText(todoList.getName());
            holder.itemView.setOnClickListener(view -> todoItemClickedCallback.onTodoItemClicked(todoList));
        }else if (holder instanceof NoItemViewHolder){
            ((NoItemViewHolder) holder).noItemText.setText("Nothing found!");
        }
    }

    public TodoList removeTodoList(int position){
        TodoList todoList= todoLists.remove(position);
        notifyItemRemoved(position);
        return todoList;
    }

    public void setTodoLists(List<TodoList> todoLists) {
        isAdapterSet=true;
        this.todoLists = todoLists;
        notifyDataSetChanged();
    }

    public void setTodoItemClickedCallback(TodoItemClickedCallback todoItemClickedCallback) {
        this.todoItemClickedCallback = todoItemClickedCallback;
    }

    @Override
    public int getItemCount() {
        return isAdapterSet?(todoLists.size()==0?1:todoLists.size()):1;
    }

    @Override
    public int getItemViewType(int position) {
        return isAdapterSet?(todoLists.size()==0?NO_LIST_HOLDER:TODO_LIST_HOLDER):LOADING_HOLDER;
    }

    public class TodoListViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.todoListNameText)
        TextView todoListNameText;

        @BindView(R.id.foregroundCell)
        ConstraintLayout foregroundCell;

        TodoListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
