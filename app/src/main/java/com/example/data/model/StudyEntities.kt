package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doubts")
data class DoubtItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val question: String,
    val questionImageUri: String? = null,
    val answer: String,
    val aiProvider: String, // Gemini, ChatGPT, Copilot
    val conceptInvolved: String = "",
    val stepByStepJson: String = "[]",
    val finalAnswer: String = "",
    val explanation: String = "",
    val similarExample: String = "",
    val practiceQuestion: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isSaved: Boolean = true
)

@Entity(tableName = "quiz_attempts")
data class QuizAttempt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val chapter: String,
    val difficulty: String, // Easy, Medium, Hard, Mixed
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
    val weakTopics: String = "", // Comma-separated or advice
    val reviewJson: String = "[]",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "test_attempts")
data class TestAttempt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val chapter: String,
    val difficulty: String,
    val durationMinutes: Int,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
    val accuracy: Int,
    val mistakesJson: String = "[]",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_notes")
data class SavedNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val chapter: String,
    val content: String,
    val subtopics: String = "",
    val definitions: String = "",
    val formulas: String = "",
    val examples: String = "",
    val examTips: String = "",
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "video_summaries")
data class VideoSummary(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val videoUrl: String,
    val title: String,
    val summaryType: String, // Very Short, Short, Detailed, Exam Revision
    val shortSummary: String,
    val detailedSummary: String,
    val keyConcepts: String,
    val formulas: String,
    val mainPoints: String,
    val examRelevant: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

@Entity(tableName = "study_schedules")
data class StudyScheduleItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val chapterOrTopic: String,
    val date: String,          // e.g. "Today", "Monday", "2026-10-04"
    val startTime: String,     // e.g. "05:00 PM"
    val endTime: String,       // e.g. "06:00 PM"
    val repeatMode: String = "None", // Daily, Weekly, Custom
    val priority: String = "Normal", // Low, Normal, High
    val notes: String = "",
    val isCompleted: Boolean = false
)

@Entity(tableName = "reminders")
data class ReminderItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val scheduledTime: String,
    val type: String = "Study", // Study, Test, Quiz, Assignment, Revision
    val isCompleted: Boolean = false
)

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val chapter: String,
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
