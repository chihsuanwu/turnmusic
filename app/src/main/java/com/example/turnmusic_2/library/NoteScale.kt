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
package com.example.turnmusic_2.library

/**
 * Enumeration of the notes in a scale (A, A#, ... G#)
 */
enum class NoteScale(val value: Int) {
    A(0),
    ASharp(1),
    B(2),
    C(3),
    CSharp(4),
    D(5),
    DSharp(6),
    E(7),
    F(8),
    FSharp(9),
    G(10),
    GSharp(11);

    companion object {
        val BFlat = ASharp
        val DFlat = CSharp
        val EFlat = DSharp
        val GFlat = FSharp
        val AFlat = GSharp

        /**
         * Convert a Midi Note number into a [NoteScale]
         */
        fun fromNumber(midiNoteNumber: Int): NoteScale {
            val value = (midiNoteNumber + 3) % 12
            return values().first { it.value == value }
        }
    }

    /**
     * Convert a note (A, A#, B, etc) and octave into a Midi Note number.
     */
    fun toMidiNoteNumber(octave: Int): Int = 9 + this.value + octave * 12

    /**
     * Whether this is a black key
     */
    val isBlackKey: Boolean by lazy {
        when (this) {
            ASharp, CSharp, DSharp, FSharp, GSharp -> true
            else -> false
        }
    }
}
