package com.example.ui.screens

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.PurpleAI
import com.example.ui.theme.RoseDoubt
import com.example.ui.viewmodel.StudyViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DoubtSolverScreen(
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    val question by viewModel.doubtQuestion.collectAsState()
    val imageBitmap by viewModel.doubtImageBitmap.collectAsState()
    val selectedAi by viewModel.doubtAiProvider.collectAsState()
    val isSolving by viewModel.isSolvingDoubt.collectAsState()
    val solution by viewModel.currentDoubtSolution.collectAsState()
    val pastDoubts by viewModel.doubts.collectAsState()

    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    viewModel.doubtImageBitmap.value = bmp
                }
            } catch (_: Exception) {}
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("doubt_solver_screen"),
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
                        text = "AI Doubt Solver",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Step-by-step educational explanations & concept clarity",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // "Choose your AI" Selector
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Choose your AI:",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Gemini", "ChatGPT", "Copilot").forEach { provider ->
                                    val isSelected = selectedAi == provider
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                            .clickable { viewModel.doubtAiProvider.value = provider }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = provider,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Question Input Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = question,
                                onValueChange = { viewModel.doubtQuestion.value = it },
                                placeholder = { Text("Type your question, math equation, or formula doubt...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("doubt_question_input"),
                                minLines = 3,
                                maxLines = 6,
                                shape = RoundedCornerShape(14.dp)
                            )

                            if (imageBitmap != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.Black)
                                ) {
                                    Image(
                                        bitmap = imageBitmap!!.asImageBitmap(),
                                        contentDescription = "Uploaded Question",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    IconButton(
                                        onClick = { viewModel.doubtImageBitmap.value = null },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { imagePicker.launch("image/*") },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Photo")
                                }

                                Button(
                                    onClick = { viewModel.solveCurrentDoubt() },
                                    enabled = !isSolving && (question.isNotBlank() || imageBitmap != null),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.testTag("solve_doubt_button")
                                ) {
                                    if (isSolving) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Solving with $selectedAi...")
                                    } else {
                                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Solve Doubt", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // 7-Part Educational Response
                if (solution != null) {
                    val sol = solution!!
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = EmeraldSuccess)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Educational Solution ($selectedAi)",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }

                                    IconButton(onClick = { viewModel.saveCurrentDoubtManual() }) {
                                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Save Doubt", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }

                                // 1. Understand the question
                                DoubtSectionCard(number = "1", title = "Understand The Question", content = sol.understandQuestion)

                                // 2. Concept involved
                                DoubtSectionCard(number = "2", title = "Concept Involved", content = sol.conceptInvolved)

                                // 3. Step-by-step solution
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "3. STEP-BY-STEP SOLUTION",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    sol.stepByStep.forEachIndexed { idx, step ->
                                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                            Text(
                                                text = "${idx + 1}. ",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                            )
                                            Text(text = step, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }

                                // 4. Final Answer
                                DoubtSectionCard(number = "4", title = "Final Answer", content = sol.finalAnswer, isHighlight = true)

                                // 5. Explanation
                                DoubtSectionCard(number = "5", title = "Detailed Explanation", content = sol.explanation)

                                // 6. Similar Example
                                DoubtSectionCard(number = "6", title = "Similar Example", content = sol.similarExample)

                                // 7. Practice Question
                                DoubtSectionCard(number = "7", title = "Practice Question For You", content = sol.practiceQuestion)

                                // Pedagogical Action Buttons
                                Text(
                                    text = "Deepen Understanding:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilterChip(
                                        selected = false,
                                        onClick = { viewModel.solveCurrentDoubt(modeModifier = "simplify the derivation to 3 clear lines") },
                                        label = { Text("Simplify") }
                                    )
                                    FilterChip(
                                        selected = false,
                                        onClick = { viewModel.solveCurrentDoubt(modeModifier = "explain like I'm a beginner using real-world analogies") },
                                        label = { Text("Explain Like I'm a Beginner") }
                                    )
                                    FilterChip(
                                        selected = false,
                                        onClick = { viewModel.solveCurrentDoubt(modeModifier = "show another alternate mathematical method") },
                                        label = { Text("Show Another Method") }
                                    )
                                    FilterChip(
                                        selected = false,
                                        onClick = { viewModel.solveCurrentDoubt(modeModifier = "generate a 2-step follow-up concept question") },
                                        label = { Text("Ask Follow-up") }
                                    )
                                }
                            }
                        }
                    }
                }

                // Doubt History Section
                if (pastDoubts.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recent Doubts (${pastDoubts.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    items(pastDoubts.take(5)) { doubt ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = doubt.question.take(60),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = doubt.aiProvider,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Answer: ${doubt.finalAnswer.take(80)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun DoubtSectionCard(number: String, title: String, content: String, isHighlight: Boolean = false) {
    if (content.isBlank()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isHighlight) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(12.dp)
    ) {
        Text(
            text = "$number. ${title.uppercase()}",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
                color = if (isHighlight) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
