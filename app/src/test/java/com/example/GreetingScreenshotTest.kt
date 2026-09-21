package com.example

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.NovaTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      NovaTheme {
        EmptyStateView(
          icon = Icons.Default.CheckCircle,
          title = "Welcome to Nova",
          description = "Capture what needs to get done and stay on track."
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }

  @Test
  fun splash_screen_renders_successfully() {
    composeTestRule.setContent {
      NovaTheme {
        com.example.ui.screens.splash.SplashScreen(
          onSplashFinished = {}
        )
      }
    }

    composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("splash_nova_logo"), useUnmergedTree = true).assertExists()
    composeTestRule.mainClock.advanceTimeBy(1000)
    composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("splash_app_title"), useUnmergedTree = true).assertExists()
  }
}

