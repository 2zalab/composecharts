package com.touzalab.composecharts.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.touzalab.composecharts.style.ChartStyle

/**
 * Thèmes prédéfinis pour les graphiques
 */
object ChartThemes {
    // Thème clair par défaut
    val Light = ChartStyle(
        backgroundColor = Color.White,
        gridColor = Color.LightGray.copy(alpha = 0.5f),
        axisColor = Color.DarkGray
    )

    // Thème sombre
    val Dark = ChartStyle(
        backgroundColor = Color(0xFF121212),
        gridColor = Color.DarkGray.copy(alpha = 0.5f),
        axisColor = Color.LightGray,
        axisLabelStyle = androidx.compose.ui.text.TextStyle(
            color = Color.LightGray,
            fontSize = 12.sp
        ),
        titleStyle = androidx.compose.ui.text.TextStyle(
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        ),
        legendTextStyle = androidx.compose.ui.text.TextStyle(
            color = Color.LightGray,
            fontSize = 12.sp
        ),
        tooltipBackgroundColor = Color(0xFF2A2A2A),
        tooltipTextStyle = androidx.compose.ui.text.TextStyle(
            color = Color.White,
            fontSize = 12.sp
        )
    )

    // Thème coloré
    val Colorful = ChartStyle(
        backgroundColor = Color(0xFFF5F5F5),
        gridColor = Color(0xFFE0E0E0),
        axisColor = Color(0xFF5B5B5B),
        titleStyle = androidx.compose.ui.text.TextStyle(
            color = Color(0xFF3F51B5),
            fontSize = 18.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    )

    // Thème minimaliste
    val Minimal = ChartStyle(
        backgroundColor = Color.White,
        gridColor = Color.Transparent,
        showGrid = false,
        axisColor = Color.LightGray,
        axisLineWidth = 0.5f.dp
    )
}
