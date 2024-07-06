package com.example.lostcitiesscorecalculator.ui.player2board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Player2BoardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Player 2 Board Fragment"
    }
    val text: LiveData<String> = _text
}