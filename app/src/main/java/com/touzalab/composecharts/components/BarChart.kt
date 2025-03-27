package com.touzalab.composecharts.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.touzalab.composecharts.data.DataSeries
import com.touzalab.composecharts.style.ChartStyle
import kotlin.math.max
import kotlin.math.min

/**
 * Composable pour afficher un diagramme à barres
 *
 * @param dataSeries Liste des séries de données à afficher
 * @param modifier Modifier pour personnaliser l'apparence
 * @param style Style du graphique
 * @param title Titre du graphique (optionnel)
 * @param xAxisTitle Titre de l'axe X (optionnel)
 * @param yAxisTitle Titre de l'axe Y (optionnel)
 * @param horizontal Si true, affiche les barres horizontalement
 * @param stacked Si true, empile les séries de données
 * @param showLegend Si true, affiche la légende
 * @param yAxisRange Plage de l'axe Y (min, max) si null, calculé automatiquement
 */
@Composable
fun BarChart(
    dataSeries: List<DataSeries>,
    modifier: Modifier = Modifier,
    style: ChartStyle = ChartStyle(),
    title: String? = null,
    xAxisTitle: String? = null,
    yAxisTitle: String? = null,
    horizontal: Boolean = false,
    stacked: Boolean = false,
    showLegend: Boolean = true,
    yAxisRange: Pair<Float, Float>? = null
) {
    require(dataSeries.isNotEmpty()) { "Data series list cannot be empty" }

    val animatedProgress = remember { Animatable(0f) }
    var selectedBar by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    LaunchedEffect(dataSeries) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = style.animationDuration,
                easing = style.animationEasing
            )
        )
    }

    // Calculer les plages d'axes
    val xLabels = dataSeries.flatMap { it.points }.map { it.label }.distinct()
    val yMin = yAxisRange?.first ?: dataSeries.flatMap { it.points }.minOfOrNull { it.y } ?: 0f
    val yMax = yAxisRange?.second ?: dataSeries.flatMap { it.points }.maxOfOrNull { it.y } ?: 0f

    val chartPadding = 36.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titre du graphique
        if (title != null) {
            Text(
                text = title,
                style = style.titleStyle,
                modifier = Modifier.padding(bottom = style.titlePadding)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(start = chartPadding, end = chartPadding, bottom = chartPadding)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(dataSeries) {
                        detectTapGestures { offset ->
                            // Détecter le clic sur une barre
                            // Logique de détection à implémenter
                        }
                    }
            ) {
                val chartWidth = size.width
                val chartHeight = size.height

                // Dessiner la grille et les axes
                drawGrid(chartWidth, chartHeight, yMin, yMax, style)

                // Variables pour stocker les rectangles de barres pour la détection de clic
                val barRects = mutableListOf<Rect>()

                if (horizontal) {
                    // Implémenter le diagramme à barres horizontal
                } else {
                    // Diagramme à barres vertical
                    val barSpacing = chartWidth * 0.1f / xLabels.size
                    val groupWidth = (chartWidth - (xLabels.size + 1) * barSpacing) / xLabels.size

                    for (xIndex in xLabels.indices) {
                        val xPos = barSpacing + xIndex * (groupWidth + barSpacing)

                        if (stacked) {
                            // Barres empilées
                            var yOffset = 0f
                            for (seriesIndex in dataSeries.indices) {
                                val series = dataSeries[seriesIndex]
                                val point = series.points.find { it.label == xLabels[xIndex] }

                                point?.let {
                                    val barHeight = (it.y / (yMax - yMin)) * chartHeight * animatedProgress.value
                                    val barRect = Rect(
                                        left = xPos,
                                        top = chartHeight - yOffset - barHeight,
                                        right = xPos + groupWidth,
                                        bottom = chartHeight - yOffset
                                    )

                                    drawRect(
                                        color = series.color,
                                        topLeft = Offset(barRect.left, barRect.top),
                                        size = Size(barRect.width, barRect.height)
                                    )

                                    barRects.add(barRect)
                                    yOffset += barHeight
                                }
                            }
                        } else {
                            // Barres groupées
                            val barWidth = groupWidth / dataSeries.size

                            for (seriesIndex in dataSeries.indices) {
                                val series = dataSeries[seriesIndex]
                                val point = series.points.find { it.label == xLabels[xIndex] }

                                point?.let {
                                    val barHeight = (it.y / (yMax - yMin)) * chartHeight * animatedProgress.value
                                    val barRect = Rect(
                                        left = xPos + seriesIndex * barWidth,
                                        top = chartHeight - barHeight,
                                        right = xPos + (seriesIndex + 1) * barWidth,
                                        bottom = chartHeight
                                    )

                                    drawRect(
                                        color = series.color,
                                        topLeft = Offset(barRect.left, barRect.top),
                                        size = Size(barRect.width, barRect.height)
                                    )

                                    barRects.add(barRect)
                                }
                            }
                        }
                    }
                }

                // Dessiner les étiquettes d'axe X
                for (xIndex in xLabels.indices) {
                    val xPos = chartWidth * (xIndex + 0.5f) / xLabels.size

                    drawContext.canvas.nativeCanvas.drawText(
                        xLabels[xIndex],
                        xPos,
                        chartHeight + 20,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = style.axisLabelStyle.fontSize.toPx()
                        }
                    )
                }

                // Dessiner les étiquettes d'axe Y
                val yAxisSteps = 5
                for (i in 0..yAxisSteps) {
                    val yValue = yMin + (yMax - yMin) * i / yAxisSteps
                    val yPos = chartHeight - chartHeight * i / yAxisSteps

                    drawContext.canvas.nativeCanvas.drawText(
                        "%.1f".format(yValue),
                        -10f,
                        yPos,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textAlign = android.graphics.Paint.Align.RIGHT
                            textSize = style.axisLabelStyle.fontSize.toPx()
                        }
                    )
                }
            }

            // Afficher le titre de l'axe Y
            if (yAxisTitle != null) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 16.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Text(
                        text = yAxisTitle,
                        style = style.axisLabelStyle,
                        modifier = Modifier
                            .rotate(-90f)
                            .align(Alignment.Center)
                    )
                }
            }

            // Afficher le titre de l'axe X
            if (xAxisTitle != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = xAxisTitle,
                        style = style.axisLabelStyle,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // Légende
        if (showLegend) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                dataSeries.forEach { series ->
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(series.color)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = series.name,
                            style = style.legendTextStyle
                        )
                    }
                }
            }
        }
    }
}

fun DrawScope.drawGrid(
    width: Float,
    height: Float,
    yMin: Float,
    yMax: Float,
    style: ChartStyle
) {
    // Dessiner la grille horizontale
    val ySteps = 5
    for (i in 0..ySteps) {
        val y = height * i / ySteps

        drawLine(
            color = style.gridColor,
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = style.gridLineWidth.toPx()
        )
    }

    // Dessiner la grille verticale
    val xSteps = 5
    for (i in 0..xSteps) {
        val x = width * i / xSteps

        drawLine(
            color = style.gridColor,
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = style.gridLineWidth.toPx()
        )
    }

    // Dessiner les axes
    drawLine(
        color = style.axisColor,
        start = Offset(0f, height),
        end = Offset(width, height),
        strokeWidth = style.axisLineWidth.toPx()
    )

    drawLine(
        color = style.axisColor,
        start = Offset(0f, 0f),
        end = Offset(0f, height),
        strokeWidth = style.axisLineWidth.toPx()
    )
}
