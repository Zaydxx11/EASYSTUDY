package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.StudyViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(viewModel: StudyViewModel) {
    val step by viewModel.onboardingStep.collectAsState()
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("onboarding_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header with Back button, Progress info and Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { viewModel.prevOnboardingStep() },
                        modifier = Modifier.testTag("onboarding_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    Text(
                        text = "Step $step of 7",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    // Balance spacer
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { step / 7f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            // Step Content area with smooth AnimatedContent
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(vertical = 16.dp)
            ) {
                AnimatedContent(
                    targetState = step,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "onboarding_step_animation"
                ) { targetStep ->
                    when (targetStep) {
                        1 -> Step1EducationLevel(viewModel)
                        2 -> Step2CurriculumBoard(viewModel)
                        3 -> Step3Subjects(viewModel)
                        4 -> Step4Books(viewModel)
                        5 -> Step5Goals(viewModel)
                        6 -> Step6Username(viewModel)
                        7 -> Step7ProfileConfirmation(viewModel)
                    }
                }
            }

            // Bottom Navigation Footer
            if (step < 7) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.prevOnboardingStep() },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text("Back")
                    }

                    Button(
                        onClick = { viewModel.nextOnboardingStep() },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("onboarding_next_button")
                    ) {
                        Text("Continue", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 1: EDUCATION LEVEL
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step1EducationLevel(viewModel: StudyViewModel) {
    val level by viewModel.obEducationLevel.collectAsState()
    val schoolClass by viewModel.obClassOrCourse.collectAsState()
    val degree by viewModel.obDegree.collectAsState()
    val year by viewModel.obYear.collectAsState()
    val semester by viewModel.obSemester.collectAsState()
    val branch by viewModel.obBranch.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "What are you currently studying?",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "EasyStudy will customize your entire academic curriculum and AI tutor.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        // Options: School, College / University, Other
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("School", "College / University", "Other").forEach { opt ->
                val isSelected = level == opt
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { viewModel.obEducationLevel.value = opt }
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = opt,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (level == "School") {
            Text(
                text = "Which class are you in?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..12).forEach { num ->
                    val className = "Class $num"
                    val isSelected = schoolClass == className
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.obClassOrCourse.value = className },
                        label = { Text(className) }
                    )
                }
                FilterChip(
                    selected = schoolClass == "Other",
                    onClick = { viewModel.obClassOrCourse.value = "Other" },
                    label = { Text("Other") }
                )
            }
        } else {
            Text(
                text = "College / University Details",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = degree,
                onValueChange = { viewModel.obDegree.value = it },
                label = { Text("Degree (e.g. B.Tech, B.Sc, B.Com, MBBS)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = year,
                    onValueChange = { viewModel.obYear.value = it },
                    label = { Text("Year (e.g. 2nd Year)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = semester,
                    onValueChange = { viewModel.obSemester.value = it },
                    label = { Text("Semester (e.g. 4th)") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = branch,
                onValueChange = { viewModel.obBranch.value = it },
                label = { Text("Branch / Major (e.g. Computer Science)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// -------------------------------------------------------------
// STEP 2: BOARD / CURRICULUM
// -------------------------------------------------------------
@Composable
private fun Step2CurriculumBoard(viewModel: StudyViewModel) {
    val board by viewModel.obBoard.collectAsState()
    val stateName by viewModel.obStateName.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Which curriculum/board do you follow?",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Our academic database matches official blueprints, books, and chapter lists.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        val boards = listOf("CBSE", "ICSE", "State Board", "IB", "Cambridge", "Other")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            boards.forEach { b ->
                val isSelected = board == b
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.obBoard.value = b },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = b,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        if (board == "State Board") {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = stateName,
                onValueChange = { viewModel.obStateName.value = it },
                label = { Text("Enter your State / Region") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// -------------------------------------------------------------
// STEP 3: SUBJECTS
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step3Subjects(viewModel: StudyViewModel) {
    val selectedSubjects by viewModel.obSelectedSubjects.collectAsState()
    var customSubjectInput by remember { mutableStateOf("") }

    val defaultSubjects = listOf(
        "Mathematics", "Physics", "Chemistry", "Biology",
        "Computer Science", "English", "History", "Geography",
        "Economics", "Accountancy", "Political Science"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Which subjects do you study?",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Select all that apply. You can also add custom subjects.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val allList = (defaultSubjects + selectedSubjects).distinct()
            allList.forEach { subj ->
                val isSelected = selectedSubjects.contains(subj)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.toggleSubject(subj) },
                    label = { Text(subj) },
                    leadingIcon = if (isSelected) {
                        { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Add custom subject
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = customSubjectInput,
                onValueChange = { customSubjectInput = it },
                label = { Text("Add Custom Subject") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (customSubjectInput.isNotBlank()) {
                        viewModel.addCustomSubject(customSubjectInput)
                        customSubjectInput = ""
                    }
                })
            )

            Button(
                onClick = {
                    if (customSubjectInput.isNotBlank()) {
                        viewModel.addCustomSubject(customSubjectInput)
                        customSubjectInput = ""
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(56.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 4: BOOKS / TEXTBOOKS
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step4Books(viewModel: StudyViewModel) {
    val selectedSubjects by viewModel.obSelectedSubjects.collectAsState()
    val selectedBooks by viewModel.obSelectedBooks.collectAsState()

    val standardPublishers = listOf(
        "NCERT", "Oswaal", "Together With", "Arihant",
        "Evergreen", "MTG", "Educart", "School Textbook", "Other"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Which books do you use?",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Choose textbook publishers for your subjects so EasyStudy references exact chapters.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            selectedSubjects.forEach { subj ->
                val currentBook = selectedBooks[subj] ?: "NCERT"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = subj,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            standardPublishers.forEach { pub ->
                                val isSelected = currentBook == pub
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setSubjectBook(subj, pub) },
                                    label = { Text(pub) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 5: STUDY GOALS
// -------------------------------------------------------------
@Composable
private fun Step5Goals(viewModel: StudyViewModel) {
    val selectedGoals by viewModel.obGoals.collectAsState()
    val dailyHours by viewModel.obDailyHours.collectAsState()

    val goalsList = listOf(
        "Improve grades",
        "Prepare for exams",
        "Complete syllabus",
        "Understand difficult concepts",
        "Prepare for competitive exams",
        "Stay consistent",
        "Create a study routine",
        "Score 95%+ in Boards"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "What are your main study goals?",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Select all that fit your personal academic target.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            goalsList.forEach { goal ->
                val isSelected = selectedGoals.contains(goal)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleGoal(goal) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = goal,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        if (isSelected) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Daily study hours slider
        Text(
            text = "Daily study target: ${String.format("%.1f", dailyHours)} hours",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Slider(
            value = dailyHours,
            onValueChange = { viewModel.obDailyHours.value = it },
            valueRange = 1f..10f,
            steps = 17,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// -------------------------------------------------------------
// STEP 6: USERNAME
// -------------------------------------------------------------
@Composable
private fun Step6Username(viewModel: StudyViewModel) {
    val username by viewModel.obUsername.collectAsState()
    val error by viewModel.obUsernameError.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Create your username",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Letters, numbers, and underscores allowed. You can change this later.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = {
                viewModel.obUsername.value = it
                viewModel.obUsernameError.value = null
            },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("username_input"),
            isError = error != null,
            supportingText = {
                if (error != null) {
                    Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
                } else {
                    Text(text = "Available and ready!")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Your username will be displayed in your study lobby, streaks, and quiz leaderboards.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 7: PROFILE CONFIRMATION & PLAY BUTTON
// -------------------------------------------------------------
@Composable
private fun Step7ProfileConfirmation(viewModel: StudyViewModel) {
    val username by viewModel.obUsername.collectAsState()
    val level by viewModel.obEducationLevel.collectAsState()
    val classOrCourse by viewModel.obClassOrCourse.collectAsState()
    val board by viewModel.obBoard.collectAsState()
    val subjects by viewModel.obSelectedSubjects.collectAsState()
    val books by viewModel.obSelectedBooks.collectAsState()
    val goals by viewModel.obGoals.collectAsState()
    val hours by viewModel.obDailyHours.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Study Profile",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Everything is customized and ready for your academic journey!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        // Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileSummaryRow(label = "Username", value = username)
                ProfileSummaryRow(label = "Education Level", value = level)
                ProfileSummaryRow(label = "Class / Course", value = classOrCourse)
                ProfileSummaryRow(label = "Board / Curriculum", value = board)
                ProfileSummaryRow(label = "Subjects", value = subjects.joinToString(", "))
                ProfileSummaryRow(label = "Main Books", value = books.values.distinct().joinToString(", "))
                ProfileSummaryRow(label = "Study Target", value = "${String.format("%.1f", hours)} hours / day")
                ProfileSummaryRow(label = "Top Goal", value = goals.firstOrNull() ?: "Improve grades")
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Large Glowing "PLAY / ENTER MY STUDY SPACE" Button
        Button(
            onClick = { viewModel.completeOnboardingAndEnter() },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .testTag("enter_study_space_button"),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ENTER MY STUDY SPACE",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { viewModel.onboardingStep.value = 1 },
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Edit Profile Details")
        }
    }
}

@Composable
private fun ProfileSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(110.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}
