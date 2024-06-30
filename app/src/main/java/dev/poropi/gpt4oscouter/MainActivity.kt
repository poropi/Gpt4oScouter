package dev.poropi.gpt4oscouter

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.poropi.gpt4oscouter.screens.main.MainScreen
import dev.poropi.gpt4oscouter.ui.theme.Gpt4oScouterTheme

/**
 * The main activity of the application.
 * This activity is responsible for setting up the edge-to-edge display and displaying the main screen of the application.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppScreen()
        }
    }
}

@Composable
fun MyAppScreen() {
    val navController = rememberNavController()
    Gpt4oScouterTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
            NavHost(
                navController = navController,
                startDestination = Route.MAIN.name, // 初期表示する画面
                modifier = Modifier.padding(padding)
            ) {
                // 画面1
                composable(route = Route.MAIN.name) {
                    MainScreen()
                }
            }
        }
    }
}

enum class Route {
    MAIN
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyAppScreen()
}