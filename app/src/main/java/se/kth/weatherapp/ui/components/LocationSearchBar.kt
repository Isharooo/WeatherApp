package se.kth.weatherapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import se.kth.weatherapp.data.model.Location

/**
 * Sökfält för platser
 */
@Composable
fun LocationSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    searchResults: List<Location>,
    onLocationSelected: (Location) -> Unit,
    onClearResults: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Sök plats (t.ex. Stockholm)...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Sök")
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = {
                        onQueryChange("")
                        onClearResults()
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Rensa")
                    }
                }
            },
            singleLine = true
        )

        if (query.length >= 2) {
            Text(
                text = if (searchResults.isEmpty())
                    "Söker..."
                else
                    "${searchResults.size} resultat",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        if (searchResults.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    searchResults.take(10).forEach { location ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLocationSelected(location)
                                    onQueryChange("")
                                }
                        ) {
                            Text(
                                text = location.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        if (location != searchResults.last()) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}