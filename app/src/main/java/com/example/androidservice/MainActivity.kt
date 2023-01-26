package com.example.androidservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var myBackGroundService: MyBackGroundService
    private lateinit var backGroundServiceIntent: Intent

    private lateinit var backgroundServiceBtn: Button
    private lateinit var boundServiceBtn: Button
    private lateinit var foregroundServiceBtn: Button
    private lateinit var playBtn: Button
    private lateinit var stopBtn: Button

    private var isBackGroundServiceStarted = false
    private var isForeGroundServiceStarted = false
    private var mBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        backgroundServiceBtn = findViewById(R.id.background_service)
        backgroundServiceBtn.setOnClickListener(this)

        boundServiceBtn = findViewById(R.id.bound_service)
        boundServiceBtn.setOnClickListener(this)

        foregroundServiceBtn = findViewById(R.id.foreground_service)
        foregroundServiceBtn.setOnClickListener(this)

        playBtn = findViewById(R.id.play_button)
        playBtn.setOnClickListener(this)

        stopBtn = findViewById(R.id.stop_button)
        stopBtn.setOnClickListener(this)


        backGroundServiceIntent = Intent(this, MyBackGroundService::class.java)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.background_service -> startMyBackgroundService()
                R.id.bound_service -> bindToMyService()
                R.id.foreground_service -> startMyForegroundService()
                R.id.stop_button -> stop()
                R.id.play_button -> play()
            }
        }
    }

    /** Used to start the background service.
    if the foreground service is running it will stop it. */
    private fun startMyBackgroundService() {
        if (isForeGroundServiceStarted) {
            stopService(backGroundServiceIntent)
        }
        startService(backGroundServiceIntent)
        isBackGroundServiceStarted = true
    }

    // binds to the service.
    private fun bindToMyService() {
        bindService(backGroundServiceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to MyService, cast the IBinder and get MyService instance
            val binder = service as MyBackGroundService.MyBackGroundBinder
            myBackGroundService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    /** Used to start the foreGround service.
   if the backGround service is running it will stop it and unbind if bounded.*/
    private fun startMyForegroundService() {
        if (mBound) {
            myBackGroundService.stopRingTone()
            unbindService(connection)
        }
        if (isBackGroundServiceStarted) {
            stopService(backGroundServiceIntent)
        }
        val foreGroundServiceIntent = Intent(this, MyForeGroundService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.startForegroundService(foreGroundServiceIntent)
        } else {
            applicationContext.startService(foreGroundServiceIntent)
        }
        isForeGroundServiceStarted = true
    }

    // stops the ringtone from playing in the service.
    private fun stop() {
        if (mBound) {
            myBackGroundService.stopRingTone()
        } else {
            Toast.makeText(this, "service not yet bound", Toast.LENGTH_SHORT).show()
        }
    }

    // plays the ringtone in the service.
    private fun play() {
        if (mBound) {
            myBackGroundService.playRingTone()
        } else {
            Toast.makeText(this, "service not yet bound", Toast.LENGTH_SHORT).show()
        }
    }


}