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
import android.graphics.Path
import com.example.turnmusic_2.library.SheetMusic
import com.example.turnmusic_2.library.sheets.Accidental
import com.example.turnmusic_2.library.sheets.Clef
import com.example.turnmusic_2.library.sheets.MusicSymbol
import com.example.turnmusic_2.library.sheets.WhiteNote

/** @class AccidentalSymbol
 * An accidental symbol represents a sharp, flat, or natural
 * accidental that is displayed at a specific position (note and clef).
 */
class AccidentalSymbol(private val accidental: Accidental?,
                       /** The accidental (sharp, flat, natural)  */
                  val note: WhiteNote?,
                       /** The white note where the symbol occurs  */
                  private val clef: Clef) : MusicSymbol {
    /** Return the white note this accidental is displayed at  */
    /** Get/Set the width (in pixels) of this symbol. The width is set
     * in SheetMusic.AlignSymbols() to vertically align symbols.
     */
    /** Which clef the symbols is in  */
    /** Width of symbol  */
    /**
     * Create a new AccidentalSymbol with the given accidental, that is
     * displayed at the given note in the given clef.
     */
    override var width: Int = minWidth

    /** Get the time (in pulses) this symbol occurs at.
     * Not used.  Instead, the StartTime of the ChordSymbol containing this
     * [AccidentalSymbol] is used.
     */
    override val startTime: Int
        get() = -1

    /** Get the minimum width (in pixels) needed to draw this symbol  */
    override val minWidth: Int
        get() = 3 * SheetMusic.NoteHeight / 2

    /** Get the number of pixels this symbol extends above the staff. Used
     * to determine the minimum height needed for the staff (Staff.FindBounds).
     */
    override val aboveStaff: Int
        get() {
            var dist = WhiteNote.Top(clef).Dist(note!!) *
                    SheetMusic.NoteHeight / 2
            if (accidental == Accidental.Sharp || accidental == Accidental.Natural) dist -= SheetMusic.NoteHeight else if (accidental == Accidental.Flat) dist -= 3 * SheetMusic.NoteHeight / 2
            return if (dist < 0) -dist else 0
        }

    /** Get the number of pixels this symbol extends below the staff. Used
     * to determine the minimum height needed for the staff (Staff.FindBounds).
     */
    override val belowStaff: Int
        get() {
            var dist = WhiteNote.Bottom(clef).Dist(note!!) *
                    SheetMusic.NoteHeight / 2 +
                    SheetMusic.NoteHeight
            if (accidental == Accidental.Sharp || accidental == Accidental.Natural) dist += SheetMusic.NoteHeight
            return if (dist > 0) dist else 0
        }

    /** Draw the symbol.
     * @param ytop The ylocation (in pixels) where the top of the staff starts.
     */
    override fun Draw(canvas: Canvas, paint: Paint?, ytop: Int) { /* Align the symbol to the right */
        canvas.translate(width - minWidth.toFloat(), 0f)
        /* Store the y-pixel value of the top of the whitenote in ynote. */
        val ynote = ytop + WhiteNote.Top(clef).Dist(note!!) *
                SheetMusic.NoteHeight / 2
        if (accidental == Accidental.Sharp) DrawSharp(canvas, paint, ynote) else if (accidental == Accidental.Flat) DrawFlat(canvas, paint, ynote) else if (accidental == Accidental.Natural) DrawNatural(canvas, paint, ynote)
        canvas.translate(-(width - minWidth).toFloat(), 0f)
    }

    /** Draw a sharp symbol.
     * @param ynote The pixel location of the top of the accidental's note.
     */
    fun DrawSharp(canvas: Canvas, paint: Paint?, ynote: Int) { /* Draw the two vertical lines */
        var ystart = ynote - SheetMusic.NoteHeight
        var yend = ynote + 2 * SheetMusic.NoteHeight
        var x = SheetMusic.NoteHeight / 2
        paint!!.strokeWidth = 1f
        canvas.drawLine(x.toFloat(), ystart + 2.toFloat(), x.toFloat(), yend.toFloat(), paint)
        x += SheetMusic.NoteHeight / 2
        canvas.drawLine(x.toFloat(), ystart.toFloat(), x.toFloat(), yend - 2.toFloat(), paint)
        /* Draw the slightly upwards horizontal lines */
        val xstart = SheetMusic.NoteHeight / 2 - SheetMusic.NoteHeight / 4
        val xend = SheetMusic.NoteHeight + SheetMusic.NoteHeight / 4
        ystart = ynote + SheetMusic.LineWidth
        yend = ystart - SheetMusic.LineWidth - SheetMusic.LineSpace / 4
        paint.strokeWidth = SheetMusic.LineSpace / 2.toFloat()
        canvas.drawLine(xstart.toFloat(), ystart.toFloat(), xend.toFloat(), yend.toFloat(), paint)
        ystart += SheetMusic.LineSpace
        yend += SheetMusic.LineSpace
        canvas.drawLine(xstart.toFloat(), ystart.toFloat(), xend.toFloat(), yend.toFloat(), paint)
        paint.strokeWidth = 1f
    }

    /** Draw a flat symbol.
     * @param ynote The pixel location of the top of the accidental's note.
     */
    fun DrawFlat(canvas: Canvas, paint: Paint?, ynote: Int) {
        val x = SheetMusic.LineSpace / 4
        /* Draw the vertical line */paint!!.strokeWidth = 1f
        canvas.drawLine(x.toFloat(), ynote - SheetMusic.NoteHeight - (SheetMusic.NoteHeight / 2).toFloat(),
                x.toFloat(), ynote + SheetMusic.NoteHeight.toFloat(), paint)
        /* Draw 3 bezier curves.
         * All 3 curves start and stop at the same points.
         * Each subsequent curve bulges more and more towards
         * the topright corner, making the curve look thicker
         * towards the top-right.
         */
        var bezierPath = Path()
        bezierPath.moveTo(x.toFloat(), ynote + SheetMusic.LineSpace / 4.toFloat())
        bezierPath.cubicTo(x + SheetMusic.LineSpace / 2.toFloat(), ynote - SheetMusic.LineSpace / 2.toFloat(),
                x + SheetMusic.LineSpace.toFloat(), ynote + SheetMusic.LineSpace / 3.toFloat(),
                x.toFloat(), ynote + SheetMusic.LineSpace + SheetMusic.LineWidth + 1.toFloat())
        canvas.drawPath(bezierPath, paint)
        bezierPath = Path()
        bezierPath.moveTo(x.toFloat(), ynote + SheetMusic.LineSpace / 4.toFloat())
        bezierPath.cubicTo(x + SheetMusic.LineSpace / 2.toFloat(), ynote - SheetMusic.LineSpace / 2.toFloat(),
                x + SheetMusic.LineSpace + (SheetMusic.LineSpace / 4).toFloat(),
                ynote + SheetMusic.LineSpace / 3 - SheetMusic.LineSpace / 4.toFloat(),
                x.toFloat(), ynote + SheetMusic.LineSpace + SheetMusic.LineWidth + 1.toFloat())
        canvas.drawPath(bezierPath, paint)
        bezierPath = Path()
        bezierPath.moveTo(x.toFloat(), ynote + SheetMusic.LineSpace / 4.toFloat())
        bezierPath.cubicTo(x + SheetMusic.LineSpace / 2.toFloat(), ynote - SheetMusic.LineSpace / 2.toFloat(),
                x + SheetMusic.LineSpace + (SheetMusic.LineSpace / 2).toFloat(),
                ynote + SheetMusic.LineSpace / 3 - SheetMusic.LineSpace / 2.toFloat(),
                x.toFloat(), ynote + SheetMusic.LineSpace + SheetMusic.LineWidth + 1.toFloat())
        canvas.drawPath(bezierPath, paint)
    }

    /** Draw a natural symbol.
     * @param ynote The pixel location of the top of the accidental's note.
     */
    fun DrawNatural(canvas: Canvas, paint: Paint?, ynote: Int) { /* Draw the two vertical lines */
        var ystart = ynote - SheetMusic.LineSpace - SheetMusic.LineWidth
        var yend = ynote + SheetMusic.LineSpace + SheetMusic.LineWidth
        var x = SheetMusic.LineSpace / 2
        paint!!.strokeWidth = 1f
        canvas.drawLine(x.toFloat(), ystart.toFloat(), x.toFloat(), yend.toFloat(), paint)
        x += SheetMusic.LineSpace - SheetMusic.LineSpace / 4
        ystart = ynote - SheetMusic.LineSpace / 4
        yend = ynote + 2 * SheetMusic.LineSpace + SheetMusic.LineWidth -
                SheetMusic.LineSpace / 4
        canvas.drawLine(x.toFloat(), ystart.toFloat(), x.toFloat(), yend.toFloat(), paint)
        /* Draw the slightly upwards horizontal lines */
        val xstart = SheetMusic.LineSpace / 2
        val xend = xstart + SheetMusic.LineSpace - SheetMusic.LineSpace / 4
        ystart = ynote + SheetMusic.LineWidth
        yend = ystart - SheetMusic.LineWidth - SheetMusic.LineSpace / 4
        paint.strokeWidth = SheetMusic.LineSpace / 2.toFloat()
        canvas.drawLine(xstart.toFloat(), ystart.toFloat(), xend.toFloat(), yend.toFloat(), paint)
        ystart += SheetMusic.LineSpace
        yend += SheetMusic.LineSpace
        canvas.drawLine(xstart.toFloat(), ystart.toFloat(), xend.toFloat(), yend.toFloat(), paint)
        paint.strokeWidth = 1f
    }

    override fun toString(): String {
        return String.format(
                "AccidentalSymbol accidental=%1\$s whitenote=%2\$s clef=%3\$s width=%4\$s",
                accidental, note, clef, width)
    }
}