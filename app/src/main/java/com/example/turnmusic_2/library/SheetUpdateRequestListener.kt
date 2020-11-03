package com.example.turnmusic_2.library

/**
 * A listener that allows [MidiPlayer] to send a request
 * to [SheetMusicActivity] to update the sheet when it
 * changes the settings
 */
interface SheetUpdateRequestListener {
    fun onSheetUpdateRequest()
}