package com.example.androidservice

import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.widget.Toast

class MyBackGroundService : Service() {
    // Binder given to clients
    private val binder = MyBackGroundBinder()
    private var play: Boolean = false
    private lateinit var uri: Uri

    override fun onBind(intent: Intent?): IBinder {
        Toast.makeText(this, "Service has been bound", Toast.LENGTH_SHORT).show()
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        playRingTone()
        return super.onStartCommand(intent, flags, startId)
    }

    fun playRingTone() {
        play = true
        uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val ringTone = RingtoneManager.getRingtone(applicationContext, uri)
        Thread {
            while (play) {
                ringTone.play()
                Thread.sleep(2000)
            }
        }.start()
    }

    fun stopRingTone() {
        play = false
    }

    inner class MyBackGroundBinder : Binder() {
        // Return this instance of MyBackGroundService so clients can call public methods
        fun getService(): MyBackGroundService = this@MyBackGroundService
    }
}