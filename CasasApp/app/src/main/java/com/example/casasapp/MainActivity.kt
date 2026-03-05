package com.example.casasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.casasapp.ui.pantallas.*
import com.example.casasapp.ui.theme.CasasAppTheme

/**
 * MainActivity - Punto de entrada de la aplicación.
 * Configura la navegación y el tema de la app.
 * 
 * Flujo de navegación: login → registro → inicio → galeria/detalle/formulario
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Habilitar edge-to-edge para una UI más inmersiva
        enableEdgeToEdge()
        
        setContent {
            CasasAppTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = "login" // Ruta inicial: Login
                ) {
                    // Pantalla de Login
                    composable("login") {
                        PantallaLogin(navController)
                    }
                    
                    // Pantalla de Registro
                    composable("registro") {
                        PantallaRegistro(navController)
                    }
                    
                    // Pantalla de inicio (Dashboard)
                    composable("inicio") {
                        PantallaInicio(navController)
                    }
                    
                    // Galería de propiedades
                    composable("galeria") {
                        PantallaGaleria(navController)
                    }
                    
                    // Formulario para nueva propiedad
                    composable("formulario") {
                        PantallaFormulario(navController)
                    }
                    
                    // Detalle de propiedad (con argumento ID)
                    composable(
                        route = "detalle/{id}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id") ?: 0
                        PantallaDetalle(navController, id)
                    }
                }
            }
        }
    }
}