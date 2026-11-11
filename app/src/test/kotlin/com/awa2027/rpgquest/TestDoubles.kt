package com.awa2027.rpgquest

import app.cash.turbine.ReceiveTurbine
import com.awa2027.rpgquest.data.DiceRoller
import com.awa2027.rpgquest.domain.Die
import com.awa2027.rpgquest.domain.DieRoll
import com.awa2027.rpgquest.domain.Quest
import com.awa2027.rpgquest.domain.QuestDifficulty
import com.awa2027.rpgquest.domain.RollResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// Archivo oficial de apoyo para los tests (no lo modifiques). Tus tests propios pueden usarlo.

/** Roller determinista: devuelve los valores en orden y registra las caras pedidas. */
class SequenceDiceRoller(vararg values: Int) : DiceRoller {
    private val pending = ArrayDeque(values.toList())
    val requestedSides = mutableListOf<Int>()

    override fun roll(sides: Int): Int {
        requestedSides += sides
        return pending.removeFirstOrNull() ?: error("SequenceDiceRoller sin valores para un d$sides")
    }
}

/** Reemplaza Dispatchers.Main (que no existe en la JVM) por un dispatcher de prueba. */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(val dispatcher: TestDispatcher = StandardTestDispatcher()) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

val TEST_QUESTS: List<Quest> = listOf(
    Quest("q-uno", "Paso del Volcán", "Altiplano", QuestDifficulty.NOVICE, difficultyClass = 10),
    Quest("q-dos", "Cueva de Cristal", "Lago Azul", QuestDifficulty.VETERAN, difficultyClass = 15),
    Quest("q-tres", "Ciudadela Perdida", "Selva Alta", QuestDifficulty.MYTHIC, difficultyClass = 20)
)

/** Tirada armada a mano para probar estadísticas. [d20] es el valor conservado de un d20 opcional. */
fun fakeRoll(id: Long, total: Int, questId: String = "q-uno", d20: Int? = null): RollResult {
    val dice = if (d20 == null) listOf(DieRoll(Die.D8, 4)) else listOf(DieRoll(Die.D20, d20))
    return RollResult(id = id, questId = questId, dice = dice, total = total)
}

/** Espera emisiones hasta que una cumpla [predicate]; StateFlow puede omitir estados intermedios. */
suspend fun <T> ReceiveTurbine<T>.awaitItemMatching(predicate: (T) -> Boolean): T {
    while (true) {
        val item = awaitItem()
        if (predicate(item)) return item
    }
}
