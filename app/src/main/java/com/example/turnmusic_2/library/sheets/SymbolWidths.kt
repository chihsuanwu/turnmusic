/*
 * Copyright (c) 2007-2011 Madhav Vaidyanathan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */
package com.midisheetmusic.sheets

import java.util.*

/** @class SymbolWidths
 * The SymbolWidths class is used to vertically align notes in different
 * tracks that occur at the same time (that have the same starttime).
 * This is done by the following:
 * - Store a list of all the start times.
 * - Store the width of symbols for each start time, for each track.
 * - Store the maximum width for each start time, across all tracks.
 * - Get the extra width needed for each track to match the maximum
 * width for that start time.
 *
 * See method SheetMusic.AlignSymbols(), which uses this class.
 */
class SymbolWidths(tracks: ArrayList<ArrayList<MusicSymbol>>, trackLyrics: ArrayList<ArrayList<LyricSymbol>?>?) {
    /** Array of maps (startTime -> symbol width), one per track  */
    private val widths: Array<Map<Int, Int>> = Array(tracks.size) { track ->
        /* Get the symbol widths for all the tracks */
        GetTrackWidths(tracks[track])
    }

    /** Map of startTime -> maximum symbol width  */
    private var maxWidths: MutableMap<Int, Int> = mutableMapOf()

    /** An array of all the startTimes, in all tracks  */
    val startTimes: IntArray

    /** Initialize the symbol width maps, given all the symbols in
     * all the tracks.
     */
    init {
        /* Calculate the maximum symbol widths */
        for (dict in widths) {
            for ((time, value) in dict.entries) {
                if (time !in maxWidths ||
                        maxWidths[time]!! < value) {
                    maxWidths[time] = value
                }
            }
        }

        if (trackLyrics != null) {
            for (lyrics in trackLyrics) {
                if (lyrics == null) {
                    continue
                }
                for (lyric in lyrics) {
                    val width = lyric.minWidth
                    val time = lyric.startTime
                    if (time !in maxWidths ||
                            maxWidths[time]!! < width) {
                        maxWidths[time] = width
                    }
                }
            }
        }

        /* Store all the start times to the starttime array */
        startTimes = maxWidths.keys.sorted().toIntArray()
    }

    /** Create a table of the symbol widths for each starttime in the track.  */
    private fun GetTrackWidths(symbols: ArrayList<MusicSymbol>): Map<Int, Int> {
        val widths = mutableMapOf<Int, Int>()
        for (m in symbols) {
            val start = m.startTime
            val w = m.minWidth
            if (m is BarSymbol) {
                continue
            } else if (start in widths) {
                widths[start] = widths[start]!! + w
            } else {
                widths[start] = w
            }
        }
        return widths.toMap()
    }

    /** Given a track and a start time, return the extra width needed so that
     * the symbols for that start time align with the other tracks.
     */
    fun GetExtraWidth(track: Int, start: Int): Int = maxWidths[start]!! - widths[track]!!.getOrElse(start) { 0 }
}