package com.mor.avoidtherock
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class AppLifecycleObserver : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        SoundManager.playMusic()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        SoundManager.stopMusic()
    }
}