package com.mrntlu.huaweiapplication.ui.todo;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mrntlu.huaweiapplication.R;
import com.mrntlu.huaweiapplication.adapters.todoitem.RecyclerTodoItemTouchHelper;
import com.mrntlu.huaweiapplication.adapters.todoitem.TodoItemRecyclerAdapter;
import com.mrntlu.huaweiapplication.callbacks.TodoItemLongPressedCallback;
import com.mrntlu.huaweiapplication.callbacks.TodoItemStatusCallback;
import com.mrntlu.huaweiapplication.models.TodoItem;
import com.mrntlu.huaweiapplication.models.TodoList;
import com.mrntlu.huaweiapplication.viewmodels.TodoViewModel;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class FragmentTodoItems extends Fragment implements RecyclerTodoItemTouchHelper.RecyclerItemTouchHelperListener, TodoItemStatusCallback,TodoItemLongPressedCallback {

    @BindView(R.id.todoItemsRV)
    RecyclerView todoItemsRV;

    @BindView(R.id.addFab)
    FloatingActionButton addFab;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private TodoViewModel todoViewModel;
    private TodoItemRecyclerAdapter adapter;
    private TodoList todoList;
    private LiveData<List<TodoItem>> liveData;
    //Order and Filter
    private int parameter=1;
    private TodoItem.TodoStatus status= TodoItem.TodoStatus.ONGOING;

    public FragmentTodoItems(TodoList todoList) {
        this.todoList=todoList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_todo_items, container, false);
        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);
        ((TodoActivity)view.getContext()).setSupportActionBar(toolbar);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
        adapter=new TodoItemRecyclerAdapter();

        initRecyclerView();
        setupObservers(status,1);
        setListeners();
    }

    private void setListeners() {
        addFab.setOnClickListener(view -> {
            //todo Open add dialog
            todoViewModel.insertTodoItem(todoList,new TodoItem("TodoItem "+adapter.getItemCount()+1,"Todo Description "+adapter.getItemCount(),new Date(),new Date())).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public void onError(Throwable e) {
                    if (e.getCause() instanceof SQLiteConstraintException){
                        Toast.makeText(getContext(), "Name's should be unique!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    private void setupObservers(TodoItem.TodoStatus todoStatus,int parameter){
        this.status=todoStatus;
        this.parameter=parameter;
        setupObservers(null);
    }

    private void setupObservers(String name){
        if ((liveData==null || !liveData.hasObservers())) {
            if (name == null) {
                liveData = todoViewModel.getTodoItems(todoList.getId(), status,parameter);
                liveData.observe(getViewLifecycleOwner(), todoItems -> adapter.setTodoItems(todoItems));
            } else{
                liveData = todoViewModel.getTodoItemsByName(todoList.getId(), name);
                liveData.observe(getViewLifecycleOwner(),todoItems -> {
                    adapter.setTodoItems(todoItems);
                    Log.d("Test", "setupObservers: "+name+" "+todoItems.toString());
                });
            }
        }else{
            liveData.removeObservers(getViewLifecycleOwner());
            if (name==null) setupObservers(status,parameter);
            else setupObservers(name);
        }
    }

    private void initRecyclerView(){
        adapter.setCallbacks(this, this);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        todoItemsRV.setLayoutManager(linearLayoutManager);
        todoItemsRV.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerTodoItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(todoItemsRV);

        todoItemsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0)
                    addFab.hide();
                else
                    addFab.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.order_create_date:{
                setupObservers(status,1);
                return true;
            }case R.id.order_deadline:{
                setupObservers(status,2);
                return true;
            }case R.id.order_name:{
                setupObservers(status,3);
                return true;
            }case R.id.order_status:{
                setupObservers(status,4);
                return true;
            }case R.id.filter_complete:{
                setupObservers(TodoItem.TodoStatus.FINISHED,parameter);
                return true;
            }case R.id.filter_expired:{
                setupObservers(TodoItem.TodoStatus.EXPIRED,parameter);
                return true;
            }case R.id.filter_name:{
                setupObservers("TodoItem"); //Todo custom query open dialog
                return true;
            }case R.id.filter_ongoing:{
                setupObservers(TodoItem.TodoStatus.ONGOING,parameter);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.todo_items_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof TodoItemRecyclerAdapter.TodoItemViewHolder) {
            TodoItem todoItem = adapter.removeTodoItem(position);
            todoViewModel.deleteTodoItem(todoItem).subscribe();
        }
    }

    @Override
    public void onTodoItemLongPressed(TodoItem todoItem) {
        Toast.makeText(getContext(), "Long Pressed "+todoItem.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTodoItemStatusChanged(TodoItem todoItem) {
        todoViewModel.updateTodoItem(todoItem).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                Toast.makeText(getContext(), "Updated!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {

            }
        });

    }
}
