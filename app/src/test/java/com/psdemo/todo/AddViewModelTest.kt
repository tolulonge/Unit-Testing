package com.psdemo.todo

import com.nhaarman.mockitokotlin2.*
import com.psdemo.todo.add.AddViewModel
import com.psdemo.todo.data.TodoRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit


class AddViewModelTest {
    @get:Rule
    val collector = MockitoJUnit.collector()

    @Test
    fun test_saveWithoutDate(){
        val repository: TodoRepository = mock()
        val model = AddViewModel(repository)
        val actualTitle = "Test Todo"
        model.todo.title = actualTitle

        val actual = model.save()

        assertNull(actual)
        verify(repository).insert(any())
        verify(repository).insert(argThat {
            title == actualTitle && dueDate == null
        })
    }

    @Test
    fun test_saveNoTitle(){
        val repository: TodoRepository = mock()
        val model = AddViewModel(repository)
        val expected = "Title is required"

        val actual = model.save()

        assertEquals(expected, actual)
        verify(repository, never()).insert(any())
    }

    @Test
    fun test_saveWithDate(){
        val repository: TodoRepository = mock()
        val model = AddViewModel(repository)
        val actualTitle = "Test Todo"
        model.todo.title = actualTitle
        val actualDate = System.currentTimeMillis()
        model.todo.dueDate = actualDate

        val actual = model.save()

        assertNull(actual)
        verify(repository).insert(argThat {
            title == actualTitle && dueDate == actualDate
        })
    }
}