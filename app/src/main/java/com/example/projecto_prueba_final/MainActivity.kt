package com.example.projecto_prueba_final

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.projecto_prueba_final.data.AppDatabase
import com.example.projecto_prueba_final.data.ProductoRepository
import com.example.projecto_prueba_final.ui.AuthViewModel
import com.example.projecto_prueba_final.ui.ProductoViewModel
import com.example.projecto_prueba_final.ui.UserRole
import com.example.projecto_prueba_final.ui.screen.*
import com.example.projecto_prueba_final.ui.theme.projecto_prueba_finalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val productoRepository = ProductoRepository(database.productoDao())
        val productoViewModel: ProductoViewModel by viewModels { 
            object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return ProductoViewModel(productoRepository) as T
                }
            } 
        }

        val authViewModel: AuthViewModel by viewModels {
            object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(database.userDao()) as T
                }
            }
        }

        setContent {
            projecto_prueba_finalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(productoViewModel, authViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(productoVm: ProductoViewModel, authVm: AuthViewModel) {
    val navController = rememberNavController()
    val userRole by authVm.userRole.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                vm = authVm,
                onLoginSuccess = { navController.navigate("welcome") { popUpTo("login") { inclusive = true } } },
                onNavigateToSignUp = { navController.navigate("signup") }
            )
        }
        composable("signup") {
            SignUpScreen(
                vm = authVm,
                onSignUpSuccess = { navController.navigate("login") },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("welcome") {
            WelcomeScreen(
                vm = productoVm,
                onCategorySelected = { category -> navController.navigate("productos/$category") },
                onNavigateToFAQ = { navController.navigate("faq") },
                onLogout = {
                    authVm.logout()
                    navController.navigate("login") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable("faq") {
            FAQScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(
            route = "productos/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "Todas"
            ProductoListScreen(
                vm = productoVm,
                initialCategory = category,
                userRole = userRole,
                onNavigateBack = { navController.popBackStack() },
                onProductClick = { productId -> navController.navigate("producto/$productId") }
            )
        }
        composable(
            route = "producto/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            ProductoDetailScreen(
                vm = productoVm,
                productoId = id,
                userRole = userRole,
                onNavigateBack = { navController.popBackStack() },
                onBuy = { 
                    productoVm.comprar(it) // "comprar" es la misma acción que "eliminar"
                    navController.popBackStack() 
                }
            )
        }
    }
}