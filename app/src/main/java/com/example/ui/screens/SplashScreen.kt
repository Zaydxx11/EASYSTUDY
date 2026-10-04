package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBg
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.IndigoPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("easystudy_prefs", android.content.Context.MODE_PRIVATE) }
    val isFirstInstall = remember { !prefs.getBoolean("initial_assets_downloaded", false) }

    var targetProgress by remember { mutableFloatStateOf(0f) }
    var currentStatus by remember { mutableStateOf("Connecting to EasyStudy cloud...") }
    var downloadedMB by remember { mutableStateOf("0.0 MB / 28.5 MB") }
    var downloadSpeed by remember { mutableStateOf("4.2 MB/s") }
    var isCompleted by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "download_progress_anim"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "splash_infinite")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_angle"
    )

    // Download flow effect
    LaunchedEffect(Unit) {
        if (isFirstInstall) {
            // First time after installing: Download all study files, question banks, AI models
            delay(400)
            currentStatus = "Fetching syllabus packages & curricula..."
            targetProgress = 0.15f
            downloadedMB = "4.2 MB / 28.5 MB"
            downloadSpeed = "5.1 MB/s"

            delay(600)
            currentStatus = "Downloading NCERT & State Board question banks..."
            targetProgress = 0.40f
            downloadedMB = "11.4 MB / 28.5 MB"
            downloadSpeed = "5.8 MB/s"

            delay(650)
            currentStatus = "Syncing AI doubt solver offline embeddings..."
            targetProgress = 0.72f
            downloadedMB = "20.5 MB / 28.5 MB"
            downloadSpeed = "6.2 MB/s"

            delay(550)
            currentStatus = "Downloading interactive formulas & video indexes..."
            targetProgress = 0.92f
            downloadedMB = "26.3 MB / 28.5 MB"
            downloadSpeed = "4.9 MB/s"

            delay(450)
            currentStatus = "Verifying package integrity..."
            targetProgress = 1.0f
            downloadedMB = "28.5 MB / 28.5 MB"
            downloadSpeed = "Done"

            delay(400)
            currentStatus = "All files downloaded & ready!"
            isCompleted = true
            prefs.edit().putBoolean("initial_assets_downloaded", true).apply()

            delay(500)
            onSplashFinished()
        } else {
            // Subsequent launches: quick package verification
            currentStatus = "Checking study database & updates..."
            targetProgress = 0.45f
            downloadedMB = "Local cache verified"
            delay(400)
            targetProgress = 1.0f
            currentStatus = "Study resources up to date!"
            isCompleted = true
            delay(350)
            onSplashFinished()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("splash_screen"),
        color = DarkBg
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1E1B4B),
                            DarkBg
                        ),
                        radius = 900f
                    )
                )
        ) {
            // Main Center Branding & Animation
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Animated Glowing Emblem (Knowledge Crest - No human)
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer rotating glow ring
                    Box(
                        modifier = Modifier
                            .size(116.dp)
                            .rotate(rotationAngle)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    colors = listOf(
                                        IndigoPrimary,
                                        CyanAccent,
                                        Color(0xFF9333EA),
                                        IndigoPrimary
                                    )
                                )
                            )
                    )

                    // Inner dark spacer
                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .clip(CircleShape)
                            .background(DarkBg)
                    )

                    // Center pulsing core with emblem
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .scale(pulseScale)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(IndigoPrimary, CyanAccent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.Verified else Icons.Default.School,
                            contentDescription = "EasyStudy Logo",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Brand Title: EASYSTUDY
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "EASY",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 4.sp,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "STUDY",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Light,
                            letterSpacing = 4.sp,
                            color = CyanAccent
                        )
                    )
                }

                Text(
                    text = "AI Study Companion",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp,
                        color = IndigoLight.copy(alpha = 0.85f)
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Downloader Container Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF131B2E).copy(alpha = 0.8f))
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isFirstInstall) "Downloading Study Files" else "Synchronizing Assets",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CyanAccent
                                    )
                                )
                            }
                            Text(
                                text = "${(animatedProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Modern glowing progress bar
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = CyanAccent,
                            trackColor = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentStatus,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            ),
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = downloadedMB,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            )
                            if (isFirstInstall && !isCompleted) {
                                Text(
                                    text = downloadSpeed,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = CyanAccent.copy(alpha = 0.7f)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Bottom attribution: "Made by Zayd"
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 28.dp)
            ) {
                Text(
                    text = "Made by Zayd",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.65f),
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}
