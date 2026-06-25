package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CigaretteLog
import com.example.ui.viewmodel.TrackerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CigaretteTrackerScreen(
    viewModel: TrackerViewModel,
    modifier: Modifier = Modifier
) {
    val cigaretteLogs by viewModel.cigaretteLogs.collectAsState()
    val pricePerPack by viewModel.pricePerPack.collectAsState()
    val cigarettesPerPack by viewModel.cigarettesPerPack.collectAsState()
    val dailyTarget by viewModel.dailyTarget.collectAsState()
    val monthlyTarget by viewModel.monthlyTarget.collectAsState()

    var showCustomLogDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Calculations
    val now = System.currentTimeMillis()
    val oneDayMs = 24 * 60 * 60 * 1000L

    val calendar = java.util.Calendar.getInstance()
    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
    calendar.set(java.util.Calendar.MINUTE, 0)
    calendar.set(java.util.Calendar.SECOND, 0)
    calendar.set(java.util.Calendar.MILLISECOND, 0)
    val startOfToday = calendar.timeInMillis

    // Filter logs
    val todayLogs = cigaretteLogs.filter { it.timestamp >= startOfToday }
    val todayCount = todayLogs.sumOf { it.quantity }

    val startOfWeek = now - 7 * oneDayMs
    val weeklyLogs = cigaretteLogs.filter { it.timestamp >= startOfWeek }
    val weeklyCount = weeklyLogs.sumOf { it.quantity }

    val startOfMonth = now - 30 * oneDayMs
    val monthlyLogs = cigaretteLogs.filter { it.timestamp >= startOfMonth }
    val monthlyCount = monthlyLogs.sumOf { it.quantity }

    // Money Calculations
    val costPerCigarette = if (cigarettesPerPack > 0) pricePerPack / cigarettesPerPack else 0f
    val todaySpent = todayCount * costPerCigarette
    val weeklySpent = weeklyCount * costPerCigarette
    val monthlySpent = monthlyCount * costPerCigarette

    // Target checks
    val isDailyTargetMet = todayCount <= dailyTarget
    val isMonthlyTargetMet = monthlyCount <= monthlyTarget

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Smoking Tracker",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Track smoking habits and money saved",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { showSettingsDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Cigarette Settings"
                    )
                }
            }
        }

        // Hero Counter card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TODAY'S COUNT",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$todayCount",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Cigarettes Smoked",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Today's Cost: ₾${String.format(Locale.US, "%.2f", todaySpent)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.logCigarette(1) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                contentColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .height(52.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Log +1 Cigarette", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showCustomLogDialog = true },
                            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                        ) {
                            Text("Custom", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Targets comparison section
        item {
            Text(
                text = "Targets Tracking",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Daily target card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDailyTargetMet) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = 1.dp,
                            color = if (isDailyTargetMet) Color(0xFFC8E6C9) else Color(0xFFFFCDD2),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Daily Target",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDailyTargetMet) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "$todayCount / $dailyTarget",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDailyTargetMet) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                        )
                        Text(
                            text = "cigarettes limit",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDailyTargetMet) Color(0xFF4CAF50) else Color(0xFFE53935)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = if (isDailyTargetMet) Color(0xFFC8E6C9) else Color(0xFFFFCDD2),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isDailyTargetMet) "Target Met" else "Target Exceeded",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isDailyTargetMet) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Monthly target card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isMonthlyTargetMet) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = 1.dp,
                            color = if (isMonthlyTargetMet) Color(0xFFC8E6C9) else Color(0xFFFFCDD2),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Monthly Target",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isMonthlyTargetMet) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "$monthlyCount / $monthlyTarget",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isMonthlyTargetMet) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                        )
                        Text(
                            text = "cigarettes limit",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isMonthlyTargetMet) Color(0xFF4CAF50) else Color(0xFFE53935)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = if (isMonthlyTargetMet) Color(0xFFC8E6C9) else Color(0xFFFFCDD2),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isMonthlyTargetMet) "Target Met" else "Target Exceeded",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isMonthlyTargetMet) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Habit stats
        item {
            Text(
                text = "Habit & Financial Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatRow(
                        label = "Today",
                        count = "$todayCount cigarettes",
                        money = "₾${String.format(Locale.US, "%.2f", todaySpent)}"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    StatRow(
                        label = "Weekly (7 days)",
                        count = "$weeklyCount cigarettes",
                        money = "₾${String.format(Locale.US, "%.2f", weeklySpent)}"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    StatRow(
                        label = "Monthly (30 days)",
                        count = "$monthlyCount cigarettes",
                        money = "₾${String.format(Locale.US, "%.2f", monthlySpent)}"
                    )
                }
            }
        }

        // Demo data action if empty
        if (cigaretteLogs.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No logs found yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Populate past logs to see beautiful visual charts and spending metrics.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(
                            onClick = { viewModel.populateCigaretteDemoData() },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Explore with Demo Data", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Log History Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Logs History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (cigaretteLogs.isNotEmpty()) {
                    TextButton(
                        onClick = { viewModel.clearAllCigaretteLogs() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (cigaretteLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your logged cigarettes will show up here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(cigaretteLogs.take(15)) { log ->
                CigaretteLogItem(
                    log = log,
                    costPerCigarette = costPerCigarette,
                    onDelete = { viewModel.deleteCigaretteLog(log.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // CUSTOM LOG DIALOG
    if (showCustomLogDialog) {
        var quantityInput by remember { mutableStateOf("2") }

        AlertDialog(
            onDismissRequest = { showCustomLogDialog = false },
            title = {
                Text("Log Custom Quantity", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Enter the number of cigarettes you smoked to add to today's log.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = quantityInput,
                        onValueChange = { quantityInput = it },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = quantityInput.toIntOrNull() ?: 1
                        if (parsed > 0) {
                            viewModel.logCigarette(parsed)
                        }
                        showCustomLogDialog = false
                    }
                ) {
                    Text("Add Log")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomLogDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // SETTINGS DIALOG
    if (showSettingsDialog) {
        var priceInput by remember { mutableStateOf(pricePerPack.toString()) }
        var countInPackInput by remember { mutableStateOf(cigarettesPerPack.toString()) }
        var dailyTargetInput by remember { mutableStateOf(dailyTarget.toString()) }
        var monthlyTargetInput by remember { mutableStateOf(monthlyTarget.toString()) }

        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Text("Smoking Parameters & Targets", fontWeight = FontWeight.Bold)
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        OutlinedTextField(
                            value = priceInput,
                            onValueChange = { priceInput = it },
                            label = { Text("Price of a Pack (₾)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = countInPackInput,
                            onValueChange = { countInPackInput = it },
                            label = { Text("Cigarettes in a Pack") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = dailyTargetInput,
                            onValueChange = { dailyTargetInput = it },
                            label = { Text("Daily Target Limit") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = monthlyTargetInput,
                            onValueChange = { monthlyTargetInput = it },
                            label = { Text("Monthly Target Limit") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = priceInput.toFloatOrNull() ?: 12.50f
                        val c = countInPackInput.toIntOrNull() ?: 20
                        val d = dailyTargetInput.toIntOrNull() ?: 10
                        val m = monthlyTargetInput.toIntOrNull() ?: 200

                        viewModel.updatePricePerPack(p)
                        viewModel.updateCigarettesPerPack(c)
                        viewModel.updateDailyTarget(d)
                        viewModel.updateMonthlyTarget(m)

                        showSettingsDialog = false
                    }
                ) {
                    Text("Save Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StatRow(
    label: String,
    count: String,
    money: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = count,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Spent: $money",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun CigaretteLogItem(
    log: CigaretteLog,
    costPerCigarette: Float,
    onDelete: () -> Unit
) {
    val formatter = remember { SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()) }
    val displayTime = formatter.format(Date(log.timestamp))
    val estimatedCost = log.quantity * costPerCigarette

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${log.quantity} Cigarette" + if (log.quantity > 1) "s" else "",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = displayTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Cost: ₾${String.format(Locale.US, "%.2f", estimatedCost)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete entry",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
