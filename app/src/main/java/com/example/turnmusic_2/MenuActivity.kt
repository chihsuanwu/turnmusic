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
        //startActivity(Intent(this@MenuActivity, HistoryActivity::class.java))
        val intent = Intent(this@MenuActivity,HistoryActivity::class.java)
        intent.putExtra("result", booleanArrayOf(true,false,true,false,true,true,true,true,false))
        intent.putExtra("title", "123")
        intent.putExtra("fileName", "/storage/emulated/0/Download/123.mid")
        startActivity(intent)
        finish()

    }
    fun exit_click(view: View) {
        finish()
        System.exit(0)
    }
}