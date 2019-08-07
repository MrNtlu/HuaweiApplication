package com.mrntlu.huaweiapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.mrntlu.huaweiapplication.models.converters.DateConverter;
import com.mrntlu.huaweiapplication.models.converters.TodoStatusConverter;

import java.util.Date;
import java.util.UUID;

@Entity(
        tableName = "todoItem",
        foreignKeys = @ForeignKey(
                entity = TodoList.class,
                parentColumns = "id",
                childColumns = "todoList_id",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
)
public class TodoItem implements Parcelable {

    public enum TodoStatus{
        FINISHED(0),
        ONGOING(1),
        EXPIRED(2);

        private int code;

        TodoStatus(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    @NonNull
    private String id;

    @ColumnInfo(name = "todoList_id")
    private String todoListId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "created_at")
    @TypeConverters(DateConverter.class)
    private Date createdAt;

    @ColumnInfo(name = "deadline")
    @TypeConverters(DateConverter.class)
    private Date deadline;

    @ColumnInfo(name = "status")
    @TypeConverters(TodoStatusConverter.class)
    private TodoStatus status;

    @Ignore
    public TodoItem() {
    }

    public TodoItem(String name, String description, Date deadline, Date createdAt) {
        this.id= UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.createdAt=createdAt;
        this.status=TodoStatus.ONGOING;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTodoListId() {
        return todoListId;
    }

    public void setTodoListId(String todoListId) {
        this.todoListId = todoListId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public TodoStatus getStatus() {
        return status;
    }

    public void setStatus(TodoStatus status) {
        this.status = status;
    }

    protected TodoItem(Parcel in) {
        id = in.readString();
        todoListId = in.readString();
        name = in.readString();
        description = in.readString();
        deadline=(Date) in.readSerializable();
        createdAt=(Date) in.readSerializable();
        status=TodoStatus.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(todoListId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeSerializable(deadline);
        dest.writeSerializable(createdAt);
        dest.writeString(status.name());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TodoItem> CREATOR = new Creator<TodoItem>() {
        @Override
        public TodoItem createFromParcel(Parcel in) {
            return new TodoItem(in);
        }

        @Override
        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };

    @Override
    public String toString() {
        return "TodoItem{" +
                "id='" + id + '\'' +
                ", todoListId='" + todoListId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", deadline=" + deadline +
                ", status=" + status +
                '}';
    }
}
