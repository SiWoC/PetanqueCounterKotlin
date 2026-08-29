package nl.siwoc.petanquecounter.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.siwoc.petanquecounter.R
import nl.siwoc.petanquecounter.app.ui.theme.FlagRed
import nl.siwoc.petanquecounter.app.ui.theme.MeneAddGreen
import nl.siwoc.petanquecounter.app.ui.theme.Navy
import nl.siwoc.petanquecounter.app.ui.theme.PetanqueCounterTheme
import nl.siwoc.petanquecounter.core.domain.MeneRequest
import nl.siwoc.petanquecounter.core.domain.Team

/** Dim behind the mène card so the board stays visible but not tappable. */
private val OverlayScrim = Color.Black.copy(alpha = 0.45f)

/**
 * 1–6 mène picker drawn on the score screen (no extra window). Green digits
 * add, red digits subtract; digits that would take the total below 0 are
 * disabled. Back or a tap on the scrim dismisses.
 *
 * @param request Which side and direction this picker is for.
 * @param currentScore That side's total, used to disable illegal subtracts.
 * @param onDigit Chosen size 1..6; the caller applies the signed delta.
 * @param onDismiss Closes without changing the score.
 */
@Composable
fun MeneOverlay(
    request: MeneRequest,
    currentScore: Int,
    onDigit: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    BackHandler(onBack = onDismiss)
    val teamName = stringResource(
        if (request.team == Team.Nous) R.string.team_nous else R.string.team_eux,
    )
    val title = if (request.add) {
        stringResource(R.string.mene_add_title, teamName)
    } else {
        stringResource(R.string.mene_subtract_title, teamName)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OverlayScrim)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MeneDigitRow(
                        digits = listOf(1, 2, 3),
                        add = request.add,
                        currentScore = currentScore,
                        onDigit = onDigit,
                    )
                    MeneDigitRow(
                        digits = listOf(4, 5, 6),
                        add = request.add,
                        currentScore = currentScore,
                        onDigit = onDigit,
                    )
                }
            }
        }
    }
}

@Composable
private fun MeneDigitRow(
    digits: List<Int>,
    add: Boolean,
    currentScore: Int,
    onDigit: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        digits.forEach { digit ->
            FilledTonalButton(
                onClick = { onDigit(digit) },
                enabled = add || currentScore >= digit,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (add) MeneAddGreen else FlagRed,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(
                    text = stringResource(R.string.mene_digit, digit),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun MeneOverlayAddPreview() {
    PetanqueCounterTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(Navy),
        ) {
            MeneOverlay(
                request = MeneRequest(Team.Nous, add = true),
                currentScore = 4,
                onDigit = {},
                onDismiss = {},
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun MeneOverlaySubtractPreview() {
    PetanqueCounterTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(Navy),
        ) {
            MeneOverlay(
                request = MeneRequest(Team.Eux, add = false),
                currentScore = 2,
                onDigit = {},
                onDismiss = {},
            )
        }
    }
}
