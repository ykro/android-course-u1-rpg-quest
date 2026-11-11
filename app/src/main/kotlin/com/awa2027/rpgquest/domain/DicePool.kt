package com.awa2027.rpgquest.domain

/**
 * Grupo de dados elegido en la arena. Es inmutable: cada operación devuelve un
 * grupo nuevo y nunca modifica el original. Nunca guarda conteos en cero.
 */
data class DicePool(val counts: Map<Die, Int> = emptyMap()) {
    val size: Int get() = counts.values.sum()

    val isEmpty: Boolean get() = size == 0

    fun countOf(die: Die): Int = counts[die] ?: 0

    /** Agrega un dado; si el grupo ya tiene [MAX_DICE] dados, devuelve el mismo grupo. */
    fun add(die: Die): DicePool {
        TODO("Taller, paso 3: devuelve un grupo nuevo con un dado más, respetando MAX_DICE")
    }

    /** Quita un dado; al llegar a cero elimina la entrada. Si no hay de ese tipo, no cambia. */
    fun remove(die: Die): DicePool {
        TODO("Taller, paso 3: devuelve un grupo nuevo con un dado menos, sin conteos en cero")
    }

    /** Notación de rol ordenada por caras, por ejemplo "2d6 + 1d20". Vacío si no hay dados. */
    fun notation(): String {
        TODO("Taller, paso 3: arma la notación recorriendo Die.entries")
    }

    companion object {
        const val MAX_DICE = 10
    }
}
