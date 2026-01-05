package com.mor.avoidtherock
import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner

class MyGameApp : Application() {

    override fun onCreate() {
        super.onCreate()

        SoundManager.init(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver())
    }

}