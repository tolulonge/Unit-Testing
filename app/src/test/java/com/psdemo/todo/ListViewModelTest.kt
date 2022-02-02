package com.psdemo.todo

import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.psdemo.todo.data.Todo
import com.psdemo.todo.data.TodoRepository
import com.psdemo.todo.list.ListViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.lang.IllegalArgumentException

class ListViewModelTest {

    @get:Rule
    val exceptionRule: ExpectedException = ExpectedException.none()
    val now = System.currentTimeMillis()
    val day = 1000 * 60 * 60 * 24

    @Test
    fun test_allTodosEmpty(){
        val expected = 0
        val repository : TodoRepository = mock()
        whenever(repository.getAllTodos())
            .thenReturn(MutableLiveData(arrayListOf()))

        val model = ListViewModel(repository)

        val todos = model.allTodos.value

        assertNotNull(todos)
        assertEquals(expected, todos!!.size)
    }

    @Test
    fun test_allTodosSingle(){
        val expected = 1
        val repository : TodoRepository = mock()
        whenever(repository.getAllTodos())
            .thenReturn(MutableLiveData(arrayListOf(
                Todo("4", "Todo 4", now + day, true, now)
            )))

        val model = ListViewModel(repository)

        val todos = model.allTodos.value

        assertNotNull(todos)
        assertEquals(expected, todos!!.size)
    }

    @Test
    fun test_allTodosMultiple(){
        val expected = 3
        val repository : TodoRepository = mock()
        whenever(repository.getAllTodos())
            .thenReturn(MutableLiveData(arrayListOf(
                Todo("2", "Todo 2", now + day, false, now),
                Todo("3", "Todo 3", now + day, false, now),
                Todo("4", "Todo 4", now + day, true, now)
            )))

        val model = ListViewModel(repository)

        val todos = model.allTodos.value

        assertNotNull(todos)
        assertEquals(expected, todos!!.size)
    }

    @Test
    fun test_upcomingTodosCountEmpty(){
        val expected = 0
        val repository : TodoRepository = mock()
        whenever(repository.getUpcomingTodosCount())
            .thenReturn(MutableLiveData(expected))

        val model = ListViewModel(repository)

        val todoCount = model.upcomingTodosCount.value

        assertNotNull(todoCount)
        assertEquals(expected,todoCount)
    }

    @Test
    fun test_upcomingTodosCountSingle(){
        val expected = 1
        val repository : TodoRepository = mock()
        whenever(repository.getUpcomingTodosCount())
            .thenReturn(MutableLiveData(expected))

        val model = ListViewModel(repository)

        val todoCount = model.upcomingTodosCount.value

        assertNotNull(todoCount)
        assertEquals(expected,todoCount)
    }

    @Test
    fun test_upcomingTodosCountMultiple(){
        val expected = 5
        val repository : TodoRepository = mock()
        whenever(repository.getUpcomingTodosCount())
            .thenReturn(MutableLiveData(expected))

        val model = ListViewModel(repository)

        val todoCount = model.upcomingTodosCount.value

        assertNotNull(todoCount)
        assertEquals(expected,todoCount)
    }

    @Test
    fun test_toggleTodo(){
        val repository : TodoRepository = mock()
        val id = "fake"
        val model = ListViewModel(repository)

        model.toggleTodo(id)

        verify(repository).toggleTodo(id)
    }

    @Test
    fun test_toggleTodoNotFound(){
        val repository : TodoRepository = mock()
        val exceptionMessage = "Todo not found"
        val id = "fake"
        whenever(repository.toggleTodo(id)).thenThrow(IllegalArgumentException(exceptionMessage))
        val model = ListViewModel(repository)
        exceptionRule.expect(IllegalArgumentException::class.java)
        exceptionRule.expectMessage(exceptionMessage)

        model.toggleTodo(id)

        verify(repository).toggleTodo(id)
    }
}