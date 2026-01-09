package com.mor.avoidtherock
import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner

class MyGameApp : Application() {

    override fun onCreate() {
        super.onCreate()

        initSound()

        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver(this))
    }

    fun initSound() {
        SoundManager.init(this)

        SoundManager.instance.loadSound(this, R.raw.snd_crash)
        SoundManager.instance.loadSound(this, R.raw.snd_game_over)
        SoundManager.instance.loadSound(this, R.raw.snd_game_start)
    }

}