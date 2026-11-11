package com.awa2027.rpgquest.domain

/** Cómo se tiran los d20 del grupo. Los demás dados siempre se tiran una vez. */
sealed interface RollMode {
    data object Normal : RollMode

    data object Advantage : RollMode

    data object Disadvantage : RollMode
}

/** Un dado tirado. [value] es el resultado que cuenta; [discarded], el que se descartó. */
data class DieRoll(val die: Die, val value: Int, val discarded: Int? = null)

/** Resultado completo de una tirada para una misión. */
data class RollResult(
    val id: Long,
    val questId: String,
    val dice: List<DieRoll>,
    val mode: RollMode = RollMode.Normal,
    val modifier: Int = 0,
    val total: Int
)

/** Crítico: algún d20 conservado muestra 20 (un 20 descartado no cuenta). */
val RollResult.isCritical: Boolean get() = dice.any { it.die == Die.D20 && it.value == 20 }

/** Pifia: algún d20 conservado muestra 1 (un 1 descartado no cuenta). */
val RollResult.isFumble: Boolean get() = dice.any { it.die == Die.D20 && it.value == 1 }
