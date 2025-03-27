package com.touzalab.composecharts.utils

import androidx.compose.ui.graphics.Color
import com.touzalab.composecharts.data.DataPoint
import com.touzalab.composecharts.data.DataSeries
import com.touzalab.composecharts.data.PieChartSegment
import kotlin.math.sqrt

/**
 * Classe utilitaire pour les opérations courantes sur les graphiques
 */
object ChartUtilities {
    /**
     * Génère une série de données à partir d'une liste de valeurs
     *
     * @param values Liste de valeurs
     * @param name Nom de la série
     * @param color Couleur de la série
     * @param startX Valeur de départ pour l'axe X
     * @param stepX Pas entre chaque valeur de X
     * @return Série de données
     */
    fun generateSeries(
        values: List<Float>,
        name: String,
        color: Color,
        startX: Float = 0f,
        stepX: Float = 1f
    ): DataSeries {
        val points = values.mapIndexed { index, value ->
            DataPoint(
                x = startX + index * stepX,
                y = value,
                label = (startX + index * stepX).toString()
            )
        }

        return DataSeries(
            name = name,
            color = color,
            points = points
        )
    }

    /**
     * Génère des segments pour un graphique circulaire à partir d'une map de valeurs
     *
     * @param data Map des étiquettes et valeurs
     * @param colors Liste de couleurs à utiliser
     * @return Liste de segments pour graphique circulaire
     */
    fun generatePieSegments(
        data: Map<String, Float>,
        colors: List<Color>
    ): List<PieChartSegment> {
        return data.entries.mapIndexed { index, entry ->
            PieChartSegment(
                label = entry.key,
                value = entry.value,
                color = colors[index % colors.size]
            )
        }
    }

    /**
     * Calcule des statistiques de base sur une liste de valeurs
     *
     * @param values Liste de valeurs
     * @return Map contenant les statistiques (min, max, moyenne, médiane, écart-type)
     */
    fun calculateStatistics(values: List<Float>): Map<String, Float> {
        if (values.isEmpty()) return emptyMap()

        val min = values.minOrNull() ?: 0f
        val max = values.maxOrNull() ?: 0f
        val sum = values.sum()
        val mean = sum / values.size

        val sortedValues = values.sorted()
        val median = if (values.size % 2 == 0) {
            (sortedValues[values.size / 2 - 1] + sortedValues[values.size / 2]) / 2
        } else {
            sortedValues[values.size / 2]
        }

        val variance = values.map { (it - mean) * (it - mean) }.sum() / values.size
        val stdDev = sqrt(variance)

        return mapOf(
            "min" to min,
            "max" to max,
            "mean" to mean,
            "median" to median,
            "stdDev" to stdDev
        )
    }

    /**
     * Lisse une série de données en utilisant une moyenne mobile
     *
     * @param series Série de données à lisser
     * @param windowSize Taille de la fenêtre pour la moyenne mobile
     * @return Série de données lissée
     */
    fun smoothSeries(
        series: DataSeries,
        windowSize: Int = 3
    ): DataSeries {
        if (windowSize <= 1 || series.points.size <= windowSize) {
            return series
        }

        val smoothedPoints = mutableListOf<DataPoint>()

        // Garder les premiers points intacts
        for (i in 0 until windowSize / 2) {
            smoothedPoints.add(series.points[i])
        }

        // Lisser les points du milieu
        for (i in windowSize / 2 until series.points.size - windowSize / 2) {
            var sum = 0f
            for (j in 0 until windowSize) {
                sum += series.points[i - windowSize / 2 + j].y
            }

            val smoothedValue = sum / windowSize
            smoothedPoints.add(
                DataPoint(
                    x = series.points[i].x,
                    y = smoothedValue,
                    label = series.points[i].label
                )
            )
        }

        // Garder les derniers points intacts
        for (i in series.points.size - windowSize / 2 until series.points.size) {
            smoothedPoints.add(series.points[i])
        }

        return DataSeries(
            name = "${series.name} (lissé)",
            color = series.color,
            points = smoothedPoints
        )
    }

    /**
     * Calcule une régression linéaire pour une série de données
     *
     * @param series Série de données
     * @return Série de données représentant la ligne de régression
     */
    fun linearRegression(series: DataSeries): DataSeries {
        val n = series.points.size
        if (n < 2) return series

        var sumX = 0.0
        var sumY = 0.0
        var sumXY = 0.0
        var sumX2 = 0.0

        for (point in series.points) {
            sumX += point.x.toDouble()
            sumY += point.y.toDouble()
            sumXY += (point.x * point.y).toDouble()
            sumX2 += (point.x * point.x).toDouble()
        }

        val slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX)
        val intercept = (sumY - slope * sumX) / n

        val startX = series.points.minByOrNull { it.x }?.x ?: 0f
        val endX = series.points.maxByOrNull { it.x }?.x ?: 0f

        val regressionPoints = listOf(
            DataPoint(
                x = startX,
                y = (slope * startX + intercept).toFloat(),
                label = "Début"
            ),
            DataPoint(
                x = endX,
                y = (slope * endX + intercept).toFloat(),
                label = "Fin"
            )
        )

        return DataSeries(
            name = "Régression (${series.name})",
            color = series.color.copy(alpha = 0.7f),
            points = regressionPoints
        )
    }
}
