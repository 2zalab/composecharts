package com.touzalab.composecharts.preview

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.touzalab.composecharts.components.*
import com.touzalab.composecharts.data.*
import com.touzalab.composecharts.style.ChartStyle
import com.touzalab.composecharts.theme.ColorPalettes

/**
 * Composable de prévisualisation pour les graphiques en courbe
 */
@Composable
fun LineChartPreview() {
    // Données de démonstration
    val dataSeries = listOf(
        DataSeries(
            name = "Série A",
            color = ColorPalettes.Default[0],
            points = listOf(
                DataPoint(x = 0f, y = 10f, label = "Jan"),
                DataPoint(x = 1f, y = 15f, label = "Fév"),
                DataPoint(x = 2f, y = 8f, label = "Mar"),
                DataPoint(x = 3f, y = 12f, label = "Avr"),
                DataPoint(x = 4f, y = 20f, label = "Mai"),
                DataPoint(x = 5f, y = 18f, label = "Jun")
            )
        ),
        DataSeries(
            name = "Série B",
            color = ColorPalettes.Default[1],
            points = listOf(
                DataPoint(x = 0f, y = 5f, label = "Jan"),
                DataPoint(x = 1f, y = 8f, label = "Fév"),
                DataPoint(x = 2f, y = 12f, label = "Mar"),
                DataPoint(x = 3f, y = 15f, label = "Avr"),
                DataPoint(x = 4f, y = 10f, label = "Mai"),
                DataPoint(x = 5f, y = 14f, label = "Jun")
            )
        )
    )

    LineChart(
        dataSeries = dataSeries,
        title = "Graphique en courbe",
        xAxisTitle = "Mois",
        yAxisTitle = "Valeurs",
        smoothCurve = true,
        showPoints = true,
        fillArea = true
    )
}

/**
 * Composable de prévisualisation pour les graphiques à barres
 */
@Composable
fun BarChartPreview() {
    // Données de démonstration
    val dataSeries = listOf(
        DataSeries(
            name = "Série A",
            color = ColorPalettes.Default[0],
            points = listOf(
                DataPoint(x = 0f, y = 10f, label = "Jan"),
                DataPoint(x = 1f, y = 15f, label = "Fév"),
                DataPoint(x = 2f, y = 8f, label = "Mar"),
                DataPoint(x = 3f, y = 12f, label = "Avr"),
                DataPoint(x = 4f, y = 20f, label = "Mai"),
                DataPoint(x = 5f, y = 18f, label = "Jun")
            )
        ),
        DataSeries(
            name = "Série B",
            color = ColorPalettes.Default[1],
            points = listOf(
                DataPoint(x = 0f, y = 5f, label = "Jan"),
                DataPoint(x = 1f, y = 8f, label = "Fév"),
                DataPoint(x = 2f, y = 12f, label = "Mar"),
                DataPoint(x = 3f, y = 15f, label = "Avr"),
                DataPoint(x = 4f, y = 10f, label = "Mai"),
                DataPoint(x = 5f, y = 14f, label = "Jun")
            )
        )
    )

    BarChart(
        dataSeries = dataSeries,
        title = "Graphique à barres",
        xAxisTitle = "Mois",
        yAxisTitle = "Valeurs",
        stacked = false
    )
}

/**
 * Composable de prévisualisation pour les graphiques circulaires
 */
@Composable
fun PieChartPreview() {
    // Données de démonstration
    val segments = listOf(
        PieChartSegment(
            label = "Segment A",
            value = 30f,
            color = ColorPalettes.Default[0]
        ),
        PieChartSegment(
            label = "Segment B",
            value = 20f,
            color = ColorPalettes.Default[1]
        ),
        PieChartSegment(
            label = "Segment C",
            value = 15f,
            color = ColorPalettes.Default[2]
        ),
        PieChartSegment(
            label = "Segment D",
            value = 35f,
            color = ColorPalettes.Default[3]
        )
    )

    PieChart(
        segments = segments,
        title = "Graphique circulaire",
        donut = true,
        showPercentages = true
    )
}

/**
 * Composable de prévisualisation pour les histogrammes
 */
@Composable
fun HistogramPreview() {
    // Données de démonstration
    val data = listOf(
        10f, 12f, 15f, 18f, 20f, 22f, 25f, 27f, 30f, 32f,
        35f, 37f, 40f, 42f, 45f, 47f, 50f, 52f, 55f, 57f,
        60f, 62f, 65f, 67f, 70f, 72f, 75f, 77f, 80f, 82f
    )

    Histogram(
        data = data,
        title = "Histogramme",
        xAxisTitle = "Valeurs",
        yAxisTitle = "Fréquence",
        barColor = ColorPalettes.Default[0],
        bins = 10
    )
}

/**
 * Composable de prévisualisation pour les graphiques radar
 */
@Composable
fun RadarChartPreview() {
    // Données de démonstration
    val categories = listOf("A", "B", "C", "D", "E", "F")
    val dataSeries = listOf(
        DataSeries(
            name = "Série A",
            color = ColorPalettes.Default[0],
            points = listOf(
                DataPoint(x = 0f, y = 80f, label = "A"),
                DataPoint(x = 1f, y = 65f, label = "B"),
                DataPoint(x = 2f, y = 90f, label = "C"),
                DataPoint(x = 3f, y = 75f, label = "D"),
                DataPoint(x = 4f, y = 85f, label = "E"),
                DataPoint(x = 5f, y = 70f, label = "F")
            )
        ),
        DataSeries(
            name = "Série B",
            color = ColorPalettes.Default[1],
            points = listOf(
                DataPoint(x = 0f, y = 70f, label = "A"),
                DataPoint(x = 1f, y = 80f, label = "B"),
                DataPoint(x = 2f, y = 60f, label = "C"),
                DataPoint(x = 3f, y = 90f, label = "D"),
                DataPoint(x = 4f, y = 65f, label = "E"),
                DataPoint(x = 5f, y = 75f, label = "F")
            )
        )
    )

    RadarChart(
        dataSeries = dataSeries,
        categories = categories,
        title = "Graphique radar",
        fillArea = true,
        showPoints = true
    )
}

/**
 * Composable de prévisualisation tous les types de graphiques
 */
@Composable
fun AllChartsPreview() {
    var selectedChartType by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedChartType) {
            Tab(
                selected = selectedChartType == 0,
                onClick = { selectedChartType = 0 },
                text = { Text("Courbe") }
            )
            Tab(
                selected = selectedChartType == 1,
                onClick = { selectedChartType = 1 },
                text = { Text("Barres") }
            )
            Tab(
                selected = selectedChartType == 2,
                onClick = { selectedChartType = 2 },
                text = { Text("Circulaire") }
            )
            Tab(
                selected = selectedChartType == 3,
                onClick = { selectedChartType = 3 },
                text = { Text("Histogramme") }
            )
            Tab(
                selected = selectedChartType == 4,
                onClick = { selectedChartType = 4 },
                text = { Text("Radar") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedChartType) {
            0 -> LineChartPreview()
            1 -> BarChartPreview()
            2 -> PieChartPreview()
            3 -> HistogramPreview()
            4 -> RadarChartPreview()
        }
    }
}
