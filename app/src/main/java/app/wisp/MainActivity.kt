package app.wisp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import app.wisp.core.designsystem.theme.WispTheme
import app.wisp.ui.HomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WispTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = WispTheme.colors.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
}
