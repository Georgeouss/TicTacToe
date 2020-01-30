package com.georgi.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.georgi.tictactoe.databinding.ActivityMainBinding
import com.georgi.tictactoe.viewModel.BoardViewModel
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: BoardViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = BoardViewModel()

        binding.viewModel = viewModel

        viewModel.endGameEvent.observe(this, Observer {
            AlertDialog.Builder(this)
                .setTitle("The game finished")
                .setMessage("Would you like to start a new game ?")
                .setPositiveButton(android.R.string.yes) { _, _ -> recreate() }
                .setNegativeButton(android.R.string.no) { _, _ ->
                    finishAffinity()
                    exitProcess(0)
                }
                .show()
        })

        viewModel.toastMessage.observe(this, Observer { value ->
            Toast.makeText(applicationContext, value, Toast.LENGTH_LONG).show()
        })

        viewModel.pcMove.observe(this, Observer {
            viewModel.pcMove()
        })
    }
}
