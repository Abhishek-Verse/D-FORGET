package com.aitaskorganizer.app.ui.completed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aitaskorganizer.app.data.model.TaskUiModel
import com.aitaskorganizer.app.ui.components.BrutalBox
import com.aitaskorganizer.app.ui.completed.viewmodel.CompletedViewModel
import com.aitaskorganizer.app.ui.theme.*
import java.time.format.DateTimeFormatter

/**
 * Completed Tasks screen showing all finished tasks with undo option.
 */
@Composable
fun CompletedScreen(
    viewModel: CompletedViewModel? = null,
    onNavigateToTaskDetail: (Long) -> Unit = {}
) {
    val completedTasks by (viewModel?.completedTasks
        ?: kotlinx.coroutines.flow.MutableStateFlow(emptyList<TaskUiModel>())).collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Text(
                text = "COMPLETED",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = BauhausBlack,
                modifier = Modifier.padding(
                    start = AppDimens.ScreenHorizontalPadding,
                    end = AppDimens.ScreenHorizontalPadding,
                    top = AppDimens.ScreenTopPadding,
                    bottom = 8.dp
                )
            )

            if (completedTasks.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "NO COMPLETED TASKS YET",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = BauhausBlack
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "TASKS YOU FINISH WILL APPEAR HERE.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = BauhausBlack.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                // Count
                Text(
                    text = "${completedTasks.size} TASKS DONE",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = BauhausBlack.copy(alpha = 0.5f),
                    modifier = Modifier.padding(
                        horizontal = AppDimens.ScreenHorizontalPadding,
                        vertical = 4.dp
                    )
                )

                LazyColumn(
                    contentPadding = PaddingValues(
                        start = AppDimens.ScreenHorizontalPadding,
                        end = AppDimens.ScreenHorizontalPadding,
                        top = 8.dp,
                        bottom = 80.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = completedTasks,
                        key = { it.id }
                    ) { task ->
                        CompletedTaskCard(
                            task = task,
                            onClick = { onNavigateToTaskDetail(task.id) },
                            onUndo = { viewModel?.undoComplete(task.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompletedTaskCard(
    task: TaskUiModel,
    onClick: () -> Unit,
    onUndo: () -> Unit
) {
    BrutalBox(
        backgroundColor = Color.White,
        cornerRadius = AppDimens.CornerRadiusSmall,
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.LineThrough,
                    color = BauhausBlack.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (task.subject.isNotBlank()) {
                    Text(
                        text = task.subject,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = BauhausBlack.copy(alpha = 0.3f)
                    )
                }
            }
            IconButton(onClick = onUndo) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Undo,
                    contentDescription = "Mark as pending",
                    tint = BauhausBlack
                )
            }
        }
    }
}
