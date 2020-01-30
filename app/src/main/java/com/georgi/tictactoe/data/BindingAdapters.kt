package com.georgi.tictactoe.data

import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import com.georgi.tictactoe.R
import com.georgi.tictactoe.TicTacToeGame.Companion.PLAYER_ONE_SYMBOL
import com.georgi.tictactoe.TicTacToeGame.Companion.PLAYER_TWO_SYMBOL

@BindingAdapter("app:state")
fun setText(view: ImageButton, value: Char) {
    if (value == PLAYER_ONE_SYMBOL) {
        view.setImageResource(R.drawable.cross_ic)
    } else if (value == PLAYER_TWO_SYMBOL)
        view.setImageResource(R.drawable.circle_ic)
}