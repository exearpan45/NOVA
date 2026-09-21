package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.navigation.NovaAppScaffold
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.theme.NovaTheme
import com.example.viewmodel.NovaViewModel
import com.example.viewmodel.NovaViewModelFactory

class MainActivity : ComponentActivity() {

  private val viewModel: NovaViewModel by viewModels {
    NovaViewModelFactory(applicationContext)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    // Install the official Android SplashScreen
    installSplashScreen()
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val uiState by viewModel.uiState.collectAsStateWithLifecycle()
      var showSplash by rememberSaveable { mutableStateOf(true) }

      NovaTheme(
        themeMode = uiState.userPreferences.themeMode,
        dynamicColor = uiState.userPreferences.dynamicColor
      ) {
        Surface(modifier = Modifier.fillMaxSize()) {
          AnimatedContent(
            targetState = showSplash,
            transitionSpec = {
              fadeIn(animationSpec = tween(400)) togetherWith
                  fadeOut(animationSpec = tween(400))
            },
            label = "splash_transition"
          ) { isSplash ->
            if (isSplash) {
              SplashScreen(
                onSplashFinished = { showSplash = false }
              )
            } else {
              NovaAppScaffold(
                viewModel = viewModel,
                uiState = uiState
              )
            }
          }
        }
      }
    }
  }
}

