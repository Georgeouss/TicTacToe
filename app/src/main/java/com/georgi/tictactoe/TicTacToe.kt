package com.georgi.tictactoe

import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*

typealias Point = Pair<Int, Int>
typealias Matrix = Array<CharArray>

class TicTacToeGame {
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

    private fun move(index: Int): Boolean {
        if (index in 1..9) {
            val col = (index - 1) % SIZE
            val row = SIZE - 1 - (index - 1) / SIZE

            if (board[row][col] == EMPTY_SPOT) {
                board[row][col] = PLAYER_ONE_SYMBOL
                return true
            }
            return false
        } else {
            return false
        }
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

                        best = max(best, minimax(board.clone(), depth + 1, !isMax, alpha, beta))
                        alpha = max(best, alpha)

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
                        best = min(best, minimax(board.clone(), depth + 1, !isMax, alpha, beta))
                        beta = min(best, beta)
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

    fun play() {
        while (true) {

            while (!move(Scanner(System.`in`).nextInt())) {
                println("Invalid move")
            }
            print()
            if (board.evaluateState() != NO_WINNER) {
                println("You win!")
                println(steps)
                break
            } else if (!isMoveLeft()) {
                println("Draw")
                println(steps)
                break
            }

            val pcMove = bestMove(board)
            board[pcMove.first][pcMove.second] = PLAYER_TWO_SYMBOL
            print()
            if (board.evaluateState() != NO_WINNER) {
                println("You lose!")
                println(steps)
                break
            } else if (!isMoveLeft()) {
                println("Draw")
                println(steps)
                break
            }
        }
    }

    fun print() {
        println(
            "  " + Arrays.deepToString(board)
                .replace(",", " ")
                .replace("[", "")
                .replace("]", "\n")
        )
    }
}