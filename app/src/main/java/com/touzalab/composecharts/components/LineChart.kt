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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.touzalab.composecharts.data.DataPoint
import com.touzalab.composecharts.data.DataSeries
import com.touzalab.composecharts.style.ChartStyle
import kotlin.math.max
import kotlin.math.min

/**
 * Composable pour afficher un graphique en courbe
 *
 * @param dataSeries Liste des séries de données à afficher
 * @param modifier Modifier pour personnaliser l'apparence
 * @param style Style du graphique
 * @param title Titre du graphique (optionnel)
 * @param xAxisTitle Titre de l'axe X (optionnel)
 * @param yAxisTitle Titre de l'axe Y (optionnel)
 * @param smoothCurve Si true, lisse les courbes
 * @param showPoints Si true, affiche les points de données
 * @param fillArea Si true, remplit la zone sous la courbe
 * @param showLegend Si true, affiche la légende
 * @param showGrid Si true, affiche la grille de fond
 * @param showTooltip Si true, affiche des tooltips au survol/tap
 * @param yAxisRange Plage de l'axe Y (min, max) si null, calculé automatiquement
 * @param pointRadius Rayon des points sur le graphique
 * @param lineWidth Épaisseur des lignes du graphique
 */
@Composable
fun LineChart(
    dataSeries: List<DataSeries>,
    modifier: Modifier = Modifier,
    style: ChartStyle = ChartStyle(),
    title: String? = null,
    xAxisTitle: String? = null,
    yAxisTitle: String? = null,
    smoothCurve: Boolean = true,
    showPoints: Boolean = true,
    fillArea: Boolean = false,
    showLegend: Boolean = true,
    showGrid: Boolean = true,
    showTooltip: Boolean = true,
    yAxisRange: Pair<Float, Float>? = null,
    pointRadius: Float = 4f,
    lineWidth: Float = 2f
) {
    require(dataSeries.isNotEmpty()) { "Data series list cannot be empty" }

    // Animation du graphique
    val animatedProgress = remember { Animatable(0f) }

    // État pour le point survolé/sélectionné
    var selectedPoint by remember { mutableStateOf<Pair<DataPoint, DataSeries>?>(null) }
    var tooltipPosition by remember { mutableStateOf<Offset?>(null) }

    // Lancer l'animation au chargement
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
    val allPoints = dataSeries.flatMap { it.points }
    val xMin = allPoints.minOfOrNull { it.x } ?: 0f
    val xMax = allPoints.maxOfOrNull { it.x } ?: 0f
    val yMin = yAxisRange?.first ?: (if (fillArea) 0f else (allPoints.minOfOrNull { it.y } ?: 0f))
    val yMax = yAxisRange?.second ?: (allPoints.maxOfOrNull { it.y } ?: 0f).let {
        // Ajouter un peu d'espace au-dessus
        it + (it - yMin) * 0.1f
    }

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
                            if (!showTooltip) return@detectTapGestures

                            // Convertir l'offset en coordonnées de graphique
                            val chartWidth = size.width
                            val chartHeight = size.height

                            val x = xMin + (offset.x / chartWidth) * (xMax - xMin)
                            val y = yMax - (offset.y / chartHeight) * (yMax - yMin)

                            // Trouver le point le plus proche
                            var closestPoint: DataPoint? = null
                            var closestSeries: DataSeries? = null
                            var minDistance = Float.MAX_VALUE

                            for (series in dataSeries) {
                                for (point in series.points) {
                                    val pointX = ((point.x - xMin) / (xMax - xMin)) * chartWidth
                                    val pointY = chartHeight - ((point.y - yMin) / (yMax - yMin)) * chartHeight

                                    val distance = kotlin.math.sqrt(
                                        (offset.x - pointX).pow(2) + (offset.y - pointY).pow(2)
                                    )

                                    if (distance < minDistance && distance < 30f) {
                                        minDistance = distance
                                        closestPoint = point
                                        closestSeries = series
                                    }
                                }
                            }

                            if (closestPoint != null && closestSeries != null) {
                                selectedPoint = closestPoint to closestSeries
                                tooltipPosition = offset
                            } else {
                                selectedPoint = null
                                tooltipPosition = null
                            }
                        }
                    }
            ) {
                val chartWidth = size.width
                val chartHeight = size.height

                // Dessiner la grille et les axes
                if (showGrid) {
                    drawGrid(chartWidth, chartHeight, yMin, yMax, style)
                }

                // Dessiner les courbes
                for (series in dataSeries) {
                    val points = series.points
                    if (points.size < 2) continue

                    // Créer le chemin pour la ligne
                    val path = Path()
                    val fillPath = Path()

                    // Premier point
                    val firstX = ((points[0].x - xMin) / (xMax - xMin)) * chartWidth
                    val firstY = chartHeight - ((points[0].y - yMin) / (yMax - yMin)) * chartHeight * animatedProgress.value

                    path.moveTo(firstX, firstY)
                    fillPath.moveTo(firstX, chartHeight)
                    fillPath.lineTo(firstX, firstY)

                    if (smoothCurve) {
                        // Courbe lissée avec Bézier
                        for (i in 1 until points.size) {
                            val prevX = ((points[i-1].x - xMin) / (xMax - xMin)) * chartWidth
                            val prevY = chartHeight - ((points[i-1].y - yMin) / (yMax - yMin)) * chartHeight * animatedProgress.value

                            val currX = ((points[i].x - xMin) / (xMax - xMin)) * chartWidth
                            val currY = chartHeight - ((points[i].y - yMin) / (yMax - yMin)) * chartHeight * animatedProgress.value

                            // Points de contrôle pour la courbe de Bézier
                            val ctrl1X = prevX + (currX - prevX) / 3
                            val ctrl1Y = prevY
                            val ctrl2X = prevX + 2 * (currX - prevX) / 3
                            val ctrl2Y = currY

                            path.cubicTo(ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, currX, currY)
                            fillPath.cubicTo(ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, currX, currY)
                        }
                    } else {
                        // Ligne brisée (segments droits)
                        for (i in 1 until points.size) {
                            val x = ((points[i].x - xMin) / (xMax - xMin)) * chartWidth
                            val y = chartHeight - ((points[i].y - yMin) / (yMax - yMin)) * chartHeight * animatedProgress.value

                            path.lineTo(x, y)
                            fillPath.lineTo(x, y)
                        }
                    }

                    // Compléter le chemin de remplissage
                    if (fillArea) {
                        fillPath.lineTo(
                            ((points.last().x - xMin) / (xMax - xMin)) * chartWidth,
                            chartHeight
                        )
                        fillPath.close()

                        // Dessiner le remplissage
                        drawPath(
                            path = fillPath,
                            color = series.color.copy(alpha = 0.2f)
                        )
                    }

                    // Dessiner la ligne
                    drawPath(
                        path = path,
                        color = series.color,
                        style = Stroke(width = lineWidth.dp.toPx())
                    )

                    // Dessiner les points
                    if (showPoints) {
                        for (point in points) {
                            val x = ((point.x - xMin) / (xMax - xMin)) * chartWidth
                            val y = chartHeight - ((point.y - yMin) / (yMax - yMin)) * chartHeight * animatedProgress.value

                            // Point extérieur
                            drawCircle(
                                color = series.color,
                                radius = pointRadius.dp.toPx(),
                                center = Offset(x, y)
                            )

                            // Point intérieur (blanc)
                            drawCircle(
                                color = Color.White,
                                radius = (pointRadius / 2).dp.toPx(),
                                center = Offset(x, y)
                            )

                            // Surbrillance pour le point sélectionné
                            if (selectedPoint?.first == point && selectedPoint?.second == series) {
                                drawCircle(
                                    color = series.color.copy(alpha = 0.3f),
                                    radius = (pointRadius * 2).dp.toPx(),
                                    center = Offset(x, y)
                                )
                            }
                        }
                    }
                }

                // Dessiner les étiquettes d'axe X
                val xAxisSteps = 5
                for (i in 0..xAxisSteps) {
                    val xValue = xMin + (xMax - xMin) * i / xAxisSteps
                    val xPos = chartWidth * i / xAxisSteps

                    // Ligne verticale
                    if (showGrid && i > 0 && i < xAxisSteps) {
                        drawLine(
                            color = style.gridColor,
                            start = Offset(xPos, 0f),
                            end = Offset(xPos, chartHeight),
                            strokeWidth = style.gridLineWidth.toPx()
                        )
                    }

                    // Étiquette
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

                // Dessiner les étiquettes d'axe Y
                val yAxisSteps = 5
                for (i in 0..yAxisSteps) {
                    val yValue = yMin + (yMax - yMin) * i / yAxisSteps
                    val yPos = chartHeight - chartHeight * i / yAxisSteps

                    // Ligne horizontale
                    if (showGrid && i > 0 && i < yAxisSteps) {
                        drawLine(
                            color = style.gridColor,
                            start = Offset(0f, yPos),
                            end = Offset(chartWidth, yPos),
                            strokeWidth = style.gridLineWidth.toPx()
                        )
                    }

                    // Étiquette
                    val formattedValue = if (yValue.toInt().toFloat() == yValue) {
                        yValue.toInt().toString()
                    } else {
                        "%.1f".format(yValue)
                    }

                    drawContext.canvas.nativeCanvas.drawText(
                        formattedValue,
                        -10f,
                        yPos + 5f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textAlign = android.graphics.Paint.Align.RIGHT
                            textSize = style.axisLabelStyle.fontSize.toPx()
                        }
                    )
                }

                // Dessiner les axes principaux
                drawLine(
                    color = style.axisColor,
                    start = Offset(0f, chartHeight),
                    end = Offset(chartWidth, chartHeight),
                    strokeWidth = style.axisLineWidth.toPx()
                )

                drawLine(
                    color = style.axisColor,
                    start = Offset(0f, 0f),
                    end = Offset(0f, chartHeight),
                    strokeWidth = style.axisLineWidth.toPx()
                )
            }

            // Tooltip
            if (showTooltip && selectedPoint != null && tooltipPosition != null) {
                Surface(
                    modifier = Modifier
                        .offset(
                            x = tooltipPosition!!.x.dp - 75.dp,
                            y = tooltipPosition!!.y.dp - 70.dp
                        )
                        .width(150.dp),
                    color = style.tooltipBackgroundColor,
                    shadowElevation = style.tooltipShadowElevation,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(style.tooltipCornerRadius)
                ) {
                    Column(
                        modifier = Modifier.padding(style.tooltipPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = selectedPoint!!.second.name,
                            style = style.tooltipTextStyle.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "X: ${selectedPoint!!.first.label}",
                                style = style.tooltipTextStyle
                            )
                            Text(
                                text = "Y: ${selectedPoint!!.first.y}",
                                style = style.tooltipTextStyle
                            )
                        }
                    }
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
        if (showLegend && dataSeries.size > 1) {
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
                                .background(series.color, shape = androidx.compose.foundation.shape.CircleShape)
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

// Extension pour l'élévation au carré
private fun Float.pow(n: Int): Float {
    var result = 1f
    repeat(n) { result *= this }
    return result
}