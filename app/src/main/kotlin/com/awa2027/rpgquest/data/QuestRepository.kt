package com.awa2027.rpgquest.data

import com.awa2027.rpgquest.domain.Quest
import com.awa2027.rpgquest.domain.QuestDifficulty

interface QuestRepository {
    fun allQuests(): List<Quest>

    fun findById(id: String): Quest?
}

class InMemoryQuestRepository(private val quests: List<Quest> = SEED_QUESTS) : QuestRepository {
    override fun allQuests(): List<Quest> = quests

    override fun findById(id: String): Quest? = quests.firstOrNull { it.id == id }
}

val SEED_QUESTS: List<Quest> = listOf(
    Quest("q-cripta", "Cripta del Eco", "Valle Gris", QuestDifficulty.NOVICE, difficultyClass = 8),
    Quest("q-bosque", "Bosque de Niebla", "Altos de Niebla", QuestDifficulty.NOVICE, difficultyClass = 10),
    Quest("q-forja", "Forja del Titán", "Cumbre Roja", QuestDifficulty.VETERAN, difficultyClass = 14),
    Quest("q-torre", "Torre del Quetzal", "Selva Esmeralda", QuestDifficulty.VETERAN, difficultyClass = 16),
    Quest("q-trono", "Trono Abisal", "Fosa Profunda", QuestDifficulty.MYTHIC, difficultyClass = 22)
)
