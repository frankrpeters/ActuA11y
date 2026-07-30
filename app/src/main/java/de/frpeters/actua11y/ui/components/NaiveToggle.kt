package de.frpeters.actua11y.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R

/**
 * The Naive/Better toggle. Lives once in [de.frpeters.actua11y.ui.AppScaffold]'s app bar and
 * controls which implementation of the current topic is shown.
 *
 * This is one of the more important reference implementations in the project: it is on every
 * screen and is the first control most users encounter, so its own accessibility has to be
 * right (requirements §4.4).
 */
@Composable
fun NaiveToggle(
    showNaive: Boolean,
    onToggle: (Boolean) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    val stateDescriptionText = stringResource(
        if (showNaive) R.string.naive_toggle_state_naive else R.string.naive_toggle_state_better
    )

    // TODO(verify): confirm on-device with TalkBack that focus remains on this control when
    // showNaive changes and the topic content below is swapped out from under it, rather than
    // jumping to the top of the new content. Record the Compose BOM version and API level tested.
    LaunchedEffect(showNaive) {
        focusRequester.requestFocus()
    }

    Row(
        modifier = modifier
            .heightIn(min = 48.dp) // WHY: WCAG 2.5.8 minimum touch target size.
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .then(
                // WHY: Modifier.toggleable draws no focus indicator of its own. A visible ring
                // is required at ≥3:1 contrast against the surrounding surface in both themes
                // (WCAG 2.2 SC 2.4.11); MaterialTheme.colorScheme.primary is chosen for exactly
                // that contrast in both the light and dark schemes.
                if (isFocused) {
                    Modifier.border(
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        RoundedCornerShape(8.dp),
                    )
                } else {
                    Modifier
                }
            )
            // WHY: label + switch are one control, not two. toggleable() with role = Switch
            // makes the whole row the single focusable/clickable target; mergeDescendants below
            // folds the child Text into that one semantics node so TalkBack announces it once.
            .toggleable(
                value = showNaive,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onToggle,
            )
            .semantics(mergeDescendants = true) {
                stateDescription = stateDescriptionText
                // WHY: semantics { disabled() } is required in addition to the `enabled`
                // parameter above — the parameter alone makes TalkBack skip the node silently,
                // while disabled() announces it as "dimmed"/disabled (requirements §4.5, topic 14).
                if (!enabled) disabled()
            }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = stringResource(R.string.naive_toggle_label))
        Switch(
            checked = showNaive,
            // WHY: onCheckedChange = null makes the Switch itself non-interactive and
            // non-focusable. Without this, the Switch stays independently reachable and a
            // TalkBack user meets the same control twice — once as the row, once as the switch.
            onCheckedChange = null,
            enabled = enabled,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun NaiveToggleEnabledPreview() {
    MaterialTheme {
        NaiveToggle(showNaive = false, onToggle = {}, enabled = true)
    }
}

@Preview(name = "Disabled, light 100%", showBackground = true)
@Composable
private fun NaiveToggleDisabledPreview() {
    MaterialTheme {
        NaiveToggle(showNaive = false, onToggle = {}, enabled = false)
    }
}
