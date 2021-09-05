package ru.fintech.devlife.util

import ru.fintech.devlife.database.PosterLocal

sealed class DataStatus {
    data class Success(val data : List<PosterLocal>?) : DataStatus()
    data class Error(val error: String?) : DataStatus()
    data class Failure(val exception : String?) : DataStatus()
}
