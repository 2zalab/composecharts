package com.touzalab.composecharts.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.touzalab.composecharts.style.ChartStyle
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Composable pour afficher un histogramme
 *
 * @param data Liste des valeurs à distribuer dans l'histogramme
 * @param modifier Modifier pour personnaliser l'apparence
 * @param style Style du graphique
 * @param title Titre du graphique (optionnel)
 * @param xAxisTitle Titre de l'axe X (optionnel)
 * @param yAxisTitle Titre de l'axe Y (optionnel)
 * @param barColor Couleur des barres
 * @param bins Nombre de groupes (bins) pour l'histogramme
 * @param customRange Plage personnalisée (min, max) si null, calculé automatiquement
 */
@Composable
fun Histogram(
    data: List<Float>,
    modifier: Modifier = Modifier,
    style: ChartStyle = ChartStyle(),
    title: String? = null,
    xAxisTitle: String? = null,
    yAxisTitle: String? = null,
    barColor: Color = Color.Blue,
    bins: Int = 10,
    customRange: Pair<Float, Float>? = null
) {
    require(data.isNotEmpty()) { "Data list cannot be empty" }
    require(bins > 1) { "Number of bins must be greater than 1" }

    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = style.animationDuration,
                easing = style.animationEasing
            )
        )
    }

    // Calculer la distribution
    val minValue = customRange?.first ?: data.minOrNull() ?: 0f
    val maxValue = customRange?.second ?: data.maxOrNull() ?: 0f

    // Créer les bins
    val binWidth = (maxValue - minValue) / bins
    val histogram = IntArray(bins)

    data.forEach { value ->
        val binIndex = ((value - minValue) / binWidth).toInt().coerceIn(0, bins - 1)
        histogram[binIndex]++
    }

    val maxFrequency = histogram.maxOrNull() ?: 0

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
                modifier = Modifier.fillMaxSize()
            ) {
                val chartWidth = size.width
                val chartHeight = size.height

                // Dessiner la grille et les axes
                drawGrid(chartWidth, chartHeight, 0f, maxFrequency.toFloat(), style)

                // Dessiner les barres
                val barWidth = chartWidth / bins
                for (i in 0 until bins) {
                    val barHeight = (histogram[i].toFloat() / maxFrequency) * chartHeight * animatedProgress.value

                    drawRect(
                        color = barColor,
                        topLeft = Offset(i * barWidth, chartHeight - barHeight),
                        size = Size(barWidth * 0.9f, barHeight)
                    )
                }

                // Dessiner les étiquettes d'axe X
                for (i in 0..bins) {
                    val xValue = minValue + binWidth * i
                    val xPos = chartWidth * i / bins

                    if (i % (bins / 5).coerceAtLeast(1) == 0 || i == bins) {
                        drawContext.canvas.nativeCanvas.drawText(
                            "%.1f".format(xValue),
                            xPos,
                            chartHeight + 20,
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.BLACK
                                textAlign = android.graphics.Paint.Align.CENTER
                                textSize = style.axisLabelStyle.fontSize.toPx()
                            }
                        )
                    }
                }

                // Dessiner les étiquettes d'axe Y
                val yAxisSteps = 5
                for (i in 0..yAxisSteps) {
                    val yValue = (maxFrequency * i / yAxisSteps).toInt()
                    val yPos = chartHeight - chartHeight * i / yAxisSteps

                    drawContext.canvas.nativeCanvas.drawText(
                        yValue.toString(),
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

            // Afficher les titres d'axes
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
    }
}