package com.awa2027.rpgquest.ui.stats

import androidx.lifecycle.ViewModel
import com.awa2027.rpgquest.data.QuestRepository
import com.awa2027.rpgquest.data.RollHistoryRepository
import com.awa2027.rpgquest.domain.Quest
import com.awa2027.rpgquest.domain.RollStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface StatsUiState {
    data object Loading : StatsUiState

    data class QuestNotFound(val questId: String) : StatsUiState

    data class Ready(val quest: Quest, val stats: RollStats) : StatsUiState
}

/** Reto: estadísticas de una misión, recalculadas cada vez que cambia el historial. */
class StatsViewModel(
    private val questId: String,
    questRepository: QuestRepository,
    historyRepository: RollHistoryRepository
) : ViewModel() {
    // Reto: publica Ready (o QuestNotFound) a partir de historyRepository.history.
    val uiState: StateFlow<StatsUiState> = MutableStateFlow(StatsUiState.Loading)
}
