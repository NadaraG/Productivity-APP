package com.example.ui.components
 
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.data.BodyMeasurement
import com.example.ui.viewmodel.MetricUnit
import com.example.ui.viewmodel.SelectedMetric
import com.example.ui.viewmodel.TrackerViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: TrackerViewModel,
    modifier: Modifier = Modifier
) {
    val measurements by viewModel.measurements.collectAsState()
    val unitSystem by viewModel.unitPreference.collectAsState()
    val selectedMetric by viewModel.selectedMetric.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Physique tracking") },
                    label = { Text("Physique") },
                    modifier = Modifier.testTag("tab_physique")
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Smoking tracking") },
                    label = { Text("Smoking") },
                    modifier = Modifier.testTag("tab_smoking")
                )
            }
        },
        floatingActionButton = {
            if (activeTab == 0) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .testTag("add_log_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Log Measurements",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            if (activeTab == 0) {
                if (measurements.isEmpty()) {
                    // Beautiful Empty Onboarding Screen
                    EmptyOnboarding(
                        onPopulateDemo = { viewModel.populateDemoData() },
                        onAddFirst = { showAddDialog = true },
                        modifier = Modifier.padding(top = statusBarPadding)
                    )
                } else {
                val latest = measurements.first()
                val oldest = measurements.last()
                val previous = if (measurements.size > 1) measurements[1] else null

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(statusBarPadding + 16.dp))
                        // Header Area
                        HeaderSection(
                            unitSystem = unitSystem,
                            onToggleUnit = { viewModel.toggleUnit() }
                        )
                    }

                    // Key Progress Overview (Differences tracking body composition)
                    item {
                        ProgressOverviewGrid(
                            latest = latest,
                            previous = previous,
                            oldest = oldest,
                            unitSystem = unitSystem
                        )
                    }

                    // Selected Progress Curve Chart & Analysis
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                // Unit Label Indicator
                                val unitLabel = if (selectedMetric == SelectedMetric.Weight) {
                                    if (unitSystem == MetricUnit.METRIC) "kg" else "lbs"
                                } else {
                                    if (unitSystem == MetricUnit.METRIC) "cm" else "in"
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${selectedMetric.displayName} Trend",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Visualizing historical entries",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = unitLabel,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Chart Component
                                val valueGetter: (BodyMeasurement) -> Float? = { selectedMetric.getValue(it) }
                                BodyChart(
                                    measurements = measurements,
                                    getValue = valueGetter,
                                    metricName = selectedMetric.displayName,
                                    unitSymbol = unitLabel,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Active Metric Metrics Statistics
                                MetricStatsRow(
                                    selectedMetric = selectedMetric,
                                    measurements = measurements,
                                    unitSystem = unitSystem
                                )
                            }
                        }
                    }

                    // Interactive Grid to select active metric
                    item {
                        Text(
                            text = "Key Metrics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    item {
                        MetricsSelectorGrid(
                            selectedMetric = selectedMetric,
                            latestMeasurement = latest,
                            unitSystem = unitSystem,
                            onMetricSelected = { viewModel.selectMetric(it) }
                        )
                    }

                    // Historical Logs List
                    item {
                        Text(
                            text = "Historical Logs",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    items(measurements, key = { it.id }) { item ->
                        HistoryCard(
                            item = item,
                            unitSystem = unitSystem,
                            onDelete = { viewModel.deleteMeasurement(item.id) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // Extra spacing for FAB
                    }
                }
            }
            } else {
                CigaretteTrackerScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(top = statusBarPadding)
                )
            }
        }

        // Modal Bottom Sheet for adding logs
        if (showAddDialog) {
            val latestEntry = if (measurements.isNotEmpty()) measurements.first() else null
            AddLogBottomSheet(
                latestEntry = latestEntry,
                unitSystem = unitSystem,
                sheetState = sheetState,
                onDismiss = { showAddDialog = false },
                onSave = { newLog ->
                    viewModel.addMeasurement(newLog)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showAddDialog = false
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun EmptyOnboarding(
    onPopulateDemo: () -> Unit,
    onAddFirst: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // High fidelity icon placeholder using a clean stylized design
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "📊",
                    fontSize = 44.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Track Physique Changes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Keep track of weight and key body measurements like chest, waist, hips, biceps, thighs, forearms, and calves. View differences over time with high-precision metrics and visual trends.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onAddFirst,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("onboarding_add_button")
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Your First Log", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = { onPopulateDemo() },
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.testTag("populate_demo_button")
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Explore with Demo Data", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun HeaderSection(
    unitSystem: MetricUnit,
    onToggleUnit: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Productivity Tracker",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Monitor physique composition & trends",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Toggle Unit Button (Metric vs Imperial)
        IconButton(
            onClick = onToggleUnit,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = Modifier
                .size(44.dp)
                .testTag("toggle_unit_button")
        ) {
            Text(
                text = if (unitSystem == MetricUnit.METRIC) "kg" else "lb",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun ProgressOverviewGrid(
    latest: BodyMeasurement,
    previous: BodyMeasurement?,
    oldest: BodyMeasurement,
    unitSystem: MetricUnit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Composition Changes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Weight highlight card
            val weightDiff = calculateDiff(latest.weight, oldest.weight)
            val weightUnit = if (unitSystem == MetricUnit.METRIC) "kg" else "lbs"
            val displayWeightDiff = formatValue(weightDiff, isLength = false, unitSystem)
            val displayWeightLatest = formatValue(latest.weight, isLength = false, unitSystem)

            HighlightCard(
                title = "Weight Change",
                latestValue = "$displayWeightLatest $weightUnit",
                diffValue = displayWeightDiff,
                isDecreaseBetter = true,
                modifier = Modifier.weight(1f)
            )

            // Waist highlight card
            val waistDiff = calculateDiff(latest.waist, oldest.waist)
            val lengthUnit = if (unitSystem == MetricUnit.METRIC) "cm" else "in"
            val displayWaistDiff = formatValue(waistDiff, isLength = true, unitSystem)
            val displayWaistLatest = formatValue(latest.waist, isLength = true, unitSystem)

            HighlightCard(
                title = "Waist Progress",
                latestValue = "$displayWaistLatest $lengthUnit",
                diffValue = displayWaistDiff,
                isDecreaseBetter = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun HighlightCard(
    title: String,
    latestValue: String,
    diffValue: String,
    isDecreaseBetter: Boolean,
    modifier: Modifier = Modifier
) {
    val isZero = diffValue == "0.0" || diffValue == "+0.0" || diffValue == "-0.0"
    val isDecrease = diffValue.startsWith("-")
    
    val badgeColor = when {
        isZero -> MaterialTheme.colorScheme.surfaceVariant
        isDecrease -> if (isDecreaseBetter) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        else -> if (isDecreaseBetter) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
    }

    val badgeTextColor = when {
        isZero -> MaterialTheme.colorScheme.onSurfaceVariant
        isDecrease -> if (isDecreaseBetter) Color(0xFF2E7D32) else Color(0xFFC62828)
        else -> if (isDecreaseBetter) Color(0xFFC62828) else Color(0xFF2E7D32)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            shape = RoundedCornerShape(16.dp)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = latestValue,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Difference Badge
            Surface(
                color = badgeColor,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isZero) {
                        Icon(
                            imageVector = if (isDecrease) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = null,
                            tint = badgeTextColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                    Text(
                        text = if (isZero) "No change" else "$diffValue total",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor
                    )
                }
            }
        }
    }
}

@Composable
fun MetricStatsRow(
    selectedMetric: SelectedMetric,
    measurements: List<BodyMeasurement>,
    unitSystem: MetricUnit
) {
    val validValues = measurements.mapNotNull { selectedMetric.getValue(it) }
    if (validValues.isEmpty()) return

    val isWeight = selectedMetric == SelectedMetric.Weight
    val unitSymbol = if (isWeight) {
        if (unitSystem == MetricUnit.METRIC) "kg" else "lbs"
    } else {
        if (unitSystem == MetricUnit.METRIC) "cm" else "in"
    }

    val latest = validValues.first()
    val minVal = validValues.minOrNull() ?: 0f
    val maxVal = validValues.maxOrNull() ?: 0f
    val totalDiff = latest - validValues.last()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text("Latest", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${formatValue(latest, !isWeight, unitSystem)} $unitSymbol", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text("Min", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${formatValue(minVal, !isWeight, unitSystem)} $unitSymbol", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text("Max", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${formatValue(maxVal, !isWeight, unitSystem)} $unitSymbol", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text("Net Change", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            val prefix = if (totalDiff > 0) "+" else ""
            Text("$prefix${formatValue(totalDiff, !isWeight, unitSystem)} $unitSymbol", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (totalDiff < 0) Color(0xFF2E7D32) else Color(0xFFC62828))
        }
    }
}

@Composable
fun MetricsSelectorGrid(
    selectedMetric: SelectedMetric,
    latestMeasurement: BodyMeasurement,
    unitSystem: MetricUnit,
    onMetricSelected: (SelectedMetric) -> Unit
) {
    val metricsList = listOf(
        SelectedMetric.Weight, SelectedMetric.Chest, SelectedMetric.Waist, SelectedMetric.Hips,
        SelectedMetric.LeftBicep, SelectedMetric.RightBicep, SelectedMetric.LeftForearm, SelectedMetric.RightForearm,
        SelectedMetric.LeftThigh, SelectedMetric.RightThigh, SelectedMetric.LeftCalf, SelectedMetric.RightCalf
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Render 2 elements per row manually to avoid layout measure conflicts inside vertical scroll
        for (i in metricsList.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val metric1 = metricsList[i]
                MetricSelectorCard(
                    metric = metric1,
                    latestValue = metric1.getValue(latestMeasurement),
                    isSelected = selectedMetric == metric1,
                    unitSystem = unitSystem,
                    onClick = { onMetricSelected(metric1) },
                    modifier = Modifier.weight(1f)
                )

                if (i + 1 < metricsList.size) {
                    val metric2 = metricsList[i + 1]
                    MetricSelectorCard(
                        metric = metric2,
                        latestValue = metric2.getValue(latestMeasurement),
                        isSelected = selectedMetric == metric2,
                        unitSystem = unitSystem,
                        onClick = { onMetricSelected(metric2) },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun MetricSelectorCard(
    metric: SelectedMetric,
    latestValue: Float?,
    isSelected: Boolean,
    unitSystem: MetricUnit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isWeight = metric == SelectedMetric.Weight
    val unitSymbol = if (isWeight) {
        if (unitSystem == MetricUnit.METRIC) "kg" else "lbs"
    } else {
        if (unitSystem == MetricUnit.METRIC) "cm" else "in"
    }

    val displayVal = if (latestValue != null) {
        "${formatValue(latestValue, !isWeight, unitSystem)} $unitSymbol"
    } else {
        "--"
    }

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
        label = "color"
    )

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("metric_selector_${metric.displayName.replace(" ", "_").lowercase()}")
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = metric.displayName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = displayVal,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun HistoryCard(
    item: BodyMeasurement,
    unitSystem: MetricUnit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val lengthUnit = if (unitSystem == MetricUnit.METRIC) "cm" else "in"
    val weightUnit = if (unitSystem == MetricUnit.METRIC) "kg" else "lbs"

    val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dateFormat.format(Date(item.timestamp)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Weight: ${formatValue(item.weight, false, unitSystem)} $weightUnit",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDelete,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete entry",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Body Measurements",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Measurements list inside details
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            MetricDetailItem("Chest", item.chest, lengthUnit, unitSystem, Modifier.weight(1f))
                            MetricDetailItem("Waist", item.waist, lengthUnit, unitSystem, Modifier.weight(1f))
                            MetricDetailItem("Hips", item.hips, lengthUnit, unitSystem, Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            MetricDetailItem("Left Bicep", item.leftBicep, lengthUnit, unitSystem, Modifier.weight(1f))
                            MetricDetailItem("Right Bicep", item.rightBicep, lengthUnit, unitSystem, Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            MetricDetailItem("Left Forearm", item.leftForearm, lengthUnit, unitSystem, Modifier.weight(1f))
                            MetricDetailItem("Right Forearm", item.rightForearm, lengthUnit, unitSystem, Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            MetricDetailItem("Left Thigh", item.leftThigh, lengthUnit, unitSystem, Modifier.weight(1f))
                            MetricDetailItem("Right Thigh", item.rightThigh, lengthUnit, unitSystem, Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            MetricDetailItem("Left Calf", item.leftCalf, lengthUnit, unitSystem, Modifier.weight(1f))
                            MetricDetailItem("Right Calf", item.rightCalf, lengthUnit, unitSystem, Modifier.weight(1f))
                        }
                    }

                    if (!item.notes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Notes: ${item.notes}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricDetailItem(
    label: String,
    value: Float?,
    unit: String,
    unitSystem: MetricUnit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 2.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = if (value != null) "${formatValue(value, true, unitSystem)} $unit" else "--",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

data class WizardStep(
    val title: String,
    val description: String,
    val isLength: Boolean,
    val testTag: String,
    val getValue: (BodyMeasurement) -> Float?,
    val buildWithNewValue: (BodyMeasurement, Float?) -> BodyMeasurement
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLogBottomSheet(
    latestEntry: BodyMeasurement?,
    unitSystem: MetricUnit,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (BodyMeasurement) -> Unit
) {
    val lengthUnit = if (unitSystem == MetricUnit.METRIC) "cm" else "in"
    val weightUnit = if (unitSystem == MetricUnit.METRIC) "kg" else "lbs"

    val wizardSteps = remember {
        listOf(
            WizardStep(
                title = "Weight",
                description = "Monitor your total body weight.",
                isLength = false,
                testTag = "input_weight",
                getValue = { it.weight },
                buildWithNewValue = { m, v -> m.copy(weight = v) }
            ),
            WizardStep(
                title = "Waist",
                description = "Measure at the narrowest part of your torso (usually above the belly button).",
                isLength = true,
                testTag = "input_waist",
                getValue = { it.waist },
                buildWithNewValue = { m, v -> m.copy(waist = v) }
            ),
            WizardStep(
                title = "Chest",
                description = "Measure around the fullest part of your chest, right under your armpits.",
                isLength = true,
                testTag = "input_chest",
                getValue = { it.chest },
                buildWithNewValue = { m, v -> m.copy(chest = v) }
            ),
            WizardStep(
                title = "Hips",
                description = "Measure around the widest part of your glutes.",
                isLength = true,
                testTag = "input_hips",
                getValue = { it.hips },
                buildWithNewValue = { m, v -> m.copy(hips = v) }
            ),
            WizardStep(
                title = "Left Bicep",
                description = "Measure around the peak of your flexed left arm.",
                isLength = true,
                testTag = "input_left_bicep",
                getValue = { it.leftBicep },
                buildWithNewValue = { m, v -> m.copy(leftBicep = v) }
            ),
            WizardStep(
                title = "Right Bicep",
                description = "Measure around the peak of your flexed right arm.",
                isLength = true,
                testTag = "input_right_bicep",
                getValue = { it.rightBicep },
                buildWithNewValue = { m, v -> m.copy(rightBicep = v) }
            ),
            WizardStep(
                title = "Left Forearm",
                description = "Measure the widest part of your left forearm.",
                isLength = true,
                testTag = "input_left_forearm",
                getValue = { it.leftForearm },
                buildWithNewValue = { m, v -> m.copy(leftForearm = v) }
            ),
            WizardStep(
                title = "Right Forearm",
                description = "Measure the widest part of your right forearm.",
                isLength = true,
                testTag = "input_right_forearm",
                getValue = { it.rightForearm },
                buildWithNewValue = { m, v -> m.copy(rightForearm = v) }
            ),
            WizardStep(
                title = "Left Thigh",
                description = "Measure around the midpoint of your left thigh.",
                isLength = true,
                testTag = "input_left_thigh",
                getValue = { it.leftThigh },
                buildWithNewValue = { m, v -> m.copy(leftThigh = v) }
            ),
            WizardStep(
                title = "Right Thigh",
                description = "Measure around the midpoint of your right thigh.",
                isLength = true,
                testTag = "input_right_thigh",
                getValue = { it.rightThigh },
                buildWithNewValue = { m, v -> m.copy(rightThigh = v) }
            ),
            WizardStep(
                title = "Left Calf",
                description = "Measure the widest part of your left calf.",
                isLength = true,
                testTag = "input_left_calf",
                getValue = { it.leftCalf },
                buildWithNewValue = { m, v -> m.copy(leftCalf = v) }
            ),
            WizardStep(
                title = "Right Calf",
                description = "Measure the widest part of your right calf.",
                isLength = true,
                testTag = "input_right_calf",
                getValue = { it.rightCalf },
                buildWithNewValue = { m, v -> m.copy(rightCalf = v) }
            )
        )
    }

    var currentStepIndex by remember { mutableStateOf(0) }
    var draftMeasurement by remember { mutableStateOf((latestEntry ?: BodyMeasurement()).copy(id = 0)) }
    var notesInput by remember { mutableStateOf("") }
    var currentTextInput by remember { mutableStateOf("") }

    val totalSteps = wizardSteps.size + 1 // 12 metrics + 1 summary/notes page

    // Update text input when step or draft updates
    LaunchedEffect(currentStepIndex) {
        if (currentStepIndex < wizardSteps.size) {
            val step = wizardSteps[currentStepIndex]
            val value = step.getValue(draftMeasurement)
            currentTextInput = value?.let { formatValue(it, step.isLength, unitSystem) } ?: ""
        }
    }

    val saveCurrentStepValue = {
        if (currentStepIndex < wizardSteps.size) {
            val step = wizardSteps[currentStepIndex]
            val parsed = parseInput(currentTextInput, step.isLength, unitSystem)
            draftMeasurement = step.buildWithNewValue(draftMeasurement, parsed)
        }
    }

    val focusManager = LocalFocusManager.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        modifier = Modifier.testTag("add_log_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "New Log Entry",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Step ${currentStepIndex + 1} of $totalSteps",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Indicator
            LinearProgressIndicator(
                progress = { (currentStepIndex + 1).toFloat() / totalSteps },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Step Navigation Jump pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(wizardSteps) { index, step ->
                    val stepValue = step.getValue(draftMeasurement)
                    val isEntered = stepValue != null
                    val isActive = currentStepIndex == index

                    Surface(
                        color = if (isActive) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else if (isEntered) {
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        },
                        shape = RoundedCornerShape(16.dp),
                        border = if (isActive) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .clickable {
                                saveCurrentStepValue()
                                currentStepIndex = index
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isEntered) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = step.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                color = if (isActive) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else if (isEntered) {
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }

                // Summary tab
                item {
                    val isActive = currentStepIndex == wizardSteps.size
                    Surface(
                        color = if (isActive) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        },
                        shape = RoundedCornerShape(16.dp),
                        border = if (isActive) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .clickable {
                                saveCurrentStepValue()
                                currentStepIndex = wizardSteps.size
                            }
                    ) {
                        Text(
                            text = "Summary & Save",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (currentStepIndex < wizardSteps.size) {
                // PARAMETER PAGE
                val currentStep = wizardSteps[currentStepIndex]
                val currentUnit = if (currentStep.isLength) lengthUnit else weightUnit

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = currentStep.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentStep.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Large input field
                        OutlinedTextField(
                            value = currentTextInput,
                            onValueChange = { currentTextInput = it },
                            label = { Text("Value in $currentUnit") },
                            placeholder = { Text("e.g. 0.0") },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag(currentStep.testTag),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Compare with previous value if exists
                        val previousValue = latestEntry?.let { currentStep.getValue(it) }
                        if (previousValue != null) {
                            val displayPrev = formatValue(previousValue, currentStep.isLength, unitSystem)
                            
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Previous log entry:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "$displayPrev $currentUnit",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "No previous logged value found for this parameter.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Bottom Action buttons for wizard
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            saveCurrentStepValue()
                            if (currentStepIndex > 0) {
                                currentStepIndex--
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .weight(0.9f)
                            .height(52.dp)
                    ) {
                        Text(
                            text = if (currentStepIndex == 0) "Cancel" else "Back",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            saveCurrentStepValue()
                            focusManager.clearFocus()
                            onSave(
                                draftMeasurement.copy(
                                    id = 0,
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(52.dp)
                            .testTag("save_and_exit_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Save & Exit",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            saveCurrentStepValue()
                            if (currentStepIndex < totalSteps - 1) {
                                currentStepIndex++
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(52.dp)
                    ) {
                        Text(
                            text = "Next Step",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // REVIEW & SUMMARY PAGE
                Text(
                    text = "Review & Complete",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "Review your entered parameters below before final saving.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Log summaries
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        wizardSteps.forEach { step ->
                            val value = step.getValue(draftMeasurement)
                            val unit = if (step.isLength) lengthUnit else weightUnit
                            val hasValue = value != null

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = step.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (hasValue) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )

                                Text(
                                    text = value?.let { "${formatValue(it, step.isLength, unitSystem)} $unit" } ?: "Skipped / Empty",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasValue) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Notes Field
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Log Notes & Context") },
                    placeholder = { Text("How do you feel today? Any dietary changes, fatigue level...") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Actions for summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = {
                            if (currentStepIndex > 0) {
                                currentStepIndex--
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Text("Back", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            onSave(
                                draftMeasurement.copy(
                                    id = 0,
                                    timestamp = System.currentTimeMillis(),
                                    notes = notesInput.trim().ifEmpty { null }
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(52.dp)
                            .testTag("save_log_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Log", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// Helpers for precise unit conversions and formatting
fun calculateDiff(latest: Float?, baseline: Float?): Float {
    if (latest == null || baseline == null) return 0f
    return latest - baseline
}

fun formatValue(value: Float?, isLength: Boolean, unitSystem: MetricUnit): String {
    if (value == null) return "0.0"
    val converted = if (unitSystem == MetricUnit.METRIC) {
        value
    } else {
        if (isLength) value * 0.393701f else value * 2.20462f
    }
    val formatted = String.format(Locale.US, "%.1f", converted)
    return if (formatted == "-0.0") "0.0" else formatted
}

fun parseInput(input: String, isLength: Boolean, unitSystem: MetricUnit): Float? {
    val floatVal = input.trim().toFloatOrNull() ?: return null
    return if (unitSystem == MetricUnit.METRIC) {
        floatVal
    } else {
        if (isLength) floatVal / 0.393701f else floatVal / 2.20462f
    }
}
