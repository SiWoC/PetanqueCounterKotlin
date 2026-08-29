package nl.siwoc.petanquecounter.wear.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.FilledIconButton
import androidx.wear.compose.material3.FilledTonalIconButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import nl.siwoc.petanquecounter.R
import nl.siwoc.petanquecounter.core.R as CoreR
import nl.siwoc.petanquecounter.core.domain.GameState
import nl.siwoc.petanquecounter.core.domain.MeneRequest
import nl.siwoc.petanquecounter.core.domain.ScoreSnapshot
import nl.siwoc.petanquecounter.core.domain.Team
import nl.siwoc.petanquecounter.wear.ScoreViewModel
import nl.siwoc.petanquecounter.wear.ui.theme.FlagBlue
import nl.siwoc.petanquecounter.wear.ui.theme.FlagRed
import nl.siwoc.petanquecounter.wear.ui.theme.Navy
import nl.siwoc.petanquecounter.wear.ui.theme.PetanqueCounterTheme
import nl.siwoc.petanquecounter.wear.ui.theme.WinGold

/**
 * Wires [ScoreViewModel] to [ScoreScreen], the mène overlay, and reset confirm.
 */
@Composable
fun ScoreRoute(
    viewModel: ScoreViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var mene by remember { mutableStateOf<MeneRequest?>(null) }
    var showReset by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        ScoreScreen(
            state = state,
            onPlus = { team -> mene = MeneRequest(team, add = true) },
            onMinus = { team -> mene = MeneRequest(team, add = false) },
            onUndo = viewModel::undo,
            onReset = { showReset = true },
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
    }
    ResetConfirmDialog(
        visible = showReset,
        onConfirm = {
            viewModel.reset()
            showReset = false
        },
        onDismiss = { showReset = false },
    )
}

/**
 * Round match board: Nous | Eux, ± per side, Undo and Reset.
 *
 * @param state Current match.
 * @param onPlus Opens an add-mène picker for that side.
 * @param onMinus Opens a subtract-mène picker for that side.
 * @param onUndo Restores the last snapshot.
 * @param onReset Asks to wipe both totals.
 */
@Composable
fun ScoreScreen(
    state: GameState,
    onPlus: (Team) -> Unit,
    onMinus: (Team) -> Unit,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        val size = minOf(maxWidth, maxHeight)
        val meneButtonSize = minOf(size * 0.19f, IconButtonDefaults.SmallButtonSize)
        val actionButtonSize = minOf(size * 0.16f, IconButtonDefaults.ExtraSmallButtonSize)
        val actionIconSize = IconButtonDefaults.iconSizeFor(actionButtonSize)
        val scoreSp = (size * 0.20f).value.sp
        val labelSp = (size * 0.06f).value.sp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 28.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ScoreColumn(
                    teamName = stringResource(R.string.team_nous),
                    score = state.nousScore,
                    winReached = state.isWinReached(Team.Nous),
                    scoreSp = scoreSp,
                    labelSp = labelSp,
                )
                ScoreColumn(
                    teamName = stringResource(R.string.team_eux),
                    score = state.euxScore,
                    winReached = state.isWinReached(Team.Eux),
                    scoreSp = scoreSp,
                    labelSp = labelSp,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TeamMenePair(
                    team = Team.Nous,
                    minusEnabled = state.score(Team.Nous) > 0,
                    buttonSize = meneButtonSize,
                    onMinus = { onMinus(Team.Nous) },
                    onPlus = { onPlus(Team.Nous) },
                )
                Spacer(Modifier.width(8.dp))
                TeamMenePair(
                    team = Team.Eux,
                    minusEnabled = state.score(Team.Eux) > 0,
                    buttonSize = meneButtonSize,
                    onMinus = { onMinus(Team.Eux) },
                    onPlus = { onPlus(Team.Eux) },
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilledTonalIconButton(
                    onClick = onUndo,
                    enabled = state.canUndo(),
                    modifier = Modifier.size(actionButtonSize),
                ) {
                    Icon(
                        painter = painterResource(CoreR.drawable.ic_history),
                        contentDescription = stringResource(R.string.action_undo),
                        modifier = Modifier.size(actionIconSize),
                    )
                }
                FilledIconButton(
                    onClick = onReset,
                    modifier = Modifier.size(actionButtonSize),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = FlagRed,
                        contentColor = Color.White,
                    ),
                ) {
                    Icon(
                        painter = painterResource(CoreR.drawable.ic_refresh),
                        contentDescription = stringResource(R.string.action_reset),
                        modifier = Modifier.size(actionIconSize),
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreColumn(
    teamName: String,
    score: Int,
    winReached: Boolean,
    scoreSp: TextUnit,
    labelSp: TextUnit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = teamName.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontSize = labelSp,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            fontSize = scoreSp,
            color = if (winReached) WinGold else Color.White,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TeamMenePair(
    team: Team,
    minusEnabled: Boolean,
    buttonSize: Dp,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
) {
    val teamName = stringResource(
        if (team == Team.Nous) R.string.team_nous else R.string.team_eux,
    )
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        MeneKey(
            glyph = "−",
            enabled = minusEnabled,
            description = stringResource(R.string.mene_subtract, teamName),
            buttonSize = buttonSize,
            onClick = onMinus,
        )
        MeneKey(
            glyph = "+",
            enabled = true,
            description = stringResource(R.string.mene_add, teamName),
            buttonSize = buttonSize,
            onClick = onPlus,
        )
    }
}

@Composable
private fun MeneKey(
    glyph: String,
    enabled: Boolean,
    description: String,
    buttonSize: Dp,
    onClick: () -> Unit,
) {
    FilledIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(buttonSize)
            .semantics { contentDescription = description },
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = Navy,
            contentColor = Color.White,
        ),
    ) {
        Text(
            text = glyph,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
private fun ScoreScreenPreview() {
    PetanqueCounterTheme {
        ScoreScreen(
            state = GameState(
                nousScore = 4,
                euxScore = 12,
                history = listOf(ScoreSnapshot(0, 0)),
            ),
            onPlus = {},
            onMinus = {},
            onUndo = {},
            onReset = {},
        )
    }
}

@WearPreviewLargeRound
@Composable
private fun ScoreScreenWinPreview() {
    PetanqueCounterTheme {
        ScoreScreen(
            state = GameState(nousScore = 13, euxScore = 8),
            onPlus = {},
            onMinus = {},
            onUndo = {},
            onReset = {},
        )
    }
}
