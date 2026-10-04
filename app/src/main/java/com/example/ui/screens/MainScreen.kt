package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.StudySubScreen
import com.example.ui.viewmodel.StudyViewModel

@Composable
fun MainScreen(viewModel: StudyViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val activeSubScreen by viewModel.activeSubScreen.collectAsState()

    // Handle Android system back button for sub-screens
    if (activeSubScreen != StudySubScreen.NONE) {
        BackHandler {
            viewModel.closeSubScreen()
        }
    } else if (currentTab != MainTab.HOME) {
        BackHandler {
            viewModel.selectTab(MainTab.HOME)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (activeSubScreen == StudySubScreen.NONE) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentTab == MainTab.HOME,
                        onClick = { viewModel.selectTab(MainTab.HOME) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontWeight = if (currentTab == MainTab.HOME) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("nav_tab_home")
                    )

                    NavigationBarItem(
                        selected = currentTab == MainTab.STUDY,
                        onClick = { viewModel.selectTab(MainTab.STUDY) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainTab.STUDY) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                                contentDescription = "Study"
                            )
                        },
                        label = { Text("Study", fontWeight = if (currentTab == MainTab.STUDY) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("nav_tab_study")
                    )

                    NavigationBarItem(
                        selected = currentTab == MainTab.PROGRESS,
                        onClick = { viewModel.selectTab(MainTab.PROGRESS) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainTab.PROGRESS) Icons.Filled.QueryStats else Icons.Outlined.QueryStats,
                                contentDescription = "Progress"
                            )
                        },
                        label = { Text("Progress", fontWeight = if (currentTab == MainTab.PROGRESS) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("nav_tab_progress")
                    )

                    NavigationBarItem(
                        selected = currentTab == MainTab.SCHEDULE,
                        onClick = { viewModel.selectTab(MainTab.SCHEDULE) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainTab.SCHEDULE) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                                contentDescription = "Schedule"
                            )
                        },
                        label = { Text("Schedule", fontWeight = if (currentTab == MainTab.SCHEDULE) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("nav_tab_schedule")
                    )

                    NavigationBarItem(
                        selected = currentTab == MainTab.PROFILE,
                        onClick = { viewModel.selectTab(MainTab.PROFILE) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainTab.PROFILE) Icons.Filled.School else Icons.Outlined.AutoAwesome,
                                contentDescription = "Profile & Settings"
                            )
                        },
                        label = { Text("Profile", fontWeight = if (currentTab == MainTab.PROFILE) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("nav_tab_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeSubScreen) {
                StudySubScreen.VIDEO_STUDY -> VideoStudyScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                StudySubScreen.DOUBT_SOLVER -> DoubtSolverScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                StudySubScreen.QUIZ -> QuizScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                StudySubScreen.TEST -> TestScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                StudySubScreen.LIBRARY -> LibraryScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                StudySubScreen.NONE, StudySubScreen.ADMIN -> {
                    when (currentTab) {
                        MainTab.HOME -> DashboardScreen(viewModel = viewModel)
                        MainTab.STUDY -> StudyHubScreen(viewModel = viewModel)
                        MainTab.PROGRESS -> ProgressScreen(viewModel = viewModel)
                        MainTab.SCHEDULE -> ScheduleScreen(viewModel = viewModel)
                        MainTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
