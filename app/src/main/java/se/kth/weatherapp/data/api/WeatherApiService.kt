package se.kth.weatherapp.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import se.kth.weatherapp.data.model.SmhiLocationResponse
import se.kth.weatherapp.data.model.SmhiResponse

/**
 * API service för väderdata
 */
interface WeatherApiService {

    @GET("api/category/pmp3g/version/2/geotype/point/lon/{lon}/lat/{lat}/data.json")
    suspend fun fetchWeatherForecast(
        @Path("lon") longitude: Double,
        @Path("lat") latitude: Double
    ): Response<SmhiResponse>

    companion object {
        const val BASE_URL = "https://opendata-download-metfcst.smhi.se/"
    }
}

/**
 * API service för plats-sökning
 */
interface LocationApiService {

    @GET("wpta/backend_solr/autocomplete/search/{query}")
    suspend fun searchPlaces(
        @Path("query") query: String
    ): Response<List<SmhiLocationResponse>>

    companion object {
        const val BASE_URL = "https://www.smhi.se/"
    }
}

/**
 * Backup geocoding API (Open-Meteo)
 */
interface GeocodingApiService {

    @GET("v1/search")
    suspend fun searchLocation(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "sv",
        @Query("format") format: String = "json"
    ): Response<GeocodingResponse>

    companion object {
        const val BASE_URL = "https://geocoding-api.open-meteo.com/"
    }
}

/**
 * Open-Meteo geocoding response
 */
data class GeocodingResponse(
    val results: List<GeocodingResult>?
)

data class GeocodingResult(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val admin1: String?
)