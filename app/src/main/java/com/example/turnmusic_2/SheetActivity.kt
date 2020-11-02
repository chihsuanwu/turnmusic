package com.example.turnmusic_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SheetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sheet)
    }

    fun meun_paly_click(view: View) {
        startActivity(Intent(this@SheetActivity,MenuActivity::class.java))
        finish()
    }
}