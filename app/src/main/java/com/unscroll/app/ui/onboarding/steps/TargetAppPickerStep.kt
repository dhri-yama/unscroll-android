package com.unscroll.app.ui.onboarding.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.ui.theme.GlassPillBackground
import com.unscroll.app.ui.theme.GlassPillBorder
import com.unscroll.app.ui.theme.TextPrimaryWhite
import com.unscroll.app.ui.theme.TextSecondaryMuted
import com.unscroll.app.ui.theme.TickerGold

data class AppOption(val pkg: String, val name: String)

val DEFAULT_APPS = listOf(
    AppOption("com.instagram.android", "Instagram"),
    AppOption("com.google.android.youtube", "YouTube Shorts"),
    AppOption("com.zhiliaoapp.musically", "TikTok"),
    AppOption("com.reddit.frontpage", "Reddit"),
    AppOption("com.twitter.android", "X (Twitter)")
)

@Composable
fun TargetAppPickerStep(
    selectedPackages: Set<String>,
    onTogglePackage: (String) -> Unit,
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
            text = "Select Tracked Apps",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryWhite,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pick the doomscrolling apps you want Unscroll to monitor.",
            fontSize = 15.sp,
            color = TextSecondaryMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(DEFAULT_APPS) { app ->
                val isSelected = selectedPackages.contains(app.pkg)
                val shape = RoundedCornerShape(16.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(GlassPillBackground)
                        .border(1.dp, if (isSelected) TickerGold else GlassPillBorder, shape)
                        .clickable { onTogglePackage(app.pkg) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = app.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimaryWhite
                    )

                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onTogglePackage(app.pkg) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = TickerGold,
                            uncheckedColor = TextSecondaryMuted
                        )
                    )
                }
            }
        }
    }
}
