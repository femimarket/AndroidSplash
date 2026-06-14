package studio.femi.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import market.femi.Splash
import studio.femi.demo.ui.theme.SplashTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashTheme {
                Splash(
                    onContinue = {
                        // Handle continue action here when testing
                    }
                )
            }
        }
    }
}