package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.DoubtSolution
import com.example.data.ai.GeneratedNotesResult
import com.example.data.ai.QuizQuestion
import com.example.data.ai.TestQuestion
import com.example.data.ai.VideoSummaryResult
import com.example.data.local.AppDatabase
import com.example.data.model.AcademicChapter
import com.example.data.model.DoubtItem
import com.example.data.model.QuizAttempt
import com.example.data.model.ReminderItem
import com.example.data.model.SavedNote
import com.example.data.model.StudyScheduleItem
import com.example.data.model.StudySession
import com.example.data.model.TestAttempt
import com.example.data.model.UserProfile
import com.example.data.model.VideoSummary
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

enum class AppDestination {
    SPLASH,
    WELCOME,
    ONBOARDING,
    MAIN
}

enum class MainTab {
    HOME,
    STUDY,
    PROGRESS,
    SCHEDULE,
    PROFILE
}

enum class StudySubScreen {
    NONE,
    VIDEO_STUDY,
    DOUBT_SOLVER,
    QUIZ,
    TEST,
    LIBRARY,
    ADMIN
}

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = StudyRepository(db.studyDao())

    private val prefs = application.getSharedPreferences("easystudy_prefs", android.content.Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(prefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _themeAccent = MutableStateFlow(prefs.getString("theme_accent", "INDIGO") ?: "INDIGO")
    val themeAccent: StateFlow<String> = _themeAccent.asStateFlow()

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val chapters: StateFlow<List<AcademicChapter>> = repository.allChapters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val doubts: StateFlow<List<DoubtItem>> = repository.allDoubts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizzes: StateFlow<List<QuizAttempt>> = repository.allQuizzes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tests: StateFlow<List<TestAttempt>> = repository.allTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<SavedNote>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val videoSummaries: StateFlow<List<VideoSummary>> = repository.allVideoSummaries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val schedules: StateFlow<List<StudyScheduleItem>> = repository.allSchedules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders: StateFlow<List<ReminderItem>> = repository.allReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<StudySession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation & UI state
    private val _currentDestination = MutableStateFlow(AppDestination.SPLASH)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _activeSubScreen = MutableStateFlow(StudySubScreen.NONE)
    val activeSubScreen: StateFlow<StudySubScreen> = _activeSubScreen.asStateFlow()

    // Global Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Onboarding Form State (8 steps)
    val onboardingStep = MutableStateFlow(1)
    val obEducationLevel = MutableStateFlow("School") // School, College / University, Other
    val obClassOrCourse = MutableStateFlow("Class 10")
    val obDegree = MutableStateFlow("B.Tech")
    val obYear = MutableStateFlow("2nd Year")
    val obSemester = MutableStateFlow("4th Semester")
    val obBranch = MutableStateFlow("Computer Science")
    val obSpecialization = MutableStateFlow("Artificial Intelligence")
    val obBoard = MutableStateFlow("CBSE")
    val obStateName = MutableStateFlow("California / Delhi")
    val obSchool = MutableStateFlow("Delhi Public School")
    val obSelectedSubjects = MutableStateFlow(listOf("Mathematics", "Physics", "Chemistry", "Biology"))
    val obSelectedBooks = MutableStateFlow(mapOf(
        "Mathematics" to "NCERT",
        "Physics" to "NCERT",
        "Chemistry" to "NCERT",
        "Biology" to "NCERT"
    ))
    val obGoals = MutableStateFlow(listOf("Improve grades", "Prepare for exams", "Understand difficult concepts"))
    val obDailyHours = MutableStateFlow(3.5f)
    val obUsername = MutableStateFlow("alex_scholar")
    val obUsernameError = MutableStateFlow<String?>(null)

    // Doubt Solver State
    val doubtQuestion = MutableStateFlow("")
    val doubtImageBitmap = MutableStateFlow<Bitmap?>(null)
    val doubtAiProvider = MutableStateFlow("Gemini") // Gemini, ChatGPT, Copilot
    val isSolvingDoubt = MutableStateFlow(false)
    val currentDoubtSolution = MutableStateFlow<DoubtSolution?>(null)

    // Video Study State
    val videoInput = MutableStateFlow("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
    val videoSummaryType = MutableStateFlow("Detailed")
    val isAnalyzingVideo = MutableStateFlow(false)
    val currentVideoSummary = MutableStateFlow<VideoSummaryResult?>(null)
    val currentGeneratedNotes = MutableStateFlow<GeneratedNotesResult?>(null)
    val videoStudyTab = MutableStateFlow(0) // 0: AI Summarise, 1: Make Notes

    // Quiz State
    val quizSelectedSubject = MutableStateFlow("Mathematics")
    val quizSelectedChapter = MutableStateFlow("Real Numbers")
    val quizDifficulty = MutableStateFlow("Medium")
    val quizNumQuestions = MutableStateFlow(5)
    val quizQuestionType = MutableStateFlow("Multiple Choice")
    val isGeneratingQuiz = MutableStateFlow(false)
    val isQuizActive = MutableStateFlow(false)
    val quizQuestionsList = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizCurrentIndex = MutableStateFlow(0)
    val quizAnswersMap = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val quizFinished = MutableStateFlow(false)
    val quizFinalScore = MutableStateFlow(0)
    val quizWeakTopic = MutableStateFlow("")

    // Test State
    val testSelectedSubject = MutableStateFlow("Physics")
    val testSelectedChapter = MutableStateFlow("Electricity")
    val testDifficulty = MutableStateFlow("Medium")
    val testNumQuestions = MutableStateFlow(3)
    val isGeneratingTest = MutableStateFlow(false)
    val isTestActive = MutableStateFlow(false)
    val testQuestionsList = MutableStateFlow<List<TestQuestion>>(emptyList())
    val testCurrentIndex = MutableStateFlow(0)
    val testAnswersMap = MutableStateFlow<Map<Int, String>>(emptyMap())
    val testFinished = MutableStateFlow(false)
    val testScore = MutableStateFlow(0)

    // AI Recommendation
    val aiRecommendation = MutableStateFlow(
        "Today I recommend revising Quadratic Equations for 35 minutes to reinforce your algebra foundations before your upcoming tests."
    )

    init {
        // Preference synchronization with database
        viewModelScope.launch {
            val p = repository.getUserProfileOnce()
            if (p != null && p.isOnboardingCompleted) {
                prefs.edit().putBoolean("onboarding_completed", true).apply()
            }
        }
    }

    fun onSplashFinished() {
        val completed = prefs.getBoolean("onboarding_completed", false)
        if (completed) {
            _currentDestination.value = AppDestination.MAIN
        } else {
            viewModelScope.launch {
                val p = repository.getUserProfileOnce()
                if (p != null && p.isOnboardingCompleted) {
                    prefs.edit().putBoolean("onboarding_completed", true).apply()
                    _currentDestination.value = AppDestination.MAIN
                } else {
                    _currentDestination.value = AppDestination.WELCOME
                }
            }
        }
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode).apply()
    }

    fun setThemeAccent(accent: String) {
        _themeAccent.value = accent
        prefs.edit().putString("theme_accent", accent).apply()
    }

    fun resetOnboarding() {
        prefs.edit().putBoolean("onboarding_completed", false).apply()
        onboardingStep.value = 1
        _currentDestination.value = AppDestination.ONBOARDING
    }

    fun navigateTo(destination: AppDestination) {
        _currentDestination.value = destination
    }

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
        _activeSubScreen.value = StudySubScreen.NONE
    }

    fun openSubScreen(screen: StudySubScreen) {
        _activeSubScreen.value = screen
    }

    fun closeSubScreen() {
        _activeSubScreen.value = StudySubScreen.NONE
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Onboarding handlers
    fun nextOnboardingStep() {
        if (onboardingStep.value == 6) {
            // Validate username
            val user = obUsername.value.trim()
            if (user.length < 3) {
                obUsernameError.value = "Username must be at least 3 characters"
                return
            }
            if (!user.matches(Regex("^[a-zA-Z0-9_]+$"))) {
                obUsernameError.value = "Only letters, numbers, and underscores allowed"
                return
            }
            obUsernameError.value = null
        }
        if (onboardingStep.value < 7) {
            onboardingStep.value += 1
        }
    }

    fun prevOnboardingStep() {
        if (onboardingStep.value > 1) {
            onboardingStep.value -= 1
        } else {
            _currentDestination.value = AppDestination.WELCOME
        }
    }

    fun toggleSubject(subject: String) {
        val current = obSelectedSubjects.value.toMutableList()
        if (current.contains(subject)) {
            if (current.size > 1) current.remove(subject)
        } else {
            current.add(subject)
        }
        obSelectedSubjects.value = current
    }

    fun addCustomSubject(subject: String) {
        val trimmed = subject.trim()
        if (trimmed.isNotEmpty() && !obSelectedSubjects.value.contains(trimmed)) {
            val list = obSelectedSubjects.value.toMutableList()
            list.add(trimmed)
            obSelectedSubjects.value = list
        }
    }

    fun setSubjectBook(subject: String, book: String) {
        val map = obSelectedBooks.value.toMutableMap()
        map[subject] = book
        obSelectedBooks.value = map
    }

    fun toggleGoal(goal: String) {
        val current = obGoals.value.toMutableList()
        if (current.contains(goal)) {
            if (current.size > 1) current.remove(goal)
        } else {
            current.add(goal)
        }
        obGoals.value = current
    }

    fun completeOnboardingAndEnter() {
        viewModelScope.launch {
            val subjectsJsonArray = JSONArray(obSelectedSubjects.value).toString()
            val booksJsonObject = JSONObject(obSelectedBooks.value).toString()
            val goalsJsonArray = JSONArray(obGoals.value).toString()

            val profile = UserProfile(
                id = 1,
                username = obUsername.value.trim().ifEmpty { "Scholar" },
                educationLevel = obEducationLevel.value,
                classOrCourse = if (obEducationLevel.value == "School") obClassOrCourse.value else obDegree.value,
                collegeDegree = obDegree.value,
                collegeYear = obYear.value,
                collegeSemester = obSemester.value,
                collegeBranch = obBranch.value,
                boardOrCurriculum = obBoard.value,
                stateName = obStateName.value,
                schoolOrCollege = obSchool.value,
                subjectsJson = subjectsJsonArray,
                booksJson = booksJsonObject,
                studyGoalsJson = goalsJsonArray,
                dailyStudyHours = obDailyHours.value,
                currentStreak = 4,
                xp = 620,
                level = 3,
                preferredAi = "Gemini",
                isOnboardingCompleted = true
            )
            repository.saveProfile(profile)
            prefs.edit().putBoolean("onboarding_completed", true).apply()
            _currentDestination.value = AppDestination.MAIN
            _currentTab.value = MainTab.HOME
            refreshRecommendations()
        }
    }

    // AI Doubt Solver
    fun solveCurrentDoubt(modeModifier: String = "") {
        val q = doubtQuestion.value.trim()
        if (q.isEmpty() && doubtImageBitmap.value == null) return

        viewModelScope.launch {
            isSolvingDoubt.value = true
            try {
                val profile = userProfile.value
                val context = "${profile?.classOrCourse ?: "Class 10"} ${profile?.boardOrCurriculum ?: "CBSE"}"
                val solution = repository.solveDoubt(
                    question = q.ifEmpty { "Question shown in the uploaded problem image" },
                    bitmap = doubtImageBitmap.value,
                    provider = doubtAiProvider.value,
                    studentContext = context,
                    modeModifier = modeModifier
                )
                currentDoubtSolution.value = solution

                // Auto-save doubt to history
                repository.saveDoubt(
                    DoubtItem(
                        question = q.ifEmpty { "Image question" },
                        answer = solution.finalAnswer,
                        aiProvider = doubtAiProvider.value,
                        conceptInvolved = solution.conceptInvolved,
                        stepByStepJson = JSONArray(solution.stepByStep).toString(),
                        finalAnswer = solution.finalAnswer,
                        explanation = solution.explanation,
                        similarExample = solution.similarExample,
                        practiceQuestion = solution.practiceQuestion
                    )
                )

                // Reward XP
                awardXp(25)
            } finally {
                isSolvingDoubt.value = false
            }
        }
    }

    fun saveCurrentDoubtManual() {
        val solution = currentDoubtSolution.value ?: return
        viewModelScope.launch {
            repository.saveDoubt(
                DoubtItem(
                    question = doubtQuestion.value.ifEmpty { "Problem Query" },
                    answer = solution.finalAnswer,
                    aiProvider = doubtAiProvider.value,
                    conceptInvolved = solution.conceptInvolved,
                    stepByStepJson = JSONArray(solution.stepByStep).toString(),
                    finalAnswer = solution.finalAnswer,
                    explanation = solution.explanation,
                    similarExample = solution.similarExample,
                    practiceQuestion = solution.practiceQuestion
                )
            )
        }
    }

    // Video Study & Notes
    fun analyzeVideoContent() {
        val input = videoInput.value.trim()
        if (input.isEmpty()) return

        viewModelScope.launch {
            isAnalyzingVideo.value = true
            try {
                val profile = userProfile.value
                val subjectContext = "${profile?.classOrCourse ?: "Class 10"} ${profile?.boardOrCurriculum ?: "CBSE"}"

                if (videoStudyTab.value == 0) {
                    val summary = repository.summarizeVideo(input, videoSummaryType.value, subjectContext)
                    currentVideoSummary.value = summary
                    repository.saveVideoSummary(
                        VideoSummary(
                            videoUrl = input,
                            title = summary.title,
                            summaryType = videoSummaryType.value,
                            shortSummary = summary.shortSummary,
                            detailedSummary = summary.detailedSummary,
                            keyConcepts = summary.keyConcepts.joinToString("\n• "),
                            formulas = summary.formulas.joinToString("\n• "),
                            mainPoints = summary.mainPoints.joinToString("\n• "),
                            examRelevant = summary.examRelevant
                        )
                    )
                } else {
                    val notes = repository.generateNotes(input, "General Studies", subjectContext)
                    currentGeneratedNotes.value = notes
                    repository.saveNote(
                        SavedNote(
                            title = notes.topic,
                            subject = "AI Lecture Notes",
                            chapter = "Video Synthesis",
                            content = notes.quickRevision,
                            subtopics = notes.subtopics.joinToString("\n• "),
                            definitions = notes.definitions.joinToString("\n• "),
                            formulas = notes.formulas.joinToString("\n• "),
                            examples = notes.examples.joinToString("\n• "),
                            examTips = notes.examTips.joinToString("\n• ")
                        )
                    )
                }
                awardXp(30)
            } finally {
                isAnalyzingVideo.value = false
            }
        }
    }

    // Quiz Workflow
    fun startQuizGeneration() {
        viewModelScope.launch {
            isGeneratingQuiz.value = true
            try {
                val qList = repository.generateQuiz(
                    subject = quizSelectedSubject.value,
                    chapter = quizSelectedChapter.value,
                    difficulty = quizDifficulty.value,
                    numQuestions = quizNumQuestions.value,
                    questionType = quizQuestionType.value
                )
                quizQuestionsList.value = qList
                quizCurrentIndex.value = 0
                quizAnswersMap.value = emptyMap()
                quizFinished.value = false
                isQuizActive.value = true
            } finally {
                isGeneratingQuiz.value = false
            }
        }
    }

    fun answerQuizQuestion(questionId: Int, selectedOption: Int) {
        val map = quizAnswersMap.value.toMutableMap()
        map[questionId] = selectedOption
        quizAnswersMap.value = map
    }

    fun submitQuiz() {
        val questions = quizQuestionsList.value
        val answers = quizAnswersMap.value
        var correct = 0
        val weakTopics = mutableListOf<String>()

        questions.forEach { q ->
            val userPick = answers[q.id]
            if (userPick == q.correctOptionIndex) {
                correct++
            } else {
                weakTopics.add(q.topic)
            }
        }

        val total = questions.size
        val pct = if (total > 0) (correct * 100) / total else 0
        quizFinalScore.value = correct
        quizFinished.value = true
        quizWeakTopic.value = if (weakTopics.isNotEmpty()) weakTopics.distinct().joinToString(", ") else "None! Excellent mastery"

        viewModelScope.launch {
            repository.saveQuizAttempt(
                QuizAttempt(
                    subject = quizSelectedSubject.value,
                    chapter = quizSelectedChapter.value,
                    difficulty = quizDifficulty.value,
                    score = correct,
                    totalQuestions = total,
                    percentage = pct,
                    weakTopics = quizWeakTopic.value
                )
            )
            awardXp(correct * 10 + 20)
            refreshRecommendations()
        }
    }

    // Test Workflow
    fun startTestGeneration() {
        viewModelScope.launch {
            isGeneratingTest.value = true
            try {
                val tList = repository.generateTest(
                    subject = testSelectedSubject.value,
                    chapter = testSelectedChapter.value,
                    difficulty = testDifficulty.value,
                    numQuestions = testNumQuestions.value
                )
                testQuestionsList.value = tList
                testCurrentIndex.value = 0
                testAnswersMap.value = emptyMap()
                testFinished.value = false
                isTestActive.value = true
            } finally {
                isGeneratingTest.value = false
            }
        }
    }

    fun submitTestAnswer(questionId: Int, answer: String) {
        val map = testAnswersMap.value.toMutableMap()
        map[questionId] = answer
        testAnswersMap.value = map
    }

    fun finishTest() {
        val questions = testQuestionsList.value
        val total = questions.size
        val score = (total * 0.85).toInt().coerceAtLeast(1)
        testScore.value = score
        testFinished.value = true

        viewModelScope.launch {
            repository.saveTestAttempt(
                TestAttempt(
                    subject = testSelectedSubject.value,
                    chapter = testSelectedChapter.value,
                    difficulty = testDifficulty.value,
                    durationMinutes = 20,
                    score = score,
                    totalQuestions = total,
                    percentage = (score * 100) / total,
                    accuracy = 88
                )
            )
            awardXp(50)
        }
    }

    // Schedule & Reminders
    fun addSchedule(subject: String, topic: String, date: String, startTime: String, endTime: String, priority: String) {
        viewModelScope.launch {
            repository.saveSchedule(
                StudyScheduleItem(
                    subject = subject,
                    chapterOrTopic = topic,
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    priority = priority
                )
            )
        }
    }

    fun toggleSchedule(id: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleScheduleCompleted(id, isCompleted)
            if (isCompleted) awardXp(15)
        }
    }

    fun deleteSchedule(id: Long) {
        viewModelScope.launch {
            repository.deleteSchedule(id)
        }
    }

    fun addReminder(title: String, subject: String, scheduledTime: String, type: String) {
        viewModelScope.launch {
            repository.saveReminder(
                ReminderItem(
                    title = title,
                    subject = subject,
                    scheduledTime = scheduledTime,
                    type = type
                )
            )
        }
    }

    fun toggleReminder(id: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleReminderCompleted(id, isCompleted)
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch {
            repository.deleteReminder(id)
        }
    }

    fun updateChapterProgress(chapterId: Long, status: String, mastery: Int) {
        viewModelScope.launch {
            repository.updateChapterProgress(chapterId, status, mastery)
        }
    }

    fun addAdminChapter(board: String, classLevel: String, subject: String, book: String, title: String, topics: String) {
        viewModelScope.launch {
            repository.addChapter(
                AcademicChapter(
                    board = board,
                    classLevel = classLevel,
                    subject = subject,
                    book = book,
                    chapterNumber = 99,
                    title = title,
                    topics = topics,
                    isCustom = true
                )
            )
        }
    }

    fun deleteAdminChapter(id: Long) {
        viewModelScope.launch {
            repository.deleteChapter(id)
        }
    }

    fun saveCustomNote(title: String, subject: String, content: String) {
        viewModelScope.launch {
            repository.saveNote(
                SavedNote(
                    title = title,
                    subject = subject,
                    chapter = "Personal Notes",
                    content = content
                )
            )
        }
    }

    fun toggleNoteFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavoriteNote(id, isFavorite)
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }

    fun deleteDoubt(id: Long) {
        viewModelScope.launch {
            repository.deleteDoubt(id)
        }
    }

    fun deleteVideoSummary(id: Long) {
        viewModelScope.launch {
            repository.deleteVideoSummary(id)
        }
    }

    fun setPreferredAi(provider: String) {
        viewModelScope.launch {
            val p = userProfile.value ?: return@launch
            repository.saveProfile(p.copy(preferredAi = provider))
        }
    }

    private fun awardXp(amount: Int) {
        viewModelScope.launch {
            val p = userProfile.value ?: return@launch
            val newXp = p.xp + amount
            val newLevel = (newXp / 250) + 1
            repository.saveProfile(p.copy(xp = newXp, level = newLevel))
        }
    }

    fun refreshRecommendations() {
        viewModelScope.launch {
            val p = userProfile.value
            val subList = obSelectedSubjects.value
            val weak = quizWeakTopic.value.split(",").map { it.trim() }.filter { it.isNotEmpty() && !it.contains("None") }
            val rec = repository.getRecommendations(
                username = p?.username ?: "Scholar",
                subjects = subList,
                weakTopics = weak,
                streak = p?.currentStreak ?: 4,
                goals = obGoals.value
            )
            aiRecommendation.value = rec
        }
    }
}
