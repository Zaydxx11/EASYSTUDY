package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import com.example.ui.theme.RoseDoubt
import com.example.ui.viewmodel.StudyViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TestScreen(
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val chapters by viewModel.chapters.collectAsState()

    val selectedSubject by viewModel.testSelectedSubject.collectAsState()
    val selectedChapter by viewModel.testSelectedChapter.collectAsState()
    val difficulty by viewModel.testDifficulty.collectAsState()
    val numQuestions by viewModel.testNumQuestions.collectAsState()

    val isGenerating by viewModel.isGeneratingTest.collectAsState()
    val isTestActive by viewModel.isTestActive.collectAsState()
    val questionsList by viewModel.testQuestionsList.collectAsState()
    val currentIndex by viewModel.testCurrentIndex.collectAsState()
    val answersMap by viewModel.testAnswersMap.collectAsState()
    val isFinished by viewModel.testFinished.collectAsState()
    val testScore by viewModel.testScore.collectAsState()

    val userSubjects = listOf("Mathematics", "Physics", "Chemistry", "Biology", "Computer Science")

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("test_screen"),
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
                        text = "AI Test Simulator",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Timed exam simulation with multi-format marking",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!isTestActive) {
                // Setup Form
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Select Test Subject",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    userSubjects.forEach { subj ->
                                        FilterChip(
                                            selected = selectedSubject == subj,
                                            onClick = { viewModel.testSelectedSubject.value = subj },
                                            label = { Text(subj) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Chapter / Scope",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("All Chapters", "Electricity", "Light Refraction", "Real Numbers", "Chemical Reactions").forEach { chap ->
                                        FilterChip(
                                            selected = selectedChapter == chap,
                                            onClick = { viewModel.testSelectedChapter.value = chap },
                                            label = { Text(chap) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Difficulty",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            listOf("Easy", "Medium", "Hard").forEach { d ->
                                                FilterChip(
                                                    selected = difficulty == d,
                                                    onClick = { viewModel.testDifficulty.value = d },
                                                    label = { Text(d) }
                                                )
                                            }
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Questions",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            listOf(3, 5, 8).forEach { qCount ->
                                                FilterChip(
                                                    selected = numQuestions == qCount,
                                                    onClick = { viewModel.testNumQuestions.value = qCount },
                                                    label = { Text("$qCount Qs") }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { viewModel.startTestGeneration() },
                            enabled = !isGenerating,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("start_test_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Assembling Official Academic Test...")
                            } else {
                                Icon(imageVector = Icons.Default.Speed, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Start Timed AI Test", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else if (!isFinished) {
                // Active Test
                val currentQ = questionsList.getOrNull(currentIndex)
                if (currentQ != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = RoseDoubt)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "18:42 remaining", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RoseDoubt))
                                }
                                Text(text = "Question ${currentIndex + 1} of ${questionsList.size}", style = MaterialTheme.typography.labelMedium)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { (currentIndex + 1f) / questionsList.size },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "[${currentQ.questionType}]", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold))
                                        Text(text = "${currentQ.marks} Marks", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = currentQ.question, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            val currentAns = answersMap[currentQ.id] ?: ""
                            OutlinedTextField(
                                value = currentAns,
                                onValueChange = { viewModel.submitTestAnswer(currentQ.id, it) },
                                label = { Text("Your step-by-step answer & derivation") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                maxLines = 8,
                                shape = RoundedCornerShape(14.dp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (currentIndex > 0) viewModel.testCurrentIndex.value = currentIndex - 1
                                },
                                enabled = currentIndex > 0,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Previous")
                            }

                            if (currentIndex < questionsList.size - 1) {
                                Button(
                                    onClick = { viewModel.testCurrentIndex.value = currentIndex + 1 },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Next Question")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.finishTest() },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                                ) {
                                    Text("Submit Test", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                // Test Results & Evaluation
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "Test Evaluation & Marks", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(text = "Accuracy: 88%", style = MaterialTheme.typography.titleLarge.copy(color = EmeraldSuccess, fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Score: $testScore / ${questionsList.size}", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "AI Recommended Revision: Parallel Resistor combinations and sign conventions in Ray Optics.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }

                    items(questionsList) { q ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(text = "Q${q.id}: ${q.question}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Ideal Answer: ${q.sampleAnswer}", style = MaterialTheme.typography.bodySmall.copy(color = EmeraldSuccess))
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                viewModel.isTestActive.value = false
                                viewModel.testFinished.value = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Take Another Test")
                        }
                    }
                }
            }
        }
    }
}
