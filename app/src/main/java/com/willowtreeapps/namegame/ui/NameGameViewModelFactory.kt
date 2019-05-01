package com.willowtreeapps.namegame.ui

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import com.willowtreeapps.namegame.core.NameGameApplication

/**
 * Factory for NameGameViewModel. Retrieves a view model object from ViewModelProviders and performs
 * dagger injection on it.
 */
class NameGameViewModelFactory {
    fun get(fragment: Fragment): NameGameViewModel {
        val model = ViewModelProviders.of(fragment).get(NameGameViewModel::class.java)
        val context = fragment.activity ?: throw IllegalStateException("Activity is null")
        NameGameApplication.get(context).component().inject(model)
        return model
    }
}