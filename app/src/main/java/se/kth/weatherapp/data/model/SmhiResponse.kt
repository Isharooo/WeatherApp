package se.kth.weatherapp.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SmhiResponse(
    @Json(name = "approvedTime")
    val approvedTime: String,

    @Json(name = "referenceTime")
    val referenceTime: String,

    @Json(name = "geometry")
    val geometry: Geometry,

    @Json(name = "timeSeries")
    val timeSeries: List<TimeSeries>
)

@JsonClass(generateAdapter = true)
data class Geometry(
    @Json(name = "type")
    val type: String,

    @Json(name = "coordinates")
    val coordinates: List<List<Double>>
)

@JsonClass(generateAdapter = true)
data class TimeSeries(
    @Json(name = "validTime")
    val validTime: String,

    @Json(name = "parameters")
    val parameters: List<Parameter>
)

@JsonClass(generateAdapter = true)
data class Parameter(
    @Json(name = "name")
    val name: String,

    @Json(name = "levelType")
    val levelType: String?,

    @Json(name = "level")
    val level: Int?,

    @Json(name = "unit")
    val unit: String?,

    @Json(name = "values")
    val values: List<Double>
)

@JsonClass(generateAdapter = true)
data class SmhiLocationResponse(
    @Json(name = "name")
    val name: String,

    @Json(name = "category")
    val category: String,

    @Json(name = "lon")
    val lon: Double,

    @Json(name = "lat")
    val lat: Double
)