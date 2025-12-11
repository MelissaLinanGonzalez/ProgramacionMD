package com.example.multimediacompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.multimediacompose.ui.theme.MultimediaComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = NavRoutes.Menu.route
            ) {
                composable(NavRoutes.Menu.route) { MenuScreen(navController) }
                composable(NavRoutes.Audio.route) { AudioScreen() }
                composable(NavRoutes.Video.route) { VideoScreen() }
                composable(NavRoutes.Camara.route) { CamaraScreen() }
                composable(NavRoutes.Recorder.route) { RecorderScreen() }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    androidx.compose.material3.Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MultimediaComposeTheme {
        Greeting("Android")
    }
}
