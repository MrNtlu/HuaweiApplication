package com.mrntlu.huaweiapplication.adapters.todoitem;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mrntlu.huaweiapplication.R;
import com.mrntlu.huaweiapplication.adapters.viewholders.LoadingItemViewHolder;
import com.mrntlu.huaweiapplication.adapters.viewholders.NoItemViewHolder;
import com.mrntlu.huaweiapplication.callbacks.TodoItemLongPressedCallback;
import com.mrntlu.huaweiapplication.callbacks.TodoItemStatusCallback;
import com.mrntlu.huaweiapplication.models.TodoItem;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoItemRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TodoItem> todoItems;
    private final int TODO_ITEM_HOLDER=0,LOADING_HOLDER=1,NO_LIST_HOLDER=2;
    private boolean isAdapterSet=false;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
    private TodoItemLongPressedCallback todoItemLongPressedCallback;
    private TodoItemStatusCallback todoItemStatusCallback;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==LOADING_HOLDER){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_loading_item,parent,false);
            return new LoadingItemViewHolder(view);
        }else if (viewType==TODO_ITEM_HOLDER){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_todo_item,parent,false);
            return new TodoItemViewHolder(view);
        }else {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_no_item,parent,false);
            return new NoItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TodoItemViewHolder){
            TodoItem todoItem=todoItems.get(position);
            TodoItemViewHolder viewHolder= (TodoItemViewHolder) holder;

            viewHolder.deadlineText.setText(dateFormat.format(todoItem.getDeadline()));
            viewHolder.descriptionText.setText(todoItem.getDescription());
            viewHolder.nameText.setText(todoItem.getName());
            viewHolder.completeCheckBox.setChecked(TodoItem.TodoStatus.FINISHED==todoItem.getStatus());
            int color;
            if (todoItem.getStatus()== TodoItem.TodoStatus.ONGOING) color=android.R.color.white;
            else if (todoItem.getStatus()== TodoItem.TodoStatus.FINISHED)color=R.color.gray;
            else color=android.R.color.holo_blue_light;
            viewHolder.foregroundCell.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(),color));

            viewHolder.completeCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
                if (compoundButton.isPressed()) {
                    TodoItem.TodoStatus status=b? TodoItem.TodoStatus.FINISHED: TodoItem.TodoStatus.ONGOING;
                    if (status!=todoItem.getStatus()) {
                        todoItem.setStatus(b ? TodoItem.TodoStatus.FINISHED : TodoItem.TodoStatus.ONGOING);
                        todoItemStatusCallback.onTodoItemStatusChanged(todoItem);
                        viewHolder.foregroundCell.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), todoItem.getStatus() != TodoItem.TodoStatus.ONGOING ? R.color.gray : android.R.color.white));
                    }
                }
            });

            viewHolder.itemView.setOnLongClickListener(view -> {
                todoItemLongPressedCallback.onTodoItemLongPressed(todoItem);
                return true;
            });

        }else if (holder instanceof NoItemViewHolder){
            ((NoItemViewHolder) holder).noItemText.setText("Nothing found!");
        }
    }

    public void setCallbacks(TodoItemStatusCallback statusCallback, TodoItemLongPressedCallback longPressedCallback){
        this.todoItemStatusCallback=statusCallback;
        this.todoItemLongPressedCallback=longPressedCallback;
    }

    public TodoItem removeTodoItem(int position){
        TodoItem todoItem= todoItems.remove(position);
        notifyDataSetChanged();
        return todoItem;
    }

    public void setTodoItems(List<TodoItem> todoItems) {
        isAdapterSet=true;
        this.todoItems = todoItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return isAdapterSet?(todoItems.size()==0?NO_LIST_HOLDER:TODO_ITEM_HOLDER):LOADING_HOLDER;
    }

    @Override
    public int getItemCount() {
        return isAdapterSet?(todoItems.size()==0?1:todoItems.size()):1;
    }

    public class TodoItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.completeCheckBox)
        CheckBox completeCheckBox;

        @BindView(R.id.nameText)
        TextView nameText;

        @BindView(R.id.descriptionText)
        TextView descriptionText;

        @BindView(R.id.deadlineText)
        TextView deadlineText;

        @BindView(R.id.foregroundCell)
        ConstraintLayout foregroundCell;

        TodoItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
