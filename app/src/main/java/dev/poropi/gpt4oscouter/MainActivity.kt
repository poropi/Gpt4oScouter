package dev.poropi.gpt4oscouter

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
 * アプリケーションのメインアクティビティ。
 *
 * このアクティビティは、エッジツーエッジディスプレイの設定とアプリケーションのメイン画面の表示を担当します。
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

/**
 * MyAppScreenのコンポーザブル関数。
 *
 * このコンポーザブルは、アプリケーションのメイン画面を表示する責任があります。
 * ナビゲーションコントローラーとナビゲーションホストを設定します。
 */
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

/**
 * ルートの列挙型。
 *
 * この列挙型は、アプリケーションの各ルートを定義します。
 */
enum class Route {
    MAIN
}

/**
 * GreetingPreviewのコンポーザブル関数。
 *
 * このコンポーザブルは、プレビュー用のメイン画面を表示します。
 */
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyAppScreen()
}