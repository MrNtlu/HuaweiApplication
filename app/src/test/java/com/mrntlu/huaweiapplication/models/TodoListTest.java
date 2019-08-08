package com.mrntlu.huaweiapplication.models;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TodoListTest {

    @Test
    public void isTodoListsEqual_identicalProperties_returnTrue() {
        TodoList todoList1=new TodoList("This is todo #1");
        TodoList todoList2=new TodoList("This is todo #1");

        assertEquals(todoList1.getName(),todoList2.getName());
    }

    @Test
    public void isTodoListsIdenticalProperties_differentIds_returnFalse() {
        TodoList todoList1=new TodoList("This is todo #1");
        TodoList todoList2=new TodoList("This is todo #1");

        assertNotEquals(todoList1.getId(),todoList2.getId());
    }
}
