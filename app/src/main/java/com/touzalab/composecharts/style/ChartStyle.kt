package com.touzalab.composecharts.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Configuration du style pour tous les graphiques
 */
data class ChartStyle(
    // Styles généraux
    val backgroundColor: Color = Color.White,
    val gridColor: Color = Color.LightGray,
    val gridLineWidth: Dp = 1.dp,
    val showGrid: Boolean = true,

    // Styles des axes
    val axisColor: Color = Color.DarkGray,
    val axisLineWidth: Dp = 1.5f.dp,
    val axisLabelStyle: TextStyle = TextStyle(
        color = Color.DarkGray,
        fontSize = 12.sp
    ),
    val axisLabelPadding: Dp = 8.dp,

    // Styles du titre
    val titleStyle: TextStyle = TextStyle(
        color = Color.Black,
        fontSize = 16.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    ),
    val titlePadding: Dp = 16.dp,

    // Styles de la légende
    val legendTextStyle: TextStyle = TextStyle(
        color = Color.DarkGray,
        fontSize = 12.sp
    ),
    val legendItemSpacing: Dp = 8.dp,
    val legendPadding: Dp = 16.dp,

    // Styles des tooltips
    val tooltipBackgroundColor: Color = Color.White,
    val tooltipTextStyle: TextStyle = TextStyle(
        color = Color.Black,
        fontSize = 12.sp
    ),
    val tooltipCornerRadius: Dp = 4.dp,
    val tooltipPadding: Dp = 8.dp,
    val tooltipShadowElevation: Dp = 4.dp,

    // Animation
    val animationDuration: Int = 1000,
    val animationEasing: androidx.compose.animation.core.Easing = androidx.compose.animation.core.FastOutSlowInEasing
)
