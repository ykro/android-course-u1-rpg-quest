package com.awa2027.rpgquest.ui.arena

import androidx.lifecycle.ViewModel
import com.awa2027.rpgquest.data.DiceRoller
import com.awa2027.rpgquest.data.QuestRepository
import com.awa2027.rpgquest.data.RollHistoryRepository
import com.awa2027.rpgquest.domain.DicePool
import com.awa2027.rpgquest.domain.Die
import com.awa2027.rpgquest.domain.Quest
import com.awa2027.rpgquest.domain.RollMode
import com.awa2027.rpgquest.domain.RollResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DiceArenaUiState(
    val quest: Quest? = null,
    val pool: DicePool = DicePool(),
    val mode: RollMode = RollMode.Normal,
    val modifier: Int = 0,
    val isRolling: Boolean = false,
    val lastRoll: RollResult? = null,
    val history: List<RollResult> = emptyList()
) {
    val canRoll: Boolean get() = !pool.isEmpty && !isRolling
}

sealed interface DiceArenaEvent {
    data class AddDie(val die: Die) : DiceArenaEvent

    data class RemoveDie(val die: Die) : DiceArenaEvent

    data object ClearPool : DiceArenaEvent

    data object Roll : DiceArenaEvent

    data class SetMode(val mode: RollMode) : DiceArenaEvent

    data class ChangeModifier(val delta: Int) : DiceArenaEvent
}

class DiceArenaViewModel(
    private val questId: String,
    questRepository: QuestRepository,
    private val roller: DiceRoller,
    private val historyRepository: RollHistoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DiceArenaUiState(quest = questRepository.findById(questId)))
    val uiState: StateFlow<DiceArenaUiState> = _uiState.asStateFlow()

    // Taller, paso 5: en un bloque init, recolecta historyRepository.history en viewModelScope
    // y guarda en el estado solo las tiradas de esta misión.

    // Reto: SetMode y ChangeModifier hoy no cambian nada. La guía del reto del curso explica qué deben hacer.
    fun onEvent(event: DiceArenaEvent) {
        when (event) {
            is DiceArenaEvent.AddDie -> TODO("Taller, paso 5: actualiza el grupo con pool.add")
            is DiceArenaEvent.RemoveDie -> TODO("Taller, paso 5: actualiza el grupo con pool.remove")
            DiceArenaEvent.ClearPool -> TODO("Taller, paso 5: deja el grupo vacío")
            DiceArenaEvent.Roll -> roll()
            is DiceArenaEvent.SetMode -> Unit
            is DiceArenaEvent.ChangeModifier -> Unit
        }
    }

    private fun roll() {
        TODO("Taller, paso 5: marca isRolling, espera ROLL_ANIMATION_MS con delay y registra la tirada")
    }

    companion object {
        const val ROLL_ANIMATION_MS = 600L
    }
}
