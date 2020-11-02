package com.example.turnmusic_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
    }

    fun meun_result_click(view: View) {
        startActivity(Intent(this@HistoryActivity,MenuActivity::class.java))
        finish()
    }

    fun exit_result_click(view: View) {
        finish()
        System.exit(0)
    }
}