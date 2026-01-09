package com.mor.avoidtherock
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class AppLifecycleObserver(private val context: Context) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        SoundManager.instance.playMusic()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        SoundManager.instance.stopMusic()
    }
}