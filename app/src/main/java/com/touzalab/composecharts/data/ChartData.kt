package com.touzalab.composecharts.data

/**
 * Classe de données représentant un point dans un graphique
 * @param x Valeur de l'axe X
 * @param y Valeur de l'axe Y
 * @param label Étiquette pour ce point (utilisé dans les légendes, tooltips, etc.)
 */
data class DataPoint(
    val x: Float,
    val y: Float,
    val label: String = ""
)

/**
 * Classe de données représentant une série de données pour les graphiques
 * @param name Nom de la série de données
 * @param color Couleur à utiliser pour cette série
 * @param points Liste des points de données dans cette série
 */
data class DataSeries(
    val name: String,
    val color: androidx.compose.ui.graphics.Color,
    val points: List<DataPoint>
)

/**
 * Classe de données pour les graphiques circulaires
 * @param label Étiquette du segment
 * @param value Valeur numérique du segment
 * @param color Couleur du segment
 */
data class PieChartSegment(
    val label: String,
    val value: Float,
    val color: androidx.compose.ui.graphics.Color
)