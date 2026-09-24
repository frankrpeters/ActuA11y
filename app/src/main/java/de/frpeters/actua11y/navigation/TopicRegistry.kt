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
import de.frpeters.actua11y.ui.topic.accessibleauthentication.AccessibleAuthenticationTopic
import de.frpeters.actua11y.ui.topic.announceforaccessibility.AnnounceForAccessibilityTopic
import de.frpeters.actua11y.ui.topic.autofillhints.AutofillHintsTopic
import de.frpeters.actua11y.ui.topic.colourcontrast.ColourContrastTopic
import de.frpeters.actua11y.ui.topic.compositecontrols.CompositeControlsTopic
import de.frpeters.actua11y.ui.topic.concatenateddescriptions.ConcatenatedDescriptionsTopic
import de.frpeters.actua11y.ui.topic.consistentidentification.ConsistentIdentificationTopic
import de.frpeters.actua11y.ui.topic.contentdescriptions.ContentDescriptionsTopic
import de.frpeters.actua11y.ui.topic.customactions.CustomActionsTopic
import de.frpeters.actua11y.ui.topic.darkmode.DarkModeTopic
import de.frpeters.actua11y.ui.topic.disabledelements.DisabledElementsTopic
import de.frpeters.actua11y.ui.topic.draggingmovements.DraggingMovementsTopic
import de.frpeters.actua11y.ui.topic.errorsemantics.ErrorSemanticsTopic
import de.frpeters.actua11y.ui.topic.focusafternavigation.FocusAfterNavigationTopic
import de.frpeters.actua11y.ui.topic.focusnotobscured.FocusNotObscuredTopic
import de.frpeters.actua11y.ui.topic.fontscale.FontScaleTopic
import de.frpeters.actua11y.ui.topic.genuinetables.GenuineTablesTopic
import de.frpeters.actua11y.ui.topic.gridsthatarenottables.GridsThatAreNotTablesTopic
import de.frpeters.actua11y.ui.topic.headings.HeadingsTopic
import de.frpeters.actua11y.ui.topic.imeactions.ImeActionsTopic
import de.frpeters.actua11y.ui.topic.inputasbutton.InputAsButtonTopic
import de.frpeters.actua11y.ui.topic.keyboardfocusindicator.KeyboardFocusIndicatorTopic
import de.frpeters.actua11y.ui.topic.keyboardonlyoperation.KeyboardOnlyOperationTopic
import de.frpeters.actua11y.ui.topic.lazylistpitfalls.LazyListPitfallsTopic
import de.frpeters.actua11y.ui.topic.liveregions.LiveRegionsTopic
import de.frpeters.actua11y.ui.topic.minimumtouchtarget.MinimumTouchTargetTopic
import de.frpeters.actua11y.ui.topic.modalsurfaces.ModalSurfacesTopic
import de.frpeters.actua11y.ui.topic.onedimensionalcollections.OneDimensionalCollectionsTopic
import de.frpeters.actua11y.ui.topic.panetitles.PaneTitlesTopic
import de.frpeters.actua11y.ui.topic.pinshowhide.PinShowHideTopic
import de.frpeters.actua11y.ui.topic.progressandsliders.ProgressAndSlidersTopic
import de.frpeters.actua11y.ui.topic.reducedmotion.ReducedMotionTopic
import de.frpeters.actua11y.ui.topic.redundantentry.RedundantEntryTopic
import de.frpeters.actua11y.ui.topic.selectablecopyabletext.SelectableCopyableTextTopic
import de.frpeters.actua11y.ui.topic.selectableiconlists.SelectableIconListsTopic
import de.frpeters.actua11y.ui.topic.statevscontentdescription.StateVsContentDescriptionTopic
import de.frpeters.actua11y.ui.topic.switchplatformvscustom.SwitchPlatformVsCustomTopic
import de.frpeters.actua11y.ui.topic.textfieldlabelling.TextFieldLabellingTopic
import de.frpeters.actua11y.ui.topic.traversalgroups.TraversalGroupsTopic
import de.frpeters.actua11y.ui.topic.traversalindex.TraversalIndexTopic
import de.frpeters.actua11y.ui.topic.validationanderrorfocus.ValidationAndErrorFocusTopic
import de.frpeters.actua11y.ui.topic.verbatimstrings.VerbatimStringsTopic
import de.frpeters.actua11y.ui.topic.voidconsistenthelp.VoidConsistentHelpTopic
import de.frpeters.actua11y.ui.topic.voidparsing.VoidParsingTopic
import de.frpeters.actua11y.ui.topic.webviewscope.WebViewScopeTopic
import de.frpeters.actua11y.ui.topic.wrappedview.WrappedViewTopic

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
    // WHY: the three fields below record where a topic's requirement comes from (requirements
    // §4.7). They are set on the WCAG 2.2 / EN 301 549 V4.1.1 topics (§3.8) and left null
    // everywhere else. Nothing in the UI reads them yet — a "binding from" badge is a logged
    // candidate enhancement, not part of this schema.
    val enClause: String? = null,       // e.g. "11.2.5.8"; null where no single clause applies
    val wcagVersion: String? = null,    // "2.1" | "2.2"; null where not WCAG-derived
    val bindingFrom: String? = null,    // e.g. "EN 301 549 V4.1.1"; null if binding today
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
            id = "text_field_labelling",
            category = TopicCategory.FORMS,
            titleRes = R.string.text_field_labelling_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                TextFieldLabellingTopic(showNaive, modifier)
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
            id = "validation_and_error_focus",
            category = TopicCategory.FORMS,
            titleRes = R.string.validation_and_error_focus_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                ValidationAndErrorFocusTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "autofill_hints",
            category = TopicCategory.FORMS,
            titleRes = R.string.autofill_hints_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                AutofillHintsTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "ime_actions",
            category = TopicCategory.FORMS,
            titleRes = R.string.ime_actions_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                ImeActionsTopic(showNaive, modifier)
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
            id = "grids_that_are_not_tables",
            category = TopicCategory.COLLECTIONS,
            titleRes = R.string.grids_that_are_not_tables_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                GridsThatAreNotTablesTopic(showNaive, modifier)
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
            id = "disabled_elements",
            category = TopicCategory.CONTROLS,
            titleRes = R.string.disabled_elements_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                DisabledElementsTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "switch_platform_vs_custom",
            category = TopicCategory.CONTROLS,
            titleRes = R.string.switch_platform_vs_custom_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                SwitchPlatformVsCustomTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "progress_and_sliders",
            category = TopicCategory.CONTROLS,
            titleRes = R.string.progress_and_sliders_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                ProgressAndSlidersTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "custom_actions",
            category = TopicCategory.CONTROLS,
            titleRes = R.string.custom_actions_title,
            // WHY: first topic to actually exercise requirements §4.5 — no functionally
            // equivalent naive version exists. AppScaffold/NaiveToggle already handle
            // supportsNaive = false correctly (disabled, semantics { disabled() } applied).
            supportsNaive = false,
            content = { showNaive, modifier ->
                CustomActionsTopic(showNaive, modifier)
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
        Topic(
            id = "state_vs_content_description",
            category = TopicCategory.TEXT,
            titleRes = R.string.state_vs_content_description_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                StateVsContentDescriptionTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "live_regions",
            category = TopicCategory.TEXT,
            titleRes = R.string.live_regions_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                LiveRegionsTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "announce_for_accessibility",
            category = TopicCategory.TEXT,
            titleRes = R.string.announce_for_accessibility_title,
            supportsNaive = false,
            content = { showNaive, modifier ->
                AnnounceForAccessibilityTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "verbatim_strings",
            category = TopicCategory.TEXT,
            titleRes = R.string.verbatim_strings_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                VerbatimStringsTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "selectable_copyable_text",
            category = TopicCategory.TEXT,
            titleRes = R.string.selectable_copyable_text_title,
            supportsNaive = false,
            content = { showNaive, modifier ->
                SelectableCopyableTextTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "error_semantics",
            category = TopicCategory.TEXT,
            titleRes = R.string.error_semantics_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                ErrorSemanticsTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "keyboard_focus_indicator",
            category = TopicCategory.VISUAL,
            titleRes = R.string.keyboard_focus_indicator_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                KeyboardFocusIndicatorTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "keyboard_only_operation",
            category = TopicCategory.VISUAL,
            titleRes = R.string.keyboard_only_operation_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                KeyboardOnlyOperationTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "font_scale",
            category = TopicCategory.VISUAL,
            titleRes = R.string.font_scale_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                FontScaleTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "colour_contrast",
            category = TopicCategory.VISUAL,
            titleRes = R.string.colour_contrast_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                ColourContrastTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "dark_mode",
            category = TopicCategory.VISUAL,
            titleRes = R.string.dark_mode_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                DarkModeTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "reduced_motion",
            category = TopicCategory.VISUAL,
            titleRes = R.string.reduced_motion_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                ReducedMotionTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "modal_surfaces",
            category = TopicCategory.VISUAL,
            titleRes = R.string.modal_surfaces_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                ModalSurfacesTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "consistent_identification",
            category = TopicCategory.TEXT,
            titleRes = R.string.consistent_identification_title,
            supportsNaive = true,
            // WHY: SC 3.2.4 predates WCAG 2.2, but EN 301 549 V3.2.1 marked this software clause
            // void — V4.1.1 is what makes it binding for native apps.
            enClause = "11.3.2.4",
            wcagVersion = "2.1",
            bindingFrom = "EN 301 549 V4.1.1",
            content = { showNaive, modifier ->
                ConsistentIdentificationTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "void_consistent_help",
            category = TopicCategory.STRUCTURE,
            titleRes = R.string.void_consistent_help_title,
            // WHY: content-only note (requirements §4.5). No clause or binding date to cite —
            // the point of the topic is that there is no binding software requirement.
            supportsNaive = false,
            wcagVersion = "2.2",
            content = { showNaive, modifier ->
                VoidConsistentHelpTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "void_parsing",
            category = TopicCategory.STRUCTURE,
            titleRes = R.string.void_parsing_title,
            // WHY: content-only note (requirements §4.5). wcagVersion records the version that
            // removed SC 4.1.1, not one that introduced it.
            supportsNaive = false,
            wcagVersion = "2.2",
            content = { showNaive, modifier ->
                VoidParsingTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "concatenated_descriptions",
            category = TopicCategory.TEXT,
            titleRes = R.string.concatenated_descriptions_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                ConcatenatedDescriptionsTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "redundant_entry",
            category = TopicCategory.FORMS,
            titleRes = R.string.redundant_entry_title,
            supportsNaive = true,
            enClause = "11.3.3.7",
            wcagVersion = "2.2",
            bindingFrom = "EN 301 549 V4.1.1",
            content = { showNaive, modifier ->
                RedundantEntryTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "accessible_authentication",
            category = TopicCategory.FORMS,
            titleRes = R.string.accessible_authentication_title,
            supportsNaive = true,
            enClause = "11.3.3.8",
            wcagVersion = "2.2",
            bindingFrom = "EN 301 549 V4.1.1",
            content = { showNaive, modifier ->
                AccessibleAuthenticationTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "dragging_movements",
            category = TopicCategory.CONTROLS,
            titleRes = R.string.dragging_movements_title,
            supportsNaive = true,
            enClause = "11.2.5.7",
            wcagVersion = "2.2",
            bindingFrom = "EN 301 549 V4.1.1",
            content = { showNaive, modifier ->
                DraggingMovementsTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "focus_not_obscured",
            category = TopicCategory.VISUAL,
            titleRes = R.string.focus_not_obscured_title,
            supportsNaive = true,
            enClause = "11.2.4.11",
            wcagVersion = "2.2",
            bindingFrom = "EN 301 549 V4.1.1",
            content = { showNaive, modifier ->
                FocusNotObscuredTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "wrapped_view",
            category = TopicCategory.INTEROP,
            titleRes = R.string.wrapped_view_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                WrappedViewTopic(showNaive, modifier)
            },
        ),
        Topic(
            id = "webview_scope",
            category = TopicCategory.INTEROP,
            titleRes = R.string.webview_scope_title,
            supportsNaive = true,
            content = { showNaive, modifier ->
                WebViewScopeTopic(showNaive, modifier)
            },
        ),
    )

    fun byRoute(route: String?): Topic? = all.firstOrNull { it.route == route }
    fun byCategory(category: TopicCategory): List<Topic> = all.filter { it.category == category }
}
