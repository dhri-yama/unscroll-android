package com.unscroll.app.ui.main.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unscroll.app.domain.model.DailyOverlayStats
import com.unscroll.app.domain.model.OverlayResponse
import com.unscroll.app.ui.components.CyberButtonStyle
import com.unscroll.app.ui.components.CyberPanel
import com.unscroll.app.ui.components.CyberSectionLabel
import com.unscroll.app.ui.components.CyberTextField
import com.unscroll.app.ui.components.DailyBarChart
import com.unscroll.app.ui.components.GlassmorphicPillButton
import com.unscroll.app.ui.components.labelForResponse
import com.unscroll.app.ui.theme.CyberMuted
import com.unscroll.app.ui.theme.CyberWhite

@Composable
fun UserProfileSettingsScreen(
    userName: String,
    onSaveName: (String) -> Unit,
    dailyStats: List<DailyOverlayStats>,
    selectedRangeDays: Int,
    onSelectRange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        CyberSectionLabel(text = "CONFIG // OPERATOR IDENTITY")
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "Who is holding the line?",
            style = MaterialTheme.typography.headlineMedium,
            color = CyberWhite
        )
        Spacer(modifier = Modifier.padding(top = 6.dp))
        Text(
            text = "The identifier is injected into intervention messages so the loop knows who it is addressing.",
            style = MaterialTheme.typography.bodyMedium,
            color = CyberMuted
        )
        Spacer(modifier = Modifier.padding(top = 20.dp))
        CyberPanel(modifier = Modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)) {
            CyberTextField(
                value = userName,
                onValueChange = onSaveName,
                label = "Callsign",
                placeholder = "Enter operator name",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Text(
                text = "LOCAL PROFILE // NO CLOUD PROFILE",
                style = MaterialTheme.typography.labelSmall,
                color = CyberMuted
            )
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        GlassmorphicPillButton(
            text = "Commit identity",
            onClick = { onSaveName(userName) },
            iconPrefix = "[=]",
            style = CyberButtonStyle.Filled
        )

        Spacer(modifier = Modifier.padding(top = 28.dp))
        CyberSectionLabel(text = "HISTORY // INTERVENTION RESPONSE", glitchSeed = 0x3F21)
        Spacer(modifier = Modifier.padding(top = 6.dp))
        Text(
            text = "Times each control was used to end an intervention, per day.",
            style = MaterialTheme.typography.bodyMedium,
            color = CyberMuted
        )
        Spacer(modifier = Modifier.padding(top = 14.dp))
        CyberRangeSelector(
            options = ChartRangeOptions,
            selectedRangeDays = selectedRangeDays,
            onSelectRange = onSelectRange
        )

        Spacer(modifier = Modifier.padding(top = 14.dp))
        OverlayResponse.entries.forEach { response ->
            DailyBarChart(
                title = labelForResponse(response),
                series = dailyStats,
                response = response,
                glitchSeed = if (response == OverlayResponse.SKIP) 0x3C1A else 0x3C2B
            )
            Spacer(modifier = Modifier.padding(top = 12.dp))
        }

        Text(
            text = "RECORDS BEGIN AT FIRST USE // STORED ON DEVICE",
            style = MaterialTheme.typography.labelSmall,
            color = CyberMuted,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(top = 12.dp))
    }
}
