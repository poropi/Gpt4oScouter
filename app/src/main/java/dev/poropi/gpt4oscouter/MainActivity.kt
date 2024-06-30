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

    /**
     * This function solves the FizzBuzz problem.
     *
     * @param max The maximum number to which the FizzBuzz problem is solved.
     * The function iterates from 1 to the maximum number (inclusive) and for each number:
     * - If the number is divisible by both 3 and 5, it prints "FizzBuzz".
     * - If the number is divisible by 3 (and not by 5), it prints "Fizz".
     * - If the number is divisible by 5 (and not by 3), it prints "Buzz".
     * - If the number is not divisible by either 3 or 5, it prints the number itself.
     */
    fun fizzBuzz(max: Int) {
        for (i in 1..max) {
            when {
                i % 15 == 0 -> println("FizzBuzz")
                i % 3 == 0 -> println("Fizz")
                i % 5 == 0 -> println("Buzz")
                else -> println(i)
            }
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