package de.frpeters.actua11y.ui.topic.contentdescriptions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ContentDescriptionsTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) ContentDescriptionsNaive(modifier) else ContentDescriptionsBetter(modifier)
}
