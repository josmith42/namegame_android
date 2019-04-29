package com.willowtreeapps.namegame.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.willowtreeapps.namegame.core.GameProfile
import com.willowtreeapps.namegame.core.GuessState
import com.willowtreeapps.namegame.core.ListRandomizer
import com.willowtreeapps.namegame.network.api.NameGameApi
import com.willowtreeapps.namegame.network.api.ProfilesRepository
import com.willowtreeapps.namegame.network.api.model.Person
import javax.inject.Inject

const val NUM_CHOICES = 6

class NameGameViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var nameGameApi: NameGameApi

    @Inject
    lateinit var listRandomizer: ListRandomizer

    private lateinit var profilesRepository: ProfilesRepository

    private var profiles : List<Person>? = null

    private val _choices = MutableLiveData<List<GameProfile>>()
    val choices : LiveData<List<GameProfile>> = _choices

    private val _correctChoice = MutableLiveData<Person>()
    val correctChoice : LiveData<Person> = _correctChoice

    private var _isGameActive = MutableLiveData<Boolean>()
    val isGameActive : LiveData<Boolean> = _isGameActive

    fun init() {
        if (::profilesRepository.isInitialized) {
            return
        }
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

    fun newGame() = profiles?.let { profile ->
        val choices = profile.subList(0, NUM_CHOICES).map { GameProfile(it) }
        _choices.postValue(choices)
        _correctChoice.postValue(listRandomizer.pickOne(choices).person)
        _isGameActive.postValue(true)
    }

    fun submitChoice(index: Int): Boolean {
        val choices = _choices.value ?: return false
        if (index < 0 || index > choices.size) {
            return false
        }

        val choice = choices[index]
        val isCorrect = choice.person == _correctChoice.value
        if (isCorrect) {
            choice.guessState = GuessState.CorrectGuess
            _choices.postValue(choices)
            _isGameActive.postValue(false)
        }
        else {
            choice.guessState = GuessState.IncorrectGuess
            _choices.postValue(choices)
        }
        return isCorrect
    }

    val overallGameState : GuessState
        get() {
            val choices = _choices.value ?: return GuessState.NotGuessed
            if (choices.any { it.guessState == GuessState.CorrectGuess }) {
                return GuessState.CorrectGuess
            }
            if (choices.any { it.guessState == GuessState.IncorrectGuess }) {
                return GuessState.IncorrectGuess
            }
            return GuessState.NotGuessed
        }

}