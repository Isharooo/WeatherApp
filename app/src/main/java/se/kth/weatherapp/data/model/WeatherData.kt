package se.kth.weatherapp.data.model

/**
 * Väderprognos för en plats
 */
data class WeatherForecast(
    val location: Location,
    val forecasts: List<HourlyForecast>
)

/**
 * Geografisk plats med koordinater
 */
data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

/**
 * Timvis väderprognos
 */
data class HourlyForecast(
    val time: String,
    val temperature: Double,
    val weatherSymbol: Int,
    val cloudCover: Int
)

/**
 * Daglig prognos (för grupperad visning)
 */
data class DailyForecast(
    val date: String,
    val weatherSymbol: Int,
    val hourlyForecasts: List<HourlyForecast>
)

/**
 * Sparad favoritplats
 */
data class FavoriteLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double
)