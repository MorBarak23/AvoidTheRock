package com.mor.avoidtherock

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import com.google.android.material.button.MaterialButton
import com.mor.avoidtherock.databinding.ActivityMainBinding
import java.util.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import android.view.Gravity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlin.math.abs
import android.os.VibrationEffect
import android.os.Vibrator


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gameManager: GameManager
    private lateinit var accSensorApi: AccSensorApi
    private var isSensorMode = false
    private var canMove = true // for sensor fun
    private var timer: Timer? = null
    private lateinit var rockLocations: Array<Array<ImageView>>
    private lateinit var carLocations: Array<ImageView>
    private val rows = 7
    private val cols = 5
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    var gameDelay : Long = 400


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // show the app all over the screen edge to edge.
        binding = ActivityMainBinding.inflate(layoutInflater) // read the XML to the phone.
        setContentView(binding.root) // give the root to the content view. (replace the R.layout.activity_main.

        hideSystemUI()

        carLocations = arrayOf(
            binding.matrix60,
            binding.matrix61,
            binding.matrix62,
            binding.matrix63,
            binding.matrix64,
        )

        SoundManager.playSound(SoundManager.SOUND_GAME_START)

        createRockLocation() // create matrix of the app screen.
        gameManager = GameManager(rows, cols)

        initSensor()


        getUserPermission()

        binding.btnLeft.setOnClickListener { whenPressing(binding.btnLeft) }
        binding.btnRight.setOnClickListener { whenPressing(binding.btnRight) }

    }

    private fun getUserPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                }
                permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                }
                else -> {
                }
            }
        }

        locationPermissionRequest.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    private fun saveScoreWithLocation() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            val lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            val location = lastKnownLocationGPS ?: lastKnownLocationNetwork

            if (location != null) {
                lat = location.latitude
                lon = location.longitude
            }
        }

    }

    private fun initSensor() {
        accSensorApi = AccSensorApi(this, object : AccSensorCallBack {
            override fun data(x: Float, y: Float, z: Float) {
                if (isSensorMode) {
                    moveCarBySensor(x)
                }
            }
        })
    }

    private fun moveCarBySensor(x: Float) {

        val targetCol: Int = when {
            x > 4.0 -> 0
            x < -4.0 -> 4

            x > 1.5 -> 1
            x < -1.5 -> 3

            else -> 2
        }

        gameManager.car.setCurrentCol(targetCol)
    }

    private fun activateSensorMode() {
        accSensorApi.start()
        binding.btnLeft.isVisible = false
        binding.btnRight.isVisible = false
    }

    private fun deactivateSensorMode() {
        accSensorApi.stop()
        binding.btnLeft.isVisible = true
        binding.btnRight.isVisible = true
    }

    private fun createRockLocation() {
        rockLocations = arrayOf(
            arrayOf(binding.matrix00, binding.matrix01, binding.matrix02, binding.matrix03, binding.matrix04),
            arrayOf(binding.matrix10, binding.matrix11, binding.matrix12,binding.matrix13, binding.matrix14),
            arrayOf(binding.matrix20, binding.matrix21, binding.matrix22, binding.matrix23, binding.matrix24),
            arrayOf(binding.matrix30, binding.matrix31, binding.matrix32, binding.matrix33, binding.matrix34),
            arrayOf(binding.matrix40, binding.matrix41, binding.matrix42, binding.matrix43, binding.matrix44),
            arrayOf(binding.matrix50, binding.matrix51, binding.matrix52, binding.matrix53, binding.matrix54),
            arrayOf(binding.matrix60, binding.matrix61, binding.matrix62, binding.matrix63, binding.matrix64))
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
                } else if (gameManager.matrix[i][j] == 2) { // show coin where matrix == 2
                    currentIm.setImageResource(R.drawable.icn_coin)
                }
            }
        }

        binding.lblScore.text = "" + gameManager.score // update score UI

        updateUIHearts()
    }

    private fun updateUIHearts() { // check's if there was life count down
        if (gameManager.wasCrash) {
            vibrate()
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

    private fun vibrate() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        if (vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    private fun checkGameOver() {
        if (gameManager.isGameOver) {
            SoundManager.playSound(SoundManager.SOUND_GAME_OVER)
            stopTimer()

            val prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE)
            val playerName = prefs.getString("PLAYER_NAME", "Guest") ?: "Guest"

            saveScoreWithLocation()
            ScoreManager.addScore(this, playerName, gameManager.score, lat, lon)

            changeActivity("-Game Over-", "Your Score is: ${gameManager.score}\nDo you want to play again?")
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

/////////////////////// onResume & onStop & Timers ///////////////////////

    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE)
        gameDelay = prefs.getLong("GAME_SPEED", 700L)

        val sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE)
        isSensorMode = sharedPreferences.getBoolean("KEY_SENSOR_MODE", false)

        if (isSensorMode) {
            activateSensorMode()
        } else {
            deactivateSensorMode()
        }

        hideSystemUI()
        startTimer()
    }

    override fun onPause() {
        super.onPause()
        accSensorApi.stop()
        stopTimer()
    }

    private fun startTimer() {
        timer = Timer()

        timer?.schedule(object : TimerTask() {
            override fun run() {
                gameManager.updateGame()
                SoundManager.stopMusic()
                runOnUiThread {
                    updateUI()
                    checkGameOver()
                }
            }
        }, 0, gameDelay)
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

}























