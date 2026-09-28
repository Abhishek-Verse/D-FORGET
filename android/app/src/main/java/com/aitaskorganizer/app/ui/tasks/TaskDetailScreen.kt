package com.aitaskorganizer.app.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aitaskorganizer.app.data.local.entities.TaskEntity
import com.aitaskorganizer.app.data.model.TaskStatus
import com.aitaskorganizer.app.ui.components.BrutalBox
import com.aitaskorganizer.app.ui.components.PriorityBadge
import com.aitaskorganizer.app.ui.tasks.viewmodel.TaskDetailUiState
import com.aitaskorganizer.app.ui.tasks.viewmodel.TaskDetailViewModel
import com.aitaskorganizer.app.ui.theme.*
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    viewModel: TaskDetailViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDeleted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TASK DETAIL", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState is TaskDetailUiState.Success) {
                        val task = (uiState as TaskDetailUiState.Success).task
                        IconButton(onClick = { onEdit(task.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { viewModel.deleteTask(onDeleted) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BauhausRed)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BauhausWhite,
                    titleContentColor = BauhausBlack
                )
            )
        },
        containerColor = BauhausWhite
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is TaskDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = BauhausBlack)
                }
                is TaskDetailUiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                        color = BauhausRed
                    )
                }
                is TaskDetailUiState.Success -> {
                    TaskDetailContent(
                        task = state.task,
                        onToggleStatus = { viewModel.toggleTaskCompletion() }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskDetailContent(
    task: TaskEntity,
    onToggleStatus: () -> Unit
) {
    val scrollState = rememberScrollState()
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(AppDimens.PaddingMedium),
        verticalArrangement = Arrangement.spacedBy(AppDimens.PaddingMedium)
    ) {
        // AI Reliability Strip
        if (task.isAiGenerated) {
            BrutalBox(
                backgroundColor = Color(0xFFE6F0FF),
                cornerRadius = AppDimens.CornerRadiusSmall,
                modifier = Modifier.fillMaxWidth(),
                borderColor = BauhausBlack
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = BauhausBlack,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI PARSED & SYNCED",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "98% CONFIDENCE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = BauhausWhite,
                        modifier = Modifier
                            .background(BauhausBlack)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Task Header Card
        BrutalBox(
            backgroundColor = BauhausWhite,
            cornerRadius = AppDimens.CornerRadiusSmall,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(AppDimens.PaddingMedium)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PriorityBadge(priority = task.priority)
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (task.category.isNotBlank()) {
                            Text(
                                text = task.category.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier
                                    .border(AppDimens.BorderThickness, BauhausBlack)
                                    .background(BauhausWhite)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BauhausBlack.copy(alpha = 0.1f), thickness = 2.dp)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = task.title.uppercase(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 28.sp
                )
                if (task.subject.isNotEmpty()) {
                    Text(
                        text = task.subject,
                        color = BauhausBlack.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Description Card
        BrutalBox(
            backgroundColor = BauhausWhite,
            cornerRadius = AppDimens.CornerRadiusSmall,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(AppDimens.PaddingMedium)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "DESCRIPTION", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Icon(
                        Icons.Default.ContentCopy, 
                        contentDescription = "Copy",
                        modifier = Modifier.size(16.dp),
                        tint = BauhausBlack
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = task.description.ifEmpty { "No description provided." },
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        // Metadata Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Deadline
            BrutalBox(
                backgroundColor = BauhausWhite,
                cornerRadius = AppDimens.CornerRadiusSmall,
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("DEADLINE", fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = task.deadline?.format(DateTimeFormatter.ofPattern("dd MMM")) ?: "NO DATE",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = task.deadline?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BauhausBlack.copy(alpha = 0.6f)
                    )
                }
            }

            // Status
            BrutalBox(
                backgroundColor = BauhausWhite,
                cornerRadius = AppDimens.CornerRadiusSmall,
                modifier = Modifier.weight(1f),
                onClick = onToggleStatus
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PendingActions, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("STATUS", fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(if (task.status == TaskStatus.COMPLETED) SuccessGreen else BauhausRed)
                                .border(1.dp, BauhausBlack)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (task.status == TaskStatus.COMPLETED) "DONE" else "PENDING",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Reminders Section
        BrutalBox(
            backgroundColor = BauhausWhite,
            cornerRadius = AppDimens.CornerRadiusSmall,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(AppDimens.PaddingMedium)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, null, modifier = Modifier.size(18.dp), tint = BauhausBlack)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("REMINDERS", fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                // Mock reminder row
                BrutalBox(
                    backgroundColor = Color(0xFFF2EDE5),
                    cornerRadius = AppDimens.CornerRadiusSmall,
                    modifier = Modifier.fillMaxWidth(),
                    shadowOffset = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(BauhausYellow, RoundedCornerShape(4.dp))
                                    .border(1.dp, BauhausBlack, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Alarm, null, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("19 September", fontSize = 14.sp, fontWeight = FontWeight.Black)
                                Text("8:00 PM • Evening before", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Switch(
                            checked = true, 
                            onCheckedChange = {},
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BauhausYellow,
                                checkedTrackColor = BauhausBlack,
                                uncheckedThumbColor = BauhausWhite,
                                uncheckedTrackColor = BauhausBlack.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp)) // Extra space for bottom actions
    }
}

@Composable
fun MetaRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(text = value, fontSize = 14.sp)
    }
}
