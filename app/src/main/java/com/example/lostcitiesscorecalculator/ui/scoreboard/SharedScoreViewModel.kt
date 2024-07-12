package com.example.lostcitiesscorecalculator.ui.scoreboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedScoreViewModel : ViewModel() {

    private val _player1TotalPoints = MutableLiveData<Int>()
    val player1TotalPoints: LiveData<Int> get() = _player1TotalPoints

    private val _player2TotalPoints = MutableLiveData<Int>()
    val player2TotalPoints: LiveData<Int> get() = _player2TotalPoints

    fun setPlayer1CurrentPoints(points: Int) {
        _player1TotalPoints.value = points
    }

    fun setPlayer2CurrentPoints(points: Int) {
        _player2TotalPoints.value = points
    }
}
