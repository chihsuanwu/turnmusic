package com.example.turnmusic_2

import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.midisheetmusic.MidiFile
import com.example.turnmusic_2.library.SheetMusic
import com.example.turnmusic_2.library.TimeSigSymbol
import com.example.turnmusic_2.library.sheets.ClefSymbol
import com.midisheetmusic.MidiTrack
import com.nclab.audiorecognition.FFT
import kotlinx.android.synthetic.main.activity_sheet.*
import java.io.File
import kotlin.math.log2
import kotlin.math.round

private const val RECORDER_SAMPLE_RATE = 44100
private const val RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO
private const val RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT
private const val MIN_FREQUENCY = 20
private const val MAX_FREQUENCY = 8000
private const val BUFFER_SIZE = 4096
private const val FFT_SIZE = 8192
private const val FFT_SIZE_LN = 13

class SheetActivity : AppCompatActivity() {

    lateinit var sheet: SheetMusic

    private var recordingThread: Thread? = null

    private var isRecording = false

    private var recorder: AudioRecord? = null

    private val handler = Handler()
    private val fft = FFT(FFT_SIZE, FFT_SIZE_LN)

    lateinit var track: MidiTrack

    private var currentIndex = 0
    private var currentShadeNote = -1
    private var currentShadeX = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sheet)

        tv_page_back.setOnClickListener {
            sheet.toPrevPage()
            currentShadeX = sheet.ShadeNotes(currentShadeNote, 0, 3)
            //sheet.draw()
            val totalPage = sheet.calculatePages()
            val currentPage = sheet.getCurrentPage()
            tv_page.text = "$currentPage / $totalPage"
        }
        tv_page_next.setOnClickListener {
            sheet.toNextPage()
            currentShadeX = sheet.ShadeNotes(currentShadeNote, 0, 3)
            //sheet.draw()
            val totalPage = sheet.calculatePages()
            val currentPage = sheet.getCurrentPage()
            tv_page.text = "$currentPage / $totalPage"
        }

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

        sheet.init(midiFile)
        fl_main.addView(sheet)
        fl_main.requestLayout()
        sheet.draw()

        track = midiFile.tracks!![0]
    }

    fun menuClick(view: View) {
        startActivity(Intent(this@SheetActivity,MenuActivity::class.java))
        finish()
    }

    fun recordClick(view: View) {
        for (note in track.notes) {
            Log.e("DEBUG", note.toString())
        }
        startRecording()
    }

    fun stopRecordClick(view: View) {
        stopRecording()
    }

    fun testClick(view: View) {
        currentIndex++
        val prevShadeNote = currentShadeNote
        currentShadeNote = track.notes[currentIndex - 1].startTime
        currentShadeX = sheet.ShadeNotes(currentShadeNote, prevShadeNote, 3)
        //sheet.draw()

        if (sheet.shouldTurnPage(currentShadeX)) {
            sheet.toNextPage()
            currentShadeX = sheet.ShadeNotes(currentShadeNote, 0, 3)
        }
    }

    private fun startRecording() {
        recorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLE_RATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BUFFER_SIZE
        ).apply {
            startRecording()
        }

        isRecording = true

        recordingThread = Thread({ audioAnalyze() }, "AudioAnalyzeThread").apply { start() }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
            isRecording = false
            recordingThread?.join()
            recordingThread = null
            recorder = null
        }
        recordingThread?.run()
    }

    private fun audioAnalyze() {
        val prevData = ShortArray(BUFFER_SIZE)
        val data = ShortArray(BUFFER_SIZE)
        while (isRecording) {
            val length = recorder?.read(data, 0, BUFFER_SIZE)
            if (length == BUFFER_SIZE) {
                getFFT(prevData + data)
                data.copyInto(prevData)
            }
        }
    }

    private fun getFFT(input: ShortArray) {

        val real = DoubleArray(FFT_SIZE)
        val y = DoubleArray(FFT_SIZE)
        for (i in 0 until FFT_SIZE) {
            real[i] = input[i] / 32768.0
            y[i] = 0.0
        }
        fft.fft(real, y)

        var maxVal = 0.0
        var maxIndex = 0
        for (i in MIN_FREQUENCY until MAX_FREQUENCY) {
            if (real[i] > maxVal) {
                maxVal = real[i]
                maxIndex = i
            }
        }

        val hz = maxIndex * RECORDER_SAMPLE_RATE / FFT_SIZE
        //Log.e("FHZ", hz.toString())

        val pitchNo = getPitch(hz)

        val pitchNoStr = getPitchStr(pitchNo)

        val result = compareWithSheet(pitchNo)


        handler.post { Runnable {
            //getPitch(hz)
            if (maxVal > 50) {
                tv_info.text = "$hz Hz, $pitchNoStr"
            } else {
                tv_info.text = "$hz Hz, -------"
            }

            if (result) {
                sheet.draw()
            }

            tv_info2.text = "AMP: $maxVal"

            tv_info3.text = "Waiting for ${getPitchStr(track.notes[currentIndex].number)}"
        }.run() }
    }

    private fun compareWithSheet(pitchNo: Int): Boolean {
        val waitingFor = track.notes[currentIndex]
        if ((pitchNo + 24) == waitingFor.number) {
            currentIndex++
            val prevShadeNote = currentShadeNote
            currentShadeNote = track.notes[currentIndex - 1].startTime
            sheet.ShadeNotes(currentShadeNote, prevShadeNote, 3)

            return true
        }
        return false
    }

    private val pitchName = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    private val pitchNameSimple = arrayOf("1", "1#", "2", "2#", "3", "4", "4#", "5", "5#", "6", "7#", "7")

    private fun getPitch(frequency: Int): Int {
        val pitchNo = 12 * log2(frequency / 440.0) + 45
        return round(pitchNo).toInt()
    }

    private fun getPitchStr(pitchNo: Int): String {
        val pitchStr = pitchName[pitchNo % 12]
        val pitchStrSimple = pitchNameSimple[pitchNo % 12]
        val offset = pitchNo / 12 - 3

        return "pitch: $pitchStr  $pitchNo"
    }
}