package com.awa2027.rpgquest

import app.cash.turbine.test
import com.awa2027.rpgquest.data.InMemoryQuestRepository
import com.awa2027.rpgquest.data.InMemoryRollHistoryRepository
import com.awa2027.rpgquest.di.appModule
import com.awa2027.rpgquest.domain.DicePool
import com.awa2027.rpgquest.domain.Die
import com.awa2027.rpgquest.domain.DieRoll
import com.awa2027.rpgquest.domain.RollMode
import com.awa2027.rpgquest.domain.RollStats
import com.awa2027.rpgquest.domain.computeStats
import com.awa2027.rpgquest.domain.resolveRoll
import com.awa2027.rpgquest.ui.arena.DiceArenaEvent
import com.awa2027.rpgquest.ui.arena.DiceArenaViewModel
import com.awa2027.rpgquest.ui.stats.StatsUiState
import com.awa2027.rpgquest.ui.stats.StatsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication

/** Reto: tests públicos (una muestra). El contrato completo de la guía del reto tiene más casos. */
@OptIn(ExperimentalCoroutinesApi::class)
class ChallengeTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun advantageKeepsTheHighestD20AndRecordsTheDiscardedOne() {
        val pool = DicePool().add(Die.D20)

        val result = resolveRoll(pool, RollMode.Advantage, 0, SequenceDiceRoller(7, 15), id = 1, questId = "q-uno")

        assertEquals(listOf(DieRoll(Die.D20, value = 15, discarded = 7)), result.dice)
        assertEquals(15, result.total)
        assertEquals(RollMode.Advantage, result.mode)
    }

    @Test
    fun modifierIsAddedToTheTotal() {
        val pool = DicePool().add(Die.D6).add(Die.D6)

        val result = resolveRoll(pool, RollMode.Normal, 3, SequenceDiceRoller(2, 4), id = 2, questId = "q-uno")

        assertEquals(9, result.total)
        assertEquals(3, result.modifier)
    }

    @Test
    fun resolveRollRejectsModifiersOutsideTheRange() {
        val pool = DicePool().add(Die.D6)
        for (modifier in listOf(11, -11)) {
            assertThrows(IllegalArgumentException::class.java) {
                resolveRoll(pool, RollMode.Normal, modifier, SequenceDiceRoller(1), id = 1, questId = "q-uno")
            }
        }
    }

    @Test
    fun statsOfAnEmptyHistoryAreZeroOrNull() {
        assertEquals(RollStats(), computeStats(emptyList(), difficultyClass = 10))
    }

    @Test
    fun statsSummarizeTotalsAgainstTheDifficultyClass() {
        // La tirada más reciente va primero.
        val history = listOf(
            fakeRoll(id = 4, total = 12),
            fakeRoll(id = 3, total = 7),
            fakeRoll(id = 2, total = 9),
            fakeRoll(id = 1, total = 16)
        )

        val stats = computeStats(history, difficultyClass = 10)

        assertEquals(4, stats.rollCount)
        assertEquals(11.0, stats.average!!, 1e-9)
        assertEquals(16, stats.best)
        assertEquals(7, stats.worst)
        assertEquals(mapOf(7 to 1, 9 to 1, 12 to 1, 16 to 1), stats.distribution)
        assertEquals(50, stats.successRatePct)
    }

    @Test
    fun statsCountStreaksAndTotalsEqualToTheDifficultyClassAsSuccess() {
        val history = listOf(
            fakeRoll(id = 5, total = 5),
            fakeRoll(id = 4, total = 10),
            fakeRoll(id = 3, total = 11, d20 = 20),
            fakeRoll(id = 2, total = 18, d20 = 1),
            fakeRoll(id = 1, total = 6)
        )

        val stats = computeStats(history, difficultyClass = 10)

        assertEquals(3, stats.longestStreak)
        assertEquals(0, stats.currentStreak)
        assertEquals(60, stats.successRatePct)
        assertEquals(1, stats.criticalCount)
        assertEquals(1, stats.fumbleCount)
    }

    @Test
    fun arenaRollsWithTheSelectedMode() = runTest {
        val viewModel = DiceArenaViewModel(
            "q-uno",
            InMemoryQuestRepository(TEST_QUESTS),
            SequenceDiceRoller(4, 11),
            InMemoryRollHistoryRepository()
        )
        viewModel.onEvent(DiceArenaEvent.SetMode(RollMode.Advantage))
        viewModel.onEvent(DiceArenaEvent.AddDie(Die.D20))

        viewModel.onEvent(DiceArenaEvent.Roll)
        advanceUntilIdle()

        assertEquals(RollMode.Advantage, viewModel.uiState.value.mode)
        assertEquals(11, viewModel.uiState.value.lastRoll?.total)
        assertEquals(RollMode.Advantage, viewModel.uiState.value.lastRoll?.mode)
    }

    @Test
    fun statsViewModelPublishesTheStatsOfItsQuest() = runTest {
        val history = InMemoryRollHistoryRepository()
        history.record(fakeRoll(id = history.nextId(), total = 16, questId = "q-dos"))
        val viewModel = StatsViewModel("q-dos", InMemoryQuestRepository(TEST_QUESTS), history)

        viewModel.uiState.test {
            val first = awaitItemMatching { it is StatsUiState.Ready } as StatsUiState.Ready
            assertEquals("Cueva de Cristal", first.quest.title)
            assertEquals(1, first.stats.rollCount)
            assertEquals(100, first.stats.successRatePct)

            history.record(fakeRoll(id = history.nextId(), total = 4, questId = "q-dos"))
            val updated = awaitItemMatching { it is StatsUiState.Ready && it.stats.rollCount == 2 }
            assertEquals(50, (updated as StatsUiState.Ready).stats.successRatePct)
        }
    }

    @Test
    fun modifierChangesAccumulateInTheArenaState() {
        val viewModel = DiceArenaViewModel(
            "q-uno",
            InMemoryQuestRepository(TEST_QUESTS),
            SequenceDiceRoller(),
            InMemoryRollHistoryRepository()
        )

        viewModel.onEvent(DiceArenaEvent.ChangeModifier(2))
        viewModel.onEvent(DiceArenaEvent.ChangeModifier(3))

        assertEquals(5, viewModel.uiState.value.modifier)
    }

    @Test
    fun koinBuildsTheStatsViewModelForTheRequestedQuest() = runTest {
        val koin = koinApplication { modules(appModule) }.koin

        val viewModel = koin.get<StatsViewModel> { parametersOf("q-torre") }

        viewModel.uiState.test {
            val ready = awaitItemMatching { it is StatsUiState.Ready } as StatsUiState.Ready
            assertEquals("Torre del Quetzal", ready.quest.title)
        }
    }
}
