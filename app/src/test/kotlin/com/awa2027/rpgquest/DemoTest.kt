package com.awa2027.rpgquest

import app.cash.turbine.test
import com.awa2027.rpgquest.data.InMemoryQuestRepository
import com.awa2027.rpgquest.data.RandomDiceRoller
import com.awa2027.rpgquest.data.SEED_QUESTS
import com.awa2027.rpgquest.domain.Die
import com.awa2027.rpgquest.domain.QuestDifficulty
import com.awa2027.rpgquest.ui.board.QuestBoardEvent
import com.awa2027.rpgquest.ui.board.QuestBoardViewModel
import kotlin.random.Random
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Demo de la unidad: ya pasa en el starter. */
class DemoTest {
    @Test
    fun questBoardFiltersByDifficultyWithoutLosingQuests() = runTest {
        val viewModel = QuestBoardViewModel(InMemoryQuestRepository(SEED_QUESTS))

        viewModel.uiState.test {
            assertEquals(5, awaitItem().visibleQuests.size)

            viewModel.onEvent(QuestBoardEvent.FilterBy(QuestDifficulty.MYTHIC))
            val filtered = awaitItem()
            assertEquals(listOf("q-trono"), filtered.visibleQuests.map { it.id })
            assertEquals("El filtro no borra misiones", 5, filtered.quests.size)

            viewModel.onEvent(QuestBoardEvent.FilterBy(null))
            assertEquals(5, awaitItem().visibleQuests.size)
        }
    }

    @Test
    fun randomDiceRollerStaysWithinFacesAndReachesBothEnds() {
        val roller = RandomDiceRoller(Random(seed = 2027))
        for (die in Die.entries) {
            val values = List(2_000) { roller.roll(die.sides) }
            assertTrue("${die.label} fuera de rango", values.all { it in 1..die.sides })
            assertEquals("${die.label} nunca sacó 1", 1, values.min())
            assertEquals("${die.label} nunca sacó ${die.sides}", die.sides, values.max())
        }
    }
}
