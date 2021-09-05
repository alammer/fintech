package ru.fintech.devlife.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.fintech.devlife.database.PosterLocal

@Serializable
data class DevLifeResponse(
    @SerialName("result")
    val responsePosterList: List<PosterDto> = emptyList(),
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

fun PosterDto.toLocalModel(category: String) = PosterLocal(
    index = null,
    category,
    posterId,
    description,
    posterUrl,
    posterWidth?.toInt(),
    posterHeight?.toInt()
)
