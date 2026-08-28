package nl.siwoc.petanquecounter.app.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.siwoc.petanquecounter.R
import nl.siwoc.petanquecounter.app.ui.theme.Navy
import nl.siwoc.petanquecounter.app.ui.theme.PetanqueCounterTheme

/** Dim behind the About card so the board stays visible but not tappable. */
private val OverlayScrim = Color.Black.copy(alpha = 0.45f)

/**
 * About / WallpaperWare / privacy on the score screen (no extra window).
 * Back, Close, or a tap on the scrim dismisses.
 *
 * @param onDismiss Closes the overlay.
 */
@Composable
fun AboutOverlay(onDismiss: () -> Unit) {
    BackHandler(onBack = onDismiss)
    val context = LocalContext.current
    val email = stringResource(R.string.about_email)
    val samplesUrl = stringResource(R.string.wallpaper_ware_samples_url)
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
                .fillMaxWidth()
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
                modifier = Modifier
                    .padding(20.dp)
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.about_title),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.wallpaper_ware_text),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        textDecoration = TextDecoration.Underline,
                    ),
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                        try {
                            context.startActivity(intent)
                        } catch (_: ActivityNotFoundException) {
                            // Address stays on screen to copy if no mail app is present.
                        }
                    },
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.wallpaper_ware_samples),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        textDecoration = TextDecoration.Underline,
                    ),
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(samplesUrl))
                        try {
                            context.startActivity(intent)
                        } catch (_: ActivityNotFoundException) {
                            // Link text stays visible if no browser is present.
                        }
                    },
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.privacy_policy),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.action_close))
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740, locale="fr")
@Preview(showBackground = true, widthDp = 360, heightDp = 740, locale="en")
@Composable
private fun AboutOverlayPreview() {
    PetanqueCounterTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(Navy),
        ) {
            AboutOverlay(onDismiss = {})
        }
    }
}
