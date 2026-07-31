package de.frpeters.actua11y.ui.topic.traversalgroups

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TraversalGroupsTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) TraversalGroupsNaive(modifier) else TraversalGroupsBetter(modifier)
}
