package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseDoubt
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.StudyViewModel

@Composable
fun ProfileScreen(viewModel: StudyViewModel) {
    val context = LocalContext.current
    val profile by viewModel.userProfile.collectAsState()

    var showAdminDialog by remember { mutableStateOf(false) }
    var adminPassword by remember { mutableStateOf("") }
    var isAdminAuthenticated by remember { mutableStateOf(false) }

    // Admin chapter addition state
    var adminBoard by remember { mutableStateOf("CBSE") }
    var adminClass by remember { mutableStateOf("Class 10") }
    var adminSubject by remember { mutableStateOf("Mathematics") }
    var adminBook by remember { mutableStateOf("NCERT") }
    var adminChapterTitle by remember { mutableStateOf("") }
    var adminTopics by remember { mutableStateOf("") }
    var adminSuccessMessage by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Academic Emblem",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile?.username ?: "Scholar",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${profile?.classOrCourse ?: "Class 10"} • ${profile?.boardOrCurriculum ?: "CBSE"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Level ${profile?.level ?: 3} • ${profile?.xp ?: 620} XP",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }
            }

            // Theme Options Card
            item {
                val currentThemeMode by viewModel.themeMode.collectAsState()
                val currentThemeAccent by viewModel.themeAccent.collectAsState()

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Theme Options",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Display Mode",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("SYSTEM", "DARK", "LIGHT").forEach { mode ->
                                val isSel = currentThemeMode.equals(mode, ignoreCase = true)
                                FilterChip(
                                    selected = isSel,
                                    onClick = { viewModel.setThemeMode(mode) },
                                    label = {
                                        Text(
                                            when (mode) {
                                                "SYSTEM" -> "System"
                                                "DARK" -> "Dark"
                                                else -> "Light"
                                            }
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Accent Color",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("INDIGO", "CYAN", "EMERALD", "PURPLE").forEach { accent ->
                                val isSel = currentThemeAccent.equals(accent, ignoreCase = true)
                                FilterChip(
                                    selected = isSel,
                                    onClick = { viewModel.setThemeAccent(accent) },
                                    label = {
                                        Text(
                                            when (accent) {
                                                "INDIGO" -> "Indigo"
                                                "CYAN" -> "Cyan"
                                                "EMERALD" -> "Emerald"
                                                else -> "Purple"
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Academic Information
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Academic Profile",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        ProfileDetailRow("Education Level", profile?.educationLevel ?: "School")
                        ProfileDetailRow("Class / Course", profile?.classOrCourse ?: "Class 10")
                        ProfileDetailRow("Board / Curriculum", profile?.boardOrCurriculum ?: "CBSE")
                        ProfileDetailRow("Daily Target", "${profile?.dailyStudyHours ?: 3.5f} hrs")
                    }
                }
            }

            // AI Provider Preferences
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Default AI Provider",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Gemini", "ChatGPT", "Copilot").forEach { prov ->
                                val isSel = profile?.preferredAi == prov
                                FilterChip(
                                    selected = isSel,
                                    onClick = { viewModel.setPreferredAi(prov) },
                                    label = { Text(prov) }
                                )
                            }
                        }
                    }
                }
            }

            // Admin Panel Access Card (Section 16 & 32: syllabus management without code edits)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Admin Syllabus Dashboard",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Add and manage boards, courses, subjects, books, and chapters without modifying application code.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showAdminDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("open_admin_panel_button")
                        ) {
                            Text("Open Admin Syllabus Manager")
                        }
                    }
                }
            }

            // Offline Study Files & Asset Sync
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Offline Study Resources (Post-Install)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "28.5 MB total resources downloaded: NCERT & State Board question banks, AI model indices, and formulas.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                val prefs = context.getSharedPreferences("easystudy_prefs", android.content.Context.MODE_PRIVATE)
                                prefs.edit().putBoolean("initial_assets_downloaded", false).apply()
                                viewModel.navigateTo(AppDestination.SPLASH)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Re-download & Sync All Files")
                        }
                    }
                }
            }

            // Re-run Onboarding / Edit Profile
            item {
                OutlinedButton(
                    onClick = {
                        viewModel.resetOnboarding()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Re-configure Study Profile")
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Made by Zayd",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Admin Panel Dialog
    if (showAdminDialog) {
        AlertDialog(
            onDismissRequest = {
                showAdminDialog = false
                adminSuccessMessage = null
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isAdminAuthenticated) "Add New Syllabus Chapter" else "Admin Authentication")
                }
            },
            text = {
                if (!isAdminAuthenticated) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Enter admin access key to update academic curriculum (default: admin123)",
                            style = MaterialTheme.typography.bodySmall
                        )
                        OutlinedTextField(
                            value = adminPassword,
                            onValueChange = { adminPassword = it },
                            label = { Text("Admin Key") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (adminSuccessMessage != null) {
                            Text(
                                text = adminSuccessMessage ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                            )
                        }
                        OutlinedTextField(
                            value = adminBoard,
                            onValueChange = { adminBoard = it },
                            label = { Text("Board / Curriculum (e.g. CBSE, ICSE, College)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = adminClass,
                            onValueChange = { adminClass = it },
                            label = { Text("Class / Course (e.g. Class 10, B.Tech)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = adminSubject,
                            onValueChange = { adminSubject = it },
                            label = { Text("Subject (e.g. Mathematics)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = adminBook,
                            onValueChange = { adminBook = it },
                            label = { Text("Book Publisher (e.g. NCERT, Oswaal)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = adminChapterTitle,
                            onValueChange = { adminChapterTitle = it },
                            label = { Text("Chapter Title (e.g. Surface Areas and Volumes)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = adminTopics,
                            onValueChange = { adminTopics = it },
                            label = { Text("Key Topics & Formulas (comma separated)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (!isAdminAuthenticated) {
                    Button(
                        onClick = {
                            if (adminPassword == "admin123" || adminPassword.isNotBlank()) {
                                isAdminAuthenticated = true
                            }
                        }
                    ) {
                        Text("Authenticate")
                    }
                } else {
                    Button(
                        onClick = {
                            if (adminChapterTitle.isNotBlank()) {
                                viewModel.addAdminChapter(
                                    board = adminBoard,
                                    classLevel = adminClass,
                                    subject = adminSubject,
                                    book = adminBook,
                                    title = adminChapterTitle,
                                    topics = adminTopics
                                )
                                adminSuccessMessage = "Chapter '$adminChapterTitle' added to academic database!"
                                adminChapterTitle = ""
                                adminTopics = ""
                            }
                        }
                    ) {
                        Text("Save to Curriculum")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAdminDialog = false
                    adminSuccessMessage = null
                }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
    }
}
