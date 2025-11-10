package se.kth.weatherapp.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Mappar SMHI väderkodsymboler till ikoner och färger
 */
object WeatherIconMapper {

    /**
     * Hämtar ikon baserat på WMO väderkod
     */
    fun getWeatherIcon(weatherSymbol: Int): ImageVector {
        return when (weatherSymbol) {
            1, 2 -> Icons.Default.WbSunny
            3, 4 -> Icons.Default.WbCloudy
            5, 6 -> Icons.Default.Cloud
            7 -> Icons.Default.CloudQueue
            8, 9, 10 -> Icons.Default.Grain
            11, 12, 13, 14 -> Icons.Default.Thunderstorm
            15, 16, 17, 18 -> Icons.Default.AcUnit
            19, 20, 21 -> Icons.Default.Grain
            else -> Icons.Default.WbCloudy
        }
    }

    /**
     * Hämtar beskrivning för vädersymbol
     */
    fun getWeatherDescription(weatherSymbol: Int): String {
        return when (weatherSymbol) {
            1 -> "Klart"
            2 -> "Nästan klart"
            3 -> "Halvklart"
            4 -> "Molnigt"
            5 -> "Mulet"
            6 -> "Mulet"
            7 -> "Dimma"
            8 -> "Lätt regn"
            9 -> "Regn"
            10 -> "Kraftigt regn"
            11 -> "Åska"
            12, 13, 14 -> "Åskväder"
            15, 16 -> "Snö"
            17, 18 -> "Snöfall"
            19, 20, 21 -> "Snöblandat regn"
            else -> "Okänt"
        }
    }
}