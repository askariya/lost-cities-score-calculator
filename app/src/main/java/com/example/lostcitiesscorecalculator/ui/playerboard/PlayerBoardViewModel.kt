package com.example.lostcitiesscorecalculator.ui.playerboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayerBoardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is a Player Board Fragment"
    }
    val text: LiveData<String> = _text
}