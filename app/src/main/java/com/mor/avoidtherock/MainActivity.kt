package com.mor.avoidtherock

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import com.google.android.material.button.MaterialButton
import com.mor.avoidtherock.databinding.ActivityMainBinding
import java.util.*
import android.content.DialogInterface
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import android.view.Gravity



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gameManager: GameManager
    private var timer: Timer? = null
    private val DELAY: Long = 500
    private lateinit var rockLocations: Array<Array<ImageView>>
    private lateinit var carLocations: Array<ImageView>
    private val rows = 5
    private val cols = 3


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // show the app all over the screen edge to edge.
        binding = ActivityMainBinding.inflate(layoutInflater) // read the XML to the phone.
        setContentView(binding.root) // give the root to the content view. (replace the R.layout.activity_main.

        hideSystemUI()

        carLocations = arrayOf(
            binding.matrix40,
            binding.matrix41,
            binding.matrix42
        )

        SoundManager.playSound(SoundManager.SOUND_GAME_START)

        createRockLocation(rows, cols) // create matrix of the app screen.
        gameManager = GameManager(rows, cols)

        binding.btnLeft.setOnClickListener { whenPressing(binding.btnLeft) }
        binding.btnRight.setOnClickListener { whenPressing(binding.btnRight) }
    }

    private fun createRockLocation(rows: Int, cols: Int) {
        rockLocations = arrayOf(
            arrayOf(binding.matrix00, binding.matrix01, binding.matrix02),
            arrayOf(binding.matrix10, binding.matrix11, binding.matrix12),
            arrayOf(binding.matrix20, binding.matrix21, binding.matrix22),
            arrayOf(binding.matrix30, binding.matrix31, binding.matrix32),
            arrayOf(binding.matrix40, binding.matrix41, binding.matrix42) )
    }

    private fun whenPressing(btn: MaterialButton) {
        if (btn == binding.btnLeft) {
            gameManager.moveCarLeft()
        } else { // btn == btnRight
            gameManager.moveCarRight()
        }
        updateUI()
    }

    private fun updateUI() {
        val carRow = rows - 1
        val currentCarCol = gameManager.car.getCurrentCol()

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val currentIm = rockLocations[i][j]
                currentIm.isVisible = true
                currentIm.setImageDrawable(null) // clean prev image

                if (i == carRow && j == currentCarCol) { // show car at the right image
                    currentIm.setImageResource(R.drawable.icn_car)
                }
                else if (gameManager.matrix[i][j] == 1) { // show rock where matrix == 1
                    currentIm.setImageResource(R.drawable.icn_rock)
                }
            }
        }

        binding.lblScore.text = "" + gameManager.score // update score UI

        updateUIHearts()
    }

    private fun updateUIHearts() { // check's if there was life count down
        if (gameManager.wasCrash) {
            SoundManager.playSound(SoundManager.SOUND_CRASH)
            gameManager.wasCrash = false
        }

        if (gameManager.lifeCounter < 1) {
            binding.icnheart3.isVisible = false
        } else if (gameManager.lifeCounter < 2) {
            binding.icnheart2.isVisible = false
        } else if (gameManager.lifeCounter < 3) {
            binding.icnheart1.isVisible = false
        }
    }

    private fun checkGameOver() {
        if (gameManager.isGameOver) {
            SoundManager.playSound(SoundManager.SOUND_GAME_OVER)
            stopTimer()

            val prefs = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE)
            val playerName = prefs.getString("PLAYER_NAME", "Guest") ?: "Guest"

            ScoreManager.addScore(this, playerName, gameManager.score)

            changeActivity("-Game Over-", "Do you want to play again?")
        }
    }

    private fun changeActivity(title: String, message: String) {
        val builder = AlertDialog.Builder(this)

        val titleView = TextView(this)
        // title settings
        titleView.text = title
        titleView.gravity = Gravity.CENTER
        titleView.textSize = 24f
        titleView.setPadding(20, 40, 20, 20)
        titleView.setTextColor(Color.BLACK)

        builder.setCustomTitle(titleView)

        val messageView = TextView(this)
        // text settings
        messageView.text = message
        messageView.gravity = Gravity.CENTER
        messageView.textSize = 18f
        messageView.setPadding(20, 10, 20, 20)
        messageView.setTextColor(Color.DKGRAY)

        builder.setView(messageView)

        builder.setCancelable(false)

        builder.setPositiveButton("Yes") { dialog, which ->
            restartGame()
        }

        builder.setNegativeButton("No") { dialog, which ->
            finish()
        }

        val dialog = builder.create() // create the dialog
        dialog.show() // show the dialog
    }

    private fun restartGame() {
        gameManager = GameManager(rows = rows, cols = cols)
        gameManager.lifeCounter = 3

        binding.icnheart1.isVisible = true
        binding.icnheart2.isVisible = true
        binding.icnheart3.isVisible = true

        updateUI()
        startTimer()
    }

    // hide bars of the phone
    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

/////////////////////// onResume & onStop ///////////////////////

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        startTimer()
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
    }

    private fun startTimer() {
        if (timer == null) {
            timer = Timer()
            timer?.schedule(object : TimerTask() {
                override fun run() {
                    SoundManager.stopMusic()
                    gameManager.updateGame()
                    runOnUiThread {
                        updateUI()
                        checkGameOver()
                    }
                }
            }, 0, DELAY)
        }
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

}























