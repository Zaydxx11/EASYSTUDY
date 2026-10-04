package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.QuizQuestion
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseDoubt
import com.example.ui.viewmodel.StudyViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuizScreen(
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val chapters by viewModel.chapters.collectAsState()

    val selectedSubject by viewModel.quizSelectedSubject.collectAsState()
    val selectedChapter by viewModel.quizSelectedChapter.collectAsState()
    val difficulty by viewModel.quizDifficulty.collectAsState()
    val numQuestions by viewModel.quizNumQuestions.collectAsState()
    val questionType by viewModel.quizQuestionType.collectAsState()

    val isGenerating by viewModel.isGeneratingQuiz.collectAsState()
    val isQuizActive by viewModel.isQuizActive.collectAsState()
    val questionsList by viewModel.quizQuestionsList.collectAsState()
    val currentIndex by viewModel.quizCurrentIndex.collectAsState()
    val answersMap by viewModel.quizAnswersMap.collectAsState()
    val isFinished by viewModel.quizFinished.collectAsState()
    val finalScore by viewModel.quizFinalScore.collectAsState()
    val weakTopic by viewModel.quizWeakTopic.collectAsState()

    var isCustomChapterMode by remember { mutableStateOf(false) }
    var customChapterInput by remember { mutableStateOf("") }

    val userSubjects = profile?.let {
        try {
            org.json.JSONArray(it.subjectsJson).let { arr ->
                (0 until arr.length()).map { i -> arr.getString(i) }
            }
        } catch (_: Exception) {
            listOf("Mathematics", "Physics", "Chemistry", "Biology")
        }
    } ?: listOf("Mathematics", "Physics", "Chemistry", "Biology")

    val relevantChapters = chapters.filter { it.subject.equals(selectedSubject, ignoreCase = true) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("quiz_screen"),
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
                        text = "AI Quiz Generator",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Curriculum-aligned mastery test with instant insights",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!isQuizActive) {
                // Configuration Screen
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Subject selection
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "1. Choose Subject (From Your Profile)",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    userSubjects.forEach { subj ->
                                        val isSel = selectedSubject.equals(subj, ignoreCase = true)
                                        FilterChip(
                                            selected = isSel,
                                            onClick = {
                                                viewModel.quizSelectedSubject.value = subj
                                                val firstChap = chapters.firstOrNull { it.subject.equals(subj, ignoreCase = true) }?.title ?: "All Chapters"
                                                viewModel.quizSelectedChapter.value = firstChap
                                            },
                                            label = { Text(subj) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Chapter selection
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "2. Choose Chapter (${profile?.boardOrCurriculum ?: "CBSE"})",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilterChip(
                                        selected = selectedChapter == "All Chapters",
                                        onClick = {
                                            viewModel.quizSelectedChapter.value = "All Chapters"
                                            isCustomChapterMode = false
                                        },
                                        label = { Text("All Chapters") }
                                    )

                                    relevantChapters.forEach { chap ->
                                        val isSel = selectedChapter == chap.title
                                        FilterChip(
                                            selected = isSel,
                                            onClick = {
                                                viewModel.quizSelectedChapter.value = chap.title
                                                isCustomChapterMode = false
                                            },
                                            label = { Text("Ch ${chap.chapterNumber}: ${chap.title.take(24)}") }
                                        )
                                    }

                                    FilterChip(
                                        selected = isCustomChapterMode,
                                        onClick = { isCustomChapterMode = true },
                                        label = { Text("+ Custom Topic") }
                                    )
                                }

                                if (isCustomChapterMode) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = customChapterInput,
                                        onValueChange = {
                                            customChapterInput = it
                                            viewModel.quizSelectedChapter.value = it
                                        },
                                        label = { Text("Enter Custom Topic") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    // Difficulty & Count
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "3. Difficulty Level",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("Easy", "Medium", "Hard", "Mixed").forEach { diff ->
                                        val isSel = difficulty == diff
                                        FilterChip(
                                            selected = isSel,
                                            onClick = { viewModel.quizDifficulty.value = diff },
                                            label = { Text(diff) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "4. Number of Questions",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(5, 10, 15, 20).forEach { count ->
                                        val isSel = numQuestions == count
                                        FilterChip(
                                            selected = isSel,
                                            onClick = { viewModel.quizNumQuestions.value = count },
                                            label = { Text("$count Qs") }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Generate Button
                    item {
                        Button(
                            onClick = { viewModel.startQuizGeneration() },
                            enabled = !isGenerating,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("start_quiz_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Generating Quiz with AI...")
                            } else {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate AI Quiz", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else if (!isFinished) {
                // Live Quiz Running Screen
                val currentQ = questionsList.getOrNull(currentIndex)
                if (currentQ != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            // Progress bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Question ${currentIndex + 1} of ${questionsList.size}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "$selectedSubject • $difficulty",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { (currentIndex + 1f) / questionsList.size },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Question Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text(
                                        text = currentQ.question,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Topic: ${currentQ.topic}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 4 Options
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                val selectedAnswer = answersMap[currentQ.id]
                                currentQ.options.forEachIndexed { optIdx, optText ->
                                    val isSelected = selectedAnswer == optIdx
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.answerQuizQuestion(currentQ.id, optIdx) },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                        ),
                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = ('A'.code + optIdx).toChar().toString(),
                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                                    )
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = optText,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Navigation buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (currentIndex > 0) viewModel.quizCurrentIndex.value = currentIndex - 1
                                },
                                enabled = currentIndex > 0,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Previous")
                            }

                            if (currentIndex < questionsList.size - 1) {
                                Button(
                                    onClick = { viewModel.quizCurrentIndex.value = currentIndex + 1 },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Next")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.submitQuiz() },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                                ) {
                                    Text("Submit Quiz", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                // Results Screen
                val total = questionsList.size
                val pct = if (total > 0) (finalScore * 100) / total else 0

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
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Quiz Results",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(CircleShape)
                                        .background(if (pct >= 70) EmeraldSuccess else MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$pct%",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color.White)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Score: $finalScore / $total",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Weak Topics Identified: $weakTopic",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }

                    // Detailed question-by-question review & explanations
                    item {
                        Text(
                            text = "Question-by-Question Review & Explanations",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    items(questionsList) { q ->
                        val userChoice = answersMap[q.id]
                        val isCorrect = userChoice == q.correctOptionIndex

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isCorrect) EmeraldSuccess else RoseDoubt)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Q${q.id}: ${q.question}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                                        contentDescription = null,
                                        tint = if (isCorrect) EmeraldSuccess else RoseDoubt
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Correct Answer: ${q.options.getOrNull(q.correctOptionIndex) ?: ""}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Explanation: ${q.explanation}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                viewModel.isQuizActive.value = false
                                viewModel.quizFinished.value = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Take Another Quiz", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
