package com.willowtreeapps.namegame.core

import com.willowtreeapps.namegame.network.api.model.Person

enum class GuessState {
    NotGuessed,
    CorrectGuess,
    IncorrectGuess
}

/**
 * A small wrapper class to contain a Person object along with a its guess state.
 */
data class GameProfile(val person: Person, var guessState: GuessState = GuessState.NotGuessed)
