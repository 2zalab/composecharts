package com.touzalab.composecharts.animation

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing

/**
 * Préréglages d'animation pour les graphiques
 */
object AnimationPresets {
    /**
     * Configuration d'animation
     */
    data class AnimationConfig(
        val duration: Int,
        val easing: Easing,
        val delay: Int = 0,
        val staggered: Boolean = false,
        val staggerDelay: Int = 50
    )

    // Animation rapide
    val Fast = AnimationConfig(
        duration = 300,
        easing = LinearOutSlowInEasing
    )

    // Animation normale
    val Normal = AnimationConfig(
        duration = 1000,
        easing = FastOutSlowInEasing
    )

    // Animation lente
    val Slow = AnimationConfig(
        duration = 2000,
        easing = FastOutSlowInEasing
    )

    // Animation avec délai
    val Delayed = AnimationConfig(
        duration = 1000,
        easing = FastOutSlowInEasing,
        delay = 300
    )

    // Animation échelonnée
    val Staggered = AnimationConfig(
        duration = 1000,
        easing = FastOutSlowInEasing,
        staggered = true,
        staggerDelay = 100
    )

    // Animation linéaire
    val Linear = AnimationConfig(
        duration = 1000,
        easing = LinearEasing
    )
}
