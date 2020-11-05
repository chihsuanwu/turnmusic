package com.example.turnmusic_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class HistoryActivity : AppCompatActivity() {

    lateinit var resultArray: BooleanArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        resultArray = intent.getBooleanArrayExtra("result")!!

        val title = intent.getStringExtra("title")!!
        val fileName = intent.getStringExtra("fileName")!!

        val correct = resultArray.count { it }
        val percentage = (correct*1.0/resultArray.size) * 100
    }

    fun meun_result_click(view: View) {
        startActivity(Intent(this@HistoryActivity,MenuActivity::class.java))
    }

    fun exit_result_click(view: View) {
        finish()
        System.exit(0)
    }
}