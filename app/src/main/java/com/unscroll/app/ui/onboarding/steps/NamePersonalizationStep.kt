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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unscroll.app.ui.components.CyberPanel
import com.unscroll.app.ui.components.CyberSectionLabel
import com.unscroll.app.ui.components.CyberTextField
import com.unscroll.app.ui.theme.CyberMuted
import com.unscroll.app.ui.theme.CyberWhite

@Composable
fun NamePersonalizationStep(
    userName: String,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        CyberSectionLabel(text = "IDENTITY // OPERATOR")
        Spacer(modifier = Modifier.padding(top = 18.dp))
        Text(
            text = "What should the system call you?",
            style = MaterialTheme.typography.headlineLarge,
            color = CyberWhite
        )
        Spacer(modifier = Modifier.padding(top = 12.dp))
        Text(
            text = "This identifier appears in the intervention terminal when the loop begins.",
            style = MaterialTheme.typography.bodyLarge,
            color = CyberMuted
        )
        Spacer(modifier = Modifier.padding(top = 24.dp))
        CyberPanel(modifier = Modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)) {
            CyberTextField(
                value = userName,
                onValueChange = onNameChange,
                label = "Callsign",
                placeholder = "Enter operator name",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Text(
                text = "OPTIONAL // USED FOR CONTEXT ONLY",
                style = MaterialTheme.typography.labelSmall,
                color = CyberMuted,
                textAlign = TextAlign.Start
            )
        }
    }
}
