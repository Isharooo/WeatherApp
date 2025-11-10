package se.kth.weatherapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import se.kth.weatherapp.data.model.DailyForecast
import se.kth.weatherapp.data.model.FavoriteLocation
import se.kth.weatherapp.data.model.Location
import se.kth.weatherapp.data.model.WeatherForecast
import se.kth.weatherapp.data.repository.WeatherRepository
import se.kth.weatherapp.utils.NetworkUtils
import se.kth.weatherapp.utils.PreferencesManager
import se.kth.weatherapp.data.api.WeatherParser

/**
 * ViewModel för väderappen
 * Hanterar UI-state och business logic
 */
class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()
    private val parser = WeatherParser()
    private lateinit var prefsManager: PreferencesManager

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Initial)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Location>>(emptyList())
    val searchResults: StateFlow<List<Location>> = _searchResults.asStateFlow()

    private val _favorites = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val favorites: StateFlow<List<FavoriteLocation>> = _favorites.asStateFlow()

    /**
     * Initierar ViewModel med context för SharedPreferences
     */
    fun initialize(context: Context) {
        prefsManager = PreferencesManager(context)
        loadFavorites()
    }

    /**
     * Hämtar väder för koordinater
     */
    fun fetchWeatherByCoordinates(
        latitude: Double,
        longitude: Double,
        locationName: String,
        context: Context
    ) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            if (!NetworkUtils.hasInternetConnection(context)) {
                val cachedData = prefsManager.getLastForecast()
                if (cachedData != null) {
                    _uiState.value = WeatherUiState.Success(
                        forecast = cachedData,
                        dailyForecasts = parser.groupForecastsByDay(cachedData.forecasts),
                        isOffline = true
                    )
                } else {
                    _uiState.value = WeatherUiState.Error("Ingen internetanslutning och ingen cachad data")
                }
                return@launch
            }

            val result = repository.fetchForecastForCoordinates(latitude, longitude, locationName)

            result.fold(
                onSuccess = { forecast ->
                    prefsManager.saveLastForecast(forecast)
                    _uiState.value = WeatherUiState.Success(
                        forecast = forecast,
                        dailyForecasts = parser.groupForecastsByDay(forecast.forecasts),
                        isOffline = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = WeatherUiState.Error(error.message ?: "Okänt fel")
                }
            )
        }
    }

    /**
     * Söker efter platser
     */
    fun searchLocations(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.value = emptyList()
                return@launch
            }

            val result = repository.searchLocationsByName(query)
            result.fold(
                onSuccess = { locations ->
                    _searchResults.value = locations
                },
                onFailure = {
                    _searchResults.value = emptyList()
                }
            )
        }
    }

    /**
     * Rensar sökresultat
     */
    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    /**
     * Laddar favoriter
     */
    private fun loadFavorites() {
        _favorites.value = prefsManager.getFavorites()
    }

    /**
     * Lägger till favorit
     */
    fun addToFavorites(location: Location) {
        val favorite = FavoriteLocation(
            name = location.name,
            latitude = location.latitude,
            longitude = location.longitude
        )
        prefsManager.addFavorite(favorite)
        loadFavorites()
    }

    /**
     * Tar bort favorit
     */
    fun removeFromFavorites(locationName: String) {
        prefsManager.removeFavorite(locationName)
        loadFavorites()
    }

    /**
     * Kollar om plats är favorit
     */
    fun isFavorite(locationName: String): Boolean {
        return _favorites.value.any { it.name == locationName }
    }
}

/**
 * UI State för väderappen
 */
sealed class WeatherUiState {
    object Initial : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(
        val forecast: WeatherForecast,
        val dailyForecasts: List<DailyForecast>,
        val isOffline: Boolean
    ) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}