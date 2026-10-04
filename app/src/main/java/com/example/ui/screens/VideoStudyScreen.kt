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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.StudyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoStudyScreen(
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    val videoInput by viewModel.videoInput.collectAsState()
    val summaryType by viewModel.videoSummaryType.collectAsState()
    val isLoading by viewModel.isAnalyzingVideo.collectAsState()
    val videoSummary by viewModel.currentVideoSummary.collectAsState()
    val generatedNotes by viewModel.currentGeneratedNotes.collectAsState()
    val activeTab by viewModel.videoStudyTab.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("video_study_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "AI Video Study",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Turn lectures and videos into actionable summaries & notes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Mode Tab Selector: "AI Summarise" vs "Make Notes"
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { viewModel.videoStudyTab.value = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AI Summarise", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { viewModel.videoStudyTab.value = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.NoteAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Make Notes", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Input card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Video URL or Educational Content",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = videoInput,
                                onValueChange = { viewModel.videoInput.value = it },
                                placeholder = { Text("Paste YouTube link, lecture URL, or paste topic transcript...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("video_url_input"),
                                maxLines = 3,
                                shape = RoundedCornerShape(12.dp)
                            )

                            if (activeTab == 0) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Summary Depth:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("Very Short", "Short", "Detailed", "Exam Revision").forEach { mode ->
                                        val isSel = summaryType == mode
                                        FilterChip(
                                            selected = isSel,
                                            onClick = { viewModel.videoSummaryType.value = mode },
                                            label = { Text(mode, style = MaterialTheme.typography.labelSmall) }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.analyzeVideoContent() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("analyze_video_button"),
                                shape = RoundedCornerShape(14.dp),
                                enabled = !isLoading,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Synthesizing Educational AI Content...")
                                } else {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (activeTab == 0) "Generate AI Summary" else "Generate Structured Notes",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // AI Output Card
                if (activeTab == 0 && videoSummary != null) {
                    val summary = videoSummary!!
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = summary.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Saved", tint = EmeraldSuccess)
                                }

                                ResultSection(title = "Short Summary", content = summary.shortSummary)
                                ResultSection(title = "Detailed Summary", content = summary.detailedSummary)
                                ResultBullets(title = "Key Concepts", items = summary.keyConcepts)
                                ResultBullets(title = "Important Definitions", items = summary.definitions)
                                ResultBullets(title = "Important Formulas", items = summary.formulas)
                                ResultBullets(title = "Main Points", items = summary.mainPoints)
                                ResultSection(title = "Exam-Relevant Information", content = summary.examRelevant)
                                ResultBullets(title = "Important Timestamps", items = summary.timestamps)
                            }
                        }
                    }
                } else if (activeTab == 1 && generatedNotes != null) {
                    val notes = generatedNotes!!
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Notes: ${notes.topic}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                ResultBullets(title = "Subtopics Covered", items = notes.subtopics)
                                ResultBullets(title = "Definitions", items = notes.definitions)
                                ResultBullets(title = "Important Concepts", items = notes.importantConcepts)
                                ResultBullets(title = "Formulas & Equations", items = notes.formulas)
                                ResultBullets(title = "Worked Examples", items = notes.examples)
                                ResultBullets(title = "Key Points", items = notes.keyPoints)
                                ResultBullets(title = "Exam Tips & Pitfalls", items = notes.examTips)
                                ResultSection(title = "Quick Revision Section", content = notes.quickRevision)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultSection(title: String, content: String) {
    if (content.isBlank()) return
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ResultBullets(title: String, items: List<String>) {
    if (items.isEmpty()) return
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        items.forEach { item ->
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                Text(text = "• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
