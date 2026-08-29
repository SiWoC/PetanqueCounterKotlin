package nl.siwoc.petanquecounter.wear.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.AnchorType
import androidx.wear.compose.foundation.CurvedLayout
import androidx.wear.compose.foundation.CurvedModifier
import androidx.wear.compose.foundation.padding
import androidx.wear.compose.material3.FilledIconButton
import androidx.wear.compose.material3.FilledTonalIconButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.SwipeToDismissBox
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.curvedText
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import kotlin.math.cos
import kotlin.math.sin
import nl.siwoc.petanquecounter.R
import nl.siwoc.petanquecounter.core.R as CoreR
import nl.siwoc.petanquecounter.core.domain.MeneRequest
import nl.siwoc.petanquecounter.core.domain.Team
import nl.siwoc.petanquecounter.wear.ui.theme.FlagRed
import nl.siwoc.petanquecounter.wear.ui.theme.MeneAddGreen
import nl.siwoc.petanquecounter.wear.ui.theme.PetanqueCounterTheme

/**
 * 1–6 mène picker stacked on the scoreboard. Digits start at 1 o'clock and run
 * clockwise; the hub cancels. Green digits add, red digits subtract; digits that
 * would take the total below 0 are disabled. Swipe-to-dismiss (Wear back) and
 * the hub both call [onDismiss].
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
    SwipeToDismissBox(
        onDismissed = onDismiss,
        modifier = Modifier.fillMaxSize(),
    ) { isBackground ->
        if (!isBackground) {
            MeneOverlayContent(
                request = request,
                currentScore = currentScore,
                onDigit = onDigit,
                onDismiss = onDismiss,
            )
        }
    }
}

@Composable
private fun MeneOverlayContent(
    request: MeneRequest,
    currentScore: Int,
    onDigit: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val teamName = stringResource(
        if (request.team == Team.Nous) R.string.team_nous else R.string.team_eux,
    )
    val title = if (request.add) {
        stringResource(R.string.mene_add_title, teamName)
    } else {
        stringResource(R.string.mene_subtract_title, teamName)
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        val size = minOf(maxWidth, maxHeight)
        val digitSize = size * 0.24f
        val hubSize = size * 0.22f
        val hubIconSize = IconButtonDefaults.iconSizeFor(hubSize)
        val ringRadius = size * 0.29f
        val labelSp = (size * 0.065f).value.sp

        CurvedLayout(
            modifier = Modifier.fillMaxSize(),
            anchor = 270f,
            anchorType = AnchorType.Center,
        ) {
            curvedText(
                text = title,
                modifier = CurvedModifier.padding(
                    outer = 6.dp,
                    inner = 0.dp,
                    before = 0.dp,
                    after = 0.dp,
                ),
                fontSize = labelSp,
                color = Color.White,
            )
        }

        FilledTonalIconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.Center)
                .size(hubSize),
        ) {
            Icon(
                painter = painterResource(CoreR.drawable.ic_undo),
                contentDescription = stringResource(R.string.action_back),
                modifier = Modifier.size(hubIconSize),
            )
        }

        for (digit in 1..6) {
            val angleRad = Math.toRadians((-60.0 + (digit - 1) * 60.0))
            val dx = ringRadius * cos(angleRad).toFloat()
            val dy = ringRadius * sin(angleRad).toFloat()
            FilledIconButton(
                onClick = { onDigit(digit) },
                enabled = request.add || currentScore >= digit,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(dx, dy)
                    .size(digitSize),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (request.add) MeneAddGreen else FlagRed,
                    contentColor = Color.White,
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

@WearPreviewLargeRound
@WearPreviewSmallRound
@Composable
private fun MeneOverlayAddPreview() {
    PetanqueCounterTheme {
        MeneOverlayContent(
            request = MeneRequest(Team.Nous, add = true),
            currentScore = 4,
            onDigit = {},
            onDismiss = {},
        )
    }
}

@WearPreviewLargeRound
@Composable
private fun MeneOverlaySubtractPreview() {
    PetanqueCounterTheme {
        MeneOverlayContent(
            request = MeneRequest(Team.Eux, add = false),
            currentScore = 2,
            onDigit = {},
            onDismiss = {},
        )
    }
}
