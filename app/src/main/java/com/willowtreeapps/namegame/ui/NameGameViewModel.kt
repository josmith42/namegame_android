package com.willowtreeapps.namegame.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.willowtreeapps.namegame.network.api.NameGameApi
import com.willowtreeapps.namegame.network.api.ProfilesRepository
import com.willowtreeapps.namegame.network.api.model.Person
import javax.inject.Inject

class NameGameViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var nameGameApi: NameGameApi

    lateinit var profilesRepository: ProfilesRepository

    private val _profiles = MutableLiveData<List<Person>>()
    val profiles : LiveData<List<Person>> = _profiles

    fun init() {
        profilesRepository = ProfilesRepository(nameGameApi, object : ProfilesRepository.Listener {
            override fun onLoadFinished(people: List<Person>) {
                _profiles.postValue(people)
            }

            override fun onError(error: Throwable) {
                Log.e("NameGameViewModel", "Failure retrieving data from server: $error")
            }
        })
    }
}