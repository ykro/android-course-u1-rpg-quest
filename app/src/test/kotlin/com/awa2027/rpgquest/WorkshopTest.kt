package com.awa2027.rpgquest

import com.awa2027.rpgquest.data.InMemoryQuestRepository
import com.awa2027.rpgquest.data.InMemoryRollHistoryRepository
import com.awa2027.rpgquest.di.appModule
import com.awa2027.rpgquest.domain.DicePool
import com.awa2027.rpgquest.domain.Die
import com.awa2027.rpgquest.domain.DieRoll
import com.awa2027.rpgquest.domain.RollMode
import com.awa2027.rpgquest.domain.rollPool
import com.awa2027.rpgquest.ui.arena.DiceArenaEvent
import com.awa2027.rpgquest.ui.arena.DiceArenaViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication

/** Taller guiado: en rojo en el starter, en verde al terminar el codelab. */
@OptIn(ExperimentalCoroutinesApi::class)
class WorkshopTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Paso 3: DicePool inmutable

    @Test
    fun addIncrementsCountWithoutMutatingTheOriginal() {
        val empty = DicePool()
        val one = empty.add(Die.D6)
        val two = one.add(Die.D6)

        assertEquals(0, empty.size)
        assertEquals(1, one.countOf(Die.D6))
        assertEquals(2, two.countOf(Die.D6))
        assertEquals(2, two.size)
    }

    @Test
    fun addIgnoresDiceBeyondTheLimit() {
        var pool = DicePool()
        repeat(DicePool.MAX_DICE) { pool = pool.add(Die.D4) }

        val overLimit = pool.add(Die.D20)

        assertEquals(DicePool.MAX_DICE, overLimit.size)
        assertEquals(0, overLimit.countOf(Die.D20))
        assertEquals(pool, overLimit)
    }

    @Test
    fun removeDecrementsAndDropsTheEntryAtZero() {
        val pool = DicePool().add(Die.D8).add(Die.D8)

        assertEquals(1, pool.remove(Die.D8).countOf(Die.D8))
        assertEquals("Sin conteos en cero", DicePool(), pool.remove(Die.D8).remove(Die.D8))
        assertFalse(pool.remove(Die.D8).remove(Die.D8).counts.containsKey(Die.D8))
    }

    @Test
    fun removeOfAMissingDieKeepsThePool() {
        val pool = DicePool().add(Die.D4)

        assertEquals(pool, pool.remove(Die.D20))
    }

    @Test
    fun notationIsOrderedBySidesRegardlessOfInsertionOrder() {
        val pool = DicePool().add(Die.D20).add(Die.D6).add(Die.D6)

        assertEquals("2d6 + 1d20", pool.notation())
        assertEquals("", DicePool().notation())
    }

    // Paso 4: rollPool con aleatoriedad inyectada

    @Test
    fun rollPoolRollsInDieOrderAndSumsTheValues() {
        val pool = DicePool().add(Die.D20).add(Die.D6).add(Die.D6)
        val roller = SequenceDiceRoller(3, 5, 17)

        val result = rollPool(pool, roller, id = 7, questId = "q-uno")

        assertEquals(listOf(6, 6, 20), roller.requestedSides)
        assertEquals(listOf(DieRoll(Die.D6, 3), DieRoll(Die.D6, 5), DieRoll(Die.D20, 17)), result.dice)
        assertEquals(25, result.total)
        assertEquals(7L, result.id)
        assertEquals("q-uno", result.questId)
        assertEquals(RollMode.Normal, result.mode)
        assertEquals(0, result.modifier)
    }

    @Test
    fun rollPoolRejectsAnEmptyPool() {
        assertThrows(IllegalArgumentException::class.java) {
            rollPool(DicePool(), SequenceDiceRoller(), id = 1, questId = "q-uno")
        }
    }

    // Paso 5: DiceArenaViewModel con StateFlow y corrutinas

    private fun arena(
        roller: SequenceDiceRoller,
        history: InMemoryRollHistoryRepository = InMemoryRollHistoryRepository()
    ) = DiceArenaViewModel("q-uno", InMemoryQuestRepository(TEST_QUESTS), roller, history)

    @Test
    fun arenaEventsBuildThePool() {
        val viewModel = arena(SequenceDiceRoller())

        viewModel.onEvent(DiceArenaEvent.AddDie(Die.D6))
        viewModel.onEvent(DiceArenaEvent.AddDie(Die.D6))
        viewModel.onEvent(DiceArenaEvent.AddDie(Die.D20))
        viewModel.onEvent(DiceArenaEvent.RemoveDie(Die.D6))

        assertEquals("q-uno", viewModel.uiState.value.quest?.id)
        assertEquals("1d6 + 1d20", viewModel.uiState.value.pool.notation())

        viewModel.onEvent(DiceArenaEvent.ClearPool)
        assertTrue(viewModel.uiState.value.pool.isEmpty)
    }

    @Test
    fun rollIsRollingDuringTheAnimationAndThenRecordsTheResult() = runTest {
        val history = InMemoryRollHistoryRepository()
        val viewModel = arena(SequenceDiceRoller(12), history)
        viewModel.onEvent(DiceArenaEvent.AddDie(Die.D20))

        viewModel.onEvent(DiceArenaEvent.Roll)
        assertTrue("isRolling se activa al pedir la tirada", viewModel.uiState.value.isRolling)

        advanceTimeBy(DiceArenaViewModel.ROLL_ANIMATION_MS - 1)
        runCurrent()
        assertTrue(viewModel.uiState.value.isRolling)
        assertNull(viewModel.uiState.value.lastRoll)

        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isRolling)
        assertEquals(12, state.lastRoll?.total)
        assertEquals(1, history.history.value.size)
        assertEquals(listOf(state.lastRoll), state.history)
    }

    @Test
    fun rollIsIgnoredWithAnEmptyPoolOrWhileRolling() = runTest {
        val roller = SequenceDiceRoller(4, 9)
        val history = InMemoryRollHistoryRepository()
        val viewModel = arena(roller, history)

        viewModel.onEvent(DiceArenaEvent.Roll)
        assertFalse("Sin dados no hay tirada", viewModel.uiState.value.isRolling)

        viewModel.onEvent(DiceArenaEvent.AddDie(Die.D8))
        viewModel.onEvent(DiceArenaEvent.Roll)
        viewModel.onEvent(DiceArenaEvent.Roll)
        advanceUntilIdle()

        assertEquals("Un segundo toque durante la animación no lanza otra tirada", 1, history.history.value.size)
        assertEquals(listOf(8), roller.requestedSides)
    }

    @Test
    fun rollUsesThePoolAsItWasWhenRequested() = runTest {
        val viewModel = arena(SequenceDiceRoller(3))
        viewModel.onEvent(DiceArenaEvent.AddDie(Die.D4))

        viewModel.onEvent(DiceArenaEvent.Roll)
        viewModel.onEvent(DiceArenaEvent.AddDie(Die.D20))
        advanceUntilIdle()

        assertEquals(listOf(DieRoll(Die.D4, 3)), viewModel.uiState.value.lastRoll?.dice)
        assertEquals("1d4 + 1d20", viewModel.uiState.value.pool.notation())
    }

    @Test
    fun arenaHistoryOnlyShowsRollsOfItsQuest() = runTest {
        val history = InMemoryRollHistoryRepository()
        history.record(fakeRoll(id = history.nextId(), total = 9, questId = "q-dos"))
        val viewModel = arena(SequenceDiceRoller(5), history)

        viewModel.onEvent(DiceArenaEvent.AddDie(Die.D6))
        viewModel.onEvent(DiceArenaEvent.Roll)
        advanceUntilIdle()

        assertEquals(listOf("q-uno"), viewModel.uiState.value.history.map { it.questId })
        assertEquals(2, history.history.value.size)
    }

    // Paso 6: Koin

    @Test
    fun koinBuildsTheArenaViewModelForTheRequestedQuest() {
        val koin = koinApplication { modules(appModule) }.koin

        val viewModel = koin.get<DiceArenaViewModel> { parametersOf("q-forja") }

        assertEquals("Forja del Titán", viewModel.uiState.value.quest?.title)
    }
}
