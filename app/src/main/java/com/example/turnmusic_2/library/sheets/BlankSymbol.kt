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

/** @class BlankSymbol
 * The Blank symbol is a music symbol that doesn't draw anything.  This
 * symbol is used for alignment purposes, to align notes in different
 * staffs which occur at the same time.
 */
class BlankSymbol
/** Create a new BlankSymbol with the given starttime and width  */(
        /** Get the time (in pulses) this symbol occurs at.
         * This is used to determine the measure this symbol belongs to.
         */
        override val startTime: Int,
        /** Get/Set the width (in pixels) of this symbol. The width is set
         * in SheetMusic.AlignSymbols() to vertically align symbols.
         */
        override var width: Int) : MusicSymbol {

    /** Get the minimum width (in pixels) needed to draw this symbol  */
    override val minWidth: Int
        get() = 0

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

    /** Draw nothing.
     * @param ytop The ylocation (in pixels) where the top of the staff starts.
     */
    override fun Draw(canvas: Canvas, paint: Paint?, ytop: Int) {}

    override fun toString(): String {
        return String.format("BlankSymbol starttime=%1\$s width=%2\$s",
                startTime, width)
    }

}