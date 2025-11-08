package se.kth.weatherapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import se.kth.weatherapp.data.model.FavoriteLocation
import se.kth.weatherapp.data.model.WeatherForecast

/**
 * Hanterar data persistence med SharedPreferences
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "weather_prefs",
        Context.MODE_PRIVATE
    )

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    /**
     * Sparar senaste väderprognos (för offline-stöd)
     */
    fun saveLastForecast(forecast: WeatherForecast) {
        val adapter = moshi.adapter(WeatherForecast::class.java)
        val json = adapter.toJson(forecast)
        prefs.edit().putString(KEY_LAST_FORECAST, json).apply()
    }

    /**
     * Hämtar senast sparad väderprognos
     */
    fun getLastForecast(): WeatherForecast? {
        val json = prefs.getString(KEY_LAST_FORECAST, null) ?: return null
        val adapter = moshi.adapter(WeatherForecast::class.java)
        return try {
            adapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Sparar favorit-platser
     */
    fun saveFavorites(favorites: List<FavoriteLocation>) {
        val type = Types.newParameterizedType(List::class.java, FavoriteLocation::class.java)
        val adapter = moshi.adapter<List<FavoriteLocation>>(type)
        val json = adapter.toJson(favorites)
        prefs.edit().putString(KEY_FAVORITES, json).apply()
    }

    /**
     * Hämtar sparade favoriter
     */
    fun getFavorites(): List<FavoriteLocation> {
        val json = prefs.getString(KEY_FAVORITES, null) ?: return emptyList()
        val type = Types.newParameterizedType(List::class.java, FavoriteLocation::class.java)
        val adapter = moshi.adapter<List<FavoriteLocation>>(type)
        return try {
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Lägger till favorit
     */
    fun addFavorite(location: FavoriteLocation) {
        val current = getFavorites().toMutableList()
        if (!current.any { it.name == location.name }) {
            current.add(location)
            saveFavorites(current)
        }
    }

    /**
     * Tar bort favorit
     */
    fun removeFavorite(locationName: String) {
        val current = getFavorites().toMutableList()
        current.removeAll { it.name == locationName }
        saveFavorites(current)
    }

    companion object {
        private const val KEY_LAST_FORECAST = "last_forecast"
        private const val KEY_FAVORITES = "favorites"
    }
}