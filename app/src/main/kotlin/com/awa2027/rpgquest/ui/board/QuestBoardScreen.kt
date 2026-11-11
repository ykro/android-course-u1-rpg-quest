package com.awa2027.rpgquest.ui.board

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.awa2027.rpgquest.data.SEED_QUESTS
import com.awa2027.rpgquest.domain.Quest
import com.awa2027.rpgquest.domain.QuestDifficulty
import com.awa2027.rpgquest.ui.theme.RpgQuestTheme

/** Con estado: obtiene el estado del ViewModel y delega el dibujo a [QuestBoardContent]. */
@Composable
fun QuestBoardScreen(viewModel: QuestBoardViewModel, onOpenArena: (String) -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    QuestBoardContent(uiState = uiState, onEvent = viewModel::onEvent, onOpenArena = onOpenArena)
}

/** Sin estado (State Hoisting): recibe el estado y emite eventos. Se puede previsualizar. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestBoardContent(
    uiState: QuestBoardUiState,
    onEvent: (QuestBoardEvent) -> Unit,
    onOpenArena: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Tablero de misiones") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                DifficultyFilter(
                    selected = uiState.filter,
                    onSelect = { onEvent(QuestBoardEvent.FilterBy(it)) }
                )
            }
            items(items = uiState.visibleQuests, key = { quest -> quest.id }) { quest ->
                QuestCard(quest = quest, onOpenArena = { onOpenArena(quest.id) })
            }
        }
    }
}

@Composable
private fun DifficultyFilter(selected: QuestDifficulty?, onSelect: (QuestDifficulty?) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(selected = selected == null, onClick = { onSelect(null) }, label = { Text("Todas") })
        QuestDifficulty.entries.forEach { difficulty ->
            FilterChip(
                selected = selected == difficulty,
                onClick = { onSelect(difficulty) },
                label = { Text(difficulty.label) }
            )
        }
    }
}

@Composable
private fun QuestCard(quest: Quest, onOpenArena: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = quest.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "${quest.region} · ${quest.difficulty.label}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Clase de dificultad ${quest.difficultyClass}", style = MaterialTheme.typography.bodyMedium)
            Button(onClick = onOpenArena, modifier = Modifier.padding(top = 8.dp)) {
                Text("Ir a la arena")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuestBoardPreview() {
    RpgQuestTheme {
        QuestBoardContent(
            uiState = QuestBoardUiState(quests = SEED_QUESTS),
            onEvent = {},
            onOpenArena = {}
        )
    }
}
