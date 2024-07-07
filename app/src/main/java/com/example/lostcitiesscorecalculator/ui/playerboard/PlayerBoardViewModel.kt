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

    private fun setPoints(column: Int, points: Int) {
        val updatedPoints = _points.value?.toMutableMap() ?: mutableMapOf()
        updatedPoints[column] = points
        _points.value = updatedPoints
    }

    private fun setButtonState(row: Int, column: Int, state: Boolean) {
        val updatedButtonStates = _buttonStates.value?.toMutableMap() ?: mutableMapOf()
        val columnStates = updatedButtonStates[column]?.toMutableMap() ?: mutableMapOf()
        columnStates[row] = state
        updatedButtonStates[column] = columnStates
        _buttonStates.value = updatedButtonStates
        updatePointsForColumn(column)
    }

    fun toggleButtonState(row: Int, column: Int) {
        val currentState = _buttonStates.value?.get(column)?.get(row) ?: false
        setButtonState(row, column, !currentState)
    }

    private fun setWagerCount(column: Int, count: Int) {
        val updatedWagerCounts = _wagerCounts.value?.toMutableMap() ?: mutableMapOf()
        updatedWagerCounts[column] = count
        _wagerCounts.value = updatedWagerCounts
        updatePointsForColumn(column)
    }

    fun toggleWagerCount(column: Int) {
        val currentCount = _wagerCounts.value?.get(column) ?: 0
        val newCount = (currentCount + 1) % 4  // Cycle through 0, 1, 2, 3
        setWagerCount(column, newCount)
    }

    private fun updatePointsForColumn(column: Int) {
        val buttonStates = _buttonStates.value?.get(column) ?: mapOf()
        val wagerButtonMultiple = (_wagerCounts.value?.get(column) ?: 0) + 1
        var columnScore = 0

        // Calculate Score for numbered buttons based on their row values
        buttonStates.forEach { (row, isSelected) ->
            if (isSelected) {
                columnScore += (row + 1)
            }
        }

        // Adjust score based on selected button count and wager multiple
        if (buttonStates.any { it.value } || wagerButtonMultiple > 1) {
            columnScore -= 20
        }

        columnScore *= wagerButtonMultiple

        // Additional logic based on your requirements
        if (buttonStates.count { it.value } >= 8) {
            columnScore += 20
        }

        setPoints(column, columnScore)
    }
}
