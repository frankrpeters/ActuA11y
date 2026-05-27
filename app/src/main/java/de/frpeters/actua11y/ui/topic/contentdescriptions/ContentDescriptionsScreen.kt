package de.frpeters.actua11y.ui.topic.contentdescriptions

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.BuildConfig
import de.frpeters.actua11y.R

/**
 * Template for all topic screens. Each topic screen follows this structure:
 * 1. TopAppBar with back navigation and optional debug action
 * 2. Scrollable column: plain-language intro → demo sections → collapsible developer note
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentDescriptionsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val paneTitleStr = stringResource(R.string.content_descriptions_pane_title)
    var developerNoteExpanded by remember { mutableStateOf(false) }

    Scaffold(
        // WHY: paneTitle announces the screen name to TalkBack on arrival so users
        // know they have navigated to a new screen without any visual cue.
        modifier = Modifier.semantics { paneTitle = paneTitleStr },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.content_descriptions_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            // WHY: "back" alone is ambiguous; this clarifies the action's destination.
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                },
                actions = {
                    // WHY: the bad-version screen exists only for developers. Hiding the entry
                    // point in release builds ensures learners never encounter broken patterns.
                    if (BuildConfig.DEBUG) {
                        TextButton(onClick = {
                            Toast.makeText(
                                context,
                                context.getString(R.string.bad_version_toast),
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                            Text(stringResource(R.string.bad_version_button))
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.content_descriptions_intro),
                style = MaterialTheme.typography.bodyLarge
            )

            HorizontalDivider()

            // ── Meaningful image ──────────────────────────────────────────────────────
            Text(
                text = stringResource(R.string.meaningful_image_section_label),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                text = stringResource(R.string.meaningful_image_instruction),
                style = MaterialTheme.typography.bodyMedium
            )
            Image(
                painter = painterResource(R.drawable.ic_demo_smartphone),
                // GOOD: describes the image so TalkBack users understand what is shown.
                contentDescription = stringResource(R.string.meaningful_image_desc),
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.CenterHorizontally)
            )

            HorizontalDivider()

            // ── Icon-only button ──────────────────────────────────────────────────────
            Text(
                text = stringResource(R.string.icon_button_section_label),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                text = stringResource(R.string.icon_button_instruction),
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(
                onClick = { /* demonstration only */ },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info),
                    // GOOD: without this, TalkBack has no way to tell the user what the button does.
                    contentDescription = stringResource(R.string.icon_button_desc)
                )
            }

            HorizontalDivider()

            // ── Decorative image ──────────────────────────────────────────────────────
            Text(
                text = stringResource(R.string.decorative_image_section_label),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                text = stringResource(R.string.decorative_image_instruction),
                style = MaterialTheme.typography.bodyMedium
            )
            Image(
                painter = painterResource(R.drawable.ic_demo_wave),
                // GOOD: null removes this node from the accessibility tree entirely.
                // Sighted users see the decoration; TalkBack users are not interrupted by it.
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

            HorizontalDivider()

            // ── Developer note (collapsible) ──────────────────────────────────────────
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { developerNoteExpanded = !developerNoteExpanded }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.developer_note_label),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(text = if (developerNoteExpanded) "▲" else "▼")
                    }
                    AnimatedVisibility(visible = developerNoteExpanded) {
                        Text(
                            text = stringResource(R.string.developer_note_body),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
