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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.touzalab.composecharts.data.PieChartSegment
import com.touzalab.composecharts.style.ChartStyle
import kotlin.math.*

/**
 * Composable pour afficher un diagramme circulaire
 *
 * @param segments Liste des segments du diagramme
 * @param modifier Modifier pour personnaliser l'apparence
 * @param style Style du graphique
 * @param donut Si true, affiche un graphique en anneau (donut) au lieu d'un graphique circulaire
 * @param donutRatio Rapport entre rayon intérieur et extérieur pour le graphique en anneau (entre 0 et 1)
 * @param title Titre du graphique (optionnel)
 * @param showPercentages Si true, affiche les pourcentages sur le graphique
 * @param showLegend Si true, affiche la légende
 */
@Composable
fun PieChart(
    segments: List<PieChartSegment>,
    modifier: Modifier = Modifier,
    style: ChartStyle = ChartStyle(),
    donut: Boolean = false,
    donutRatio: Float = 0.6f,
    title: String? = null,
    showPercentages: Boolean = true,
    showLegend: Boolean = true
) {
    require(segments.isNotEmpty()) { "Segments list cannot be empty" }
    require(donutRatio in 0f..1f) { "Donut ratio must be between 0 and 1" }

    val total = segments.sumOf { it.value.toDouble() }.toFloat()
    var selectedSegment by remember { mutableStateOf<PieChartSegment?>(null) }

    // Animation de rotation
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(segments) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = style.animationDuration,
                easing = style.animationEasing
            )
        )
    }

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

        // Graphique circulaire
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(16.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(segments) {
                        detectTapGestures { offset ->
                            // Calculer le segment cliqué
                            val center = Offset((size.width / 2).toFloat(),
                                (size.height / 2).toFloat()
                            )
                            val radius = minOf(size.width, size.height) / 2

                            val clickVector = Offset(offset.x - center.x, offset.y - center.y)
                            val distance = clickVector.getDistance()

                            if (distance <= radius && (!donut || distance >= radius * donutRatio)) {
                                // Calculer l'angle en degrés (0° à droite, sens horaire)
                                var angle = atan2(clickVector.y, clickVector.x) * 180 / PI
                                if (angle < 0) angle += 360

                                // Trouver le segment correspondant à cet angle
                                var startAngle = 0f
                                for (segment in segments) {
                                    val sweepAngle = (segment.value / total) * 360f
                                    if (angle >= startAngle && angle <= startAngle + sweepAngle) {
                                        selectedSegment = segment
                                        break
                                    }
                                    startAngle += sweepAngle
                                }
                            }
                        }
                    }
            ) {
                val radius = minOf(size.width, size.height) / 2
                val innerRadius = if (donut) radius * donutRatio else 0f
                val center = Offset(size.width / 2, size.height / 2)

                var startAngle = 0f

                // Dessiner chaque segment
                segments.forEach { segment ->
                    val sweepAngle = (segment.value / total) * 360f * animatedProgress.value

                    // Segment plein
                    drawArc(
                        color = segment.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )

                    // Trou central pour le donut
                    if (donut) {
                        drawArc(
                            color = style.backgroundColor,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                            size = Size(innerRadius * 2, innerRadius * 2)
                        )
                    }

                    // Afficher les pourcentages
                    if (showPercentages && sweepAngle > 15) {
                        val percentageText = "%.1f%%".format((segment.value / total) * 100)
                        val middleAngle = startAngle + sweepAngle / 2
                        val cos = cos(Math.toRadians(middleAngle.toDouble())).toFloat()
                        val sin = sin(Math.toRadians(middleAngle.toDouble())).toFloat()

                        // Position du texte (à mi-chemin entre le centre et le bord)
                        val textRadius = if (donut) {
                            (radius + innerRadius) / 2
                        } else {
                            radius * 0.7f
                        }

                        val textPosition = Offset(
                            center.x + cos * textRadius,
                            center.y + sin * textRadius
                        )

                        drawContext.canvas.nativeCanvas.drawText(
                            percentageText,
                            textPosition.x,
                            textPosition.y,
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.WHITE
                                textAlign = android.graphics.Paint.Align.CENTER
                                textSize = 12.sp.toPx()
                            }
                        )
                    }

                    startAngle += sweepAngle
                }

                // Mettre en évidence le segment sélectionné avec un contour
                selectedSegment?.let { selected ->
                    var highlightStartAngle = 0f
                    for (segment in segments) {
                        val segmentAngle = (segment.value / total) * 360f
                        if (segment == selected) {
                            drawArc(
                                color = Color.White,
                                startAngle = highlightStartAngle,
                                sweepAngle = segmentAngle,
                                useCenter = true,
                                topLeft = Offset(center.x - radius - 5, center.y - radius - 5),
                                size = Size((radius + 5) * 2, (radius + 5) * 2),
                                style = Stroke(width = 5f)
                            )
                            break
                        }
                        highlightStartAngle += segmentAngle
                    }
                }
            }

            // Afficher le tooltip pour le segment sélectionné
            selectedSegment?.let { segment ->
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    color = style.tooltipBackgroundColor,
                    shadowElevation = style.tooltipShadowElevation,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(style.tooltipCornerRadius)
                ) {
                    Column(
                        modifier = Modifier.padding(style.tooltipPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = segment.label,
                            style = style.tooltipTextStyle.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        )
                        Text(
                            text = segment.value.toString(),
                            style = style.tooltipTextStyle
                        )
                        Text(
                            text = "%.1f%%".format((segment.value / total) * 100),
                            style = style.tooltipTextStyle
                        )
                    }
                }
            }
        }

        // Légende
        if (showLegend) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                segments.forEach { segment ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(segment.color)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = segment.label,
                            style = style.legendTextStyle,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "%.1f%%".format((segment.value / total) * 100),
                            style = style.legendTextStyle,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}
