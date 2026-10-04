package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "academic_chapters")
data class AcademicChapter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val board: String,            // e.g. CBSE, ICSE, State Board, IB, College
    val classLevel: String,       // e.g. Class 10, Class 12, 1st Year
    val subject: String,          // e.g. Mathematics, Physics, Computer Science
    val book: String,             // e.g. NCERT, Oswaal, Standard
    val chapterNumber: Int,
    val title: String,
    val topics: String = "",       // Comma-separated or bullet list of subtopics
    val status: String = "Not Started", // Not Started, Learning, Completed, Needs Revision
    val masteryPercentage: Int = 0,
    val isCustom: Boolean = false
)
