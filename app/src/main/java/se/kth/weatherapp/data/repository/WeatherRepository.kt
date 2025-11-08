package se.kth.weatherapp.data.repository

import se.kth.weatherapp.data.api.RetrofitClient
import se.kth.weatherapp.data.api.WeatherParser
import se.kth.weatherapp.data.model.Location
import se.kth.weatherapp.data.model.WeatherForecast

/**
 * Repository för att hantera väderdata
 * Hanterar API-anrop och cache
 */
class WeatherRepository {

    private val weatherApi = RetrofitClient.weatherApi
    private val locationApi = RetrofitClient.locationApi
    private val parser = WeatherParser()

    /**
     * Hämtar väderprognos för koordinater
     */
    suspend fun fetchForecastForCoordinates(
        latitude: Double,
        longitude: Double,
        locationName: String = "Unknown"
    ): Result<WeatherForecast> {
        return try {
            val response = weatherApi.fetchWeatherForecast(longitude, latitude)

            if (response.isSuccessful && response.body() != null) {
                val forecast = parser.parseWeatherData(response.body()!!, locationName)
                Result.success(forecast)
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Söker efter platser baserat på namn
     */
    suspend fun searchLocationsByName(query: String): Result<List<Location>> {
        return try {
            val response = locationApi.searchPlaces(query)

            if (response.isSuccessful && response.body() != null) {
                val locations = parser.parseLocations(response.body()!!)
                Result.success(locations)
            } else {
                Result.failure(Exception("Search failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}