package com.unscroll.app.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.UnscrollApplication
import com.unscroll.app.domain.model.formatElapsedTime
import com.unscroll.app.service.monitor.SessionMonitorForegroundService
import com.unscroll.app.ui.components.AmbientBackground
import com.unscroll.app.ui.components.CyberBottomNav
import com.unscroll.app.ui.components.CyberMetric
import com.unscroll.app.ui.components.CyberNavDestination
import com.unscroll.app.ui.components.CyberPanel
import com.unscroll.app.ui.components.CyberRule
import com.unscroll.app.ui.components.CyberSectionLabel
import com.unscroll.app.ui.components.CyberStatusChip
import com.unscroll.app.ui.components.CyberTopBar
import com.unscroll.app.ui.components.CyberButtonStyle
import com.unscroll.app.ui.components.GlassmorphicPillButton
import com.unscroll.app.ui.components.GlitchNoise
import com.unscroll.app.ui.components.GlitchText
import com.unscroll.app.ui.components.glitchShimmer
import com.unscroll.app.ui.components.glitchFrame
import com.unscroll.app.ui.components.glitchJitter
import com.unscroll.app.ui.components.glitchedText
import com.unscroll.app.ui.main.screens.CadenceSettingsScreen
import com.unscroll.app.ui.main.screens.PermissionsDashboardScreen
import com.unscroll.app.ui.main.screens.TargetAppsSettingsScreen
import com.unscroll.app.ui.main.screens.UserProfileSettingsScreen
import com.unscroll.app.ui.onboarding.OnboardingFlowScreen
import com.unscroll.app.ui.onboarding.OnboardingViewModel
import com.unscroll.app.ui.theme.CyberBlack
import com.unscroll.app.ui.theme.CyberMuted
import com.unscroll.app.ui.theme.CyberSoft
import com.unscroll.app.ui.theme.CyberWhite
import com.unscroll.app.ui.theme.UnscrollTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as UnscrollApplication).container

        val mainViewModel: MainViewModel by viewModels {
            MainViewModel.Factory(
                container.settingsRepository,
                container.sessionStatsRepository,
                container.dailyStatsRepository
            )
        }
        val onboardingViewModel: OnboardingViewModel by viewModels {
            OnboardingViewModel.Factory(container.settingsRepository)
        }

        setContent {
            UnscrollTheme {
                val context = LocalContext.current
                val isOnboardingCompleted by mainViewModel.isOnboardingCompleted.collectAsState()
                val onboardingState by onboardingViewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    onboardingViewModel.checkPermissions(context)
                }

                LaunchedEffect(isOnboardingCompleted) {
                    if (isOnboardingCompleted) {
                        SessionMonitorForegroundService.startSafely(context)
                    }
                }

                if (!isOnboardingCompleted) {
                    OnboardingFlowScreen(
                        viewModel = onboardingViewModel,
                        onOnboardingFinished = {}
                    )
                } else {
                    MainDashboardScreen(
                        viewModel = mainViewModel,
                        isUsageGranted = onboardingState.isUsagePermissionGranted,
                        isOverlayGranted = onboardingState.isOverlayPermissionGranted,
                        isA11yGranted = onboardingState.isAccessibilityPermissionGranted,
                        onRefreshPermissions = { onboardingViewModel.checkPermissions(context) }
                    )
                }
            }
        }
    }
}

@Composable
fun MainDashboardScreen(
    viewModel: MainViewModel,
    isUsageGranted: Boolean,
    isOverlayGranted: Boolean,
    isA11yGranted: Boolean,
    onRefreshPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val userProfile by viewModel.userProfile.collectAsState()
    val trackedPackages by viewModel.trackedPackages.collectAsState()
    val sessionStats by viewModel.sessionStats.collectAsState()
    val dailyStats by viewModel.dailyStats.collectAsState()
    val selectedRangeDays by viewModel.selectedRangeDays.collectAsState()
    val destinations = remember {
        listOf(
            CyberNavDestination("Live", Icons.Outlined.Dashboard),
            CyberNavDestination("Targets", Icons.Outlined.Apps),
            CyberNavDestination("Cadence", Icons.Outlined.Timer),
            CyberNavDestination("Profile", Icons.Outlined.Person),
            CyberNavDestination("System", Icons.Outlined.Settings)
        )
    }
    val permissionCount = listOf(isUsageGranted, isOverlayGranted, isA11yGranted).count { it }
    val status = if (permissionCount == 3) "SYSTEM LIVE" else "$permissionCount/3 CHANNELS"

    Scaffold(
        modifier = modifier,
        containerColor = CyberBlack,
        bottomBar = {
            CyberBottomNav(
                destinations = destinations,
                selectedIndex = selectedTab,
                onSelect = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        AmbientBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                CyberTopBar(
                    title = when (selectedTab) {
                        0 -> "UNSCROLL // LIVE"
                        1 -> "UNSCROLL // TARGETS"
                        2 -> "UNSCROLL // CADENCE"
                        3 -> "UNSCROLL // PROFILE"
                        else -> "UNSCROLL // SYSTEM"
                    },
                    status = status
                )
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (selectedTab) {
                        0 -> LiveSessionDashboardTab(
                            userName = userProfile.userName,
                            sessionStats = sessionStats,
                            onResetSession = viewModel::resetSessionStats
                        )
                        1 -> TargetAppsSettingsScreen(
                            selectedPackages = trackedPackages,
                            onTogglePackage = viewModel::togglePackage
                        )
                        2 -> CadenceSettingsScreen(
                            selectedInterval = userProfile.interruptionIntervalMinutes,
                            onSelectInterval = viewModel::updateInterval
                        )
                        3 -> UserProfileSettingsScreen(
                            userName = userProfile.userName,
                            onSaveName = viewModel::updateUserName,
                            dailyStats = dailyStats,
                            selectedRangeDays = selectedRangeDays,
                            onSelectRange = viewModel::selectRange
                        )
                        else -> PermissionsDashboardScreen(
                            isUsageGranted = isUsageGranted,
                            isOverlayGranted = isOverlayGranted,
                            isA11yGranted = isA11yGranted,
                            onRefreshPermissions = onRefreshPermissions
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiveSessionDashboardTab(
    userName: String,
    sessionStats: com.unscroll.app.domain.model.SessionStats,
    onResetSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val densityDpi = LocalContext.current.resources.displayMetrics.densityDpi
    val meters = sessionStats.getScrollDistanceMeters(densityDpi)
    val distanceText = if (meters >= 1.0f) {
        String.format(java.util.Locale.getDefault(), "%.1fm", meters)
    } else {
        "${(meters * 100).toInt()}cm"
    }
    var nowMillis by remember(sessionStats.sessionStartMillis, sessionStats.isActive) {
        mutableLongStateOf(System.currentTimeMillis())
    }
    LaunchedEffect(sessionStats.sessionStartMillis, sessionStats.isActive) {
        while (sessionStats.isActive) {
            nowMillis = System.currentTimeMillis()
            delay(1_000L)
        }
    }
    val elapsedMillis = if (sessionStats.isActive) {
        maxOf(
            sessionStats.totalTimeSpentMillis,
            nowMillis - sessionStats.sessionStartMillis
        ).coerceAtLeast(0L)
    } else {
        0L
    }
    val activeTimeText = formatElapsedTime(elapsedMillis)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                CyberSectionLabel(text = "SESSION // LIVE TELEMETRY")
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Text(
                    text = if (userName.isNotBlank()) "Signal acquired, $userName." else "Signal acquired, operator.",
                    style = MaterialTheme.typography.headlineMedium,
                    color = CyberWhite
                )
            }
            CyberStatusChip(
                text = if (sessionStats.isActive) "LIVE" else "STANDBY",
                active = sessionStats.isActive
            )
        }
        Spacer(modifier = Modifier.padding(top = 22.dp))
        CyberPanel(modifier = Modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)) {
            CyberSectionLabel(text = "RECKLESS CONSUMPTION", glitchSeed = 0x5C0F)
            Spacer(modifier = Modifier.padding(top = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                GlitchText(
                    text = sessionStats.estimatedReelsCount.toString().padStart(2, '0'),
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 66.sp),
                    color = CyberWhite,
                    scramble = false,
                    glitchSeed = 0x5C0F
                )
                Spacer(modifier = Modifier.padding(horizontal = 12.dp))
                Text(
                    text = "REELS / SHORTS\nINTERCEPTED",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberMuted,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .glitchJitter(
                            frame = glitchFrame(0x5C0F, gain = 0.9f),
                            maxShiftDp = 2.4f,
                            verticalShiftDp = 0.6f
                        )
                )
            }
            Spacer(modifier = Modifier.padding(top = 18.dp))
            CyberRule()
            Spacer(modifier = Modifier.padding(top = 14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                CyberMetric(
                    value = sessionStats.totalScrollsCount.toString(),
                    label = "Gestures",
                    modifier = Modifier.weight(1f),
                    glitchSeed = 0x11A5
                )
                CyberMetric(
                    value = distanceText,
                    label = "Distance",
                    modifier = Modifier.weight(1f),
                    glitchSeed = 0x22B6
                )
                CyberMetric(
                    value = activeTimeText,
                    label = "Elapsed",
                    modifier = Modifier.weight(1f),
                    glitchSeed = 0x33C7
                )
            }
        }
        Spacer(modifier = Modifier.padding(top = 14.dp))
        CyberPanel(modifier = Modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)) {
            CyberSectionLabel(text = "EVENT STREAM // LAST SESSION")
            Spacer(modifier = Modifier.padding(top = 12.dp))
            TelemetryRow("GESTURES DETECTED", sessionStats.totalScrollsCount.toString().padStart(2, '0'))
            TelemetryRow("DISTANCE LOGGED", distanceText)
            TelemetryRow("TIME IN LOOP", activeTimeText)
            TelemetryRow("STATUS", "SIGNAL STABLE")
        }
        Spacer(modifier = Modifier.padding(top = 18.dp))
        GlassmorphicPillButton(
            text = "Clear telemetry",
            onClick = onResetSession,
            iconPrefix = "[0]",
            style = CyberButtonStyle.Outlined
        )
        Spacer(modifier = Modifier.padding(top = 12.dp))
        Text(
            text = "Tap a target to arm monitoring. Keep the signal intentional.",
            style = MaterialTheme.typography.bodyMedium,
            color = CyberMuted
        )
        Spacer(modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun TelemetryRow(
    label: String,
    value: String
) {
    val frame = glitchFrame(label.hashCode(), gain = 0.85f)
    val displayedValue = remember(frame.step, frame.intensity, value) {
        glitchedText(value, frame, strength = 0.35f)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .glitchShimmer(
                frame = frame,
                minAlpha = 0.4f,
                maxShiftDp = 1.8f,
                verticalShiftDp = 0.4f
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = CyberMuted,
            modifier = Modifier.clearAndSetSemantics { }
        )
        Text(
            text = displayedValue,
            style = MaterialTheme.typography.labelLarge,
            color = CyberSoft,
            modifier = Modifier
                .clearAndSetSemantics { contentDescription = value }
                .graphicsLayer {
                    translationX = if (frame.isActive) {
                        GlitchNoise.range(frame.seed, frame.step, -2.5f, 2.5f, salt = 61)
                    } else {
                        0f
                    }
                }
        )
    }
}
