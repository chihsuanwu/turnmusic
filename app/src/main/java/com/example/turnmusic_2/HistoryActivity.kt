package com.example.turnmusic_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.example.turnmusic_2.library.SheetMusic
import com.midisheetmusic.MidiFile
import com.midisheetmusic.MidiTrack
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_history.fl_main
import kotlinx.android.synthetic.main.activity_sheet.*
import java.io.File


class HistoryActivity : AppCompatActivity() {

    lateinit var sheet: SheetMusic
    lateinit var track: MidiTrack

    lateinit var resultArray: BooleanArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        resultArray = intent.getBooleanArrayExtra("result")!!
        //val title = intent.getStringExtra("title")!!
        //val fileName = intent.getStringExtra("fileName")!!
        val correct = resultArray.count { it }
        val fault =  resultArray.count() - correct
        val percentage = correct*100/resultArray.size

        accuracy_textView.text = "${percentage}%"
        coorecttimes_textView.text="${correct}"
        faulttimes_textView.text="${fault}"

        createSheet()

        btn_prev.setOnClickListener {
            sheet.toPrevPage()
            draw()
        }

        btn_next.setOnClickListener {
            sheet.toNextPage()
            draw()
        }
    }

    private fun createSheet() {
        sheet = SheetMusic(this)

        val title = intent.getStringExtra("title")!!
        val fileName = intent.getStringExtra("fileName")!!

        val file = File(fileName)
        val midiFile = MidiFile(file.readBytes(), title)
        textView.text=fileName
        textView2.text= resultArray.toString()
        sheet.init(midiFile)
        fl_main.addView(sheet)
        fl_main.requestLayout()

        track = midiFile.tracks!![0]



        //sheet.draw()

        Handler().postDelayed({
            draw()
        },400)

    }

    private fun draw() {
        val len = resultArray.size
        val wrongTime = mutableListOf<Int>()

        for (i in 0 until len) {
            if (resultArray[i]) continue
            val note = track.notes[i]
            sheet.ShadeNotes(note.startTime, -1, 3, true)
            wrongTime.add(note.startTime)
        }
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