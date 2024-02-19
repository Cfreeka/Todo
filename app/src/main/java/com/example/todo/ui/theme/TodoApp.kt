package com.example.todo.ui.theme

import android.app.Application
import androidx.room.Room
import com.example.todo.data.TodoDatabase
import com.example.todo.repositories.TodoRepo
import com.example.todo.repositories.TodoRepoImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class TodoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TodoApp)
            modules(databaseModule, repositoryModule)
        }
    }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            TodoDatabase::class.java,
            "todo_database"
        ).build()
    }
}

val repositoryModule = module {
    single<TodoRepo> {
        TodoRepoImpl(get())
    }
}
