package com.touzalab.composecharts.interaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.touzalab.composecharts.data.DataPoint

/**
 * Gestionnaire d'interactions pour les graphiques
 */
class ChartInteractionManager {
    // État de sélection
    var selectedPoint by mutableStateOf<DataPoint?>(null)
        private set

    var selectedSeries by mutableStateOf<Int?>(null)
        private set

    var isTooltipVisible by mutableStateOf(false)
        private set

    // État de zoom
    var zoomLevel by mutableStateOf(1f)
        private set

    var panOffset by mutableStateOf(0f)
        private set

    /**
     * Sélectionne un point de données
     *
     * @param point Point de données à sélectionner
     * @param seriesIndex Index de la série
     */
    fun selectPoint(point: DataPoint, seriesIndex: Int) {
        selectedPoint = point
        selectedSeries = seriesIndex
        isTooltipVisible = true
    }

    /**
     * Désélectionne le point actuel
     */
    fun deselectPoint() {
        selectedPoint = null
        selectedSeries = null
        isTooltipVisible = false
    }

    /**
     * Applique un niveau de zoom
     *
     * @param delta Delta de zoom à appliquer
     * @param centerPoint Point central du zoom
     */
    fun zoom(delta: Float, centerPoint: Float) {
        val newZoomLevel = (zoomLevel * (1f + delta)).coerceIn(0.5f, 5f)

        // Ajuster le pan pour maintenir le point central
        if (newZoomLevel != zoomLevel) {
            val oldCenter = (centerPoint - panOffset) / zoomLevel
            val newCenter = (centerPoint - panOffset) / newZoomLevel
            panOffset += (newCenter - oldCenter) * newZoomLevel
            zoomLevel = newZoomLevel
        }
    }

    /**
     * Applique un déplacement
     *
     * @param delta Delta de déplacement à appliquer
     */
    fun pan(delta: Float) {
        panOffset += delta
    }

    /**
     * Réinitialise le zoom et le déplacement
     */
    fun resetView() {
        zoomLevel = 1f
        panOffset = 0f
    }
}