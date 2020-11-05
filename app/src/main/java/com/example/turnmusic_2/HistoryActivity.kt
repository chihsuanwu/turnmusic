package com.example.turnmusic_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_result.*


class HistoryActivity : AppCompatActivity() {
    var filename_this="test"
    lateinit var resultArray: BooleanArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        resultArray = intent.getBooleanArrayExtra("result")!!

        val title = intent.getStringExtra("title")!!
        val fileName = intent.getStringExtra("fileName")!!
        filename_this =fileName
        val correct = resultArray.count { it }
        val fault =  resultArray.count() - correct
        val percentage = (correct*1.0/resultArray.size) * 100

        accuracy_textView.text = "${percentage}%"
        coorecttimes_textView.text="${correct}"
        faulttimes_textView.text="${fault}"
    }

    fun meun_result_click(view: View) {
        startActivity(Intent(this@HistoryActivity,MenuActivity::class.java))
    }

    fun again_result_click(view: View) {
        startActivity(Intent(this@HistoryActivity,ChooseSongActivity::class.java))
    }

    fun exit_result_click(view: View) {
        finish()
        System.exit(0)
    }
}