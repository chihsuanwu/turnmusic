package com.example.turnmusic_2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

    }

    fun input_click(view: View) {
        startActivity(Intent(this@MenuActivity, ChooseSongActivity::class.java))

    }

    fun history_click(view: View) {
        startActivity(Intent(this@MenuActivity, HistoryActivity::class.java))

    }
    fun exit_click(view: View) {
        finish()
        System.exit(0)
    }
}