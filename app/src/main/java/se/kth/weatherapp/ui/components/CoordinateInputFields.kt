package se.kth.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Inputfält för koordinater
 */
@Composable
fun CoordinateInputFields(
    latitude: String,
    longitude: String,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = latitude,
            onValueChange = { value ->
                if (value.isEmpty() || value.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                    onLatitudeChange(value)
                }
            },
            label = { Text("Latitud") },
            placeholder = { Text("59.3293") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = longitude,
            onValueChange = { value ->
                if (value.isEmpty() || value.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                    onLongitudeChange(value)
                }
            },
            label = { Text("Longitud") },
            placeholder = { Text("18.0686") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = latitude.isNotEmpty() && longitude.isNotEmpty()
        ) {
            Text("Hämta väder")
        }
    }
}