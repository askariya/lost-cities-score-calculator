package com.askariya.lostcitiesscorecalculator.ui.scoreboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Scoreboard Fragment"
    }
    val text: LiveData<String> = _text
}