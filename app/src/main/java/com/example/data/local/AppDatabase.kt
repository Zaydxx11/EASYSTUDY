package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        AcademicChapter::class,
        DoubtItem::class,
        QuizAttempt::class,
        TestAttempt::class,
        SavedNote::class,
        VideoSummary::class,
        StudyScheduleItem::class,
        ReminderItem::class,
        StudySession::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studyDao(): StudyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "easystudy_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateInitialAcademicCatalog(database.studyDao())
                    }
                }
            }
        }

        suspend fun populateInitialAcademicCatalog(dao: StudyDao) {
            val defaultChapters = listOf(
                // Mathematics (CBSE / School)
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Mathematics", book = "NCERT", chapterNumber = 1, title = "Real Numbers", topics = "Fundamental Theorem of Arithmetic, Irrational Numbers, Decimal Expansions", status = "Completed", masteryPercentage = 95),
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Mathematics", book = "NCERT", chapterNumber = 2, title = "Polynomials", topics = "Geometrical Meaning of Zeroes, Relationship between Zeroes and Coefficients", status = "Completed", masteryPercentage = 88),
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Mathematics", book = "NCERT", chapterNumber = 3, title = "Pair of Linear Equations in Two Variables", topics = "Graphical Method, Algebraic Methods (Substitution, Elimination)", status = "Learning", masteryPercentage = 75),
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Mathematics", book = "NCERT", chapterNumber = 4, title = "Quadratic Equations", topics = "Standard Form, Solutions by Factorization, Quadratic Formula, Nature of Roots", status = "Learning", masteryPercentage = 60),
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Mathematics", book = "NCERT", chapterNumber = 5, title = "Arithmetic Progressions", topics = "nth Term of an AP, Sum of First n Terms of an AP", status = "Needs Revision", masteryPercentage = 45),
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Mathematics", book = "NCERT", chapterNumber = 6, title = "Triangles", topics = "Similar Figures, Basic Proportionality Theorem, Criteria for Similarity", status = "Not Started", masteryPercentage = 0),
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Mathematics", book = "NCERT", chapterNumber = 7, title = "Coordinate Geometry", topics = "Distance Formula, Section Formula", status = "Not Started", masteryPercentage = 0),
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Mathematics", book = "NCERT", chapterNumber = 8, title = "Introduction to Trigonometry", topics = "Trigonometric Ratios, Specific Angles, Trigonometric Identities", status = "Not Started", masteryPercentage = 0),

                // Physics
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Physics", book = "NCERT", chapterNumber = 1, title = "Light: Reflection and Refraction", topics = "Spherical Mirrors, Mirror Formula, Magnification, Refraction, Snell's Law, Lens Power", status = "Completed", masteryPercentage = 90),
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Physics", book = "NCERT", chapterNumber = 2, title = "Human Eye and the Colourful World", topics = "Structure of Eye, Defects of Vision, Atmospheric Refraction, Dispersion, Scattering", status = "Learning", masteryPercentage = 65),
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Physics", book = "NCERT", chapterNumber = 3, title = "Electricity", topics = "Ohm's Law, Resistance and Resistivity, Series and Parallel Circuits, Heating Effect, Electric Power", status = "Needs Revision", masteryPercentage = 40),
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Physics", book = "NCERT", chapterNumber = 4, title = "Magnetic Effects of Electric Current", topics = "Magnetic Field Lines, Right-Hand Thumb Rule, Solenoid, Fleming's Left-Hand Rule, Electromagnetic Induction", status = "Not Started", masteryPercentage = 0),

                // Chemistry
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Chemistry", book = "NCERT", chapterNumber = 1, title = "Chemical Reactions and Equations", topics = "Balanced Chemical Equations, Types of Reactions: Combination, Decomposition, Displacement, Redox", status = "Completed", masteryPercentage = 85),
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Chemistry", book = "NCERT", chapterNumber = 2, title = "Acids, Bases and Salts", topics = "Indicators, Neutralization, pH Scale, Common Salts, Bleaching Powder, Baking Soda, Plaster of Paris", status = "Learning", masteryPercentage = 70),
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Chemistry", book = "NCERT", chapterNumber = 3, title = "Metals and Non-Metals", topics = "Physical and Chemical Properties, Reactivity Series, Ionic Compounds, Metallurgy, Corrosion", status = "Not Started", masteryPercentage = 0),
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Chemistry", book = "NCERT", chapterNumber = 4, title = "Carbon and Its Compounds", topics = "Covalent Bonding, Versatile Nature of Carbon, Homologous Series, Functional Groups, Saponification", status = "Not Started", masteryPercentage = 0),

                // Biology
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Biology", book = "NCERT", chapterNumber = 1, title = "Life Processes", topics = "Nutrition in Plants & Animals, Respiration, Transportation in Humans & Plants, Excretion", status = "Completed", masteryPercentage = 92),
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Biology", book = "NCERT", chapterNumber = 2, title = "Control and Coordination", topics = "Nervous System, Reflex Arc, Human Brain, Plant Hormones, Endocrine Glands", status = "Learning", masteryPercentage = 55),
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Biology", book = "NCERT", chapterNumber = 3, title = "How do Organisms Reproduce?", topics = "Asexual Reproduction, Sexual Reproduction in Plants and Humans, Reproductive Health", status = "Not Started", masteryPercentage = 0),
                AcademicChapter(board = "CBSE", classLevel = "Class 10", subject = "Biology", book = "NCERT", chapterNumber = 4, title = "Heredity and Evolution", topics = "Mendel's Laws, Monohybrid & Dihybrid Cross, Sex Determination", status = "Not Started", masteryPercentage = 0),

                // Computer Science / College
                AcademicChapter(board = "College", classLevel = "B.Tech / College", subject = "Computer Science", book = "Standard", chapterNumber = 1, title = "Data Structures & Algorithms", topics = "Arrays, Linked Lists, Stacks, Queues, Binary Trees, Graph Traversal, Big-O Complexity", status = "Learning", masteryPercentage = 70),
                AcademicChapter(board = "College", classLevel = "B.Tech / College", subject = "Computer Science", book = "Standard", chapterNumber = 2, title = "Database Management Systems", topics = "ER Models, Relational Algebra, SQL Queries, Normalization, ACID Transactions", status = "Completed", masteryPercentage = 85),
                AcademicChapter(board = "College", classLevel = "B.Tech / College", subject = "Computer Science", book = "Standard", chapterNumber = 3, title = "Computer Networks", topics = "OSI Model, TCP/IP, IP Addressing & Subnetting, Routing Protocols, DNS, HTTP/HTTPS", status = "Not Started", masteryPercentage = 0)
            )
            dao.insertChapters(defaultChapters)

            // Seed initial sample quiz attempt for analytics showcase
            dao.insertQuizAttempt(
                QuizAttempt(
                    subject = "Mathematics",
                    chapter = "Real Numbers",
                    difficulty = "Medium",
                    score = 5,
                    totalQuestions = 5,
                    percentage = 100,
                    weakTopics = "None - Excellent Mastery"
                )
            )
            dao.insertQuizAttempt(
                QuizAttempt(
                    subject = "Physics",
                    chapter = "Electricity",
                    difficulty = "Medium",
                    score = 3,
                    totalQuestions = 5,
                    percentage = 60,
                    weakTopics = "Ohm's Law numericals, parallel resistance combinations"
                )
            )

            // Seed initial study sessions for realistic analytics
            dao.insertSession(StudySession(subject = "Mathematics", chapter = "Quadratic Equations", durationMinutes = 55))
            dao.insertSession(StudySession(subject = "Physics", chapter = "Light Refraction", durationMinutes = 45))
            dao.insertSession(StudySession(subject = "Chemistry", chapter = "Acids, Bases and Salts", durationMinutes = 40))

            // Seed study schedule items
            dao.insertSchedule(
                StudyScheduleItem(
                    subject = "Mathematics",
                    chapterOrTopic = "Quadratic Equations - Practice Formula",
                    date = "Today",
                    startTime = "05:00 PM",
                    endTime = "06:00 PM",
                    repeatMode = "Daily",
                    priority = "High",
                    notes = "Complete exercises 4.2 and 4.3"
                )
            )
            dao.insertSchedule(
                StudyScheduleItem(
                    subject = "Physics",
                    chapterOrTopic = "Electricity - Parallel Circuits",
                    date = "Today",
                    startTime = "06:30 PM",
                    endTime = "07:30 PM",
                    repeatMode = "None",
                    priority = "High",
                    notes = "Solve NCERT example problems"
                )
            )

            // Seed reminders
            dao.insertReminder(
                ReminderItem(
                    title = "Time to study Mathematics ⚡",
                    subject = "Mathematics",
                    scheduledTime = "05:00 PM",
                    type = "Study"
                )
            )
            dao.insertReminder(
                ReminderItem(
                    title = "Physics Electricity Chapter Quiz 📝",
                    subject = "Physics",
                    scheduledTime = "Tomorrow 10:00 AM",
                    type = "Quiz"
                )
            )
        }
    }
}
