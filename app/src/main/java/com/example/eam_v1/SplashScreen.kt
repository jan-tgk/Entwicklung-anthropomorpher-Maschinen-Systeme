package com.example.eam_v1

import android.content.Intent
import android.os.Bundle
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import java.util.*

class SplashScreen : RobotActivity() {

    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)
    }

    override fun onResume() {
        super.onResume()

        timer = Timer()
        timer?.schedule(object : TimerTask(){
            override fun run() {
                gotoStartScreen()
            }
        }, 2500)
    }

    override fun onPause(){
        timer?.cancel()
        super.onPause()
    }

    private fun gotoStartScreen(){
        val intent = Intent(this,StartScreen::class.java)
        startActivity(intent)

        finish()
    }
}