package com.awa2027.rpgquest.domain

import com.awa2027.rpgquest.data.DiceRoller

const val MIN_MODIFIER = -10
const val MAX_MODIFIER = 10
const val MIN_TOTAL = 1
val MODIFIER_RANGE: IntRange = MIN_MODIFIER..MAX_MODIFIER

/**
 * Taller: tira cada dado del grupo una vez, en el orden de [Die.entries]
 * (de menos a más caras), y suma los valores.
 */
fun rollPool(pool: DicePool, roller: DiceRoller, id: Long, questId: String): RollResult {
    TODO("Taller, paso 4: rechaza el grupo vacío, tira los dados en orden y suma")
}

/**
 * Reto: tirada con reglas. Con ventaja o desventaja cada d20 se tira dos veces
 * seguidas y se conserva el mayor o el menor; el otro queda en [DieRoll.discarded].
 * El total es la suma de los valores conservados más el modificador, y nunca baja de [MIN_TOTAL].
 * El contrato completo está en la guía del reto.
 */
fun resolveRoll(
    pool: DicePool,
    mode: RollMode,
    modifier: Int,
    roller: DiceRoller,
    id: Long,
    questId: String
): RollResult {
    TODO("Reto: implementa las reglas de ventaja, desventaja, modificador y límites")
}
