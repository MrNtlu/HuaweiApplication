package com.mrntlu.huaweiapplication.ui.todo;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mrntlu.huaweiapplication.R;
import com.mrntlu.huaweiapplication.adapters.todolist.RecyclerTodoListTouchHelper;
import com.mrntlu.huaweiapplication.adapters.todolist.TodoListRecyclerAdapter;
import com.mrntlu.huaweiapplication.callbacks.TodoItemClickedCallback;
import com.mrntlu.huaweiapplication.models.TodoList;
import com.mrntlu.huaweiapplication.viewmodels.TodoViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

public class FragmentTodo extends Fragment implements RecyclerTodoListTouchHelper.RecyclerItemTouchHelperListener, TodoItemClickedCallback {

    @BindView(R.id.todoListRV)
    RecyclerView todoListRV;

    @BindView(R.id.addFab)
    FloatingActionButton addFab;

    private TodoViewModel todoViewModel;
    private TodoListRecyclerAdapter adapter;

    public FragmentTodo() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
        adapter=new TodoListRecyclerAdapter();

        initRecyclerView();
        setupObservers();
        setListeners();
    }

    private void setListeners(){
        addFab.setOnClickListener(view -> {
            //todo Open add dialog
            todoViewModel.insertTodoList(new TodoList("Todo "+adapter.getItemCount()+1)).subscribe(new CompletableObserver() {
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

    private void initRecyclerView() {
        adapter.setTodoItemClickedCallback(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        todoListRV.setLayoutManager(linearLayoutManager);
        todoListRV.addItemDecoration(new DividerItemDecoration(todoListRV.getContext(), DividerItemDecoration.VERTICAL));
        todoListRV.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerTodoListTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(todoListRV);

        todoListRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    private void setupObservers() {
        todoViewModel.getTodoLists().observe(getViewLifecycleOwner(),todoLists -> {
            adapter.setTodoLists(todoLists);
        });
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        TodoList todoList=adapter.removeTodoList(position);
        todoViewModel.deleteTodoList(todoList).subscribe();
    }

    @Override
    public void onTodoItemClicked(TodoList todoList) {
        Toast.makeText(getContext(), todoList.getName()+" clicked!", Toast.LENGTH_SHORT).show();
    }
}
