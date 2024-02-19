package com.example.todo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo.data.TodoEntity
import java.sql.Time

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    // State for todos and pending tasks count
    val todos by viewModel.todos.collectAsState()
    var pendingTasksCount by remember { mutableStateOf(todos.count { !it.done }) }
    var completedTasksCount by remember { mutableStateOf(todos.count { it.done }) }

    // Function to update the pending tasks count
    val updatePendingTasksCount: () -> Unit = {
        pendingTasksCount = todos.count { !it.done }
        completedTasksCount = todos.count { it.done }
    }

    val (dialogOpen, setDialogOpen) = remember {
        mutableStateOf(false)
    }
    var (title, setTitle) = remember {
        mutableStateOf("")
    }
    var (description, setDescription) = remember {
        mutableStateOf("")
    }
    var (editingTodo, setEditingTodo) = remember { mutableStateOf<TodoEntity?>(null) }

    if (dialogOpen) {
        val todoToEdit = editingTodo ?: TodoEntity(0, "", "")
        Dialog(onDismissRequest = {
            setDialogOpen(false)
            setEditingTodo(null)
        }
        ) {

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { setTitle(it) },
                    label = {
                        Text(text = "title")
                    }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { setDescription(it) },
                    label = {
                        Text(text = "description")
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))

                Button(onClick = {
                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        if (editingTodo != null) {
                            viewModel.updateTodo(
                                editingTodo.copy(
                                    title = title,
                                    description = description
                                )
                            )
                        } else {
                            viewModel.addTodo(
                                TodoEntity(
                                    title = title,
                                    description = description,
                                    added = System.currentTimeMillis()
                                )
                            )
                        }
                        setTitle("")
                        setDescription("")
                        setDialogOpen(false)
                        setEditingTodo(null)
                        updatePendingTasksCount() // Update pending tasks count after editing/adding a todo
                    }
                }) {
                    Text(text = if (editingTodo != null) "Edit Task" else "Add Task")

                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondary,
        topBar = {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                Row() {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search task")
                    Spacer(modifier = Modifier.width(7.dp))
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null)
                }

            }
        },


        floatingActionButton = {
            FloatingActionButton(
                onClick = { setDialogOpen(true) },
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add task",
                    modifier = Modifier.clip(CircleShape),


                    )
            }
        },


        ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {

            Column(modifier = Modifier.padding(top = 5.dp, start = 15.dp)) {
                Text(
                    text = "Hello, big ceph",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.padding(2.dp)
                )

            }

            Text(
                text = "Ongoing tasks",
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(15.dp)
            )


            if (todos.isEmpty()) {
                // Show message when no todos available
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your task list is empty",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "You don't have any active tasks right now, try and add some.",
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(todos.sortedBy { it.done }, key = { it.id }) { todo ->
                        // Display each todo item
                        TodoItem(
                            todo = todo,
                            onclick = {
                                setTitle(todo.title)
                                setDescription(todo.description)
                                setEditingTodo(todo)
                                setDialogOpen(true)
                            },
                            onDelete = { viewModel.deleteTodo(todo) },
                            onCheckboxToggle = { isChecked ->
                                viewModel.updateTodo(
                                    todo.copy(done = isChecked)
                                )
                                updatePendingTasksCount()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodoItem(
    todo: TodoEntity,
    onclick: () -> Unit,
    onDelete: () -> Unit,
    onCheckboxToggle: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onclick() }
            .background(MaterialTheme.colorScheme.primary),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(7.dp)
        ) {
            Text(
                text = todo.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(3.dp)

            )
            Text(
                text = todo.description,
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                modifier = Modifier.padding(3.dp)

            )
        }
        Column(
            modifier = Modifier.padding(bottom = 5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val isChecked = remember { mutableStateOf(todo.done) }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked.value,
                    onCheckedChange = {
                        isChecked.value = it
                        onCheckboxToggle(it) // Notify HomeScreen about checkbox toggle
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Green,
                        uncheckedColor = Color.White
                    )
                )
                IconButton(onClick = { onDelete() }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete todo")
                }
            }
            Text(
                text = "${Time(todo.added)})",
            )
        }
    }
}


