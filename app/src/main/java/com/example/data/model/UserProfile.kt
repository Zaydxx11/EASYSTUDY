package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val username: String = "",
    val educationLevel: String = "School", // School, College / University, Other
    val classOrCourse: String = "",       // e.g. Class 10 or B.Tech CS
    val collegeDegree: String = "",
    val collegeYear: String = "",
    val collegeSemester: String = "",
    val collegeBranch: String = "",
    val boardOrCurriculum: String = "",   // CBSE, ICSE, State Board, IB, Cambridge, Other
    val stateName: String = "",
    val schoolOrCollege: String = "",
    val subjectsJson: String = "[]",      // List of subjects
    val booksJson: String = "{}",         // Map of Subject -> Book name / publisher
    val studyGoalsJson: String = "[]",    // List of goals
    val dailyStudyHours: Float = 3f,
    val currentStreak: Int = 3,
    val xp: Int = 450,
    val level: Int = 2,
    val preferredAi: String = "Gemini",    // Gemini, ChatGPT, Copilot
    val isNotificationsEnabled: Boolean = true,
    val isDarkMode: Boolean = false,
    val isOnboardingCompleted: Boolean = false,
    val lastStudyDate: Long = System.currentTimeMillis()
)
