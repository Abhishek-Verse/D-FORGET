package com.aitaskorganizer.app.ui.tasks

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aitaskorganizer.app.data.model.TaskUiModel
import com.aitaskorganizer.app.ui.components.BrutalBox
import com.aitaskorganizer.app.ui.components.TaskCard
import com.aitaskorganizer.app.ui.tasks.viewmodel.TasksViewModel
import com.aitaskorganizer.app.ui.theme.AITaskOrganizerTheme
import com.aitaskorganizer.app.ui.theme.AppDimens
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TasksScreen(
    viewModel: TasksViewModel? = null,
    onNavigateToTaskDetail: (Long) -> Unit = {},
    onNavigateToAddTask: () -> Unit = {}
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val tabs = TaskTab.entries

    // Collect from ViewModel (or empty for preview)
    val allPending by (viewModel?.allPending ?: kotlinx.coroutines.flow.MutableStateFlow(emptyList<TaskUiModel>())).collectAsState()
    val todayTasksState by (viewModel?.todayTasks ?: kotlinx.coroutines.flow.MutableStateFlow(emptyList<TaskUiModel>())).collectAsState()
    val upcomingTasksState by (viewModel?.upcomingTasks ?: kotlinx.coroutines.flow.MutableStateFlow(emptyList<TaskUiModel>())).collectAsState()
    val overdueTasksState by (viewModel?.overdueTasks ?: kotlinx.coroutines.flow.MutableStateFlow(emptyList<TaskUiModel>())).collectAsState()

    val tabFiltered = when (tabs[selectedTabIndex]) {
        TaskTab.ALL -> allPending
        TaskTab.TODAY -> todayTasksState
        TaskTab.UPCOMING -> upcomingTasksState
        TaskTab.OVERDUE -> overdueTasksState
    }

    val filteredTasks = if (searchQuery.isBlank()) {
        tabFiltered
    } else {
        tabFiltered.filter { task ->
            task.title.contains(searchQuery, ignoreCase = true) ||
                task.description.contains(searchQuery, ignoreCase = true) ||
                task.subject.contains(searchQuery, ignoreCase = true) ||
                task.category.contains(searchQuery, ignoreCase = true)
        }
    }

    val groupedTasks = remember(filteredTasks) {
        groupTasksByDate(filteredTasks)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            BrutalBox(
                backgroundColor = MaterialTheme.colorScheme.primary,
                cornerRadius = AppDimens.CornerRadiusMedium,
                onClick = onNavigateToAddTask
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add new task",
                        tint = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "NEW TASK",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1A1A)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.padding(
                    start = AppDimens.ScreenHorizontalPadding,
                    end = AppDimens.ScreenHorizontalPadding,
                    top = AppDimens.ScreenTopPadding
                )
            ) {
                Text(
                    text = "TASKS",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(modifier = Modifier.height(12.dp))

                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TaskTabRow(
                tabs = tabs,
                selectedIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it },
                taskCounts = mapOf(
                    TaskTab.ALL to allPending.size,
                    TaskTab.TODAY to todayTasksState.size,
                    TaskTab.UPCOMING to upcomingTasksState.size,
                    TaskTab.OVERDUE to overdueTasksState.size
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_content",
                modifier = Modifier.weight(1f)
            ) { _ ->
                if (filteredTasks.isEmpty()) {
                    EmptyTasksState(tab = tabs[selectedTabIndex], hasSearch = searchQuery.isNotBlank())
                } else {
                    TaskList(
                        groupedTasks = groupedTasks,
                        onTaskClick = onNavigateToTaskDetail,
                        onToggleComplete = { }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "SEARCH TASKS...",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A).copy(alpha = 0.6f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = Color(0xFF1A1A1A),
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = "Filters",
                    tint = Color(0xFF1A1A1A),
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(AppDimens.CornerRadiusSmall),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color(0xFF1A1A1A),
            focusedBorderColor = Color(0xFF1A1A1A)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color(0xFF1A1A1A), shape = RoundedCornerShape(AppDimens.CornerRadiusSmall))
    )
}

@Composable
private fun TaskTabRow(
    tabs: List<TaskTab>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    taskCounts: Map<TaskTab, Int>
) {
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = Color(0xFF1A1A1A),
        edgePadding = AppDimens.ScreenHorizontalPadding,
        divider = {},
        indicator = {}
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = selectedIndex == index
            val count = taskCounts[tab] ?: 0

            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                            shape = RoundedCornerShape(AppDimens.CornerRadiusSmall)
                        )
                        .border(
                            width = AppDimens.BorderThickness,
                            color = Color(0xFF1A1A1A),
                            shape = RoundedCornerShape(AppDimens.CornerRadiusSmall)
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = tab.label.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1A1A)
                        )
                        if (count > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF1A1A1A), shape = RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = count.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskList(
    groupedTasks: Map<String, List<TaskUiModel>>,
    onTaskClick: (Long) -> Unit,
    onToggleComplete: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            start = AppDimens.ScreenHorizontalPadding,
            end = AppDimens.ScreenHorizontalPadding,
            top = 8.dp,
            bottom = 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        groupedTasks.forEach { (dateLabel, tasks) ->
            item(key = "header_$dateLabel") {
                Text(
                    text = dateLabel.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            items(
                items = tasks,
                key = { it.id }
            ) { task ->
                TaskCard(
                    task = task,
                    onTaskClick = onTaskClick,
                    onToggleComplete = onToggleComplete
                )
            }
        }
    }
}

@Composable
private fun EmptyTasksState(tab: TaskTab, hasSearch: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val (emoji, title, subtitle) = when {
                hasSearch -> Triple(
                    "🔍",
                    "NO RESULTS FOUND",
                    "TRY A DIFFERENT SEARCH TERM."
                )
                tab == TaskTab.TODAY -> Triple(
                    "🎉",
                    "ALL CLEAR FOR TODAY!",
                    "NO TASKS DUE TODAY."
                )
                tab == TaskTab.OVERDUE -> Triple(
                    "✅",
                    "NOTHING OVERDUE",
                    "YOU'RE ALL CAUGHT UP!"
                )
                tab == TaskTab.UPCOMING -> Triple(
                    "📅",
                    "NO UPCOMING TASKS",
                    "ADD A TASK TO GET STARTED."
                )
                else -> Triple(
                    "📝",
                    "NO TASKS YET",
                    "TAP + TO CREATE YOUR FIRST TASK."
                )
            }

            Text(text = emoji, style = MaterialTheme.typography.displaySmall)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A).copy(alpha = 0.7f)
            )
        }
    }
}

private fun groupTasksByDate(tasks: List<TaskUiModel>): Map<String, List<TaskUiModel>> {
    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d")

    return tasks
        .sortedBy { it.deadline ?: LocalDate.MAX.atStartOfDay() }
        .groupBy { task ->
            val date = task.deadline?.toLocalDate()
            when {
                date == null -> "No deadline"
                date.isBefore(today) -> "Overdue"
                date == today -> "Today"
                date == tomorrow -> "Tomorrow"
                date.isBefore(today.plusWeeks(1)) -> "This week"
                else -> date.format(dateFormatter)
            }
        }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TasksScreenPreview() {
    AITaskOrganizerTheme {
        TasksScreen()
    }
}
