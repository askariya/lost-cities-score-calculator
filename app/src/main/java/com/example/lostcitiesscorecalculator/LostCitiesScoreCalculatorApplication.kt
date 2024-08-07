package com.example.lostcitiesscorecalculator

import android.app.Application
import com.example.lostcitiesscorecalculator.ui.utils.GameStateManager

class LostCitiesScoreCalculatorApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize GameStateManager with Application context
        GameStateManager.initialize(this)
    }
}