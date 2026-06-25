package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BodyMeasurement
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BodyChart(
    measurements: List<BodyMeasurement>,
    getValue: (BodyMeasurement) -> Float?,
    metricName: String,
    unitSymbol: String,
    modifier: Modifier = Modifier
) {
    // Filter out measurements where the selected metric is null, and sort chronologically (oldest first for charts)
    val validData = measurements
        .filter { getValue(it) != null }
        .sortedBy { it.timestamp }

    if (validData.size < 2) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Log at least 2 entries to see progress trends",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        return
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    val values = validData.map { getValue(it)!! }
    val minVal = values.minOrNull() ?: 0f
    val maxVal = values.maxOrNull() ?: 100f
    val valRange = (maxVal - minVal).coerceAtLeast(0.1f)
    
    // Add 10% padding buffer to the chart extremes
    val yMin = minVal - (valRange * 0.15f)
    val yMax = maxVal + (valRange * 0.15f)
    val yRange = yMax - yMin

    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                    )
                ),
                shape = MaterialTheme.shapes.medium
            )
            .padding(top = 16.dp, bottom = 8.dp, start = 8.dp, end = 16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val paddingLeft = 60f
            val paddingBottom = 60f
            val paddingTop = 30f
            val paddingRight = 30f

            val chartWidth = width - paddingLeft - paddingRight
            val chartHeight = height - paddingTop - paddingBottom

            // Draw Y-axis gridlines & labels
            val gridLines = 4
            for (i in 0..gridLines) {
                val gridVal = yMin + (yRange * i / gridLines)
                val y = height - paddingBottom - (chartHeight * i / gridLines)
                
                // Grid line
                drawLine(
                    color = labelColor.copy(alpha = 0.1f),
                    start = Offset(paddingLeft, y),
                    end = Offset(width - paddingRight, y),
                    strokeWidth = 1.dp.toPx()
                )

                // Label
                drawContext.canvas.nativeCanvas.drawText(
                    String.format(Locale.US, "%.1f", gridVal),
                    10f,
                    y + 10f,
                    android.graphics.Paint().apply {
                        color = labelColor.hashCode()
                        textSize = 28f
                        isAntiAlias = true
                    }
                )
            }

            // Map data points to coordinates
            val points = validData.mapIndexed { index, item ->
                val valY = getValue(item)!!
                val x = paddingLeft + (index * chartWidth / (validData.size - 1))
                val y = height - paddingBottom - ((valY - yMin) / yRange * chartHeight)
                Offset(x, y)
            }

            // Draw area gradient under the curve
            val fillPath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, height - paddingBottom)
                    for (pt in points) {
                        lineTo(pt.x, pt.y)
                    }
                    lineTo(points.last().x, height - paddingBottom)
                    close()
                }
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.35f),
                        primaryColor.copy(alpha = 0.0f)
                    ),
                    startY = paddingTop,
                    endY = height - paddingBottom
                )
            )

            // Draw the line curve
            val strokePath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        // Draw smooth cubic curves
                        val controlX1 = prev.x + (curr.x - prev.x) / 2
                        val controlY1 = prev.y
                        val controlX2 = prev.x + (curr.x - prev.x) / 2
                        val controlY2 = curr.y
                        
                        cubicTo(controlX1, controlY1, controlX2, controlY2, curr.x, curr.y)
                    }
                }
            }
            drawPath(
                path = strokePath,
                color = primaryColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw dots & dates
            points.forEachIndexed { index, pt ->
                // Outer glow
                drawCircle(
                    color = primaryColor.copy(alpha = 0.3f),
                    radius = 8.dp.toPx(),
                    center = pt
                )
                // Inner dot
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = primaryColor,
                    radius = 4.dp.toPx(),
                    center = pt,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Date label at bottom
                // Only show labels to prevent overcrowding if size matches standard limits
                if (validData.size <= 6 || index == 0 || index == validData.size - 1 || index == validData.size / 2) {
                    val dateStr = dateFormat.format(Date(validData[index].timestamp))
                    drawContext.canvas.nativeCanvas.drawText(
                        dateStr,
                        pt.x - 35f,
                        height - 15f,
                        android.graphics.Paint().apply {
                            color = labelColor.hashCode()
                            textSize = 24f
                            isAntiAlias = true
                        }
                    )
                }
            }
        }
    }
}
