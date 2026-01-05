package com.mor.avoidtherock

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch
import com.mor.avoidtherock.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: SharedPreferences
    private val FAST : Long = 350L
    private val SLOW : Long = 600L

    lateinit var speedSwitch: MaterialSwitch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        speedSwitch = binding.btnFastMode
        prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE)
        val savedDelay = prefs.getLong("GAME_SPEED", SLOW)

        speedSwitch.isChecked = savedDelay != SLOW

        speedSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = prefs.edit()

            if (isChecked) {
                editor.putLong("GAME_SPEED", FAST)
            } else {
                editor.putLong("GAME_SPEED", SLOW)
            }
            editor.apply()
        }

        val sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE)
        val isSensorOn = sharedPreferences.getBoolean("KEY_SENSOR_MODE", false)
        binding.btnSensorMode.isChecked = isSensorOn

        binding.btnSensorMode.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("KEY_SENSOR_MODE", isChecked)
            editor.apply()
        }

        initViews()
        loadPlayerName()

    }

    private fun initViews() {
        binding.btnWriteName.setOnClickListener {
            showNameDialog()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

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

}