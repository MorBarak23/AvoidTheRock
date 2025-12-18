package com.mor.avoidtherock

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mor.avoidtherock.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPlayerName()

        SoundManager.init(this)
        SoundManager.playMusic()

        binding.btnRecords.setOnClickListener {
            val intent = Intent(this, RecordsActivity::class.java)
            startActivity(intent)
        }

        binding.btnStartGame.setOnClickListener {
            startGame()
        }

        binding.btnWriteName.setOnClickListener {
            showNameDialog()
        }
    }

    private fun startGame() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }

    private fun showNameDialog() {
        val input = EditText(this) // create a writing field

        input.filters = arrayOf(InputFilter.LengthFilter(11)) // max of 11 chars

        input.hint = "Name"

        // create dialog
        val builder = AlertDialog.Builder(this)
        val titleView = TextView(this)
        // title settings
        titleView.text = "Enter User Name"
        titleView.gravity = Gravity.CENTER
        titleView.textSize = 24f
        titleView.setPadding(20, 40, 20, 20)
        titleView.setTextColor(Color.BLACK)

        builder.setCustomTitle(titleView)

        builder.setView(input)


        // save the name into lbl_name
        builder.setPositiveButton("Save") { dialog, which ->
            val newName = input.text.toString()
            if (newName.isNotEmpty()) {
                savePlayerName(newName) // save in memory
                binding.lblName.text = newName // update UI
            }
        }
        // define cancel button
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        builder.show() // show dialog
    }

    // save name in phone memory function
    private fun savePlayerName(name: String) {
        val sharedPreferences = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("PLAYER_NAME", name)
        editor.apply()
    }

    // load name from phone memory function
    private fun loadPlayerName() {
        val sharedPreferences = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE)
        val savedName = sharedPreferences.getString("PLAYER_NAME", "Guest")
        binding.lblName.text = savedName
    }

    override fun onResume() {
        super.onResume()
        SoundManager.init(this)
        SoundManager.playMusic()
    }
}