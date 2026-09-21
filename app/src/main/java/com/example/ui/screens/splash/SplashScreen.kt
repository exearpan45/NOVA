package com.example.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
  onSplashFinished: () -> Unit,
  modifier: Modifier = Modifier
) {
  val logoScale = remember { Animatable(0.6f) }
  val logoAlpha = remember { Animatable(0f) }
  val glowAlpha = remember { Animatable(0.2f) }
  var textVisible by remember { mutableStateOf(false) }
  var progressVisible by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    // Animate the logo appearance
    launch {
      logoScale.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
      )
    }
    launch {
      logoAlpha.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 600, easing = LinearEasing)
      )
    }
    launch {
      glowAlpha.animateTo(
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
          animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse
        )
      )
    }

    // Reveal title and subtitle
    delay(350)
    textVisible = true

    // Reveal progress line
    delay(200)
    progressVisible = true

    // Keep on screen for a pleasant duration then transition to app
    delay(1200)
    onSplashFinished()
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .testTag("nova_splash_screen")
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF0B0F19),
            Color(0xFF111827),
            Color(0xFF0F172A),
            Color(0xFF0A0E1A)
          )
        )
      )
      .statusBarsPadding()
      .navigationBarsPadding()
      .pointerInput(Unit) {
        detectTapGestures {
          onSplashFinished()
        }
      },
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(horizontal = 32.dp)
    ) {
      // Glow + App Logo Container
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(160.dp)
      ) {
        // Radiant ambient glow behind the logo
        Box(
          modifier = Modifier
            .size(140.dp)
            .scale(logoScale.value * 1.15f)
            .alpha(glowAlpha.value * 0.45f)
            .background(
              Brush.radialGradient(
                colors = listOf(
                  Color(0xFF6366F1),
                  Color(0xFF8B5CF6),
                  Color(0xFF06B6D4),
                  Color.Transparent
                )
              ),
              shape = CircleShape
            )
        )

        // The actual App Logo
        Image(
          painter = painterResource(id = R.drawable.ic_nova_logo),
          contentDescription = "Nova Logo",
          modifier = Modifier
            .size(110.dp)
            .scale(logoScale.value)
            .alpha(logoAlpha.value)
            .testTag("splash_nova_logo")
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Animated Brand Name & Tagline
      AnimatedVisibility(
        visible = textVisible,
        enter = fadeIn(tween(500)) + slideInVertically(
          animationSpec = tween(500),
          initialOffsetY = { it / 2 }
        )
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "NOVA",
            style = MaterialTheme.typography.displaySmall.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = 6.sp,
              fontSize = 32.sp
            ),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("splash_app_title")
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Elevate Your Focus & Workflow",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.Normal,
              letterSpacing = 0.5.sp
            ),
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("splash_app_tagline")
          )
        }
      }

      Spacer(modifier = Modifier.height(48.dp))

      // Subtle loading progress indicator
      AnimatedVisibility(
        visible = progressVisible,
        enter = fadeIn(tween(400))
      ) {
        LinearProgressIndicator(
          modifier = Modifier
            .width(120.dp)
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp))
            .testTag("splash_progress_bar"),
          color = Color(0xFF6366F1),
          trackColor = Color(0xFF1E293B)
        )
      }
    }

    // Bottom subtle brand label
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 32.dp)
    ) {
      Text(
        text = "TASK • FOCUS • NOTES",
        style = MaterialTheme.typography.labelSmall.copy(
          letterSpacing = 2.5.sp,
          fontWeight = FontWeight.SemiBold
        ),
        color = Color(0xFF475569)
      )
    }
  }
}
