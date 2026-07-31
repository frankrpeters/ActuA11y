package de.frpeters.actua11y.ui.topic.traversalindex

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TraversalIndexTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) TraversalIndexNaive(modifier) else TraversalIndexBetter(modifier)
}
