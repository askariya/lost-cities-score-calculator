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

    // Holds the Eight Card Bonus state for each column
    private val _eightCardBonusStates = MutableLiveData<Map<Int, Boolean>>()
    val eightCardBonusStates: LiveData<Map<Int, Boolean>> get() = _eightCardBonusStates

    // Holds the wager counts for each column
    private val _wagerCounts = MutableLiveData<Map<Int, Int>>()
    val wagerCounts: LiveData<Map<Int, Int>> get() = _wagerCounts

    // Holds the total points for the player
    private val _totalPoints = MutableLiveData<Int>()
    val totalPoints: LiveData<Int> get() = _totalPoints

    // Numbered button indices in the grid
    private val _buttonRowCount = 10
    private val _buttonColCount = 6
    private val _buttonRowStartIndex = 1
    private val _buttonColStartIndex = 0

    init {
        resetBoardCommand()
    }

    fun resetBoardCommand()
    {
        _totalPoints.value = 0
        _points.value = (_buttonColStartIndex until _buttonColCount).associateWith { 0 }
        _buttonStates.value = (_buttonColStartIndex until _buttonColCount).associateWith {
            (_buttonRowStartIndex until _buttonRowCount).associateWith { false }
        }
        _wagerCounts.value = (_buttonColStartIndex until _buttonColCount).associateWith { 0 }
    }

    fun toggleButtonStateCommand(row: Int, column: Int) {
        val currentState = _buttonStates.value?.get(column)?.get(row) ?: false
        setButtonState(row, column, !currentState)
    }

    fun toggleWagerCountCommand(column: Int) {
        val currentCount = _wagerCounts.value?.get(column) ?: 0
        val newCount = (currentCount + 1) % 4  // Cycle through 0, 1, 2, 3
        setWagerCount(column, newCount)
    }

    private fun updateTotalPoints() {
        _totalPoints.value = _points.value?.values?.sum() ?: 0
    }

    private fun setPoints(column: Int, points: Int) {
        val updatedPoints = _points.value?.toMutableMap() ?: mutableMapOf()
        updatedPoints[column] = points
        _points.value = updatedPoints
        updateTotalPoints()
    }

    private fun setButtonState(row: Int, column: Int, state: Boolean) {
        val updatedButtonStates = _buttonStates.value?.toMutableMap() ?: mutableMapOf()
        val columnStates = updatedButtonStates[column]?.toMutableMap() ?: mutableMapOf()
        columnStates[row] = state
        updatedButtonStates[column] = columnStates
        _buttonStates.value = updatedButtonStates
        updatePointsForColumn(column)
    }

    private fun setEightCardBonusState(column: Int, state: Boolean) {
        val updatedEightCardStates = _eightCardBonusStates.value?.toMutableMap() ?: mutableMapOf()
        updatedEightCardStates[column] = state
        _eightCardBonusStates.value = updatedEightCardStates
    }

    private fun setWagerCount(column: Int, count: Int) {
        val updatedWagerCounts = _wagerCounts.value?.toMutableMap() ?: mutableMapOf()
        updatedWagerCounts[column] = count
        _wagerCounts.value = updatedWagerCounts
        updatePointsForColumn(column)
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
        if ((buttonStates.count { it.value } + (wagerButtonMultiple - 1)) >= 8) {
            //TODO set the 8 card bonus
            columnScore += 20
            setEightCardBonusState(column, true)
        }
        else {
            setEightCardBonusState(column, false)
        }

        setPoints(column, columnScore)
    }
}
