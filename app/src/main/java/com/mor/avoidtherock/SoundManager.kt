package com.mor.avoidtherock
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object SoundManager {

    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private val soundMap = HashMap<Int, Int>()
    private val executor: Executor = Executors.newSingleThreadExecutor()
    val SOUND_CRASH = R.raw.snd_crash
    val SOUND_GAME_OVER = R.raw.snd_game_over
    val SOUND_GAME_START = R.raw.snd_game_start
    val SOUND_MENU = R.raw.menu_music


    fun init(context: Context) {
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


            loadSound(context, SOUND_CRASH)
            loadSound(context, SOUND_GAME_START)
            loadSound(context, SOUND_GAME_OVER)

        }
    }

    private fun loadSound(context: Context, resId: Int) {
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

    fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
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
        soundPool?.play(soundId, 1f, 1f, 0, 0, 1f)
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        soundPool?.release()
        soundPool = null
        soundMap.clear()
    }
}