package se.kth.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import se.kth.weatherapp.data.model.HourlyForecast
import se.kth.weatherapp.utils.WeatherIconMapper

/**
 * Komponent för att visa en timmes väderprognos
 */
@Composable
fun HourlyWeatherCard(
    forecast: HourlyForecast,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = forecast.time.substring(11, 16),
                style = MaterialTheme.typography.bodyLarge
            )

            Icon(
                imageVector = WeatherIconMapper.getWeatherIcon(forecast.weatherSymbol),
                contentDescription = WeatherIconMapper.getWeatherDescription(forecast.weatherSymbol),
                modifier = Modifier.size(32.dp)
            )

            Text(
                text = "${forecast.temperature.toInt()}°C",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "${forecast.cloudCover}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

/**
 * Komponent för att visa dagsprognos
 */
@Composable
fun DailyWeatherCard(
    date: String,
    weatherSymbol: Int,
    hourlyForecasts: List<HourlyForecast>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.titleMedium
                )

                Icon(
                    imageVector = WeatherIconMapper.getWeatherIcon(weatherSymbol),
                    contentDescription = WeatherIconMapper.getWeatherDescription(weatherSymbol),
                    modifier = Modifier.size(32.dp)
                )

                val minTemp = hourlyForecasts.minOfOrNull { it.temperature }?.toInt() ?: 0
                val maxTemp = hourlyForecasts.maxOfOrNull { it.temperature }?.toInt() ?: 0

                Text(
                    text = "$minTemp° / $maxTemp°",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}