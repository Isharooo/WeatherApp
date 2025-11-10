package se.kth.weatherapp.data.api

import se.kth.weatherapp.data.model.*

/**
 * Parser för att konvertera SMHI API-svar till appens datamodeller
 */
class WeatherParser {

    /**
     * Konverterar SMHI-respons till WeatherForecast
     */
    fun parseWeatherData(
        response: SmhiResponse,
        locationName: String
    ): WeatherForecast {
        val coords = response.geometry.coordinates.firstOrNull()
        val location = Location(
            name = locationName,
            longitude = coords?.get(0) ?: 0.0,
            latitude = coords?.get(1) ?: 0.0
        )

        val forecasts = response.timeSeries.map { series ->
            extractHourlyData(series)
        }

        return WeatherForecast(
            location = location,
            forecasts = forecasts
        )
    }

    /**
     * Extraherar data för en timme
     */
    private fun extractHourlyData(series: TimeSeries): HourlyForecast {
        val params = series.parameters

        val cloudOktas = findParamValue(params, "tcc_mean") ?: 0.0
        val cloudPercent = ((cloudOktas / 8.0) * 100.0).toInt()

        return HourlyForecast(
            time = series.validTime,
            temperature = findParamValue(params, "t") ?: 0.0,
            weatherSymbol = findParamValue(params, "Wsymb2")?.toInt() ?: 1,
            cloudCover = cloudPercent
        )
    }

    /**
     * Hittar parametervärde baserat på namn
     */
    private fun findParamValue(params: List<Parameter>, name: String): Double? {
        return params.find { it.name == name }?.values?.firstOrNull()
    }

    /**
     * Grupperar prognoser per dag (för högre nivå)
     */
    fun groupForecastsByDay(forecasts: List<HourlyForecast>): List<DailyForecast> {
        return forecasts
            .groupBy { it.time.substring(0, 10) }
            .map { (date, hourlyList) ->
                val midday = hourlyList.getOrNull(12) ?: hourlyList.first()
                DailyForecast(
                    date = date,
                    weatherSymbol = midday.weatherSymbol,
                    hourlyForecasts = hourlyList
                )
            }
            .take(7)
    }

    /**
     * Konverterar plats-sökresultat
     * SMHI returnerar ibland olika format
     */
    fun parseLocations(results: List<SmhiLocationResponse>): List<Location> {
        return results
            .filter { it.category in listOf("locality", "municipality", "populated place") }
            .map { result ->
                Location(
                    name = result.name,
                    latitude = result.lat,
                    longitude = result.lon
                )
            }
            .distinctBy { it.name }
    }
}