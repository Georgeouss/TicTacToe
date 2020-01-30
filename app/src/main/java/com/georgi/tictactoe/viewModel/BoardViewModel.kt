package com.georgi.tictactoe.viewModel

import androidx.databinding.Bindable
import com.georgi.tictactoe.data.ObservableViewModel
import com.georgi.tictactoe.data.SingleLiveEvent

typealias Point = Pair<Int, Int>
typealias Matrix = Array<CharArray>

class BoardViewModel : ObservableViewModel() {
    companion object {
        const val PLAYER_ONE_SYMBOL = 'X'
        const val PLAYER_TWO_SYMBOL = 'O'
        const val EMPTY_SPOT = '.'
        const val SIZE = 3
        const val WIN_VALUE = 777
        const val LOSE_VALUE = -777
        const val NO_WINNER = 0
    }

    private val board: Matrix by lazy {
        Matrix(SIZE) { CharArray(SIZE) { EMPTY_SPOT } }
    }

    @get:Bindable
    val cell1
        get() = board[0][0]

    @get:Bindable
    val cell2
        get() = board[0][1]

    @get:Bindable
    val cell3
        get() = board[0][2]

    @get:Bindable
    val cell4
        get() = board[1][0]

    @get:Bindable
    val cell5
        get() = board[1][1]

    @get:Bindable
    val cell6
        get() = board[1][2]

    @get:Bindable
    val cell7
        get() = board[2][0]

    @get:Bindable
    val cell8
        get() = board[2][1]

    @get:Bindable
    val cell9
        get() = board[2][2]


    val toastMessage = SingleLiveEvent<String>()
    val endGameEvent = SingleLiveEvent<Unit>()
    val pcMove = SingleLiveEvent<Unit>()
    private var hasPlayed = false

    private fun isMoveLeft(): Boolean {
        for (row in board) {
            if (row.contains(EMPTY_SPOT))
                return true
        }
        return false
    }

    private fun Matrix.evaluateState(): Int {
        val playerOneWinningStreak =
            charArrayOf(PLAYER_ONE_SYMBOL, PLAYER_ONE_SYMBOL, PLAYER_ONE_SYMBOL)
        val playerTwoWinningStreak =
            charArrayOf(PLAYER_TWO_SYMBOL, PLAYER_TWO_SYMBOL, PLAYER_TWO_SYMBOL)
        for (row in this) {
            if (row.contentEquals(playerOneWinningStreak))
                return LOSE_VALUE
            if (row.contentEquals(playerTwoWinningStreak))
                return WIN_VALUE
        }
        for (col in 0 until SIZE) {
            this.map { it[col] }.toCharArray().also {
                if (it.contentEquals(playerOneWinningStreak))
                    return LOSE_VALUE
                if (it.contentEquals(playerTwoWinningStreak))
                    return WIN_VALUE
            }
        }

        this.mapIndexed { index, row -> row[index] }.toCharArray().also {
            if (it.contentEquals(playerOneWinningStreak))
                return LOSE_VALUE
            if (it.contentEquals(playerTwoWinningStreak))
                return WIN_VALUE
        }
        this.mapIndexed { index, row -> row[SIZE - 1 - index] }.toCharArray().also {
            if (it.contentEquals(playerOneWinningStreak))
                return LOSE_VALUE
            if (it.contentEquals(playerTwoWinningStreak))
                return WIN_VALUE
        }

        return NO_WINNER
    }

    private fun minimax(board: Matrix, depth: Int, isMax: Boolean, alpha: Int, beta: Int): Int {
        val score = board.evaluateState()
        var alpha = alpha
        var beta = beta

        steps++

        if (score == WIN_VALUE)
            return score - depth
        if (score == LOSE_VALUE)
            return score + depth
        if (!isMoveLeft())
            return NO_WINNER

        if (isMax) {
            var best = Int.MIN_VALUE
            for (row in 0 until SIZE) {
                for (col in 0 until SIZE) {
                    if (board[row][col] == EMPTY_SPOT) {
                        board[row][col] = PLAYER_TWO_SYMBOL

                        best = Integer.max(
                            best,
                            minimax(board.clone(), depth + 1, !isMax, alpha, beta)
                        )
                        alpha = Integer.max(best, alpha)

                        board[row][col] = EMPTY_SPOT

                        if (beta <= alpha)
                            return best
                    }
                }
            }
            return best
        } else {
            var best = Int.MAX_VALUE
            for (row in 0 until SIZE) {
                for (col in 0 until SIZE) {
                    if (board[row][col] == EMPTY_SPOT) {
                        board[row][col] = PLAYER_ONE_SYMBOL
                        best = Integer.min(
                            best,
                            minimax(board.clone(), depth + 1, !isMax, alpha, beta)
                        )
                        beta = Integer.min(best, beta)
                        board[row][col] = EMPTY_SPOT

                        if (beta <= alpha)
                            return best
                    }
                }
            }
            return best
        }
    }

    var steps = 0

    private fun bestMove(board: Matrix): Point {
        var bestVal = Int.MIN_VALUE
        var bestMove = Point(-1, -1)

        for (row in 0 until SIZE) {
            for (col in 0 until SIZE) {
                if (board[row][col] == EMPTY_SPOT) {
                    board[row][col] = PLAYER_TWO_SYMBOL

                    val moveVal = minimax(board.clone(), 0, false, Int.MIN_VALUE, Int.MAX_VALUE)
                    board[row][col] = EMPTY_SPOT

                    if (moveVal > bestVal) {
                        bestVal = moveVal
                        bestMove = Point(row, col)
                    }
                }
            }
        }

        return bestMove
    }

    fun onCell1Click() {
        onCellClick(Point(0, 0))
    }

    fun onCell2Click() {
        onCellClick(Point(0, 1))
    }

    fun onCell3Click() {
        onCellClick(Point(0, 2))
    }

    fun onCell4Click() {
        onCellClick(Point(1, 0))
    }

    fun onCell5Click() {
        onCellClick(Point(1, 1))
    }

    fun onCell6Click() {
        onCellClick(Point(1, 2))
    }

    fun onCell7Click() {
        onCellClick(Point(2, 0))
    }

    fun onCell8Click() {
        onCellClick(Point(2, 1))
    }

    fun onCell9Click() {
        onCellClick(Point(2, 2))
    }

    private fun onCellClick(point: Point) {
        if (board[point.first][point.second] == EMPTY_SPOT) {
            board[point.first][point.second] = PLAYER_ONE_SYMBOL
            hasPlayed = true
            if (board.evaluateState() != NO_WINNER) {
                toastMessage.value = "You win!"
                endGameEvent.call()
            } else if (!isMoveLeft()) {
                toastMessage.value = "Draw"
                endGameEvent.call()
            } else {
                pcMove.call()
            }
        } else {
            toastMessage.value = "Invalid move"
        }
        notifyChange()
    }

    fun pcMove() {
        if (hasPlayed) {
            val pcMove = bestMove(board)
            board[pcMove.first][pcMove.second] = PLAYER_TWO_SYMBOL
            if (board.evaluateState() != NO_WINNER) {
                toastMessage.value = "You lose!"
                endGameEvent.call()
            } else if (!isMoveLeft()) {
                toastMessage.value = "Draw"
                endGameEvent.call()
            }
            hasPlayed = false
            notifyChange()
        }
    }
}