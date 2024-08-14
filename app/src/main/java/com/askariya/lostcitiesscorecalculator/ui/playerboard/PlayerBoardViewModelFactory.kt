import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.askariya.lostcitiesscorecalculator.ui.playerboard.PlayerBoardViewModel

class PlayerBoardViewModelFactory(private val playerId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerBoardViewModel::class.java)) {
            return PlayerBoardViewModel(playerId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}