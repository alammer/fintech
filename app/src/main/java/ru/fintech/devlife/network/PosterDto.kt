package ru.fintech.devlife.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DevLifesResponse(
    @SerialName("result")
    val responseMoviesList: List<PosterDto> = emptyList(),
)

@Serializable
data class PosterDto(
    @SerialName("id")
    val posterId: Int,

    @SerialName("description")
    val description: String?,

    @SerialName("gifURL")
    val posterUrl: String?,

    @SerialName("width")
    val posterWidth: String?,

    @SerialName("height")
    val posterHeight: String?,
)
