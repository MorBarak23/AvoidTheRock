package com.mor.avoidtherock
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.VibrationEffect
import android.os.Vibrator
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class SoundManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private val soundMap = HashMap<Int, Int>()
    private val executor: Executor = Executors.newSingleThreadExecutor()
    val SOUND_MENU = R.raw.menu_music

    init{
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, SOUND_MENU)
            mediaPlayer?.isLooping = true
            mediaPlayer?.setVolume(0.5f, 0.5f)
        }

        if (soundPool == null) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build()
        }
    }

    fun loadSound(context: Context, resId: Int) {
        executor.execute {
            val soundId = soundPool?.load(context, resId, 1) ?: 0
            soundMap[resId] = soundId
        }
    }

    fun playMusic() {
        if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
        }
    }

    fun vibrate() {
        val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator

        if (vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    fun stopMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            mediaPlayer?.seekTo(0)
        }
    }

    fun playSound(resId: Int) {
        val soundId = soundMap[resId] ?: return
        soundPool?.play(soundId, 1f, 1f, 2, 0, 1f)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: SoundManager
            private set

        fun init(context: Context) {
            if (!::instance.isInitialized) {
                instance = SoundManager(context)
            }
        }
    }

}