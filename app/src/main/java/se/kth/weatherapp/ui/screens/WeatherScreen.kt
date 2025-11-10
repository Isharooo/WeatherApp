package se.kth.weatherapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import se.kth.weatherapp.ui.components.*
import se.kth.weatherapp.viewmodel.WeatherUiState
import se.kth.weatherapp.viewmodel.WeatherViewModel

/**
 * Huvudskärm för väderappen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun WeatherScreen(
    viewModel: WeatherViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Väderprognos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                CoordinateInputFields(
                    latitude = latitude,
                    longitude = longitude,
                    onLatitudeChange = { latitude = it },
                    onLongitudeChange = { longitude = it },
                    onSubmit = {
                        val lat = latitude.toDoubleOrNull()
                        val lon = longitude.toDoubleOrNull()
                        if (lat != null && lon != null) {
                            viewModel.fetchWeatherByCoordinates(
                                latitude = lat,
                                longitude = lon,
                                locationName = "Plats (${lat}, ${lon})",
                                context = context
                            )
                        }
                    }
                )
            }

            if (favorites.isNotEmpty()) {
                item {
                    Text(
                        text = "Favoriter:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                }

                items(favorites) { favorite ->
                    OutlinedButton(
                        onClick = {
                            latitude = favorite.latitude.toString()
                            longitude = favorite.longitude.toString()
                            viewModel.fetchWeatherByCoordinates(
                                latitude = favorite.latitude,
                                longitude = favorite.longitude,
                                locationName = favorite.name,
                                context = context
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(favorite.name)
                    }
                }
            }

            when (val state = uiState) {
                is WeatherUiState.Initial -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Ange koordinater för att se väderprognos",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                is WeatherUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is WeatherUiState.Error -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = "Fel: ${state.message}",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                is WeatherUiState.Success -> {
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = state.forecast.location.name,
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                if (state.isOffline) {
                                    Text(
                                        text = "Offline-data",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    val locationName = state.forecast.location.name
                                    if (viewModel.isFavorite(locationName)) {
                                        viewModel.removeFromFavorites(locationName)
                                    } else {
                                        viewModel.addToFavorites(state.forecast.location)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (viewModel.isFavorite(state.forecast.location.name)) {
                                        Icons.Default.Favorite
                                    } else {
                                        Icons.Default.FavoriteBorder
                                    },
                                    contentDescription = "Favorit"
                                )
                            }
                        }
                    }

                    item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

                    val today = state.dailyForecasts.firstOrNull()
                    if (today != null) {
                        item {
                            Text(
                                text = "Idag - ${today.date}",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(today.hourlyForecasts.take(24)) { forecast ->
                            HourlyWeatherCard(forecast = forecast)
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Kommande dagar",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    items(state.dailyForecasts.drop(1)) { daily ->
                        DailyWeatherCard(
                            date = daily.date,
                            weatherSymbol = daily.weatherSymbol,
                            hourlyForecasts = daily.hourlyForecasts
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}