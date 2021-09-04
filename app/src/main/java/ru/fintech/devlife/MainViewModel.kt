package ru.fintech.devlife

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import ru.fintech.devlife.network.DevLifeService
import ru.fintech.devlife.network.PosterDto

class MainViewModel : ViewModel() {

    private val networkData = DevLifeService

    val posterList: LiveData<List<PosterDto>?> get() = _posterList
    private val _posterList = MutableLiveData<List<PosterDto>?>()

    init {
        fetchPost()
    }

    @ExperimentalSerializationApi
    private fun fetchPost (category: String = "top", page: Int = 0 ) {
        Log.i("fetchPost", "Function called: fetchPost()")
        viewModelScope.launch {
            try {
                val devLifeResponce = networkData.retrofitService.getPoster(category, page)
                Log.i("fetchPost", "${devLifeResponce.toString()}")
                if (devLifeResponce.isSuccessful) {
                    devLifeResponce.body()?.let {
                        Log.i("fetchPost", "${it.responsePosterList.toString()}")
                    }
                    Log.i("Success", "Function called: getStockList()")
                } else {
                    devLifeResponce.errorBody()?.let {
                        Log.i("fetchPost", "errorBody: ${devLifeResponce.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.i("fetchPost", "Exception -  ${e.message}")
            }
        }
    }

}