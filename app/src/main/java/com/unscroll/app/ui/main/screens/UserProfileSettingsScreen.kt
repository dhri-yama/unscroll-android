package com.unscroll.app.ui.main.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.ui.components.GlassmorphicPillButton
import com.unscroll.app.ui.theme.GlassPillBorder
import com.unscroll.app.ui.theme.TextPrimaryWhite
import com.unscroll.app.ui.theme.TextSecondaryMuted
import com.unscroll.app.ui.theme.TickerGold

@Composable
fun UserProfileSettingsScreen(
    userName: String,
    onSaveName: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Profile Settings",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryWhite
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Personalize the name used in session intervention insults.",
            fontSize = 14.sp,
            color = TextSecondaryMuted
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = userName,
            onValueChange = onSaveName,
            label = { Text("Your Name", color = TextSecondaryMuted) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimaryWhite,
                unfocusedTextColor = TextPrimaryWhite,
                focusedBorderColor = TickerGold,
                unfocusedBorderColor = GlassPillBorder
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        GlassmorphicPillButton(
            text = "Save Profile",
            onClick = { onSaveName(userName) }
        )
    }
}
