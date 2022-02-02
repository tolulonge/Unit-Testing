package com.psdemo.todo

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import com.psdemo.todo.data.Todo
import com.psdemo.todo.data.TodoDao
import com.psdemo.todo.data.TodoRoomDatabase
import com.psdemo.todo.data.TodoRoomRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import java.lang.RuntimeException
import kotlin.math.exp

@RunWith(AndroidJUnit4::class)
class TodoRoomRepositoryTest {

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @get:Rule
    val exceptionRule = ExpectedException.none()

    val now = System.currentTimeMillis()
    val day = 1000 * 60 * 60 * 24

    private lateinit var db: TodoRoomDatabase

    @Before
    fun setUp(){
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(context, TodoRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Test
    fun test_getUpcomingTodoCountEmpty(){
        val dao = spy(db.todoDao())
        val repo = TodoRoomRepository(dao)

        val expected = 0
        val actual = repo.getUpcomingTodosCount().test().value()

        assertEquals(expected, actual)
        verify(dao).getDateCount(any())
    }

    @Test
    fun test_getUpcomingTodoCountSingleMatch(){
        val dao = spy(db.todoDao())
        db.todoDao().insert(Todo("5", "Todo 5", now - day, false, now))
        db.todoDao().insert(Todo("4", "Todo 4", now + day, true, now))
        db.todoDao().insert(Todo("3", "Todo 3", now + day, false, now))
        val repo = TodoRoomRepository(dao)

        val expected = 1
        val actual = repo.getUpcomingTodosCount().test().value()

        assertEquals(expected, actual)
        verify(dao).getDateCount(any())
    }

    @Test
    fun test_getAllTodosMultiple(){
        val dao = spy(db.todoDao())
        val testTodo = Todo("5", "Todo 5", now - day, false, now)
        db.todoDao().insert(testTodo)
        db.todoDao().insert(Todo("4", "Todo 4", now + day, true, now))
        db.todoDao().insert(Todo("3", "Todo 3", now + day, false, now))
        val repo = TodoRoomRepository(dao)
        val expected = 3

        val actual = repo.getAllTodos().test().value()

        assertEquals(expected, actual.size)
        verify(dao).getAllTodos()
        assertTrue(actual.contains(testTodo))
    }

    @Test
    fun test_toggleTodoGoodId(){
        val dao = mock<TodoDao> {
            on(it.toggleTodo(any()))
                .doAnswer {
                    val id = it.arguments[0]
                    require(id != "bad") {"bad id"}
                    1
                }
        }

        val repo = TodoRoomRepository(dao)
        val id = "good"

        repo.toggleTodo(id)

        verify(dao).toggleTodo(id)
    }

    @Test
    fun test_toggleTodoBadId(){
        val dao = mock<TodoDao> {
            on(it.toggleTodo(any()))
                .doAnswer {
                    val id = it.arguments[0]
                    require(id != "bad") {"bad id"}
                    1
                }
        }

        val repo = TodoRoomRepository(dao)
        val id = "bad"
        exceptionRule.expect(RuntimeException::class.java)

        repo.toggleTodo(id)

        verify(dao).toggleTodo(id)
    }

    @Test
    fun test_insert(){
        val dao = mock<TodoDao>()
        val repo = TodoRoomRepository(dao)

        val expected = Todo("1","Test todo", null, false, now)

        repo.insert(expected)

        argumentCaptor<Todo>()
            .apply {
                verify(dao).insert(capture())
                assertEquals(expected, firstValue)
            }
    }


    @After
    fun teardown(){
        db.close()
    }
}