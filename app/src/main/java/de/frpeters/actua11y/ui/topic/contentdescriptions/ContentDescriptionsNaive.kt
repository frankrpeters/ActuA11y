@file:Suppress("ContentDescription")   // NAIVE: intentional — see the developer note in this file

package de.frpeters.actua11y.ui.topic.contentdescriptions

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Naive implementation. Same three elements, same layout, same behaviour for a sighted touch
 * user as [ContentDescriptionsBetter] — the accessibility work was simply never done.
 */
@Composable
fun ContentDescriptionsNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.content_descriptions_pane_title)

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.content_descriptions_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.content_descriptions_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Meaningful image ──────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.meaningful_image_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // NAIVE: no contentDescription. Compose's Image only adds a semantics node when
        // contentDescription is non-null, so this image carries none at all — TalkBack has
        // nothing to focus and silently skips it, indistinguishable from a genuinely
        // decorative image. The information it conveyed is simply gone, with no trace in the
        // accessibility tree that anything is missing.
        Image(
            painter = painterResource(R.drawable.ic_demo_smartphone),
            contentDescription = null,
            modifier = Modifier
                .size(96.dp)
                .align(Alignment.CenterHorizontally),
        )

        HorizontalDivider()

        // ── Icon-only button ──────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.icon_button_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // NAIVE: no contentDescription on the Icon. Unlike the bare Image above, IconButton
        // is still a real clickable semantics node (it carries an onClick and Role.Button),
        // so TalkBack does focus it — just with no name for what it does.
        // TODO(verify): confirm the exact TalkBack announcement for an unlabeled IconButton
        // on a real device; reasoning about wording without TalkBack running is not verification.
        IconButton(
            onClick = { /* demonstration only */ },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = null,
            )
        }

        HorizontalDivider()

        // ── Decorative image ──────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.decorative_image_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // Unchanged from the Better version: a decorative image is correctly null in both
        // implementations, so there is nothing naive about this element specifically — it is
        // here only to keep the three-element structure identical between the two files.
        Image(
            painter = painterResource(R.drawable.ic_demo_wave),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
        )

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.content_descriptions_developer_note_naive))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun ContentDescriptionsNaivePreview() {
    MaterialTheme {
        ContentDescriptionsNaive()
    }
}
