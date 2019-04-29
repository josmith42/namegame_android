package com.willowtreeapps.namegame.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.willowtreeapps.namegame.network.api.NameGameApi
import com.willowtreeapps.namegame.network.api.model.Profiles
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class NameGameViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var nameGameApi: NameGameApi

    private val _profiles = MutableLiveData<Profiles?>()
    val profiles : LiveData<Profiles?> = _profiles

    fun init() {

         nameGameApi.profiles.enqueue(object : Callback<Profiles> {
            override fun onResponse(call: Call<Profiles>, response: Response<Profiles>) {
                _profiles.postValue(response.body())
            }

            override fun onFailure(call: Call<Profiles>, t: Throwable) {
                Log.e("NameGameViewModel", "Failure retrieving data from server: $t")
            }

        })
    }
}