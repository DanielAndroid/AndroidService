package com.example.androidservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyBroadCastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Toast.makeText(context, "Broadcast Started", Toast.LENGTH_SHORT).show()

            val foreGroundServiceIntent = Intent(context, MyForeGroundService::class.java)
            context?.startForegroundService(
                foreGroundServiceIntent.setAction(
                    intent?.getStringExtra(
                        "Action"
                    )
                )
            )
        }

    }

}