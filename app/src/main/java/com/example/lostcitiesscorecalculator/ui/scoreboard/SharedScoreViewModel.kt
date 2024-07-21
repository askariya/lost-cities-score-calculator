package com.example.lostcitiesscorecalculator.ui.scoreboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedScoreViewModel : ViewModel() {

    private val _player1CurrentPoints = MutableLiveData<Int>()
    private val _player2CurrentPoints = MutableLiveData<Int>()
    private val _player1TotalPoints = MutableLiveData<Int>()
    private val _player2TotalPoints = MutableLiveData<Int>()
    private val _roundCounter = MutableLiveData<Int>()
    private val _roundScores: MutableLiveData<MutableMap<Int, Pair<Int, Int>>> = MutableLiveData(mutableMapOf())

    val player1CurrentPoints: LiveData<Int> get() = _player1CurrentPoints
    val player2CurrentPoints: LiveData<Int> get() = _player2CurrentPoints
    val player1TotalPoints: LiveData<Int> get() = _player1TotalPoints
    val player2TotalPoints: LiveData<Int> get() = _player2TotalPoints
    val roundCounter: LiveData<Int> get() = _roundCounter
    val roundScores: MutableLiveData<MutableMap<Int, Pair<Int, Int>>> get() = _roundScores

    init {
        // Round counter should begin at 1
        resetGame()
    }

    fun setPlayer1CurrentPoints(points: Int) {
        _player1CurrentPoints.value = points
    }

    fun setPlayer2CurrentPoints(points: Int) {
        _player2CurrentPoints.value = points
    }

    private fun setPlayer1TotalPoints(points: Int) {
        _player1TotalPoints.value = points
    }

    private fun setPlayer2TotalPoints(points: Int) {
        _player2TotalPoints.value = points
    }

    private fun addRoundScore(round: Int, player1RoundScore: Int, player2RoundScore: Int)
    {
        val roundScoreMap = roundScores.value ?: mutableMapOf()
        roundScoreMap[round] = Pair(player1RoundScore, player2RoundScore)
        roundScores.value = roundScoreMap
        setPlayer1TotalPoints((player1TotalPoints.value ?: 0) + player1RoundScore)
        setPlayer2TotalPoints((player2TotalPoints.value ?: 0) + player2RoundScore)
        incrementRoundCounter()
    }

    private fun resetRoundScores()
    {
        roundScores.value = mutableMapOf()
        setPlayer1TotalPoints(0)
        setPlayer2TotalPoints(0)
        resetRoundCounter()
    }

    private fun incrementRoundCounter() {
        _roundCounter.value = (_roundCounter.value ?: 1) + 1
    }

    private fun resetRoundCounter() {
        _roundCounter.value = 1
    }

    fun submitCurrentPointsToTotal() {
        addRoundScore(roundCounter.value ?: 1, player1CurrentPoints.value ?: 0, player2CurrentPoints.value ?: 0)
    }

    fun resetGame() {
        setPlayer1CurrentPoints(0)
        setPlayer2CurrentPoints(0)
        resetRoundScores()
    }

}
