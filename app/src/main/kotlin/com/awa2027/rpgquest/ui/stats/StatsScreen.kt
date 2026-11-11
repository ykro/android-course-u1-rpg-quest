package com.awa2027.rpgquest.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.awa2027.rpgquest.data.SEED_QUESTS
import com.awa2027.rpgquest.domain.RollStats
import com.awa2027.rpgquest.ui.theme.RpgQuestTheme
import java.util.Locale

@Composable
fun StatsScreen(viewModel: StatsViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    StatsContent(uiState = uiState, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsContent(uiState: StatsUiState, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Volver") } }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            // when sobre una sealed interface, sin else: si se agrega un estado, esto deja de compilar.
            when (uiState) {
                StatsUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                is StatsUiState.QuestNotFound -> Text(
                    text = "No existe la misión ${uiState.questId}",
                    modifier = Modifier.padding(16.dp)
                )

                is StatsUiState.Ready -> ReadyStats(uiState)
            }
        }
    }
}

@Composable
private fun ReadyStats(state: StatsUiState.Ready) {
    val stats = state.stats
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text(state.quest.title, style = MaterialTheme.typography.titleLarge)
            Text("Éxito con total de ${state.quest.difficultyClass} o más", style = MaterialTheme.typography.bodyMedium)
        }
        if (stats.rollCount == 0) {
            item { Text("Todavía no hay tiradas para esta misión. Lanza dados en la arena.") }
            return@LazyColumn
        }
        items(items = statCards(stats).chunked(2)) { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { (label, value) -> StatCard(label, value, Modifier.weight(1f)) }
            }
        }
        item { Text("Distribución de totales", style = MaterialTheme.typography.titleMedium) }
        val maxCount = stats.distribution.values.max()
        items(items = stats.distribution.entries.toList(), key = { entry -> entry.key }) { (total, count) ->
            DistributionBar(total = total, count = count, fraction = count.toFloat() / maxCount)
        }
    }
}

private fun statCards(stats: RollStats): List<Pair<String, String>> = listOf(
    "Tiradas" to "${stats.rollCount}",
    "Media" to String.format(Locale.forLanguageTag("es-GT"), "%.1f", stats.average ?: 0.0),
    "Mejor y peor" to "${stats.best} y ${stats.worst}",
    "Éxito" to "${stats.successRatePct} %",
    "Racha actual" to "${stats.currentStreak}",
    "Racha más larga" to "${stats.longestStreak}",
    "Críticos" to "${stats.criticalCount}",
    "Pifias" to "${stats.fumbleCount}"
)

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DistributionBar(total: Int, count: Int, fraction: Float) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text("$total", modifier = Modifier.width(40.dp))
        Box(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(16.dp)
                    .background(MaterialTheme.colorScheme.secondary)
            )
        }
        Text("$count", modifier = Modifier.padding(start = 8.dp).width(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsPreview() {
    RpgQuestTheme {
        StatsContent(
            uiState = StatsUiState.Ready(
                quest = SEED_QUESTS[2],
                stats = RollStats(
                    rollCount = 5,
                    average = 13.4,
                    best = 21,
                    worst = 6,
                    distribution = mapOf(6 to 1, 12 to 2, 21 to 2),
                    successRatePct = 40,
                    longestStreak = 2,
                    currentStreak = 1,
                    criticalCount = 1,
                    fumbleCount = 0
                )
            ),
            onBack = {}
        )
    }
}
