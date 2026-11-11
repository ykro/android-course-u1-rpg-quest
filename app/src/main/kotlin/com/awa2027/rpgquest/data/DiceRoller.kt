package com.awa2027.rpgquest.data

import kotlin.random.Random

/** Fuente de aleatoriedad inyectable: devuelve un valor entre 1 y [sides]. */
fun interface DiceRoller {
    fun roll(sides: Int): Int
}

/** Implementación de producción. Los tests usan un roller con valores fijos. */
class RandomDiceRoller(private val random: Random = Random.Default) : DiceRoller {
    override fun roll(sides: Int): Int {
        require(sides > 0) { "Un dado necesita al menos una cara" }
        return random.nextInt(from = 1, until = sides + 1)
    }
}
