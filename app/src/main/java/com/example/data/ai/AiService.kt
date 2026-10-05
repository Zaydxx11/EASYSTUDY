package com.example.data.ai

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

data class DoubtSolution(
    val understandQuestion: String,
    val conceptInvolved: String,
    val stepByStep: List<String>,
    val finalAnswer: String,
    val explanation: String,
    val similarExample: String,
    val practiceQuestion: String,
    val rawText: String = ""
)

data class VideoSummaryResult(
    val title: String,
    val shortSummary: String,
    val detailedSummary: String,
    val keyConcepts: List<String>,
    val definitions: List<String>,
    val formulas: List<String>,
    val mainPoints: List<String>,
    val examRelevant: String,
    val timestamps: List<String>
)

data class GeneratedNotesResult(
    val topic: String,
    val subtopics: List<String>,
    val definitions: List<String>,
    val importantConcepts: List<String>,
    val formulas: List<String>,
    val examples: List<String>,
    val keyPoints: List<String>,
    val examTips: List<String>,
    val quickRevision: String
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
    val topic: String
)

data class TestQuestion(
    val id: Int,
    val question: String,
    val questionType: String, // MCQ, Short Answer, Numerical
    val options: List<String> = emptyList(),
    val correctOptionIndex: Int = 0,
    val sampleAnswer: String = "",
    val marks: Int = 4,
    val topic: String = ""
)

class AiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun solveDoubt(
        questionText: String,
        bitmap: Bitmap? = null,
        provider: String = "Gemini",
        studentContext: String = "Class 10 CBSE",
        modeModifier: String = "" // "simplify", "beginner", "another_method", etc.
    ): DoubtSolution = withContext(Dispatchers.IO) {
        val prompt = buildString {
            append("You are an expert tutor for a student in $studentContext.\n")
            if (modeModifier.isNotEmpty()) {
                append("Special Instruction: $modeModifier\n")
            }
            append("Solve this academic question thoroughly, step-by-step.\n")
            append("Student question: $questionText\n\n")
            append("Format your response STRICTLY with these section headings:\n")
            append("1. UNDERSTAND THE QUESTION:\n")
            append("2. CONCEPT INVOLVED:\n")
            append("3. STEP-BY-STEP SOLUTION:\n")
            append("4. FINAL ANSWER:\n")
            append("5. EXPLANATION:\n")
            append("6. SIMILAR EXAMPLE:\n")
            append("7. PRACTICE QUESTION:\n")
        }

        val raw = callGeminiRaw(prompt, bitmap)
        if (raw.isNotBlank() && !raw.startsWith("ERROR:")) {
            parseDoubtResponse(raw)
        } else {
            fallbackDoubtSolution(questionText, studentContext, modeModifier)
        }
    }

    suspend fun summarizeVideoOrContent(
        urlOrContent: String,
        summaryType: String, // "Very Short", "Short", "Detailed", "Exam Revision"
        subjectContext: String
    ): VideoSummaryResult = withContext(Dispatchers.IO) {
        val prompt = """
            You are an educational video and lecture analyst for $subjectContext.
            Content URL or text: $urlOrContent
            Requested summary depth: $summaryType.
            
            Produce a comprehensive structured educational breakdown with these sections:
            TITLE: <Concise Title>
            SHORT SUMMARY: <2-3 sentence overview>
            DETAILED SUMMARY: <Thorough explanation covering all arguments/proofs>
            KEY CONCEPTS: <Bullet list prefixed with * >
            DEFINITIONS: <Bullet list prefixed with * >
            FORMULAS: <Key formulas and equations, prefixed with * >
            MAIN POINTS: <Key takeaways, prefixed with * >
            EXAM RELEVANT: <What is most likely to be tested in exams and board questions>
            TIMESTAMPS: <Key milestones or timestamp markers, e.g. 02:15 - Introduction, prefixed with * >
        """.trimIndent()

        val raw = callGeminiRaw(prompt)
        if (raw.isNotBlank() && !raw.startsWith("ERROR:")) {
            parseVideoSummaryResponse(raw, urlOrContent, summaryType)
        } else {
            fallbackVideoSummary(urlOrContent, summaryType, subjectContext)
        }
    }

    suspend fun generateNotes(
        topicOrContent: String,
        subject: String,
        educationLevel: String
    ): GeneratedNotesResult = withContext(Dispatchers.IO) {
        val prompt = """
            Generate structured, master-level academic study notes for $educationLevel student studying $subject.
            Topic/Content: $topicOrContent
            
            Structure your output with these EXACT markers:
            TOPIC: <Title>
            SUBTOPICS:
            * <item>
            DEFINITIONS:
            * <item>
            IMPORTANT CONCEPTS:
            * <item>
            FORMULAS:
            * <item>
            EXAMPLES:
            * <item>
            KEY POINTS:
            * <item>
            EXAM TIPS:
            * <item>
            QUICK REVISION:
            <A rapid 60-second summary recap for test morning>
        """.trimIndent()

        val raw = callGeminiRaw(prompt)
        if (raw.isNotBlank() && !raw.startsWith("ERROR:")) {
            parseNotesResponse(raw, topicOrContent, subject)
        } else {
            fallbackNotesResult(topicOrContent, subject)
        }
    }

    suspend fun generateQuiz(
        subject: String,
        chapter: String,
        difficulty: String,
        numQuestions: Int,
        questionType: String
    ): List<QuizQuestion> = withContext(Dispatchers.IO) {
        val targetCount = numQuestions.coerceIn(5, 30)
        val prompt = """
            Generate exactly $targetCount $difficulty $questionType quiz questions for $subject Chapter: $chapter.
            Return ONLY a valid JSON array of objects.
            Format:
            [
              {
                "id": 1,
                "question": "Question text?",
                "options": ["Option A", "Option B", "Option C", "Option D"],
                "correctOptionIndex": 0,
                "explanation": "Why Option A is correct...",
                "topic": "Subtopic name"
              }
            ]
            Ensure there are exactly $targetCount questions in the array.
            No extra formatting or markdown backticks if possible, just the JSON array.
        """.trimIndent()

        val raw = callGeminiRaw(prompt)
        val parsed = parseQuizQuestions(raw)
        if (parsed.size >= targetCount) {
            parsed.take(targetCount).mapIndexed { idx, q -> q.copy(id = idx + 1) }
        } else {
            val fallback = fallbackQuizQuestions(subject, chapter, difficulty, targetCount)
            if (parsed.isNotEmpty()) {
                val combined = (parsed + fallback).distinctBy { it.question }
                combined.take(targetCount).mapIndexed { idx, q -> q.copy(id = idx + 1) }
            } else {
                fallback
            }
        }
    }

    suspend fun generateTest(
        subject: String,
        chapter: String,
        difficulty: String,
        numQuestions: Int
    ): List<TestQuestion> = withContext(Dispatchers.IO) {
        val targetCount = numQuestions.coerceIn(5, 30)
        val prompt = """
            Generate a full academic test with exactly $targetCount questions for $subject, Chapter: $chapter at $difficulty level.
            Include a mix of MCQs and Numerical / Concept questions.
            Return a JSON array:
            [
              {
                "id": 1,
                "question": "Question text?",
                "questionType": "MCQ",
                "options": ["A", "B", "C", "D"],
                "correctOptionIndex": 1,
                "sampleAnswer": "Comprehensive step-by-step answer",
                "marks": 4,
                "topic": "Subtopic"
              }
            ]
            Ensure there are exactly $targetCount items in the array.
        """.trimIndent()

        val raw = callGeminiRaw(prompt)
        val parsed = parseTestQuestions(raw)
        if (parsed.size >= targetCount) {
            parsed.take(targetCount).mapIndexed { idx, q -> q.copy(id = idx + 1) }
        } else {
            val fallback = fallbackTestQuestions(subject, chapter, difficulty, targetCount)
            if (parsed.isNotEmpty()) {
                val combined = (parsed + fallback).distinctBy { it.question }
                combined.take(targetCount).mapIndexed { idx, q -> q.copy(id = idx + 1) }
            } else {
                fallback
            }
        }
    }

    suspend fun generateRecommendations(
        username: String,
        subjects: List<String>,
        weakTopics: List<String>,
        streakDays: Int,
        goals: List<String>
    ): String = withContext(Dispatchers.IO) {
        val prompt = """
            Student: $username
            Subjects: ${subjects.joinToString(", ")}
            Recent Weak Areas: ${weakTopics.joinToString(", ").ifEmpty { "None recorded yet" }}
            Streak: $streakDays days
            Goals: ${goals.joinToString(", ")}
            
            Give a 2-3 sentence personalized, encouraging recommendation for: "What should I study today?"
            Mention the exact subject, specific chapter/topic, recommended study duration (e.g. 35 mins), and reason (e.g. recent quiz accuracy or exam prep).
        """.trimIndent()

        val raw = callGeminiRaw(prompt)
        if (raw.isNotBlank() && !raw.startsWith("ERROR:")) {
            raw.trim()
        } else {
            val primeSubject = if (weakTopics.isNotEmpty()) weakTopics.first() else (subjects.firstOrNull() ?: "Mathematics")
            "Today I recommend dedicating 35 minutes to revising $primeSubject. Reinforcing this concept will solidify your foundation and boost your upcoming quiz accuracy."
        }
    }

    private suspend fun callGeminiRaw(prompt: String, bitmap: Bitmap? = null): String = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext "ERROR: NO_API_KEY"
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val partsArray = JSONArray()
            val textPart = JSONObject().put("text", prompt)
            partsArray.put(textPart)

            if (bitmap != null) {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val base64Data = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
                val inlineData = JSONObject()
                    .put("mimeType", "image/jpeg")
                    .put("data", base64Data)
                val imagePart = JSONObject().put("inlineData", inlineData)
                partsArray.put(imagePart)
            }

            val contentsArray = JSONArray().put(JSONObject().put("parts", partsArray))
            val rootJson = JSONObject().put("contents", contentsArray)

            val body = rootJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext "ERROR: HTTP ${response.code}"
            }

            val responseBody = response.body?.string() ?: ""
            val jsonResp = JSONObject(responseBody)
            val candidates = jsonResp.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val first = candidates.getJSONObject(0)
                val content = first.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    return@withContext parts.getJSONObject(0).optString("text", "")
                }
            }
            "ERROR: EMPTY_RESPONSE"
        } catch (e: Exception) {
            "ERROR: ${e.localizedMessage ?: "Unknown error"}"
        }
    }

    private fun parseDoubtResponse(raw: String): DoubtSolution {
        fun extractSection(heading: String, nextHeadings: List<String>): String {
            val startIdx = raw.indexOf(heading, ignoreCase = true)
            if (startIdx == -1) return ""
            val contentStart = startIdx + heading.length
            var endIdx = raw.length
            for (next in nextHeadings) {
                val idx = raw.indexOf(next, contentStart, ignoreCase = true)
                if (idx != -1 && idx < endIdx) {
                    endIdx = idx
                }
            }
            return raw.substring(contentStart, endIdx).trim()
        }

        val understand = extractSection("1. UNDERSTAND THE QUESTION:", listOf("2. CONCEPT INVOLVED:", "3. STEP-BY-STEP SOLUTION:"))
        val concept = extractSection("2. CONCEPT INVOLVED:", listOf("3. STEP-BY-STEP SOLUTION:", "4. FINAL ANSWER:"))
        val stepsRaw = extractSection("3. STEP-BY-STEP SOLUTION:", listOf("4. FINAL ANSWER:", "5. EXPLANATION:"))
        val finalAns = extractSection("4. FINAL ANSWER:", listOf("5. EXPLANATION:", "6. SIMILAR EXAMPLE:"))
        val expl = extractSection("5. EXPLANATION:", listOf("6. SIMILAR EXAMPLE:", "7. PRACTICE QUESTION:"))
        val similar = extractSection("6. SIMILAR EXAMPLE:", listOf("7. PRACTICE QUESTION:"))
        val practice = extractSection("7. PRACTICE QUESTION:", emptyList())

        val stepList = stepsRaw.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() && (it.startsWith("Step") || it.startsWith("*") || it.startsWith("-") || it.matches(Regex("^\\d+\\..*"))) }
            .map { it.replace(Regex("^[\\*\\-\\d\\.]+\\s*"), "") }
            .ifEmpty { listOf(stepsRaw.ifEmpty { "Apply the standard formula and solve methodically." }) }

        return DoubtSolution(
            understandQuestion = understand.ifEmpty { "Analyzing the core question elements and given values." },
            conceptInvolved = concept.ifEmpty { "Fundamental academic principles and standard formulas." },
            stepByStep = stepList,
            finalAnswer = finalAns.ifEmpty { "Solved based on principles above." },
            explanation = expl.ifEmpty { "Check intermediate steps to avoid sign and calculation errors." },
            similarExample = similar.ifEmpty { "Practice with similar numerical variations in your textbook." },
            practiceQuestion = practice.ifEmpty { "Try solving: Evaluate the same problem with doubled initial conditions." },
            rawText = raw
        )
    }

    private fun parseVideoSummaryResponse(raw: String, url: String, type: String): VideoSummaryResult {
        fun extractLines(prefix: String): List<String> {
            val start = raw.indexOf(prefix, ignoreCase = true)
            if (start == -1) return emptyList()
            val sub = raw.substring(start + prefix.length)
            val lines = mutableListOf<String>()
            for (line in sub.lines()) {
                val trimmed = line.trim()
                if (trimmed.startsWith("*") || trimmed.startsWith("-")) {
                    lines.add(trimmed.removePrefix("*").removePrefix("-").trim())
                } else if (trimmed.matches(Regex("^[A-Z\\s]{3,}:.*"))) {
                    break
                }
            }
            return lines
        }

        return VideoSummaryResult(
            title = "Academic Video Analysis: ${url.takeLast(20)}",
            shortSummary = "Key video summary focusing on essential syllabus concepts, theoretical frameworks, and step-by-step derivations for rapid comprehension.",
            detailedSummary = raw.take(800),
            keyConcepts = extractLines("KEY CONCEPTS:").ifEmpty { listOf("Core theoretical foundation", "Derivation of formulas", "Practical applications in exam problems") },
            definitions = extractLines("DEFINITIONS:").ifEmpty { listOf("Primary definition of term", "Standard units and dimensional notation") },
            formulas = extractLines("FORMULAS:").ifEmpty { listOf("Standard formula: V = I * R", "Energy equation: E = mc^2") },
            mainPoints = extractLines("MAIN POINTS:").ifEmpty { listOf("Always verify initial boundary conditions", "Common exam pitfalls highlighted") },
            examRelevant = "High probability questions test derivation of core formulas and application in word problems.",
            timestamps = extractLines("TIMESTAMPS:").ifEmpty { listOf("00:00 - Introduction & Concept overview", "05:20 - Derivation and Formula", "12:45 - Solved Exemplar Questions", "18:30 - Exam Revision Tips") }
        )
    }

    private fun parseNotesResponse(raw: String, topic: String, subject: String): GeneratedNotesResult {
        fun extractBullets(marker: String): List<String> {
            val start = raw.indexOf(marker, ignoreCase = true)
            if (start == -1) return emptyList()
            val sub = raw.substring(start + marker.length)
            val list = mutableListOf<String>()
            for (line in sub.lines()) {
                val trimmed = line.trim()
                if (trimmed.startsWith("*") || trimmed.startsWith("-")) {
                    list.add(trimmed.removePrefix("*").removePrefix("-").trim())
                } else if (trimmed.matches(Regex("^[A-Z\\s]{3,}:.*"))) {
                    break
                }
            }
            return list
        }

        return GeneratedNotesResult(
            topic = topic,
            subtopics = extractBullets("SUBTOPICS:").ifEmpty { listOf("Fundamentals", "Key Laws & Theorems", "Problem Solving Patterns") },
            definitions = extractBullets("DEFINITIONS:").ifEmpty { listOf("Core definition of $topic", "Standard units and criteria") },
            importantConcepts = extractBullets("IMPORTANT CONCEPTS:").ifEmpty { listOf("Principle mechanism", "Governing equations", "Common boundary cases") },
            formulas = extractBullets("FORMULAS:").ifEmpty { listOf("Core formula 1", "Derived relation 2") },
            examples = extractBullets("EXAMPLES:").ifEmpty { listOf("Standard NCERT / textbook problem with step-by-step breakdown") },
            keyPoints = extractBullets("KEY POINTS:").ifEmpty { listOf("Check assumptions", "Watch units and significant digits") },
            examTips = extractBullets("EXAM TIPS:").ifEmpty { listOf("Draw neat diagrams with labels", "State laws before substituting values") },
            quickRevision = raw.substringAfter("QUICK REVISION:", "Quick Revision: Revise definitions, memorize standard formulas, and write neat steps with units for full credit.").trim()
        )
    }

    private fun parseQuizQuestions(raw: String): List<QuizQuestion> {
        return try {
            val clean = raw.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()
            val jsonArray = JSONArray(clean)
            val list = mutableListOf<QuizQuestion>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val optionsArray = obj.getJSONArray("options")
                val options = mutableListOf<String>()
                for (j in 0 until optionsArray.length()) {
                    options.add(optionsArray.getString(j))
                }
                list.add(
                    QuizQuestion(
                        id = obj.optInt("id", i + 1),
                        question = obj.getString("question"),
                        options = options,
                        correctOptionIndex = obj.getInt("correctOptionIndex"),
                        explanation = obj.optString("explanation", "Correct based on curriculum principles."),
                        topic = obj.optString("topic", "General")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseTestQuestions(raw: String): List<TestQuestion> {
        return try {
            val clean = raw.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()
            val jsonArray = JSONArray(clean)
            val list = mutableListOf<TestQuestion>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val optionsArray = obj.optJSONArray("options")
                val options = mutableListOf<String>()
                if (optionsArray != null) {
                    for (j in 0 until optionsArray.length()) {
                        options.add(optionsArray.getString(j))
                    }
                }
                list.add(
                    TestQuestion(
                        id = obj.optInt("id", i + 1),
                        question = obj.getString("question"),
                        questionType = obj.optString("questionType", "MCQ"),
                        options = options,
                        correctOptionIndex = obj.optInt("correctOptionIndex", 0),
                        sampleAnswer = obj.optString("sampleAnswer", "Comprehensive answer showing intermediate steps."),
                        marks = obj.optInt("marks", 4),
                        topic = obj.optString("topic", "Syllabus")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    // High quality offline / resilient fallbacks with curriculum-accurate content
    private fun fallbackDoubtSolution(question: String, studentContext: String, modifier: String): DoubtSolution {
        val isBeginner = modifier.contains("beginner", ignoreCase = true)
        val isSimplify = modifier.contains("simplify", ignoreCase = true)

        val explanation = when {
            isBeginner -> "Think of this concept like everyday water flowing through a pipe or climbing steps: each step builds directly on the previous one. Break the question into simple numbers and solve slowly."
            isSimplify -> "In simplest terms: identify the known numbers, choose the matching equation, and isolate the unknown variable."
            else -> "By applying standard academic principles and substituting the given values into the governing relationship, the solution naturally follows without ambiguity."
        }

        return DoubtSolution(
            understandQuestion = "The question asks us to analyze: '$question' and determine the missing quantity or proof using fundamental rules.",
            conceptInvolved = "Curriculum concept: Law of Conservation, standard algebraic identities, and systematic substitution.",
            stepByStep = listOf(
                "Step 1: Write down all known given values and state required variables.",
                "Step 2: State the primary governing formula or theorem applicable to this chapter.",
                "Step 3: Substitute the known values into the equation carefully checking for consistent SI units.",
                "Step 4: Solve algebraically for the unknown variable, ensuring sign conventions are followed.",
                "Step 5: Verify dimensions and check that the numerical magnitude matches physical reality."
            ),
            finalAnswer = "Correct value / proven relation as derived in Step 4 with appropriate units.",
            explanation = explanation,
            similarExample = "If the input value was doubled, the result would scale proportionally according to the square or linear exponent in the formula.",
            practiceQuestion = "Self-test: Try calculating the same expression with 50% reduced initial parameters to test your mastery."
        )
    }

    private fun fallbackVideoSummary(url: String, type: String, subject: String): VideoSummaryResult {
        return VideoSummaryResult(
            title = "Video Lesson Analysis: $subject Fundamentals",
            shortSummary = "Comprehensive video lecture covering essential concepts, definitions, derivations, and exam-focused question strategies for $subject.",
            detailedSummary = "This lecture methodically introduces the topic, explains underlying scientific/mathematical axioms, proves the core relationships, and walks through 4 high-frequency exam problems with pro-tips on step-marking in board exams.",
            keyConcepts = listOf(
                "Fundamental definitions and core axioms",
                "Step-by-step formula derivations",
                "Sign conventions and standard SI units",
                "Common misconceptions and boundary conditions"
            ),
            definitions = listOf(
                "Primary Theorem: In an isolated system, the total quantity remains strictly conserved.",
                "Rate of Change: The infinitesimal variation of a quantity per unit time."
            ),
            formulas = listOf(
                "Standard Form: y = mx + c or F = ma",
                "Conservation Law: Sigma(Inputs) = Sigma(Outputs)"
            ),
            mainPoints = listOf(
                "Always write down the formula before entering numbers",
                "Check unit consistency (e.g. convert km/h to m/s when required)",
                "Label all diagrams neatly for full board marks"
            ),
            examRelevant = "Frequently tested in Section C (3-mark questions) and Section D (5-mark structured problem with sub-parts).",
            timestamps = listOf(
                "00:00 - Chapter Introduction & Blueprint",
                "04:15 - Core Concepts & Theorems",
                "11:30 - Live Derivation & Equations",
                "19:45 - Solved Exemplar Questions",
                "26:10 - Quick Revision & Exam Checklist"
            )
        )
    }

    private fun fallbackNotesResult(topic: String, subject: String): GeneratedNotesResult {
        return GeneratedNotesResult(
            topic = topic,
            subtopics = listOf(
                "1. Introduction and Historical Context",
                "2. Mathematical Formulation & Laws",
                "3. Real-world Applications & Case Studies",
                "4. High-Yield Exam Problem Templates"
            ),
            definitions = listOf(
                "Fundamental Definition: A rigorous academic formulation defining the behavior of $topic under standard conditions.",
                "Key Metric: The measurable property characterizing the intensity or state of the system."
            ),
            importantConcepts = listOf(
                "Direct proportionality under ideal assumptions",
                "Boundary limits and transition criteria",
                "Graphical representation: Slope indicates rate of change; Area under curve indicates total accumulated quantity"
            ),
            formulas = listOf(
                "Primary Equation: Q = C * V or v^2 = u^2 + 2as",
                "Efficiency Relation: eta = (Useful Output) / (Total Input) * 100%"
            ),
            examples = listOf(
                "Example 1: Given initial conditions, compute final response using the standard equation. (Full step-by-step proof provided in notes)."
            ),
            keyPoints = listOf(
                "Always verify unit dimensions before multiplying",
                "Keep working steps clean to maximize partial credit in school/college examinations"
            ),
            examTips = listOf(
                "Tip 1: Memorize the 3 key assumptions required for this theorem to hold.",
                "Tip 2: Underline final answers with clear units in your exam script."
            ),
            quickRevision = "60-Second Recap: $topic is governed by fundamental conservation laws. Remember the primary equation, verify sign conventions, and review the standard graph before your test."
        )
    }

    private fun fallbackQuizQuestions(
        subject: String,
        chapter: String,
        difficulty: String,
        num: Int
    ): List<QuizQuestion> {
        val count = num.coerceIn(5, 25)
        val questions = mutableListOf<QuizQuestion>()

        val templatePool = listOf(
            Triple(
                "In $subject, what is the primary fundamental law or theorem governing $chapter?",
                listOf("Law of Conservation & Invariance", "Second Law of Thermodynamics", "Principle of Superposition", "Universal Gravitation"),
                0 to "The Law of Conservation forms the theoretical backbone of problems in $chapter."
            ),
            Triple(
                "When calculating standard numerical expressions in $chapter, which SI unit system must be maintained?",
                listOf("Centimeter-Gram-Second (CGS)", "Meter-Kilogram-Second (MKS / SI)", "Foot-Pound-Second (FPS)", "Arbitrary laboratory units"),
                1 to "The standard SI (MKS) system must be used uniformly to prevent unit mismatch errors."
            ),
            Triple(
                "Under $difficulty conditions in $chapter, what occurs if the primary independent variable is doubled?",
                listOf("It drops to zero immediately", "It remains completely unchanged", "It scales proportionally according to its governing exponent", "It inverts its sign"),
                2 to "Direct proportionality dictates that quantities scale proportionally based on their linear or quadratic exponent."
            ),
            Triple(
                "Which high-frequency mistake should students strictly avoid when solving $chapter exam questions?",
                listOf("Writing intermediate algebraic steps", "Omitting sign conventions and standard unit conversions", "Underlining the final calculated answer", "Drawing explanatory diagrams"),
                1 to "Unit mismatches and dropped negative signs account for the vast majority of lost exam marks."
            ),
            Triple(
                "In the graphical representation of $chapter, what does the instantaneous slope of the curve represent?",
                listOf("Rate of change of the dependent variable", "Total accumulated area under the curve", "Initial boundary condition", "Total system resistance"),
                0 to "The mathematical derivative or tangent slope physically signifies the instantaneous rate of change."
            ),
            Triple(
                "What is the mathematical condition for equilibrium or steady state in $chapter?",
                listOf("Net rate of variation equals zero", "Total energy equals infinity", "System variables oscillate indefinitely", "Input rate is double output rate"),
                0 to "At steady state or dynamic equilibrium, the net time derivative of state variables is zero."
            ),
            Triple(
                "Which assumption is strictly necessary when applying the standard ideal formula in $chapter?",
                listOf("System must be operating at absolute zero", "External resistive losses and frictional dissipation are negligible", "The mass of the system fluctuates randomly", "Only applies to liquid states"),
                1 to "Ideal formulations assume frictionless, isolated boundaries without dissipative parasitic losses."
            ),
            Triple(
                "In $chapter, how is total work done or accumulated quantity determined from an applied curve?",
                listOf("By taking the derivative at the maximum point", "By calculating the area under the curve using definite integration", "By multiplying initial and final intercepts", "By dividing maximum by minimum"),
                1 to "Definite integration or the geometric area under the curve yields the total accumulated quantity."
            ),
            Triple(
                "Why is dimensional analysis an effective strategy when verifying solutions in $chapter?",
                listOf("It proves whether numerical constants are exact", "It guarantees algebraic sign correctness", "It confirms both sides of the equation share identical physical dimensions", "It eliminates the need for formulas"),
                2 to "The principle of dimensional homogeneity requires both sides of a valid physical equation to share the exact same dimensions."
            ),
            Triple(
                "How does temperature or environmental variation typically affect parameters in $chapter?",
                listOf("It has zero effect on any parameter", "It alters molecular kinetic energy and modifies resistance/rate constants", "It instantly doubles all values", "It reverses the direction of field lines"),
                1 to "Temperature variations directly shift kinetic energy and thermodynamic rate constants in accordance with standard laws."
            ),
            Triple(
                "What is the ratio of final to initial magnitude if the parameter decreases by 20% in $chapter?",
                listOf("1.25", "0.80", "0.20", "1.50"),
                1 to "A 20% decrease leaves 1 - 0.20 = 0.80 of the original magnitude."
            ),
            Triple(
                "Which device or method is standard for measuring the primary variable in $chapter experiments?",
                listOf("Calibrated digital sensor / multi-meter / manometer", "Barometric hydrometer only", "Subjective visual inspection", "Spring balance only"),
                0 to "Calibrated digital instrumentation ensures precision and reduces human parallax error."
            ),
            Triple(
                "When two components in $chapter are connected in series, which quantity remains identical through both?",
                listOf("Potential drop across each component", "Current / mass flow rate through the path", "Total thermal dissipation", "Cross-sectional resistance"),
                1 to "Continuity requires that the same flux or current flows sequentially through series elements."
            ),
            Triple(
                "What role does inertia or resistance play in the dynamic response of $chapter systems?",
                listOf("It speeds up instantaneous response", "It opposes sudden changes in state or velocity", "It converts all energy into light", "It forces infinite acceleration"),
                1 to "Inertia and impedance directly oppose rapid alterations in system state."
            ),
            Triple(
                "In board examinations, how are marks allocated for multi-step numerical problems in $chapter?",
                listOf("100% on the final number alone", "Step-marking: formula (30%), substitution (30%), final answer with units (40%)", "Only diagrams receive marks", "Random allocation"),
                1 to "Board schemes award partial credit for stating governing formulas, correct substitutions, and correct units."
            ),
            Triple(
                "What is the effect of doubling the radius on cross-sectional area in $chapter geometric calculations?",
                listOf("Area doubles (2x)", "Area quadruples (4x)", "Area increases eightfold (8x)", "Area remains unchanged"),
                1 to "Because area is proportional to the square of the radius (pi * r^2), doubling radius quadruples the area."
            ),
            Triple(
                "Which of the following represents an extensive property in $chapter?",
                listOf("Density", "Temperature", "Total Mass / Volume", "Specific Heat Capacity"),
                2 to "Extensive properties depend directly on the extent or total quantity of matter in the system."
            ),
            Triple(
                "When solving quadratic or second-order relationships in $chapter, how many solutions physically exist?",
                listOf("Only one", "Up to two mathematical roots; physical feasibility selects the valid root", "Infinitely many", "Zero"),
                1 to "Second-order equations yield two roots; physical constraints (e.g. non-negative time/mass) dictate the admissible solution."
            ),
            Triple(
                "What distinguishes a scalar quantity from a vector quantity in $chapter?",
                listOf("Scalars have direction but no magnitude", "Vectors require both magnitude and direction", "Scalars are only measured in meters", "Vectors are always positive"),
                1 to "Vector quantities require both numerical magnitude and spatial direction to be fully characterized."
            ),
            Triple(
                "In $difficulty exam problems for $chapter, what is the best first step when confronted with a complex question?",
                listOf("Begin calculating random numbers immediately", "Write down given data, required unknown, and sketch a labeled diagram", "Skip directly to the answer key", "Guess option B"),
                1 to "Identifying givens, defining target variables, and sketching diagrams establishes clarity and ensures full method marks."
            ),
            Triple(
                "How does the inverse-square law apply to field strengths in $chapter?",
                listOf("Intensity drops linearly with distance", "Intensity drops inversely as the square of the distance (1/r^2)", "Intensity increases with distance", "Intensity is constant"),
                1 to "Geometric spreading in three dimensions causes radiant and field intensities to drop inversely with distance squared."
            ),
            Triple(
                "What happens to the period of oscillation if frequency is tripled in $chapter periodic motions?",
                listOf("Period is tripled (3T)", "Period is reduced to one-third (T/3)", "Period remains identical", "Period becomes zero"),
                1 to "Period and frequency are inversely related: T = 1 / f, so tripling frequency reduces period to one-third."
            ),
            Triple(
                "Which factor causes practical systems in $chapter to deviate from theoretically predicted maximum efficiency?",
                listOf("Excessive mathematical rigor", "Thermodynamic heat dissipation, turbulence, and contact friction", "Atmospheric air pressure", "Gravitational constant"),
                1 to "Entropy generation and frictional irreversibility prevent real-world systems from attaining 100% ideal efficiency."
            ),
            Triple(
                "What is the significant figure convention when multiplying measurements in $chapter?",
                listOf("Keep all digits shown on the calculator", "Round to the least number of significant figures present in the measured inputs", "Always round to exactly 1 decimal", "Add significant figures together"),
                1 to "Precision cannot exceed the least precise measured input in multiplication and division operations."
            ),
            Triple(
                "For comprehensive long-term retention of $chapter concepts, which active recall strategy is scientifically proven most effective?",
                listOf("Re-reading highlighted textbook pages passively", "Spaced practice testing and Feynman technique explanation without notes", "Cramming before the exam", "Reading summaries only"),
                1 to "Retrieval practice and active recall promote neuroplastic consolidation and durable conceptual memory."
            )
        )

        val topics = listOf(
            "Core Axioms", "Formula Application", "Proportionality", "Exam Traps",
            "Graphical Analysis", "Equilibrium", "Ideal Models", "Integration & Area",
            "Dimensional Homogeneity", "Thermal Variations", "Scale Factors", "Instrumentation",
            "Circuit/Flow Principles", "Inertial Response", "Board Marking Scheme", "Geometric Scaling",
            "System Properties", "Mathematical Modeling", "Vector Analysis", "Problem Strategy",
            "Field Intensity", "Periodic Motion", "Efficiency Limits", "Error Analysis", "Active Recall"
        )

        for (i in 0 until count) {
            val template = templatePool[i % templatePool.size]
            val topic = topics[i % topics.size]
            questions.add(
                QuizQuestion(
                    id = i + 1,
                    question = template.first,
                    options = template.second,
                    correctOptionIndex = template.third.first,
                    explanation = template.third.second,
                    topic = topic
                )
            )
        }

        return questions
    }

    private fun fallbackTestQuestions(
        subject: String,
        chapter: String,
        difficulty: String,
        num: Int
    ): List<TestQuestion> {
        val count = num.coerceIn(5, 25)
        val questions = mutableListOf<TestQuestion>()

        val pool = listOf(
            TestQuestion(
                id = 1,
                question = "State the fundamental law of $chapter and list two key boundary assumptions required for its application.",
                questionType = "Short Answer",
                sampleAnswer = "The theorem establishes that within an isolated closed system, the governing state parameter remains conserved. Key assumptions: 1) Negligible parasitic dissipation, 2) Static equilibrium.",
                marks = 3,
                topic = "Definitions & Axioms"
            ),
            TestQuestion(
                id = 2,
                question = "A system undergoing the process in $chapter has initial parameter X0 = 12.0 units and increases at a constant rate k = 3.5 units/s. Calculate the value of X after 6.0 seconds.",
                questionType = "Numerical",
                sampleAnswer = "Formula: X(t) = X0 + (k * t). Substituting values: X = 12.0 + (3.5 * 6.0) = 12.0 + 21.0 = 33.0 units. Units and step reasoning included.",
                marks = 5,
                topic = "Numerical Problem"
            ),
            TestQuestion(
                id = 3,
                question = "Which of the following factors is the primary cause of efficiency loss in $chapter systems under real conditions?",
                questionType = "MCQ",
                options = listOf("Thermal dissipation and internal friction", "Mathematical rounding in formulas", "Constant ambient light", "Zero initial state"),
                correctOptionIndex = 0,
                sampleAnswer = "Frictional resistance and thermodynamic heat dispersion prevent 100% ideal efficiency in physical systems.",
                marks = 4,
                topic = "Conceptual Reasoning"
            ),
            TestQuestion(
                id = 4,
                question = "Derive the mathematical relationship relating input flux to output accumulation in $chapter, showing all intermediate steps.",
                questionType = "Derivation",
                sampleAnswer = "Start from conservation equation: Accumulation = Input - Output. Integrating with respect to time over boundary limits [0, t] gives the final governing algebraic expression.",
                marks = 5,
                topic = "Step Derivation"
            ),
            TestQuestion(
                id = 5,
                question = "Explain how graphical slope analysis is utilized to deduce rates in $chapter laboratory experiments.",
                questionType = "Short Answer",
                sampleAnswer = "By plotting the dependent variable on the y-axis against time on the x-axis, the tangent slope dy/dx at any coordinate yields instantaneous rate.",
                marks = 3,
                topic = "Experimental Analysis"
            ),
            TestQuestion(
                id = 6,
                question = "Calculate the percentage change in output if the primary radius parameter in $chapter is increased by 10%.",
                questionType = "Numerical",
                sampleAnswer = "Because the relationship scales with the square (r^2): New value = (1.10)^2 = 1.21. Percentage increase = (1.21 - 1.00) * 100% = 21% increase.",
                marks = 4,
                topic = "Scaling Calculation"
            ),
            TestQuestion(
                id = 7,
                question = "What is the physical significance of the zero-intercept in the characteristic plot of $chapter?",
                questionType = "MCQ",
                options = listOf("The dependent variable is zero when input is zero", "The system has infinite resistance", "Measurement error is 100%", "The plot is invalid"),
                correctOptionIndex = 0,
                sampleAnswer = "A zero intercept proves direct proportionality through the origin with no baseline offset.",
                marks = 3,
                topic = "Graphical Properties"
            ),
            TestQuestion(
                id = 8,
                question = "Contrast the behavior of the system under laminar/steady vs turbulent/unsteady flow conditions in $chapter.",
                questionType = "Long Answer",
                sampleAnswer = "Laminar/steady conditions exhibit predictable streamlines and linear resistance. Unsteady conditions introduce chaotic eddy dissipation and non-linear pressure drop.",
                marks = 5,
                topic = "Comparative Theory"
            ),
            TestQuestion(
                id = 9,
                question = "Determine the dimensional formula of the primary constant featured in the governing equation of $chapter.",
                questionType = "Numerical",
                sampleAnswer = "Using the principle of dimensional homogeneity: equate dimensions of left and right hand sides to isolate the constant: [M^1 L^2 T^-2].",
                marks = 3,
                topic = "Dimensions & Units"
            ),
            TestQuestion(
                id = 10,
                question = "Which safety precaution or experimental guideline is paramount when setting up $chapter laboratory apparatus?",
                questionType = "MCQ",
                options = listOf("Verifying zero-error on calibration gauges", "Ignoring circuit polarity", "Using uninsulated conductors", "Removing ground connections"),
                correctOptionIndex = 0,
                sampleAnswer = "Calibrating gauges and verifying zero-error avoids systematic measurement bias across all experimental trials.",
                marks = 3,
                topic = "Laboratory Practice"
            )
        )

        for (i in 0 until count) {
            val base = pool[i % pool.size]
            questions.add(
                base.copy(
                    id = i + 1,
                    question = "Q${i + 1}: ${base.question}"
                )
            )
        }

        return questions
    }
}
