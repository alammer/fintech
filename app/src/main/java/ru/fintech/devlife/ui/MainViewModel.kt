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

@ExperimentalSerializationApi
class MainViewModel : ViewModel() {

    private val repository = DevLifeRepository()

    val posterList: LiveData<List<PosterLocal>?> get() = _posterList
    private val _posterList = MutableLiveData<List<PosterLocal>?>()

    init {
        getPosters()
    }

    private fun getPosters (category: String = "latest", page: Int = 0 ) {
        Log.i("fetchPost", "Function called: fetchPost()")
        viewModelScope.launch {
            val posters = repository.getPostersByCategory(category)
            _posterList.value = posters
        }
    }
}