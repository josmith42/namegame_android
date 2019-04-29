package com.willowtreeapps.namegame.core

import com.willowtreeapps.namegame.network.api.model.Person

enum class GuessState {
    NotGuessed,
    CorrectGuess,
    IncorrectGuess
}

data class GameProfile(val person: Person, var guessState: GuessState = GuessState.NotGuessed)
