package app.wisp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.wisp.core.designsystem.theme.WispTheme
import app.wisp.ui.HomeScreen
import app.wisp.ui.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeSpec by themeViewModel.themeSpec.collectAsStateWithLifecycle()

            WispTheme(themeSpec = themeSpec) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = WispTheme.colors.background
                ) {
                    HomeScreen(
                        currentPreset = themeSpec.preset,
                        onPresetSelected = { preset -> themeViewModel.selectPreset(preset) }
                    )
                }
            }
        }
    }
}
