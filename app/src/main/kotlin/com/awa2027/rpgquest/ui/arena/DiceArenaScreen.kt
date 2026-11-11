package com.awa2027.rpgquest.ui.arena

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.awa2027.rpgquest.data.SEED_QUESTS
import com.awa2027.rpgquest.domain.DicePool
import com.awa2027.rpgquest.domain.Die
import com.awa2027.rpgquest.domain.DieRoll
import com.awa2027.rpgquest.domain.RollMode
import com.awa2027.rpgquest.domain.RollResult
import com.awa2027.rpgquest.domain.isCritical
import com.awa2027.rpgquest.domain.isFumble
import com.awa2027.rpgquest.ui.theme.RpgQuestTheme

@Composable
fun DiceArenaScreen(viewModel: DiceArenaViewModel, onOpenStats: () -> Unit, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DiceArenaContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onOpenStats = onOpenStats,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceArenaContent(
    uiState: DiceArenaUiState,
    onEvent: (DiceArenaEvent) -> Unit,
    onOpenStats: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(uiState.quest?.title ?: "Arena de dados") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Volver") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { QuestSummary(uiState) }
            item { PoolSelector(pool = uiState.pool, onEvent = onEvent) }
            item { RulesSelector(mode = uiState.mode, modifier = uiState.modifier, onEvent = onEvent) }
            item { RollButton(uiState = uiState, onRoll = { onEvent(DiceArenaEvent.Roll) }) }
            uiState.lastRoll?.let { roll -> item { LastRollCard(roll) } }
            item {
                OutlinedButton(onClick = onOpenStats, modifier = Modifier.fillMaxWidth()) {
                    Text("Ver estadísticas de la misión")
                }
            }
            item { Text("Historial de esta misión", style = MaterialTheme.typography.titleMedium) }
            items(items = uiState.history, key = { roll -> roll.id }) { roll -> HistoryRow(roll) }
        }
    }
}

@Composable
private fun QuestSummary(uiState: DiceArenaUiState) {
    val quest = uiState.quest
    val text = if (quest == null) {
        "Misión no encontrada"
    } else {
        "${quest.region} · ${quest.difficulty.label} · CD ${quest.difficultyClass}"
    }
    Text(text = text, style = MaterialTheme.typography.bodyLarge)
}

@Composable
private fun PoolSelector(pool: DicePool, onEvent: (DiceArenaEvent) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = pool.notation().ifEmpty { "Sin dados todavía" },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text("${pool.size}/${DicePool.MAX_DICE}", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = { onEvent(DiceArenaEvent.ClearPool) }) { Text("Vaciar") }
            }
            Die.entries.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    pair.forEach { die ->
                        DieCounter(
                            die = die,
                            count = pool.countOf(die),
                            onAdd = { onEvent(DiceArenaEvent.AddDie(die)) },
                            onRemove = { onEvent(DiceArenaEvent.RemoveDie(die)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DieCounter(die: Die, count: Int, onAdd: () -> Unit, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(die.label, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
        TextButton(onClick = onRemove) { Text("-") }
        Text("$count", textAlign = TextAlign.Center, modifier = Modifier.width(20.dp))
        TextButton(onClick = onAdd) { Text("+") }
    }
}

private val MODES = listOf(
    RollMode.Normal to "Normal",
    RollMode.Advantage to "Ventaja",
    RollMode.Disadvantage to "Desventaja"
)

@Composable
private fun RulesSelector(mode: RollMode, modifier: Int, onEvent: (DiceArenaEvent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Modo de los d20", style = MaterialTheme.typography.titleSmall)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            MODES.forEachIndexed { index, (option, label) ->
                SegmentedButton(
                    selected = mode == option,
                    onClick = { onEvent(DiceArenaEvent.SetMode(option)) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = MODES.size),
                    icon = {}
                ) { Text(label) }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Modificador", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
            TextButton(onClick = { onEvent(DiceArenaEvent.ChangeModifier(-1)) }) { Text("-") }
            Text(signed(modifier), textAlign = TextAlign.Center, modifier = Modifier.width(36.dp))
            TextButton(onClick = { onEvent(DiceArenaEvent.ChangeModifier(+1)) }) { Text("+") }
        }
    }
}

@Composable
private fun RollButton(uiState: DiceArenaUiState, onRoll: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onRoll, enabled = uiState.canRoll, modifier = Modifier.fillMaxWidth()) {
            val label = when {
                uiState.isRolling -> "Lanzando..."
                uiState.pool.isEmpty -> "Elige al menos un dado"
                uiState.modifier == 0 -> "Lanzar ${uiState.pool.notation()}"
                else -> "Lanzar ${uiState.pool.notation()} ${signed(uiState.modifier)}"
            }
            Text(label)
        }
        if (uiState.isRolling) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun LastRollCard(roll: RollResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Total ${roll.total}", style = MaterialTheme.typography.headlineMedium)
            Text(roll.dice.joinToString("   ") { describe(it) }, style = MaterialTheme.typography.bodyMedium)
            if (roll.modifier != 0) Text("Modificador ${signed(roll.modifier)}")
            if (roll.isCritical) Text("Crítico", fontWeight = FontWeight.Bold)
            if (roll.isFumble) Text("Pifia", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HistoryRow(roll: RollResult) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("Tirada ${roll.id}", modifier = Modifier.weight(1f))
        Text(roll.dice.joinToString(" ") { "${it.value}" }, modifier = Modifier.weight(1f))
        Text("Total ${roll.total}", fontWeight = FontWeight.Bold)
    }
}

private fun describe(roll: DieRoll): String {
    val discarded = roll.discarded?.let { " (descartado $it)" }.orEmpty()
    return "${roll.die.label} ${roll.value}$discarded"
}

private fun signed(value: Int): String = if (value > 0) "+$value" else "$value"

@Preview(showBackground = true)
@Composable
private fun DiceArenaPreview() {
    val roll = RollResult(
        id = 3,
        questId = "q-forja",
        dice = listOf(DieRoll(Die.D6, 4), DieRoll(Die.D20, 17, discarded = 6)),
        mode = RollMode.Advantage,
        modifier = 2,
        total = 23
    )
    RpgQuestTheme {
        DiceArenaContent(
            uiState = DiceArenaUiState(
                quest = SEED_QUESTS[2],
                pool = DicePool(mapOf(Die.D6 to 1, Die.D20 to 1)),
                mode = RollMode.Advantage,
                modifier = 2,
                lastRoll = roll,
                history = listOf(roll)
            ),
            onEvent = {},
            onOpenStats = {},
            onBack = {}
        )
    }
}
