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

const val NUM_CHOICES = 6

class NameGameViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var nameGameApi: NameGameApi

    private lateinit var profilesRepository: ProfilesRepository

    private var profiles : List<Person>? = null

    private val _choices = MutableLiveData<List<Person>>()
    val choices : LiveData<List<Person>> = _choices

    private val _correctChoice = MutableLiveData<Person>()
    val correctChoice : LiveData<Person> = _correctChoice

    private var _isGameActive = MutableLiveData<Boolean>()
    val isGameActive : LiveData<Boolean> = _isGameActive

    fun init() {
        profilesRepository = ProfilesRepository(nameGameApi, object : ProfilesRepository.Listener {
            override fun onLoadFinished(people: List<Person>) {
                profiles = people
                newGame()
            }

            override fun onError(error: Throwable) {
                Log.e("NameGameViewModel", "Failure retrieving data from server: $error")
            }
        })
    }

    fun newGame() = profiles?.let {
        val choices = it.subList(0, NUM_CHOICES)
        _choices.postValue(choices)
        _correctChoice.postValue(choices[4])
        _isGameActive.postValue(true)
    }

    fun isCorrectChoice(index: Int): Boolean {
        val choices = _choices.value ?: return false
        if (index < 0 || index > choices.size) {
            return false
        }

        val choice = choices[index]
        if (choice == _correctChoice.value) {
            _isGameActive.postValue(false)
            return true
        }

        return false
    }
}