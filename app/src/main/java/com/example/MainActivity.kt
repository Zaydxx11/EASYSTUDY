package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.MainScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.StudyViewModel

class MainActivity : ComponentActivity() {

    private val studyViewModel: StudyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by studyViewModel.themeMode.collectAsState()
            val themeAccent by studyViewModel.themeAccent.collectAsState()

            MyApplicationTheme(
                themeMode = themeMode,
                themeAccent = themeAccent
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    StudyAppRoot(viewModel = studyViewModel)
                }
            }
        }
    }
}

@Composable
fun StudyAppRoot(viewModel: StudyViewModel) {
    val destination by viewModel.currentDestination.collectAsState()

    AnimatedContent(
        targetState = destination,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "root_navigation_transition"
    ) { dest ->
        when (dest) {
            AppDestination.SPLASH -> {
                SplashScreen(
                    onSplashFinished = {
                        viewModel.onSplashFinished()
                    }
                )
            }
            AppDestination.WELCOME -> {
                WelcomeScreen(
                    onGetStarted = {
                        viewModel.onboardingStep.value = 1
                        viewModel.navigateTo(AppDestination.ONBOARDING)
                    },
                    onLoginOrDemo = {
                        viewModel.completeOnboardingAndEnter()
                    }
                )
            }
            AppDestination.ONBOARDING -> {
                OnboardingScreen(viewModel = viewModel)
            }
            AppDestination.MAIN -> {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
