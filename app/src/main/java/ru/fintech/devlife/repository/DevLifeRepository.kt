package ru.fintech.devlife.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import ru.fintech.devlife.database.PosterDataBase
import ru.fintech.devlife.database.PosterLocal
import ru.fintech.devlife.network.DevLifeService
import ru.fintech.devlife.network.toLocalModel
import ru.fintech.devlife.util.DataStatus

@ExperimentalSerializationApi
class DevLifeRepository {

    private val networkAPI = DevLifeService.retrofitService

    private val dataDao = PosterDataBase.instance.posterDataDao

    suspend fun getPostersByCategory(category: String = "latest"): DataStatus = withContext(Dispatchers.IO) {
        var localPosters = dataDao.getPostersByCategory(category)
        if (localPosters.isNullOrEmpty()) {
            fetchNewPosters(category, 0)
        }
        else DataStatus.Success(localPosters)
    }

    suspend fun fetchNewPosters(category: String, page: Int): DataStatus {
        return try {
            val responsePosters = networkAPI.getPosters(category, page)
            if (responsePosters.isSuccessful) {
                val newPosters = responsePosters.body()?.responsePosterList
                if (!newPosters.isNullOrEmpty()) {
                    newPosters.map { it.toLocalModel(category) }.let { newPosters ->
                        dataDao.insertPosters(newPosters)
                    }
                }
                DataStatus.Success(dataDao.getPostersByCategory(category))
            } else {
               DataStatus.Error(responsePosters.errorBody()?.toString() ?: "Uknown server error")
            }
        } catch (e: Exception) {
            DataStatus.Failure(e.message ?: "Uknown network exception")
        }
    }
}