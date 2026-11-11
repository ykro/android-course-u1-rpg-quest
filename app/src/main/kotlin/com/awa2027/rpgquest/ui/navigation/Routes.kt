package com.awa2027.rpgquest.ui.navigation

import kotlinx.serialization.Serializable

/** Rutas type-safe: el compilador revisa los argumentos de cada destino. */
@Serializable
data object QuestBoardRoute

@Serializable
data class DiceArenaRoute(val questId: String)

@Serializable
data class StatsRoute(val questId: String)
