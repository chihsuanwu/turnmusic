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

import android.graphics.Canvas
import android.graphics.Paint
import com.example.turnmusic_2.library.SheetMusic
import com.example.turnmusic_2.library.sheets.MusicSymbol

/** @class BarSymbol
 * The BarSymbol represents the vertical bars which delimit measures.
 * The starttime of the symbol is the beginning of the new
 * measure.
 */
class BarSymbol(
        /** Get the time (in pulses) this symbol occurs at.
         * This is used to determine the measure this symbol belongs to.
         */
        override val startTime: Int) : MusicSymbol {

    /** Get/Set the width (in pixels) of this symbol. The width is set
     * in SheetMusic.AlignSymbols() to vertically align symbols.
     */
    /** Create a BarSymbol. The starttime should be the beginning of a measure.  */
    override var width: Int = minWidth

    /** Get the minimum width (in pixels) needed to draw this symbol  */
    override val minWidth: Int
        get() = 2 * SheetMusic.LineSpace

    /** Get the number of pixels this symbol extends above the staff. Used
     * to determine the minimum height needed for the staff (Staff.FindBounds).
     */
    override val aboveStaff: Int
        get() = 0

    /** Get the number of pixels this symbol extends below the staff. Used
     * to determine the minimum height needed for the staff (Staff.FindBounds).
     */
    override val belowStaff: Int
        get() = 0

    /** Draw a vertical bar.
     * @param ytop The ylocation (in pixels) where the top of the staff starts.
     */
    override fun Draw(canvas: Canvas, paint: Paint?, ytop: Int) {
        val yend = ytop + SheetMusic.LineSpace * 4 + SheetMusic.LineWidth * 4
        paint!!.strokeWidth = 1f
        canvas.drawLine(SheetMusic.NoteWidth / 2.toFloat(), ytop.toFloat(), SheetMusic.NoteWidth / 2.toFloat(), yend.toFloat(), paint)
    }

    override fun toString(): String {
        return String.format("BarSymbol starttime=%1\$s width=%2\$s",
                startTime, width)
    }
}