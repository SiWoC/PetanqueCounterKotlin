package nl.siwoc.petanquecounter.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.siwoc.petanquecounter.R
import nl.siwoc.petanquecounter.app.ScoreViewModel
import nl.siwoc.petanquecounter.app.ui.theme.FlagBlue
import nl.siwoc.petanquecounter.app.ui.theme.FlagRed
import nl.siwoc.petanquecounter.app.ui.theme.PetanqueCounterTheme
import nl.siwoc.petanquecounter.app.ui.theme.Navy
import nl.siwoc.petanquecounter.app.ui.theme.WinGold
import nl.siwoc.petanquecounter.core.R as CoreR
import nl.siwoc.petanquecounter.core.domain.GameState
import nl.siwoc.petanquecounter.core.domain.MeneRequest
import nl.siwoc.petanquecounter.core.domain.PhoneLayout
import nl.siwoc.petanquecounter.core.domain.ScoreSnapshot
import nl.siwoc.petanquecounter.core.domain.Team

/** Rounded square for ± and action keys (not M3's circular icon-button). */
private val AppButtonShape = RoundedCornerShape(16.dp)

/**
 * Wires [ScoreViewModel] to [ScoreScreen], the mène and About overlays, and dialogs.
 *
 * @param onExit Closes the hosting activity.
 */
@Composable
fun ScoreRoute(
    viewModel: ScoreViewModel = viewModel(),
    onExit: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var mene by remember { mutableStateOf<MeneRequest?>(null) }
    var showReset by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        ScoreScreen(
            state = state,
            onPlus = { team -> mene = MeneRequest(team, add = true) },
            onMinus = { team -> mene = MeneRequest(team, add = false) },
            onUndo = viewModel::undo,
            onReset = { showReset = true },
            onCycleLayout = viewModel::cycleLayout,
            onAbout = { showAbout = true },
            onExit = onExit,
        )
        mene?.let { request ->
            MeneOverlay(
                request = request,
                currentScore = state.score(request.team),
                onDigit = { digit ->
                    val delta = if (request.add) digit else -digit
                    viewModel.applyMene(request.team, delta)
                    mene = null
                },
                onDismiss = { mene = null },
            )
        }
        if (showAbout) {
            AboutOverlay(onDismiss = { showAbout = false })
        }
    }
    if (showReset) {
        ResetConfirmDialog(
            onConfirm = {
                viewModel.reset()
                showReset = false
            },
            onDismiss = { showReset = false },
        )
    }
}

/**
 * Match board: navy column. Flag arches from [R.drawable.app_background] fill
 * the score band (the slit is transparent), then ±, then actions.
 *
 * @param state Current match.
 * @param onPlus Opens an add-mène picker for that side.
 * @param onMinus Opens a subtract-mène picker for that side.
 * @param onUndo Restores the last snapshot.
 * @param onReset Asks to wipe both totals.
 * @param onCycleLayout Walks the three one-hand ± placements.
 * @param onAbout Opens privacy / About.
 * @param onExit Closes the activity.
 */
@Composable
fun ScoreScreen(
    state: GameState,
    onPlus: (Team) -> Unit,
    onMinus: (Team) -> Unit,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    onCycleLayout: () -> Unit,
    onAbout: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(Navy)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
                ),
            ),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            Box(Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(R.drawable.app_background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                )
                Scoreboard(
                    state = state,
                    modifier = Modifier
                        .matchParentSize()
                        .padding(horizontal = 12.dp),
                )
                FilledTonalIconButton(
                    onClick = onExit,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(48.dp),
                    shape = AppButtonShape,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = Color.White,
                    ),
                ) {
                    Icon(
                        painter = painterResource(CoreR.drawable.ic_close),
                        contentDescription = stringResource(R.string.action_exit),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 50.dp, end = 16.dp),
                contentAlignment = when (state.layout) {
                    PhoneLayout.ButtonsLeft -> Alignment.TopStart
                    PhoneLayout.ButtonsRight -> Alignment.TopEnd
                    PhoneLayout.ButtonsCenter -> Alignment.TopCenter
                },
            ) {
                MenePad(
                    state = state,
                    onPlus = onPlus,
                    onMinus = onMinus,
                )
            }
        }
        ActionRow(
            canUndo = state.canUndo(),
            onUndo = onUndo,
            onReset = onReset,
            onCycleLayout = onCycleLayout,
            onAbout = onAbout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = 16.dp),
        )
    }
}

@Composable
private fun Scoreboard(state: GameState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ScoreColumn(
            teamName = stringResource(R.string.team_nous),
            score = state.nousScore,
            winReached = state.isWinReached(Team.Nous),
        )
        ScoreColumn(
            teamName = stringResource(R.string.team_eux),
            score = state.euxScore,
            winReached = state.isWinReached(Team.Eux),
        )
    }
}

@Composable
private fun RowScope.ScoreColumn(
    teamName: String,
    score: Int,
    winReached: Boolean,
) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.displayLarge,
            color = if (winReached) WinGold else Color.White,
            textAlign = TextAlign.Center,
        )
        Text(
            text = teamName,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
        )
    }
}

@Composable
private fun MenePad(
    state: GameState,
    onPlus: (Team) -> Unit,
    onMinus: (Team) -> Unit,
) {
    val nous = stringResource(R.string.team_nous)
    val eux = stringResource(R.string.team_eux)
    val nousRow = @Composable {
        TeamMeneRow(
            teamName = nous,
            minusEnabled = state.score(Team.Nous) > 0,
            minusDescription = stringResource(R.string.mene_subtract, nous),
            plusDescription = stringResource(R.string.mene_add, nous),
            onMinus = { onMinus(Team.Nous) },
            onPlus = { onPlus(Team.Nous) },
        )
    }
    val euxRow = @Composable {
        TeamMeneRow(
            teamName = eux,
            minusEnabled = state.score(Team.Eux) > 0,
            minusDescription = stringResource(R.string.mene_subtract, eux),
            plusDescription = stringResource(R.string.mene_add, eux),
            onMinus = { onMinus(Team.Eux) },
            onPlus = { onPlus(Team.Eux) },
        )
    }
    when (state.layout) {
        PhoneLayout.ButtonsCenter -> {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                nousRow()
                euxRow()
            }
        }
        PhoneLayout.ButtonsLeft -> {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                nousRow()
                euxRow()
            }
        }
        PhoneLayout.ButtonsRight -> {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                euxRow()
                nousRow()
            }
        }
    }
}

@Composable
private fun TeamMeneRow(
    teamName: String,
    minusEnabled: Boolean,
    minusDescription: String,
    plusDescription: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MeneKey(
            teamName = teamName,
            glyph = "−",
            enabled = minusEnabled,
            description = minusDescription,
            onClick = onMinus,
        )
        MeneKey(
            teamName = teamName,
            glyph = "+",
            enabled = true,
            description = plusDescription,
            onClick = onPlus,
        )
    }
}

@Composable
private fun MeneKey(
    teamName: String,
    glyph: String,
    enabled: Boolean,
    description: String,
    onClick: () -> Unit,
) {
    FilledIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(72.dp)
            .semantics { contentDescription = description },
        shape = AppButtonShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = FlagBlue,
            contentColor = Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Box(Modifier.fillMaxSize()) {
            Text(
                text = teamName,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 6.dp),
            )
            Text(
                text = glyph,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun ActionRow(
    canUndo: Boolean,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    onCycleLayout: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilledTonalIconButton(
            onClick = onUndo,
            enabled = canUndo,
            modifier = Modifier.size(64.dp),
            shape = AppButtonShape,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = Color.White,
            ),
        ) {
            Icon(
                painter = painterResource(CoreR.drawable.ic_history),
                contentDescription = stringResource(R.string.action_undo),
            )
        }
        FilledIconButton(
            onClick = onReset,
            modifier = Modifier.size(64.dp),
            shape = AppButtonShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = FlagRed,
                contentColor = Color.White,
            ),
        ) {
            Icon(
                painter = painterResource(CoreR.drawable.ic_refresh),
                contentDescription = stringResource(R.string.action_reset),
            )
        }
        FilledTonalIconButton(
            onClick = onCycleLayout,
            modifier = Modifier.size(64.dp),
            shape = AppButtonShape,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = Color.White,
            ),
        ) {
            Icon(
                painter = painterResource(CoreR.drawable.ic_dashboard_customize),
                contentDescription = stringResource(R.string.action_layout),
            )
        }
        FilledIconButton(
            onClick = onAbout,
            modifier = Modifier.size(64.dp),
            shape = AppButtonShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color.White,
            ),
        ) {
            Image(
                painter = painterResource(R.drawable.schnappi),
                contentDescription = stringResource(R.string.action_about),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740, locale = "fr")
@Composable
private fun ScoreScreenRightPreview() {
    PetanqueCounterTheme {
        ScoreScreen(
            state = GameState(
                nousScore = 4,
                euxScore = 2,
                layout = PhoneLayout.ButtonsRight,
                history = listOf(ScoreSnapshot(0, 0)),
            ),
            onPlus = {},
            onMinus = {},
            onUndo = {},
            onReset = {},
            onCycleLayout = {},
            onAbout = {},
            onExit = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun ScoreScreenLeftPreview() {
    PetanqueCounterTheme {
        ScoreScreen(
            state = GameState(nousScore = 13, euxScore = 8, layout = PhoneLayout.ButtonsLeft),
            onPlus = {},
            onMinus = {},
            onUndo = {},
            onReset = {},
            onCycleLayout = {},
            onAbout = {},
            onExit = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun ScoreScreenCenterPreview() {
    PetanqueCounterTheme {
        ScoreScreen(
            state = GameState(
                nousScore = 4,
                euxScore = 2,
                layout = PhoneLayout.ButtonsCenter,
            ),
            onPlus = {},
            onMinus = {},
            onUndo = {},
            onReset = {},
            onCycleLayout = {},
            onAbout = {},
            onExit = {},
        )
    }
}
