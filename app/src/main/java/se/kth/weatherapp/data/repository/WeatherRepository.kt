package se.kth.weatherapp.data.repository

import android.util.Log
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
    private val geocodingApi = RetrofitClient.geocodingApi
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
                Log.e("WeatherRepository", "API error: ${response.code()} - ${response.message()}")
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Exception fetching weather", e)
            Result.failure(e)
        }
    }

    /**
     * Söker efter platser baserat på namn
     * Försöker först SMHI, sedan Open-Meteo som backup
     */
    suspend fun searchLocationsByName(query: String): Result<List<Location>> {
        return try {
            Log.d("WeatherRepository", "Searching for: $query")

            try {
                val smhiResponse = locationApi.searchPlaces(query)
                if (smhiResponse.isSuccessful && smhiResponse.body() != null) {
                    val locations = parser.parseLocations(smhiResponse.body()!!)
                    if (locations.isNotEmpty()) {
                        Log.d("WeatherRepository", "SMHI: Found ${locations.size} locations")
                        return Result.success(locations)
                    }
                }
            } catch (e: Exception) {
                Log.w("WeatherRepository", "SMHI search failed, trying Open-Meteo", e)
            }

            val geocodingResponse = geocodingApi.searchLocation(query)
            if (geocodingResponse.isSuccessful && geocodingResponse.body()?.results != null) {
                val results = geocodingResponse.body()!!.results!!
                val locations = results.map { result ->
                    Location(
                        name = "${result.name}${if (result.admin1 != null) ", ${result.admin1}" else ""}",
                        latitude = result.latitude,
                        longitude = result.longitude
                    )
                }
                Log.d("WeatherRepository", "Open-Meteo: Found ${locations.size} locations")
                Result.success(locations)
            } else {
                Log.e("WeatherRepository", "Both APIs failed")
                Result.failure(Exception("Inga platser hittades"))
            }
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Exception searching locations", e)
            Result.failure(e)
        }
    }
}