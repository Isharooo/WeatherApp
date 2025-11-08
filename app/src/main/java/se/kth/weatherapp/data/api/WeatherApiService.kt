package se.kth.weatherapp.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
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