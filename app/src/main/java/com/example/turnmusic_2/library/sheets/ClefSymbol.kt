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
package com.example.turnmusic_2.library.sheets

import android.content.Context
import android.graphics.*
import com.example.turnmusic_2.R
import com.example.turnmusic_2.library.SheetMusic

/** @class ClefSymbol
 * A ClefSymbol represents either a Treble or Bass Clef image.
 * The clef can be either normal or small size.  Normal size is
 * used at the beginning of a new staff, on the left side.  The
 * small symbols are used to show clef changes within a staff.
 */
class ClefSymbol(
        /** True if this is a small clef, false otherwise  */
        private val clef: Clef,
        /** The bass clef image  */
        override val startTime: Int,
        /** Start time of the symbol  */
        private val smallsize: Boolean) : MusicSymbol {
    /** Get the time (in pulses) this symbol occurs at.
     * This is used to determine the measure this symbol belongs to.
     */
    /** Get/Set the width (in pixels) of this symbol. The width is set
     * in SheetMusic.AlignSymbols() to vertically align symbols.
     */
    /** The clef, Treble or Bass  */
    /** Create a new ClefSymbol, with the given clef, starttime, and size  */
    override var width: Int = minWidth

    /** Get the minimum width (in pixels) needed to draw this symbol  */
    override val minWidth: Int
        get() = if (smallsize) SheetMusic.NoteWidth * 2 else SheetMusic.NoteWidth * 3

    /** Get the number of pixels this symbol extends above the staff. Used
     * to determine the minimum height needed for the staff (Staff.FindBounds).
     */
    override val aboveStaff: Int
        get() = if (clef == Clef.Treble && !smallsize) SheetMusic.NoteHeight * 2 else 0

    /** Get the number of pixels this symbol extends below the staff. Used
     * to determine the minimum height needed for the staff (Staff.FindBounds).
     */
    override val belowStaff: Int
        get() = if (clef == Clef.Treble && !smallsize) SheetMusic.NoteHeight * 2 else if (clef == Clef.Treble && smallsize) SheetMusic.NoteHeight else 0

    /** Draw the symbol.
     * @param ytop The ylocation (in pixels) where the top of the staff starts.
     */
    override fun Draw(canvas: Canvas, paint: Paint?, ytop: Int) {
        canvas.translate(width - minWidth.toFloat(), 0f)
        var y = ytop
        val image: Bitmap?
        val height: Int
        /* Get the image, height, and top y pixel, depending on the clef
         * and the image size.
         */
        if (clef == Clef.Treble) {
            image = treble
            if (smallsize) {
                height = SheetMusic.StaffHeight + SheetMusic.StaffHeight / 4
            } else {
                height = 3 * SheetMusic.StaffHeight / 2 + SheetMusic.NoteHeight / 2
                y = ytop - SheetMusic.NoteHeight
            }
        } else {
            image = bass
            height = if (smallsize) {
                SheetMusic.StaffHeight - 3 * SheetMusic.NoteHeight / 2
            } else {
                SheetMusic.StaffHeight - SheetMusic.NoteHeight
            }
        }
        /* Scale the image width to match the height */
        val imgwidth = image!!.width * height / image.height
        val src = Rect(0, 0, image.width, image.height)
        val dest = Rect(0, y, 0 + imgwidth, y + height)
        canvas.drawBitmap(image, src, dest, paint)
        canvas.translate(-(width - minWidth).toFloat(), 0f)
    }

    override fun toString(): String {
        return String.format("ClefSymbol clef=%1\$s small=%2\$s width=%3\$s",
                clef, smallsize, width)
    }

    companion object {
        var treble: Bitmap? = null
        /** The treble clef image  */
        private var bass: Bitmap? = null

        /** Set the Treble/Bass clef images into memory.  */
        fun LoadImages(context: Context) {
            if (treble == null || bass == null) {
                val res = context.resources
                treble = BitmapFactory.decodeResource(res, R.drawable.treble)
                bass = BitmapFactory.decodeResource(res, R.drawable.bass)
            }
        }
    }
}