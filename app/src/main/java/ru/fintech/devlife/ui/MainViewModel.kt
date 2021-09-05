package ru.fintech.devlife.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import ru.fintech.devlife.database.PosterLocal
import ru.fintech.devlife.repository.DevLifeRepository
import ru.fintech.devlife.util.DataStatus

enum class UiStatus {
    START,
    OK,
    LOADING,
    ERROR,
    END,
}

data class CategoryStatus (var currentPosterIndex: Int = 0, var nextCategoryPage: Int = 0)

@ExperimentalSerializationApi
class MainViewModel : ViewModel() {
    private var currentCategory = "latest"

    private var currentPosterIndex = 0

    private var currentCategoryListSize = 0

    private val currentCategoryList = mutableListOf<PosterLocal>()

    private val categoryIndex = mutableMapOf("latest" to CategoryStatus(), "hot" to CategoryStatus(), "top" to CategoryStatus())

    private val repository = DevLifeRepository()

    val uiStatus: LiveData <UiStatus> get() = _uiStatus
    private val _uiStatus = MutableLiveData<UiStatus>()

    val poster: LiveData <PosterLocal> get() = _poster
    private val _poster = MutableLiveData<PosterLocal>()

    init {
        getPosters()
    }

    private fun getPosters (category: String =  "latest") {
        Log.i("fetchPost", "Function called: fetchPost()")
        viewModelScope.launch {
            val posters = repository.getPostersByCategory(category)
            when (posters) {
                is DataStatus.Success -> {
                    acceptNewData(category, posters.data)
                }
                is DataStatus.Error -> getConnectionError(posters.error)
                is DataStatus.Failure -> getConnectionFailure(posters.exception)
            }
        }
    }

    private fun acceptNewData(category: String, data: List<PosterLocal>?){
        if (!data.isNullOrEmpty()) {
            currentCategoryListSize = data.size
            currentCategoryList.clear()
            currentCategoryList.addAll(data)
            currentPosterIndex++
            categoryIndex[category]?.currentPosterIndex = currentPosterIndex
            categoryIndex[category]?.nextCategoryPage = categoryIndex[category]?.nextCategoryPage?.inc() ?: 1
            _uiStatus.postValue(UiStatus.OK)
            _poster.postValue(currentCategoryList[currentPosterIndex - 1])
        }
        else {
            _uiStatus.postValue(UiStatus.END)
        }
    }

    private fun getConnectionError(error: String?) {
        _uiStatus.postValue(UiStatus.ERROR)
    }

    private fun getConnectionFailure(error: String?) {
        _uiStatus.postValue(UiStatus.ERROR)
    }

    fun changeCategory(category: String) {
        _uiStatus.postValue(UiStatus.LOADING)
        currentCategory = category
        getPosters(category)
    }

    fun nextPoster() {
        _uiStatus.postValue(UiStatus.LOADING)
        if (currentPosterIndex < currentCategoryListSize - 1) {
            currentPosterIndex++
            categoryIndex[currentCategory]?.currentPosterIndex = currentPosterIndex
            _poster.postValue(currentCategoryList[currentPosterIndex -1])
        }
        else {
            viewModelScope.launch {
                val posters = repository.fetchNewPosters(
                    currentCategory,
                    categoryIndex[currentCategory]?.nextCategoryPage ?: 0
                )
                when (posters) {
                    is DataStatus.Success -> {
                        acceptNewData(currentCategory, posters.data)
                    }
                    is DataStatus.Error -> getConnectionError(posters.error)
                    is DataStatus.Failure -> getConnectionFailure(posters.exception)
                }
            }
        }
    }

    fun previousPoster() {
        _uiStatus.postValue(UiStatus.LOADING)
        if (currentPosterIndex > 1) {
            currentPosterIndex--
            categoryIndex[currentCategory]?.currentPosterIndex = currentPosterIndex
            _poster.postValue(currentCategoryList[currentPosterIndex -1])
        }
        else {
            _uiStatus.postValue(UiStatus.START)
        }
    }

    fun complete() {
        _uiStatus.postValue(UiStatus.OK)
    }
}