package com.example.lostcitiesscorecalculator

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.lostcitiesscorecalculator.ui.scoreboard.SharedScoreViewModel
import com.example.lostcitiesscorecalculator.ui.utils.GameStateManager

class LostCitiesScoreCalculatorApplication : Application() {
    lateinit var sharedScoreViewModel: SharedScoreViewModel

    override fun onCreate() {
        super.onCreate()

        // Initialize SharedScoreViewModel or inject it as needed
        sharedScoreViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this)
            .create(SharedScoreViewModel::class.java)

        // Initialize GameStateManager with Application context and SharedScoreViewModel
        GameStateManager.initialize(this, sharedScoreViewModel)
    }
}