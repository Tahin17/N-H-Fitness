package com.aegisfit.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aegisfit.app.presentation.theme.NeonCyan

@Composable
fun ExerciseSetRow(
    setNumber: Int,
    reps: String,
    weight: String,
    isCompleted: Boolean,
    onRepsChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onToggleCompleted: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    ghostData: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = setNumber.toString(), modifier = Modifier.width(32.dp))
            
            OutlinedTextField(
                value = weight,
                onValueChange = onWeightChange,
                label = { Text("kg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                singleLine = true
            )
            
            OutlinedTextField(
                value = reps,
                onValueChange = onRepsChange,
                label = { Text("Reps") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                singleLine = true
            )
            
            Checkbox(
                checked = isCompleted,
                onCheckedChange = onToggleCompleted,
                colors = CheckboxDefaults.colors(
                    checkedColor = NeonCyan,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
        
        if (ghostData != null) {
            Text(
                text = "Last time: $ghostData",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 64.dp, bottom = 4.dp)
            )
        }
    }
}
