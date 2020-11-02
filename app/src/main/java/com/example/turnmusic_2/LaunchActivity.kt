package com.example.turnmusic_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityCompat


class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)


        Handler().postDelayed({
            ActivityCompat.requestPermissions(this, arrayOf(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ), 200)
        },2000)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        startActivity(Intent(this ,MenuActivity::class.java))
        finish()
    }
}