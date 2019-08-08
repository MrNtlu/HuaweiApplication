package com.mrntlu.huaweiapplication.ui.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
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
    private Dialog createDialog;
    private Dialog nameFilterDialog;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd yyyy", Locale.ENGLISH);
    private SimpleDateFormat dateDisplayFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
    private final int DIALOG_CREATE_STATE=0,DIALOG_EDIT_STATE=1;

    //Order and Filter
    private int orderby =1;
    private int filterby =1;
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
        createDialog=new Dialog(view.getContext());
        nameFilterDialog=new Dialog(view.getContext());

        checkExpired();
        initRecyclerView();
        setupObservers(status,1);
        setListeners();
    }

    private void checkExpired(){
        LiveData<List<TodoItem>> liveData=todoViewModel.getAllTodoItems(todoList.getId());
        liveData.observe(getViewLifecycleOwner(), todoItems -> {
            for (TodoItem todoItem:todoItems){
                if (todoItem.getDeadline().before(new Date()) && todoItem.getStatus()== TodoItem.TodoStatus.ONGOING){
                    todoItem.setStatus(TodoItem.TodoStatus.EXPIRED);
                    todoViewModel.updateTodoItem(todoItem).subscribe();
                }
            }
            liveData.removeObservers(getViewLifecycleOwner());
        });
    }

    private void showDialog(TodoItem todoItem,int state){
        createDialog.setContentView(R.layout.dialog_todo_items);
        final Date[] deadline = new Date[1];

        EditText itemNameText=createDialog.findViewById(R.id.itemNameText);
        EditText itemDescriptionText=createDialog.findViewById(R.id.itemDescriptionText);
        TextView dateTextview=createDialog.findViewById(R.id.dateTextview);
        ImageButton pickdateButton=createDialog.findViewById(R.id.pickdateButton);
        Button createButton=createDialog.findViewById(R.id.createButton);

        if (todoItem!=null && state==DIALOG_EDIT_STATE){
            createButton.setText("Update");
            itemNameText.setText(todoItem.getName());
            itemDescriptionText.setText(todoItem.getDescription());
            dateTextview.setText(dateDisplayFormat.format(todoItem.getDeadline()));
            deadline[0]=todoItem.getDeadline();
        }

        DatePickerDialog.OnDateSetListener dateSetListener= (datePicker, year, month, date) -> {
            try {
                deadline[0] =dateFormat.parse((month+1)+" "+date+" "+year);
                if (deadline[0]!=null) dateTextview.setText(dateDisplayFormat.format(deadline[0]));
            } catch (ParseException e) {
                e.printStackTrace();
                dateTextview.setText(e.getMessage());
                Toast.makeText(getContext(), "Error! Please select date again!", Toast.LENGTH_SHORT).show();
            }
        };

        pickdateButton.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog=new DatePickerDialog(view.getContext(),dateSetListener,
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 2000);
            datePickerDialog.show();
        });

        createButton.setOnClickListener(view -> {
            if (!String.valueOf(itemNameText.getText()).isEmpty() && !String.valueOf(itemDescriptionText.getText()).isEmpty() && deadline[0]!=null){
                Completable completable;
                if (state==DIALOG_CREATE_STATE && todoItem==null)
                    completable=todoViewModel.insertTodoItem(todoList,new TodoItem(String.valueOf(itemNameText.getText()),String.valueOf(itemDescriptionText.getText()),deadline[0],new Date()));
                else{
                    todoItem.setName(String.valueOf(itemNameText.getText()));
                    todoItem.setDescription(String.valueOf(itemDescriptionText.getText()));
                    completable=todoViewModel.updateTodoItem(todoItem);
                    todoItem.setDeadline(deadline[0]);
                }
                completable.subscribe(new CompletableObserver() {
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
                createDialog.dismiss();
            }else{
                Toast.makeText(getContext(), "Please don't leave anything empty!", Toast.LENGTH_SHORT).show();
            }
        });

        createDialog.setCanceledOnTouchOutside(true);
        createDialog.show();
    }

    private void showNameFilterDialog(){
        nameFilterDialog.setContentView(R.layout.dialog_todo_list);

        EditText todoNameText=nameFilterDialog.findViewById(R.id.todoNameText);
        Button createButton=nameFilterDialog.findViewById(R.id.createButton);
        todoNameText.setHint("Filter by name");
        createButton.setText("Filter");
        createButton.setOnClickListener(view -> {
            if (!String.valueOf(todoNameText.getText()).isEmpty()){
                setupObservers(String.valueOf(todoNameText.getText()));
                nameFilterDialog.dismiss();
            }else {
                Toast.makeText(getContext(), "Please don't leave it empty!", Toast.LENGTH_SHORT).show();
            }
        });

        nameFilterDialog.setCanceledOnTouchOutside(true);
        nameFilterDialog.show();
    }

    private void setListeners() {
        addFab.setOnClickListener(view -> {
            showDialog(null,DIALOG_CREATE_STATE);
        });
    }

    private void setupObservers(TodoItem.TodoStatus todoStatus,int orderby){
        this.status=todoStatus;
        this.orderby =orderby;
        setupObservers(null);
    }

    private void setupObservers(String name){
        if ((liveData==null || !liveData.hasObservers())) {
            filterby=name!=null?0:1;
            liveData = todoViewModel.getTodoItems(todoList.getId(), status,name,filterby,orderby);
            liveData.observe(getViewLifecycleOwner(), todoItems -> adapter.setTodoItems(todoItems));
        }else{
            liveData.removeObservers(getViewLifecycleOwner());
            if (name==null) setupObservers(status, orderby);
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
                setupObservers(TodoItem.TodoStatus.FINISHED, orderby);
                return true;
            }case R.id.filter_expired:{
                setupObservers(TodoItem.TodoStatus.EXPIRED, orderby);
                return true;
            }case R.id.filter_name:{
                showNameFilterDialog();
                return true;
            }case R.id.filter_ongoing:{
                setupObservers(TodoItem.TodoStatus.ONGOING, orderby);
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
        showDialog(todoItem,DIALOG_EDIT_STATE);
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
