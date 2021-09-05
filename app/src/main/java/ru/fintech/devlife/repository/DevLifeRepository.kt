package ru.fintech.devlife.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import ru.fintech.devlife.database.PosterDataBase
import ru.fintech.devlife.database.PosterLocal
import ru.fintech.devlife.network.DevLifeService
import ru.fintech.devlife.network.toLocalModel

@ExperimentalSerializationApi
class DevLifeRepository {

    private val networkAPI = DevLifeService.retrofitService

    private val dataDao = PosterDataBase.instance.posterDataDao

    suspend fun getPostersByCategory(category: String = "latest"): List<PosterLocal>? = withContext(Dispatchers.IO) {
        var localPosters = dataDao.getPostersByCategory(category)
        if (localPosters.isNullOrEmpty()) {
            fetchNewPosters(category, 0)
            localPosters = dataDao.getPostersByCategory(category)
        }
        localPosters
    }

    private suspend fun fetchNewPosters(category: String, page: Int) {
        try {
            val responsePosters = networkAPI.getPosters(category, page)

            if (responsePosters.isSuccessful) {
                val newPosters = responsePosters.body()?.responsePosterList
                if (!newPosters.isNullOrEmpty()) {
                    newPosters.map { it.toLocalModel(category) }.let { newPosters ->
                        dataDao.insertPosters(newPosters)
                    }
                }
            } else {
                responsePosters.errorBody()?.let {
                    Log.i("fetchNewPosters", "errorBody: ${responsePosters.code()}")
                }
            }
        } catch (e: Exception) {
            Log.i("fetchNewPosters", "Exception -  ${e.message}")
        }
    }
}