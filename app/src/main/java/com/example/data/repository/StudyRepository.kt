package com.example.data.repository

import android.graphics.Bitmap
import com.example.data.ai.AiService
import com.example.data.ai.DoubtSolution
import com.example.data.ai.GeneratedNotesResult
import com.example.data.ai.QuizQuestion
import com.example.data.ai.TestQuestion
import com.example.data.ai.VideoSummaryResult
import com.example.data.local.StudyDao
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
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray

class StudyRepository(
    private val dao: StudyDao,
    private val aiService: AiService = AiService()
) {

    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val allChapters: Flow<List<AcademicChapter>> = dao.getAllChapters()
    val allDoubts: Flow<List<DoubtItem>> = dao.getAllDoubts()
    val allQuizzes: Flow<List<QuizAttempt>> = dao.getAllQuizAttempts()
    val allTests: Flow<List<TestAttempt>> = dao.getAllTestAttempts()
    val allNotes: Flow<List<SavedNote>> = dao.getAllNotes()
    val allVideoSummaries: Flow<List<VideoSummary>> = dao.getAllVideoSummaries()
    val allSchedules: Flow<List<StudyScheduleItem>> = dao.getAllSchedules()
    val allReminders: Flow<List<ReminderItem>> = dao.getAllReminders()
    val allSessions: Flow<List<StudySession>> = dao.getAllSessions()

    fun getChaptersForSubject(subject: String): Flow<List<AcademicChapter>> {
        return dao.getChaptersBySubject(subject)
    }

    suspend fun getUserProfileOnce(): UserProfile? {
        return dao.getUserProfileOnce()
    }

    suspend fun saveProfile(profile: UserProfile) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun updateChapterProgress(chapterId: Long, status: String, mastery: Int) {
        dao.updateChapterProgress(chapterId, status, mastery)
    }

    suspend fun addChapter(chapter: AcademicChapter): Long {
        return dao.insertChapter(chapter)
    }

    suspend fun deleteChapter(chapterId: Long) {
        dao.deleteChapter(chapterId)
    }

    // AI operations
    suspend fun solveDoubt(
        question: String,
        bitmap: Bitmap? = null,
        provider: String = "Gemini",
        studentContext: String = "Class 10 CBSE",
        modeModifier: String = ""
    ): DoubtSolution {
        return aiService.solveDoubt(question, bitmap, provider, studentContext, modeModifier)
    }

    suspend fun saveDoubt(doubt: DoubtItem): Long {
        return dao.insertDoubt(doubt)
    }

    suspend fun deleteDoubt(id: Long) {
        dao.deleteDoubt(id)
    }

    suspend fun summarizeVideo(url: String, type: String, subjectContext: String): VideoSummaryResult {
        return aiService.summarizeVideoOrContent(url, type, subjectContext)
    }

    suspend fun saveVideoSummary(summary: VideoSummary): Long {
        return dao.insertVideoSummary(summary)
    }

    suspend fun deleteVideoSummary(id: Long) {
        dao.deleteVideoSummary(id)
    }

    suspend fun generateNotes(topic: String, subject: String, educationLevel: String): GeneratedNotesResult {
        return aiService.generateNotes(topic, subject, educationLevel)
    }

    suspend fun saveNote(note: SavedNote): Long {
        return dao.insertNote(note)
    }

    suspend fun toggleFavoriteNote(id: Long, isFavorite: Boolean) {
        dao.updateNoteFavorite(id, isFavorite)
    }

    suspend fun deleteNote(id: Long) {
        dao.deleteNote(id)
    }

    suspend fun generateQuiz(
        subject: String,
        chapter: String,
        difficulty: String,
        numQuestions: Int,
        questionType: String
    ): List<QuizQuestion> {
        return aiService.generateQuiz(subject, chapter, difficulty, numQuestions, questionType)
    }

    suspend fun saveQuizAttempt(attempt: QuizAttempt): Long {
        return dao.insertQuizAttempt(attempt)
    }

    suspend fun generateTest(
        subject: String,
        chapter: String,
        difficulty: String,
        numQuestions: Int
    ): List<TestQuestion> {
        return aiService.generateTest(subject, chapter, difficulty, numQuestions)
    }

    suspend fun saveTestAttempt(attempt: TestAttempt): Long {
        return dao.insertTestAttempt(attempt)
    }

    suspend fun saveSchedule(schedule: StudyScheduleItem): Long {
        return dao.insertSchedule(schedule)
    }

    suspend fun toggleScheduleCompleted(id: Long, isCompleted: Boolean) {
        dao.updateScheduleCompleted(id, isCompleted)
    }

    suspend fun deleteSchedule(id: Long) {
        dao.deleteSchedule(id)
    }

    suspend fun saveReminder(reminder: ReminderItem): Long {
        return dao.insertReminder(reminder)
    }

    suspend fun toggleReminderCompleted(id: Long, isCompleted: Boolean) {
        dao.updateReminderCompleted(id, isCompleted)
    }

    suspend fun deleteReminder(id: Long) {
        dao.deleteReminder(id)
    }

    suspend fun logSession(session: StudySession): Long {
        return dao.insertSession(session)
    }

    suspend fun getRecommendations(
        username: String,
        subjects: List<String>,
        weakTopics: List<String>,
        streak: Int,
        goals: List<String>
    ): String {
        return aiService.generateRecommendations(username, subjects, weakTopics, streak, goals)
    }
}
