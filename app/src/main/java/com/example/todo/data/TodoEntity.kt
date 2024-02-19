package com.example.todo.data

import android.icu.text.SimpleDateFormat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity("todos")
data class TodoEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("title")
    val title: String,
    @ColumnInfo("description")
    val description: String,
    @ColumnInfo("added")
    val added : Long = System.currentTimeMillis(),
    @ColumnInfo("done")
    val done: Boolean = false

)
val TodoEntity.addDate: String get() = SimpleDateFormat("yyyy/dd/mm hh:mm").format(Date(added))