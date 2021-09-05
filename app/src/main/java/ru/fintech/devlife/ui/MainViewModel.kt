package ru.fintech.devlife.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import ru.fintech.devlife.database.PosterLocal
import ru.fintech.devlife.repository.DevLifeRepository
import ru.fintech.devlife.util.DataStatus

enum class UiAction {
    OK,
    LOADING,
    ERROR,
}

enum class UiStatus {
    START,
    MIDDLE,
    END
}

data class CategoryStatus(var currentPosterIndex: Int = 0, var nextCategoryPage: Int = 0)

@ExperimentalSerializationApi
class MainViewModel : ViewModel() {
    private var currentCategory = "latest"

    private var currentPosterIndex = 0

    private var currentCategoryListSize = 0

    private val currentCategoryList = mutableListOf<PosterLocal>()

    private val categoryIndex = mutableMapOf(
        "latest" to CategoryStatus(),
        "hot" to CategoryStatus(),
        "top" to CategoryStatus()
    )

    private val repository = DevLifeRepository()

    val uiStatus: LiveData<UiStatus> get() = _uiStatus
    private val _uiStatus = MutableLiveData<UiStatus>()


    val uiAction: LiveData<UiAction> get() = _uiAction
    private val _uiAction = MutableLiveData<UiAction>()

    val poster: LiveData<PosterLocal> get() = _poster
    private val _poster = MutableLiveData<PosterLocal>()

    init {
        _uiStatus.postValue(UiStatus.START)
        getPosters()
    }

    private fun getPosters(category: String = "latest") {
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

    private fun acceptNewData(category: String, data: List<PosterLocal>?) {
        if (!data.isNullOrEmpty()) {
            currentCategoryListSize = data.size
            if (currentPosterIndex == currentCategoryListSize - 1) {
                _uiStatus.postValue(UiStatus.END)
            }
            if (currentPosterIndex == 0) {
                _uiStatus.postValue(UiStatus.START)
            }
            currentCategoryList.clear()
            currentCategoryList.addAll(data)
            categoryIndex[category]?.nextCategoryPage =
                categoryIndex[category]?.nextCategoryPage?.inc() ?: 0
            _poster.postValue(currentCategoryList[currentPosterIndex])
        } else {
            _uiAction.postValue(UiAction.OK)
            _uiStatus.postValue(UiStatus.END)
        }
    }

    private fun getConnectionError(error: String?) {
        _uiAction.postValue(UiAction.ERROR)
    }

    private fun getConnectionFailure(error: String?) {
        _uiAction.postValue(UiAction.ERROR)
    }

    fun changeCategory(category: String) {
        _uiAction.postValue(UiAction.LOADING)
        currentCategory = category
        currentPosterIndex = categoryIndex[currentCategory]?.currentPosterIndex ?: 1
        when (currentPosterIndex) {
            0 -> _uiStatus.postValue(UiStatus.START)
            else -> _uiStatus.postValue(UiStatus.MIDDLE)
        }
        getPosters(category)
    }

    fun nextPoster() {
        _uiAction.postValue(UiAction.LOADING)
        if (currentPosterIndex < currentCategoryListSize - 1) {
            currentPosterIndex++
            categoryIndex[currentCategory]?.currentPosterIndex = currentPosterIndex
            _poster.postValue(currentCategoryList[currentPosterIndex])
        } else {
            viewModelScope.launch {
                val posters = repository.fetchNewPosters(
                    currentCategory,
                    categoryIndex[currentCategory]?.nextCategoryPage ?: 0
                )
                when (posters) {
                    is DataStatus.Success -> {
                        currentPosterIndex++
                        acceptNewData(currentCategory, posters.data)
                        _uiStatus.postValue(UiStatus.MIDDLE)

                    }
                    is DataStatus.Error -> getConnectionError(posters.error)
                    is DataStatus.Failure -> getConnectionFailure(posters.exception)
                }
            }
        }
    }

    fun previousPoster() {
        if (currentPosterIndex > 0) {
            currentPosterIndex--
            categoryIndex[currentCategory]?.currentPosterIndex = currentPosterIndex
            _uiAction.postValue(UiAction.LOADING)
            _poster.postValue(currentCategoryList[currentPosterIndex])
            if (currentPosterIndex == 0) {
                _uiStatus.postValue(UiStatus.START)
            }
        }
    }

    fun retryLoad() {
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

    fun complete() {
        _uiAction.postValue(UiAction.OK)
    }
}