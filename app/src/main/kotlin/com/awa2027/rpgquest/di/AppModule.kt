package com.awa2027.rpgquest.di

import com.awa2027.rpgquest.data.DiceRoller
import com.awa2027.rpgquest.data.InMemoryQuestRepository
import com.awa2027.rpgquest.data.QuestRepository
import com.awa2027.rpgquest.data.RandomDiceRoller
import com.awa2027.rpgquest.ui.board.QuestBoardViewModel
import com.awa2027.rpgquest.ui.stats.StatsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Las dependencias se declaran por interfaz. El questId de las pantallas llega con
 * parametersOf desde la ruta.
 * Taller, paso 6: agrega el historial de tiradas y el ViewModel de la arena.
 */
val appModule = module {
    single<QuestRepository> { InMemoryQuestRepository() }
    single<DiceRoller> { RandomDiceRoller() }
    viewModelOf(::QuestBoardViewModel)
    viewModelOf(::StatsViewModel)
}
