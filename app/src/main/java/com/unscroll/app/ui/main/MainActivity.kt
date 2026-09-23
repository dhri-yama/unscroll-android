package com.unscroll.app.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.UnscrollApplication
import com.unscroll.app.ui.components.AmbientBackground
import com.unscroll.app.ui.components.GlassmorphicPillButton
import com.unscroll.app.ui.components.StatCounter
import com.unscroll.app.ui.main.screens.CadenceSettingsScreen
import com.unscroll.app.ui.main.screens.PermissionsDashboardScreen
import com.unscroll.app.ui.main.screens.TargetAppsSettingsScreen
import com.unscroll.app.ui.main.screens.UserProfileSettingsScreen
import com.unscroll.app.ui.onboarding.OnboardingFlowScreen
import com.unscroll.app.ui.onboarding.OnboardingViewModel
import com.unscroll.app.ui.theme.AmbientDarkBackground
import com.unscroll.app.ui.theme.GlassPillBackground
import com.unscroll.app.ui.theme.TextPrimaryWhite
import com.unscroll.app.ui.theme.TextSecondaryMuted
import com.unscroll.app.ui.theme.TickerGold
import com.unscroll.app.ui.theme.UnscrollTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as UnscrollApplication).container


        val mainViewModel: MainViewModel by viewModels {
            MainViewModel.Factory(container.settingsRepository, container.sessionStatsRepository)
        }
        val onboardingViewModel: OnboardingViewModel by viewModels {
            OnboardingViewModel.Factory(container.settingsRepository)
        }

        setContent {
            UnscrollTheme {
                val isOnboardingCompleted by mainViewModel.isOnboardingCompleted.collectAsState()

                if (!isOnboardingCompleted) {
                    OnboardingFlowScreen(
                        viewModel = onboardingViewModel,
                        onOnboardingFinished = {
                            // Onboarding finished
                        }
                    )
                } else {
                    MainDashboardScreen(viewModel = mainViewModel)
                }
            }
        }
    }
}

@Composable
fun MainDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val userProfile by viewModel.userProfile.collectAsState()
    val trackedPackages by viewModel.trackedPackages.collectAsState()
    val sessionStats by viewModel.sessionStats.collectAsState()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                containerColor = GlassPillBackground,
                contentColor = TextPrimaryWhite
            ) {
                val tabs = listOf("Dashboard", "Apps", "Timer", "Profile", "Status")
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(title, fontSize = 11.sp) },
                        icon = {
                            Text(
                                text = when(index) {
                                    0 -> "📊"
                                    1 -> "📱"
                                    2 -> "⏱️"
                                    3 -> "👤"
                                    else -> "⚙️"
                                },
                                fontSize = 16.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TickerGold,
                            selectedTextColor = TickerGold,
                            unselectedIconColor = TextSecondaryMuted,
                            unselectedTextColor = TextSecondaryMuted,
                            indicatorColor = AmbientDarkBackground
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        AmbientBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
                    onSaveName = viewModel::updateUserName
                )
                4 -> PermissionsDashboardScreen(
                    isUsageGranted = true,
                    isOverlayGranted = true,
                    isA11yGranted = true,
                    onRefreshPermissions = {}
                )
            }
        }
    }
}

@Composable
fun LiveSessionDashboardTab(
    userName: String,
    sessionStats: com.unscroll.app.domain.model.SessionStats,
    onResetSession: () -> Unit
) {
    val meters = sessionStats.getScrollDistanceMeters()
    val distanceText = if (meters >= 1.0f) {
        String.format(java.util.Locale.getDefault(), "%.1fm", meters)
    } else {
        "${(meters * 100).toInt()}cm"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text(
                text = if (userName.isNotBlank()) "Welcome back, $userName" else "Unscroll Reckoning",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryWhite
            )
            Text(
                text = "Active Doomscroll Session Metrics",
                fontSize = 14.sp,
                color = TextSecondaryMuted
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${sessionStats.estimatedReelsCount}",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = TickerGold
            )
            Text(
                text = "Reels / Shorts Scrolled",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimaryWhite
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCounter(
                    value = "${sessionStats.totalScrollsCount}",
                    label = "Total Gestures"
                )
                StatCounter(
                    value = distanceText,
                    label = "Scroll Distance"
                )
                StatCounter(
                    value = "${sessionStats.totalTimeSpentMillis / (1000 * 60)}m",
                    label = "Time Active"
                )
            }
        }

        GlassmorphicPillButton(
            text = "Reset Active Session",
            onClick = onResetSession,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}
