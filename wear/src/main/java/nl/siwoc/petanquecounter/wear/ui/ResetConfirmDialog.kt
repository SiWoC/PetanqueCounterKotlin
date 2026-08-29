package nl.siwoc.petanquecounter.wear.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material3.AlertDialog
import androidx.wear.compose.material3.AlertDialogDefaults
import androidx.wear.compose.material3.Text
import nl.siwoc.petanquecounter.R

/**
 * Confirm before wiping both totals. Yes/No (Oui/Non) sit on Wear's confirm
 * and dismiss slots so the dialog matches the round two-button pattern.
 *
 * @param visible When true, the dialog is shown (kept composed for dismiss animation).
 * @param onConfirm Runs [nl.siwoc.petanquecounter.wear.ScoreViewModel.reset].
 * @param onDismiss Closes without changing scores.
 */
@Composable
fun ResetConfirmDialog(
    visible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.reset_title)) },
        text = { Text(stringResource(R.string.reset_message)) },
        confirmButton = {
            AlertDialogDefaults.ConfirmButton(onClick = onConfirm) {
                Text(stringResource(R.string.action_yes))
            }
        },
        dismissButton = {
            AlertDialogDefaults.DismissButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_no))
            }
        },
    )
}
