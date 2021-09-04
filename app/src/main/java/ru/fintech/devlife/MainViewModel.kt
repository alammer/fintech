package ru.fintech.devlife

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.fintech.devlife.network.DevLifeService

class MainViewModel : ViewModel() {

    private val networkData = DevLifeService

    //val actorList: LiveData<List<MovieCrew>?> get() = _actorList
    //private val _actorList = MutableLiveData<List<MovieCrew>?>()

    fun fetchPost (movieId: Int) {
        viewModelScope.launch {
            try {
                val devLifeResponce = networkData.retrofitService.getPoster("",0)
                if (devLifeResponce.isSuccessful) {
                    devLifeResponce.body()?.let {
                        //_actorList.value = it
                    }
                    Log.i("Success", "Function called: getStockList()")
                } else {
                    devLifeResponce.errorBody()?.let {
                        Log.i("fetchActors", "errorBody: ${devLifeResponce.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.i("fetchActors", "Exception -  ${e.message}")
            }
        }
    }

}