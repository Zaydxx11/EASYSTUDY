package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberStreak
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.PurpleAI
import com.example.ui.theme.RoseDoubt
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.StudySubScreen
import com.example.ui.viewmodel.StudyViewModel

data class FeatureCardItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val onClick: () -> Unit
)

@Composable
fun DashboardScreen(viewModel: StudyViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val recommendation by viewModel.aiRecommendation.collectAsState()
    val schedules by viewModel.schedules.collectAsState()
    val chapters by viewModel.chapters.collectAsState()

    val username = profile?.username?.ifEmpty { "Scholar" } ?: "Scholar"
    val streak = profile?.currentStreak ?: 4
    val xp = profile?.xp ?: 620
    val level = profile?.level ?: 3

    val completedChapters = chapters.count { it.status == "Completed" }
    val totalChapters = chapters.size.coerceAtLeast(1)
    val syllabusProgress = (completedChapters * 100) / totalChapters

    val nextSchedule = schedules.firstOrNull { !it.isCompleted }

    val featureCards = listOf(
        FeatureCardItem(
            id = "ai_study",
            title = "1. AI Study",
            subtitle = "Smart study planner & deep concepts",
            icon = Icons.Default.AutoAwesome,
            gradientColors = listOf(IndigoPrimary, IndigoLight),
            onClick = { viewModel.selectTab(MainTab.STUDY) }
        ),
        FeatureCardItem(
            id = "video_summarizer",
            title = "2. Video Summarizer",
            subtitle = "Extract key concepts & formulas",
            icon = Icons.Default.PlayCircle,
            gradientColors = listOf(CyanAccent, IndigoPrimary),
            onClick = {
                viewModel.videoStudyTab.value = 0
                viewModel.openSubScreen(StudySubScreen.VIDEO_STUDY)
            }
        ),
        FeatureCardItem(
            id = "ai_notes",
            title = "3. AI Notes",
            subtitle = "Generate structured revision notes",
            icon = Icons.Default.NoteAlt,
            gradientColors = listOf(PurpleAI, IndigoPrimary),
            onClick = {
                viewModel.videoStudyTab.value = 1
                viewModel.openSubScreen(StudySubScreen.VIDEO_STUDY)
            }
        ),
        FeatureCardItem(
            id = "quiz",
            title = "4. Quiz",
            subtitle = "Interactive chapter MCQs & feedback",
            icon = Icons.Default.Quiz,
            gradientColors = listOf(EmeraldSuccess, CyanAccent),
            onClick = { viewModel.openSubScreen(StudySubScreen.QUIZ) }
        ),
        FeatureCardItem(
            id = "test",
            title = "5. Test",
            subtitle = "Timed exams & accuracy analysis",
            icon = Icons.Default.Speed,
            gradientColors = listOf(RoseDoubt, AmberStreak),
            onClick = { viewModel.openSubScreen(StudySubScreen.TEST) }
        ),
        FeatureCardItem(
            id = "doubt_solver",
            title = "6. AI Doubt Solver",
            subtitle = "Multi-model step-by-step logic",
            icon = Icons.Default.Psychology,
            gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFF6366F1)),
            onClick = { viewModel.openSubScreen(StudySubScreen.DOUBT_SOLVER) }
        ),
        FeatureCardItem(
            id = "study_analytics",
            title = "7. Study Analytics",
            subtitle = "Visual trends & weakness alerts",
            icon = Icons.Default.Analytics,
            gradientColors = listOf(IndigoPrimary, CyanAccent),
            onClick = { viewModel.selectTab(MainTab.PROGRESS) }
        ),
        FeatureCardItem(
            id = "schedule",
            title = "8. Schedule",
            subtitle = "Daily timetable & reminders",
            icon = Icons.Default.CalendarMonth,
            gradientColors = listOf(AmberStreak, EmeraldSuccess),
            onClick = { viewModel.selectTab(MainTab.SCHEDULE) }
        ),
        FeatureCardItem(
            id = "my_subjects",
            title = "9. My Subjects",
            subtitle = "Curriculum, textbooks & chapters",
            icon = Icons.Default.Book,
            gradientColors = listOf(CyanAccent, EmeraldSuccess),
            onClick = { viewModel.selectTab(MainTab.PROGRESS) }
        ),
        FeatureCardItem(
            id = "my_progress",
            title = "10. My Progress",
            subtitle = "Subject completion & mastery",
            icon = Icons.Default.CheckCircle,
            gradientColors = listOf(EmeraldSuccess, IndigoPrimary),
            onClick = { viewModel.selectTab(MainTab.PROGRESS) }
        ),
        FeatureCardItem(
            id = "my_library",
            title = "11. My Library",
            subtitle = "Saved notes, summaries & doubts",
            icon = Icons.Default.VideoLibrary,
            gradientColors = listOf(PurpleAI, RoseDoubt),
            onClick = { viewModel.openSubScreen(StudySubScreen.LIBRARY) }
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Greeting & Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Good morning, $username 👋",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${profile?.classOrCourse ?: "Class 10"} • ${profile?.boardOrCurriculum ?: "CBSE"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Gamification Streak & XP Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = AmberStreak,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$streak d",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = AmberStreak
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lv $level",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // Global Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search subjects, chapters, notes, doubts...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("global_search_input"),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        }

        // Study Status & Today's Goal Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Study Goal",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${profile?.dailyStudyHours ?: 3f} hrs target",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Overall Syllabus Progress", style = MaterialTheme.typography.bodySmall)
                        Text(text = "$syllabusProgress%", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { syllabusProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EmeraldSuccess,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    if (nextSchedule != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Next: ${nextSchedule.subject} (${nextSchedule.startTime})",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
            }
        }

        // Section 21: "What should I study today?" AI Study Recommendations
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "What should I study today?",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = recommendation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.openSubScreen(StudySubScreen.QUIZ) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Start Study Session",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Main Feature Grid Title
        item {
            Text(
                text = "Study Life & AI Features",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // 11 Feature Cards rendered cleanly
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                featureCards.forEach { card ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { card.onClick() }
                            .testTag("feature_card_${card.id}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Brush.linearGradient(card.gradientColors)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = card.icon,
                                    contentDescription = card.title,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = card.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = card.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Made by Zayd",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
