package com.mrntlu.huaweiapplication.models;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class TodoItemTest {

    @Test
    public void isTodoItemsEqual_identicalProperties_returnTrue() {
        TodoItem todoItem1=new TodoItem("TodoItem","Todo Items #1",new Date(),new Date());
        TodoItem todoItem2=new TodoItem("TodoItem","Todo Items #1",new Date(),new Date());

        assertEquals(todoItem1.getName(),todoItem2.getName());
    }

    @Test
    public void isTodoItemsIdenticalNameDifferentDescriptions_returnFalse() {
        TodoItem todoItem1=new TodoItem("TodoItem","Todo Items #1",new Date(),new Date());
        TodoItem todoItem2=new TodoItem("TodoItem","Todo Items #2",new Date(),new Date());

        assertEquals(todoItem1.getName(),todoItem2.getName());
        assertNotEquals(todoItem1.getDescription(),todoItem2.getDescription());
        assertNotEquals(todoItem1,todoItem2);
    }
}
