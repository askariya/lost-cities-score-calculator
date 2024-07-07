package com.example.lostcitiesscorecalculator.ui.playerboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayerBoardViewModel(private val playerId: Int) : ViewModel() {
    // Holds the points for each column
    private val _points = MutableLiveData<Map<Int, Int>>()
    val points: LiveData<Map<Int, Int>> get() = _points

    // Holds the button states for each column
    private val _buttonStates = MutableLiveData<Map<Int, Map<Int, Boolean>>>()
    val buttonStates: LiveData<Map<Int, Map<Int, Boolean>>> get() = _buttonStates

    // Holds the wager counts for each column
    private val _wagerCounts = MutableLiveData<Map<Int, Int>>()
    val wagerCounts: LiveData<Map<Int, Int>> get() = _wagerCounts

    init {
        _points.value = emptyMap()
        _buttonStates.value = emptyMap()
        _wagerCounts.value = emptyMap()
    }

    fun setPoints(column: Int, points: Int) {
        val updatedPoints = _points.value?.toMutableMap() ?: mutableMapOf()
        updatedPoints[column] = points
        _points.value = updatedPoints
    }

    fun setButtonState(row: Int, column: Int, state: Boolean) {
        val updatedButtonStates = _buttonStates.value?.toMutableMap() ?: mutableMapOf()
        val columnStates = updatedButtonStates[column]?.toMutableMap() ?: mutableMapOf()
        columnStates[row] = state
        updatedButtonStates[column] = columnStates
        _buttonStates.value = updatedButtonStates
    }

    fun setWagerCount(column: Int, count: Int) {
        val updatedWagerCounts = _wagerCounts.value?.toMutableMap() ?: mutableMapOf()
        updatedWagerCounts[column] = count
        _wagerCounts.value = updatedWagerCounts
    }
}
