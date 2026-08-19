package com.aegisfit.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aegisfit.app.domain.model.AsymmetryResult
import com.aegisfit.app.domain.model.AsymmetrySeverity
import com.aegisfit.app.presentation.theme.NeonAmber
import com.aegisfit.app.presentation.theme.NeonGreen
import com.aegisfit.app.presentation.theme.NeonRed

@Composable
fun AsymmetryIndicator(
    results: List<AsymmetryResult>,
    modifier: Modifier = Modifier
) {
    if (results.isEmpty()) return

    val worstSeverity = results.maxOfOrNull { it.severity } ?: AsymmetrySeverity.Normal
    val borderColor = when (worstSeverity) {
        AsymmetrySeverity.Alert -> NeonRed
        AsymmetrySeverity.Warning -> NeonAmber
        AsymmetrySeverity.Normal -> NeonGreen
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bilateral Symmetry",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            results.forEachIndexed { index, result ->
                val resultColor = when (result.severity) {
                    AsymmetrySeverity.Alert -> NeonRed
                    AsymmetrySeverity.Warning -> NeonAmber
                    AsymmetrySeverity.Normal -> NeonGreen
                }
                
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = result.bodyPart,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Δ ${"%.1f".format(result.differenceCm)} cm (${"%.1f".format(result.differencePercent)}%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = resultColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "L: ${result.leftCm} cm",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "R: ${result.rightCm} cm",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    val total = result.leftCm + result.rightCm
                    val progress = if (total > 0) result.leftCm / total else 0.5
                    
                    LinearProgressIndicator(
                        progress = { progress.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp)
                            .height(4.dp),
                        color = resultColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                }

                if (index < results.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}
