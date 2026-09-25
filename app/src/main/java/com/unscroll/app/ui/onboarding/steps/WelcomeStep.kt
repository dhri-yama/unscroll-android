package com.unscroll.app.ui.onboarding.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unscroll.app.ui.components.CyberPanel
import com.unscroll.app.ui.components.CyberSectionLabel
import com.unscroll.app.ui.components.GlitchText
import com.unscroll.app.ui.theme.CyberMuted
import com.unscroll.app.ui.theme.CyberWhite

@Composable
fun WelcomeStep(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        CyberSectionLabel(text = "SYS // BOOT SEQUENCE")
        Spacer(modifier = Modifier.padding(top = 18.dp))
        GlitchText(
            text = "UNSCROLL",
            style = MaterialTheme.typography.displayLarge,
            color = CyberWhite
        )
        Text(
            text = "// reclaim the signal",
            style = MaterialTheme.typography.titleMedium,
            color = CyberMuted
        )
        Spacer(modifier = Modifier.padding(top = 30.dp))
        CyberPanel(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Your attention is a finite resource. Unscroll detects the scroll loop, interrupts the signal, and returns control to the operator.",
                style = MaterialTheme.typography.bodyLarge,
                color = CyberWhite
            )
            Spacer(modifier = Modifier.padding(top = 14.dp))
            Text(
                text = "NO FEED. NO ALGORITHM. JUST THE NEXT CHOICE.",
                style = MaterialTheme.typography.labelSmall,
                color = CyberMuted
            )
        }
    }
}
