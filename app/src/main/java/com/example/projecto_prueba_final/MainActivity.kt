package com.example.projecto_prueba_final

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.example.projecto_prueba_final.ui.FaqViewModel
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

        val faqViewModel: FaqViewModel by viewModels {
            object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return FaqViewModel(database.faqDao()) as T
                }
            }
        }

        setContent {
            projecto_prueba_finalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(productoViewModel, authViewModel, faqViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(productoVm: ProductoViewModel, authVm: AuthViewModel, faqVm: FaqViewModel) {
    val navController = rememberNavController()
    val userRole by authVm.userRole.collectAsStateWithLifecycle()

    NavHost(
        navController = navController, 
        startDestination = "login",
    ) {
        val slideIn = slideInHorizontally(animationSpec = tween(400)) { fullWidth -> fullWidth }
        val slideOut = slideOutHorizontally(animationSpec = tween(400)) { fullWidth -> -fullWidth }
        val popSlideIn = slideInHorizontally(animationSpec = tween(400)) { fullWidth -> -fullWidth }
        val popSlideOut = slideOutHorizontally(animationSpec = tween(400)) { fullWidth -> fullWidth }

        composable(
            "login",
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(400)) }
        ) {
            LoginScreen(
                vm = authVm,
                onLoginSuccess = { navController.navigate("welcome") { popUpTo("login") { inclusive = true } } },
                onNavigateToSignUp = { navController.navigate("signup") }
            )
        }
        composable(
            "signup",
            enterTransition = { slideIn }, exitTransition = { slideOut },
            popEnterTransition = { popSlideIn }, popExitTransition = { popSlideOut }
        ) {
            SignUpScreen(
                vm = authVm,
                onSignUpSuccess = { navController.navigate("login") },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(
            "welcome",
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(400)) }
        ) {
            WelcomeScreen(
                vm = productoVm,
                userRole = userRole,
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
        composable(
            "faq",
            enterTransition = { slideIn }, exitTransition = { slideOut },
            popEnterTransition = { popSlideIn }, popExitTransition = { popSlideOut }
        ) {
            FAQScreen(
                vm = faqVm,
                userRole = userRole,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            "thankyou",
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(400)) }
        ) {
            ThankYouScreen(onTimeout = { navController.popBackStack() })
        }
        composable(
            route = "productos/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType }),
            enterTransition = { slideIn }, exitTransition = { slideOut },
            popEnterTransition = { popSlideIn }, popExitTransition = { popSlideOut }
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "Todas"
            ProductoListScreen(
                vm = productoVm,
                initialCategory = category,
                userRole = userRole,
                onNavigateBack = { navController.popBackStack() },
                onProductClick = { productId -> navController.navigate("producto/$productId") },
                onAddProduct = { 
                    productoVm.editar(null, if (category != "Todas") category else null)
                    navController.navigate("admin_form")
                }
            )
        }
        composable(
            route = "producto/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
            enterTransition = { slideIn }, exitTransition = { slideOut },
            popEnterTransition = { popSlideIn }, popExitTransition = { popSlideOut }
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            ProductoDetailScreen(
                vm = productoVm,
                productoId = id,
                userRole = userRole,
                onNavigateBack = { navController.popBackStack() },
                onBuy = { 
                    productoVm.comprar(it)
                    navController.navigate("thankyou")
                },
                onEdit = { 
                    productoVm.editar(it)
                    navController.navigate("admin_form")
                }
            )
        }
        composable(
            "admin_form",
            enterTransition = { slideIn }, exitTransition = { slideOut },
            popEnterTransition = { popSlideIn }, popExitTransition = { popSlideOut }
        ) {
            AdminFormScreen(
                vm = productoVm,
                onNavigateBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }
    }
}