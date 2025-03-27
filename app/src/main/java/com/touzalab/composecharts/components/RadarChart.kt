package com.touzalab.composecharts.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.touzalab.composecharts.data.DataSeries
import com.touzalab.composecharts.style.ChartStyle
import kotlin.math.*

/**
 * Composable pour afficher un graphique radar (toile d'araignée)
 *
 * @param dataSeries Liste des séries de données à afficher
 * @param categories Liste des catégories (axes)
 * @param modifier Modifier pour personnaliser l'apparence
 * @param style Style du graphique
 * @param title Titre du graphique (optionnel)
 * @param maxValue Valeur maximale sur les axes, si null calculée automatiquement
 * @param showLegend Si true, affiche la légende
 * @param fillArea Si true, remplit les polygones
 * @param showPoints Si true, affiche les points sur les axes
 */
@Composable
fun RadarChart(
    dataSeries: List<DataSeries>,
    categories: List<String>,
    modifier: Modifier = Modifier,
    style: ChartStyle = ChartStyle(),
    title: String? = null,
    maxValue: Float? = null,
    showLegend: Boolean = true,
    fillArea: Boolean = true,
    showPoints: Boolean = true
) {
    require(dataSeries.isNotEmpty()) { "Data series list cannot be empty" }
    require(categories.isNotEmpty()) { "Categories list cannot be empty" }

    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(dataSeries) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = style.animationDuration,
                easing = style.animationEasing
            )
        )
    }

    // Calculer la valeur maximale
    val calculatedMaxValue = maxValue ?: dataSeries.flatMap { series ->
        series.points.map { it.y }
    }.maxOrNull() ?: 0f

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
                .aspectRatio(1f)
                .padding(16.dp)
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = minOf(size.width, size.height) / 2 * 0.8f

                // Dessiner les cercles concentriques et les axes
                val steps = 5
                for (i in 1..steps) {
                    val currentRadius = radius * i / steps

                    // Cercle concentrique
                    drawCircle(
                        color = style.gridColor,
                        radius = currentRadius,
                        center = center,
                        style = Stroke(width = style.gridLineWidth.toPx())
                    )

                    // Étiquette de valeur sur l'axe vertical supérieur
                    val valueOnAxis = calculatedMaxValue * i / steps
                    drawContext.canvas.nativeCanvas.drawText(
                        "%.1f".format(valueOnAxis),
                        center.x,
                        center.y - currentRadius - 5,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = style.axisLabelStyle.fontSize.toPx()
                        }
                    )
                }

                // Dessiner les axes et les étiquettes
                for (i in categories.indices) {
                    val angle = 2 * PI * i / categories.size - PI / 2
                    val cos = cos(angle).toFloat()
                    val sin = sin(angle).toFloat()

                    // Ligne d'axe
                    drawLine(
                        color = style.axisColor,
                        start = center,
                        end = Offset(
                            center.x + cos * radius,
                            center.y + sin * radius
                        ),
                        strokeWidth = style.axisLineWidth.toPx()
                    )

                    // Étiquette de catégorie
                    val labelDistance = radius * 1.1f
                    drawContext.canvas.nativeCanvas.drawText(
                        categories[i],
                        center.x + cos * labelDistance,
                        center.y + sin * labelDistance,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = style.axisLabelStyle.fontSize.toPx()
                        }
                    )
                }

                // Dessiner les données
                for (series in dataSeries) {
                    val path = Path()
                    var first = true

                    // Trouver les valeurs pour chaque catégorie
                    for (i in categories.indices) {
                        val point = series.points.find { it.label == categories[i] }
                        val value = point?.y ?: 0f

                        val angle = 2 * PI * i / categories.size - PI / 2
                        val cos = cos(angle).toFloat()
                        val sin = sin(angle).toFloat()
                        val distance = (value / calculatedMaxValue) * radius * animatedProgress.value

                        val x = center.x + cos * distance
                        val y = center.y + sin * distance

                        if (first) {
                            path.moveTo(x, y)
                            first = false
                        } else {
                            path.lineTo(x, y)
                        }

                        // Dessiner les points
                        if (showPoints) {
                            drawCircle(
                                color = series.color,
                                radius = 4.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }
                    }

                    // Fermer le chemin
                    path.close()

                    // Remplir la zone
                    if (fillArea) {
                        drawPath(
                            path = path,
                            color = series.color.copy(alpha = 0.3f)
                        )
                    }

                    // Dessiner le contour
                    drawPath(
                        path = path,
                        color = series.color,
                        style = Stroke(width = 2.dp.toPx())
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