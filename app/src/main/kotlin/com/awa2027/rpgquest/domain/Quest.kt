package com.awa2027.rpgquest.domain

enum class QuestDifficulty(val label: String) {
    NOVICE("Novato"),
    VETERAN("Veterano"),
    MYTHIC("Mítico")
}

/**
 * Una misión del tablero. [difficultyClass] (CD) es el total mínimo que cuenta
 * como tirada exitosa: un total igual a la CD también es éxito.
 */
data class Quest(
    val id: String,
    val title: String,
    val region: String,
    val difficulty: QuestDifficulty,
    val difficultyClass: Int
)
