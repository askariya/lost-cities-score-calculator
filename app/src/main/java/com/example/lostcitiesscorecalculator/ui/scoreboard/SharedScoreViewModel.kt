package com.example.lostcitiesscorecalculator.ui.scoreboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedScoreViewModel : ViewModel() {

    private val _player1CurrentPoints = MutableLiveData<Int>()
    private val _player2CurrentPoints = MutableLiveData<Int>()
    private val _player1TotalPoints = MutableLiveData<Int>()
    private val _player2TotalPoints = MutableLiveData<Int>()
    private val _player1RoundScore = MutableLiveData<Int>()
    private val _player2RoundScore = MutableLiveData<Int>()
    private val _roundCounter = MutableLiveData<Int>()

    val player1CurrentPoints: LiveData<Int> get() = _player1CurrentPoints
    val player2CurrentPoints: LiveData<Int> get() = _player2CurrentPoints
    val player1TotalPoints: LiveData<Int> get() = _player1TotalPoints
    val player2TotalPoints: LiveData<Int> get() = _player2TotalPoints
    val player1RoundScore: LiveData<Int> get() = _player1RoundScore
    val player2RoundScore: LiveData<Int> get() = _player2RoundScore
    val roundCounter: LiveData<Int> get() = _roundCounter

    init {
        // Round counter should begin at 1
        resetRoundCounter()
    }

    fun setPlayer1CurrentPoints(points: Int) {
        _player1CurrentPoints.value = points
    }

    fun setPlayer2CurrentPoints(points: Int) {
        _player2CurrentPoints.value = points
    }

    private fun setPlayer1RoundScore(points: Int) {
        _player1RoundScore.value = points
    }

    private fun setPlayer2RoundScore(points: Int) {
        _player2RoundScore.value = points
    }

    private fun setPlayer1TotalPoints(points: Int) {
        _player1TotalPoints.value = points
    }

    private fun setPlayer2TotalPoints(points: Int) {
        _player2TotalPoints.value = points
    }

    private fun incrementRoundCounter() {
        _roundCounter.value = (_roundCounter.value ?: 1) + 1
    }

    private fun resetRoundCounter() {
        _roundCounter.value = 1
    }

    //This is the only place the total points can be set.
    fun submitCurrentPointsToTotal() {
        setPlayer1TotalPoints((player1TotalPoints.value ?: 0) + (player1CurrentPoints.value ?: 0))
        setPlayer2TotalPoints((player2TotalPoints.value ?: 0) + (player2CurrentPoints.value ?: 0))
        setPlayer1RoundScore(player1CurrentPoints.value ?: 0)
        setPlayer2RoundScore(player2CurrentPoints.value ?: 0)
        incrementRoundCounter()
    }

}
