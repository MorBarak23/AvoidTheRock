package com.mor.avoidtherock

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mor.avoidtherock.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SoundManager.instance.playMusic()

        initViews()
    }

    private fun initViews() {
        binding.btnRecords.setOnClickListener {
            val intent = Intent(this, RecordsActivity::class.java)
            startActivity(intent)
        }

        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.btnStartGame.setOnClickListener {
            startGame()
        }
    }

    private fun startGame() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        SoundManager.instance.playMusic()
    }

}