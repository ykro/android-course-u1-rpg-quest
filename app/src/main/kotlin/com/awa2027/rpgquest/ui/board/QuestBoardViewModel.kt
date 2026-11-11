package com.awa2027.rpgquest.ui.board

import androidx.lifecycle.ViewModel
import com.awa2027.rpgquest.data.QuestRepository
import com.awa2027.rpgquest.domain.Quest
import com.awa2027.rpgquest.domain.QuestDifficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class QuestBoardUiState(val quests: List<Quest> = emptyList(), val filter: QuestDifficulty? = null) {
    /** Estado derivado: se calcula a partir de quests y filter, nunca se guarda aparte. */
    val visibleQuests: List<Quest>
        get() = if (filter == null) quests else quests.filter { it.difficulty == filter }
}

sealed interface QuestBoardEvent {
    data class FilterBy(val difficulty: QuestDifficulty?) : QuestBoardEvent
}

/** Demo: el estado baja como StateFlow y los eventos suben por onEvent (UDF). */
class QuestBoardViewModel(repository: QuestRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(QuestBoardUiState(quests = repository.allQuests()))
    val uiState: StateFlow<QuestBoardUiState> = _uiState.asStateFlow()

    fun onEvent(event: QuestBoardEvent) {
        when (event) {
            is QuestBoardEvent.FilterBy -> _uiState.update { it.copy(filter = event.difficulty) }
        }
    }
}
