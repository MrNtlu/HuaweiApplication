package com.mrntlu.huaweiapplication.ui.todo;

import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mrntlu.huaweiapplication.R;
import com.mrntlu.huaweiapplication.adapters.todolist.RecyclerTodoListTouchHelper;
import com.mrntlu.huaweiapplication.adapters.todolist.TodoListRecyclerAdapter;
import com.mrntlu.huaweiapplication.callbacks.TodoListClickedCallback;
import com.mrntlu.huaweiapplication.callbacks.TodoListExportClickedCallback;
import com.mrntlu.huaweiapplication.models.TodoItem;
import com.mrntlu.huaweiapplication.models.TodoList;
import com.mrntlu.huaweiapplication.viewmodels.TodoViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

public class FragmentTodo extends Fragment implements RecyclerTodoListTouchHelper.RecyclerItemTouchHelperListener, TodoListClickedCallback, TodoListExportClickedCallback {

    @BindView(R.id.todoListRV)
    RecyclerView todoListRV;

    @BindView(R.id.addFab)
    FloatingActionButton addFab;

    private TodoViewModel todoViewModel;
    private TodoListRecyclerAdapter adapter;
    private FragmentTransaction fragmentTransaction;
    private Dialog createDialog;

    public FragmentTodo() {}

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
        fragmentTransaction=((AppCompatActivity)view.getContext()).getSupportFragmentManager().beginTransaction();
        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
        adapter=new TodoListRecyclerAdapter();
        createDialog=new Dialog(view.getContext());

        //view.getContext().deleteDatabase(TodoDatabase.DATABASE_NAME);
        initRecyclerView();
        setupObservers();
        setListeners();
    }

    private void showDialog(){
        createDialog.setContentView(R.layout.dialog_todo_list);

        EditText todoNameText=createDialog.findViewById(R.id.todoNameText);
        Button createButton=createDialog.findViewById(R.id.createButton);

        createButton.setOnClickListener(view -> {
            if (!String.valueOf(todoNameText.getText()).isEmpty()) {
                todoViewModel.insertTodoList(new TodoList(String.valueOf(todoNameText.getText()))).subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e.getCause() instanceof SQLiteConstraintException) {
                            Toast.makeText(getContext(), "Name's should be unique!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                createDialog.dismiss();
            }else {
                Toast.makeText(getContext(), "Please don't leave it empty!", Toast.LENGTH_SHORT).show();
            }
        });

        createDialog.setCanceledOnTouchOutside(true);
        createDialog.show();
    }

    private void setListeners(){
        addFab.setOnClickListener(view -> showDialog());
    }

    private void initRecyclerView() {
        adapter.setCallbacks(this,this);
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
        if (viewHolder instanceof TodoListRecyclerAdapter.TodoListViewHolder) {
            TodoList todoList = adapter.removeTodoList(position);
            todoViewModel.deleteTodoList(todoList).subscribe();
        }
    }

    @Override
    public void onTodoItemClicked(TodoList todoList) {
        fragmentTransaction.replace(R.id.frameLayout,new FragmentTodoItems(todoList)).addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void sendEmail(String json){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"example@huawei.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Todo JSON");
        i.putExtra(Intent.EXTRA_TEXT   , json);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onExportClicked(TodoList todoList) {
        LiveData<List<TodoItem>> liveData=todoViewModel.getAllTodoItems(todoList.getId());
        liveData.observe(getViewLifecycleOwner(), todoItems -> {
            String json=new Gson().toJson(todoItems);
            sendEmail(json);
            liveData.removeObservers(getViewLifecycleOwner());
        });
    }
}
