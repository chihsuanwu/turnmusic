package com.example.turnmusic_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.midisheetmusic.MidiFile
import com.example.turnmusic_2.library.SheetMusic
import com.example.turnmusic_2.library.TimeSigSymbol
import com.example.turnmusic_2.library.sheets.ClefSymbol
import kotlinx.android.synthetic.main.activity_sheet.*
import java.io.File

class SheetActivity : AppCompatActivity() {

    lateinit var sheet: SheetMusic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sheet)

        createSheet()
    }

    private fun createSheet() {
        sheet = SheetMusic(this)
        val title = intent.getStringExtra("title")
        val fileName = intent.getStringExtra("fileName")
        val file = File(fileName!!)
        val midiFile = MidiFile(file.readBytes(), title)

        TimeSigSymbol.LoadImages(this) // TEMP FIX
        ClefSymbol.LoadImages(this)

        val sheetMusic = SheetMusic(this)
        sheetMusic.init(midiFile)
        csl_main.addView(sheetMusic)
        csl_main.requestLayout()
        sheetMusic.draw()
    }

    fun menuClick(view: View) {
        startActivity(Intent(this@SheetActivity,MenuActivity::class.java))
        finish()
    }
}