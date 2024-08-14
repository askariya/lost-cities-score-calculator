package com.askariya.lostcitiesscorecalculator

import android.app.Application
import com.askariya.lostcitiesscorecalculator.ui.utils.GameStateManager

class LostCitiesScoreCalculatorApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize GameStateManager with Application context
        GameStateManager.initialize(this)
    }
}