package com.awa2027.rpgquest.domain

/** Estadísticas derivadas del historial de una misión. Sin tiradas, todo queda en cero o null. */
data class RollStats(
    val rollCount: Int = 0,
    val average: Double? = null,
    val best: Int? = null,
    val worst: Int? = null,
    val distribution: Map<Int, Int> = emptyMap(),
    val successRatePct: Int = 0,
    val longestStreak: Int = 0,
    val currentStreak: Int = 0,
    val criticalCount: Int = 0,
    val fumbleCount: Int = 0
)

/**
 * Reto: calcula las estadísticas de [history] (la tirada más reciente primero).
 * Una tirada es exitosa si su total es mayor o igual que [difficultyClass].
 */
fun computeStats(history: List<RollResult>, difficultyClass: Int): RollStats {
    TODO("Reto: calcula las estadísticas según el contrato de la guía del reto")
}
