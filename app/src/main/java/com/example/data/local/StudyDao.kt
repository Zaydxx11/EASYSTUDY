package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface StudyDao {

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    // Academic Catalog
    @Query("SELECT * FROM academic_chapters ORDER BY chapterNumber ASC")
    fun getAllChapters(): Flow<List<AcademicChapter>>

    @Query("SELECT * FROM academic_chapters WHERE subject = :subject ORDER BY chapterNumber ASC")
    fun getChaptersBySubject(subject: String): Flow<List<AcademicChapter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: AcademicChapter): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<AcademicChapter>)

    @Update
    suspend fun updateChapter(chapter: AcademicChapter)

    @Query("UPDATE academic_chapters SET status = :status, masteryPercentage = :mastery WHERE id = :id")
    suspend fun updateChapterProgress(id: Long, status: String, mastery: Int)

    @Query("DELETE FROM academic_chapters WHERE id = :id")
    suspend fun deleteChapter(id: Long)

    // Doubts
    @Query("SELECT * FROM doubts ORDER BY timestamp DESC")
    fun getAllDoubts(): Flow<List<DoubtItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoubt(doubt: DoubtItem): Long

    @Query("DELETE FROM doubts WHERE id = :id")
    suspend fun deleteDoubt(id: Long)

    // Quizzes
    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun getAllQuizAttempts(): Flow<List<QuizAttempt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizAttempt(quiz: QuizAttempt): Long

    // Tests
    @Query("SELECT * FROM test_attempts ORDER BY timestamp DESC")
    fun getAllTestAttempts(): Flow<List<TestAttempt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestAttempt(test: TestAttempt): Long

    // Saved Notes
    @Query("SELECT * FROM saved_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<SavedNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: SavedNote): Long

    @Query("UPDATE saved_notes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateNoteFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM saved_notes WHERE id = :id")
    suspend fun deleteNote(id: Long)

    // Video Summaries
    @Query("SELECT * FROM video_summaries ORDER BY timestamp DESC")
    fun getAllVideoSummaries(): Flow<List<VideoSummary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideoSummary(summary: VideoSummary): Long

    @Query("DELETE FROM video_summaries WHERE id = :id")
    suspend fun deleteVideoSummary(id: Long)

    // Study Schedules
    @Query("SELECT * FROM study_schedules ORDER BY id DESC")
    fun getAllSchedules(): Flow<List<StudyScheduleItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: StudyScheduleItem): Long

    @Query("UPDATE study_schedules SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateScheduleCompleted(id: Long, isCompleted: Boolean)

    @Query("DELETE FROM study_schedules WHERE id = :id")
    suspend fun deleteSchedule(id: Long)

    // Reminders
    @Query("SELECT * FROM reminders ORDER BY id DESC")
    fun getAllReminders(): Flow<List<ReminderItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderItem): Long

    @Query("UPDATE reminders SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateReminderCompleted(id: Long, isCompleted: Boolean)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminder(id: Long)

    // Study Sessions
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession): Long
}
