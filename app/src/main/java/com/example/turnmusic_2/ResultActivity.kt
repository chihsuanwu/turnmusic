package com.example.turnmusic_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    lateinit var resultArray: BooleanArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        resultArray = intent.getBooleanArrayExtra("result")!!

        val title = intent.getStringExtra("title")!!
        val fileName = intent.getStringExtra("fileName")!!

        val correct = resultArray.count { it }

        tv_percentage.text = "${((correct*1.0/resultArray.size) * 100)}%"
    }
}