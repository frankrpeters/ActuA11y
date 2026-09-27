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

package de.frpeters.actua11y

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import de.frpeters.actua11y.ui.AppScaffold
import de.frpeters.actua11y.ui.theme.ActuA11yTheme

// WHY: FragmentActivity rather than ComponentActivity because androidx.biometric's BiometricPrompt
// (Topic 41, Accessible Authentication) must be constructed with a FragmentActivity. It is a
// subclass of ComponentActivity, so setContent and edge-to-edge work unchanged.
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ActuA11yTheme {
                AppScaffold()
            }
        }
    }
}
