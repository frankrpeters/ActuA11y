package de.frpeters.actua11y

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.frpeters.actua11y.navigation.ActuA11yNavHost
import de.frpeters.actua11y.ui.theme.ActuA11yTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ActuA11yTheme {
                ActuA11yNavHost()
            }
        }
    }
}
