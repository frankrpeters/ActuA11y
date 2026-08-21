/*
 * Copyright 2026 Frank R. Peters
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.frpeters.actua11y.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.topic.compositecontrols.CompositeControlsTopic
import de.frpeters.actua11y.ui.topic.contentdescriptions.ContentDescriptionsTopic
import de.frpeters.actua11y.ui.topic.focusafternavigation.FocusAfterNavigationTopic
import de.frpeters.actua11y.ui.topic.genuinetables.GenuineTablesTopic
import de.frpeters.actua11y.ui.topic.headings.HeadingsTopic
import de.frpeters.actua11y.ui.topic.inputasbutton.InputAsButtonTopic
import de.frpeters.actua11y.ui.topic.lazylistpitfalls.LazyListPitfallsTopic
import de.frpeters.actua11y.ui.topic.minimumtouchtarget.MinimumTouchTargetTopic
import de.frpeters.actua11y.ui.topic.onedimensionalcollections.OneDimensionalCollectionsTopic
import de.frpeters.actua11y.ui.topic.panetitles.PaneTitlesTopic
import de.frpeters.actua11y.ui.topic.pinshowhide.PinShowHideTopic
import de.frpeters.actua11y.ui.topic.selectableiconlists.SelectableIconListsTopic
import de.frpeters.actua11y.ui.topic.traversalgroups.TraversalGroupsTopic
import de.frpeters.actua11y.ui.topic.traversalindex.TraversalIndexTopic

// WHY: single source of truth for every topic (requirements §4.7). Navigation, the home
// screen, category listings, app-bar titles, and the toggle's enabled state are all derived
// from this list — adding a topic is "create the package, append one entry here."

enum class TopicCategory(@param:StringRes val titleRes: Int) {
    STRUCTURE(R.string.category_structure),
    COLLECTIONS(R.string.category_collections),
    CONTROLS(R.string.category_controls),
    TEXT(R.string.category_text),
    FORMS(R.string.category_forms),
    VISUAL(R.string.category_visual),
    INTEROP(R.string.category_interop),
}

data class Topic(
    val id: String,
    val category: TopicCategory,
    @param:StringRes val titleRes: Int,
    val supportsNaive: Boolean,
    val content: @Composable (showNaive: Boolean, modifier: Modifier) -> Unit,
) {
    val route: String get() = "topic/$id"
}

object TopicRegistry {
    val all: List<Topic> = listOf(
        Topic(
            id = "traversal_groups",
            category = TopicCategory.STRUCTURE,
            titleRes = R.string.traversal_groups_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                TraversalGroupsTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "traversal_index",
            category = TopicCategory.STRUCTURE,
            titleRes = R.string.traversal_index_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                TraversalIndexTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "headings",
            category = TopicCategory.STRUCTURE,
            titleRes = R.string.headings_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                HeadingsTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "pane_titles",
            category = TopicCategory.STRUCTURE,
            titleRes = R.string.pane_titles_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                PaneTitlesTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "focus_after_navigation",
            category = TopicCategory.STRUCTURE,
            titleRes = R.string.focus_after_navigation_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                FocusAfterNavigationTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "pin_show_hide",
            category = TopicCategory.FORMS,
            titleRes = R.string.pin_show_hide_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                PinShowHideTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "one_dimensional_collections",
            category = TopicCategory.COLLECTIONS,
            titleRes = R.string.one_dimensional_collections_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                OneDimensionalCollectionsTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "genuine_tables",
            category = TopicCategory.COLLECTIONS,
            titleRes = R.string.genuine_tables_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                GenuineTablesTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "lazy_list_pitfalls",
            category = TopicCategory.COLLECTIONS,
            titleRes = R.string.lazy_list_pitfalls_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                LazyListPitfallsTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "input_as_button",
            category = TopicCategory.CONTROLS,
            titleRes = R.string.input_as_button_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                InputAsButtonTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "composite_controls",
            category = TopicCategory.CONTROLS,
            titleRes = R.string.composite_controls_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                CompositeControlsTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "selectable_icon_lists",
            category = TopicCategory.CONTROLS,
            titleRes = R.string.selectable_icon_lists_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                SelectableIconListsTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "minimum_touch_target",
            category = TopicCategory.CONTROLS,
            titleRes = R.string.minimum_touch_target_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                MinimumTouchTargetTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "content_descriptions",
            category = TopicCategory.TEXT,
            titleRes = R.string.content_descriptions_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                ContentDescriptionsTopic(showNaive, modifier)
            },
        ),
    )

    fun byRoute(route: String?): Topic? = all.firstOrNull { it.route == route }
    fun byCategory(category: TopicCategory): List<Topic> = all.filter { it.category == category }
}
