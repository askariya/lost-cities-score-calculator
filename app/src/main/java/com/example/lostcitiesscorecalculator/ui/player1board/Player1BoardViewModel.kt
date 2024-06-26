package com.example.lostcitiesscorecalculator.ui.player1board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Player1BoardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Player 1 Board Fragment"
    }
    val text: LiveData<String> = _text
}