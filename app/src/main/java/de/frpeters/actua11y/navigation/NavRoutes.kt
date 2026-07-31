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

// WHY: only non-topic routes live here. Topic routes are derived from TopicRegistry
// (Topic.route) so there is exactly one place that declares them.
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Category : Screen("category/{categoryName}") {
        fun route(category: TopicCategory) = "category/${category.name}"
    }
}
