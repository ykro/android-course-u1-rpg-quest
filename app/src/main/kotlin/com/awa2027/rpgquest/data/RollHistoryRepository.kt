package com.awa2027.rpgquest.data

import com.awa2027.rpgquest.domain.RollResult
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Fuente única de verdad de las tiradas de todas las misiones, compartida por la
 * arena y las estadísticas. [history] tiene la tirada más reciente primero.
 */
interface RollHistoryRepository {
    val history: StateFlow<List<RollResult>>

    fun nextId(): Long

    fun record(result: RollResult)
}

class InMemoryRollHistoryRepository : RollHistoryRepository {
    private val _history = MutableStateFlow<List<RollResult>>(emptyList())
    override val history: StateFlow<List<RollResult>> = _history.asStateFlow()
    private val ids = AtomicLong(0)

    override fun nextId(): Long = ids.incrementAndGet()

    override fun record(result: RollResult) {
        _history.update { current -> listOf(result) + current }
    }
}
