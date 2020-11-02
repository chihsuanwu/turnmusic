package com.example.turnmusic_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_choose_song.*

class ChooseSongActivity : AppCompatActivity() {

    private var midiFilesList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_song)


        loadDirectory()

        lv_file.setOnItemClickListener { _, _, position, _ ->
            Log.e("DEBUG", midiFilesList[position])
//            startActivity(Intent(this@MainActivity2, ChooseSongActivity::class.java))
//            finish()
            //FileManager.loadFile(midiFilesList[position])

//            val byteArray = FileManager.readBytes()
//            for (b in byteArray) {
//                Log.e("DEBUG", b.toString())
//            }
//
//            Log.e("DEBUG=", byteArray.size.toString())
        }
    }

    private fun loadDirectory() {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val currentDirectory = directory.absolutePath


        Log.e("DEBUG", directory.absolutePath)
        val files = directory.list()
        Log.e("DEBUG", "???"+directory.listFiles()?.toString())

        val midiFiles = mutableListOf<String>()
        if (files != null) {
            for (file in files) {
                if (file.endsWith(".mid") ||
                    file.endsWith(".MID") ||
                    file.endsWith(".midi") ||
                    file.endsWith(".MIDI")) {
                    midiFiles.add(file)
                }
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, midiFiles)

        lv_file.adapter = adapter

        midiFilesList.clear()
        midiFiles.forEach {
            midiFilesList.add("$currentDirectory/$it")
        }


    }
}