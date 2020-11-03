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
package com.midisheetmusic

import com.example.turnmusic_2.library.NoteScale
import com.example.turnmusic_2.library.sheets.Accidental
import com.midisheetmusic.sheets.AccidentalSymbol
import com.example.turnmusic_2.library.sheets.Clef
import com.example.turnmusic_2.library.sheets.WhiteNote
import java.util.*

/** @class KeySignature
 * The KeySignature class represents a key signature, like G Major
 * or B-flat Major.  For sheet music, we only care about the number
 * of sharps or flats in the key signature, not whether it is major
 * or minor.
 *
 * The main operations of this class are:
 * - Guessing the key signature, given the notes in a song.
 * - Generating the accidental symbols for the key signature.
 * - Determining whether a particular note requires an accidental
 * or not.
 */
class KeySignature {
    private var num_flats: Int

    /** The number of sharps in the key, 0 thru 6  */
    private var num_sharps: Int
    /** The number of flats in the key, 0 thru 6  */
    /** The accidental symbols that denote this key, in a treble clef  */
    private lateinit var treble: Array<AccidentalSymbol?>

    /** The accidental symbols that denote this key, in a bass clef  */
    private lateinit var bass: Array<AccidentalSymbol?>

    /** The key map for this key signature:
     * keymap[notenumber] -> Accidental
     */
    private var keymap: Array<Accidental?>

    /** The measure used in the previous call to GetAccidental()  */
    private var prevmeasure = 0

    /** Create new key signature, with the given number of
     * sharps and flats.  One of the two must be 0, you can't
     * have both sharps and flats in the key signature.
     */
    constructor(num_sharps: Int, num_flats: Int) {
        require(num_sharps == 0 || num_flats == 0)
        this.num_sharps = num_sharps
        this.num_flats = num_flats
        keymap = arrayOfNulls(160)
        ResetKeyMap()
        CreateSymbols()
    }

    /** Create new key signature, with the given notescale.
     */
    constructor(notescale: NoteScale) {
        num_flats = 0
        num_sharps = num_flats
        when (notescale) {
            NoteScale.A -> num_sharps = 3
            NoteScale.BFlat -> num_flats = 2
            NoteScale.B -> num_sharps = 5
            NoteScale.C -> {
            }
            NoteScale.DFlat -> num_flats = 5
            NoteScale.D -> num_sharps = 2
            NoteScale.EFlat -> num_flats = 3
            NoteScale.E -> num_sharps = 4
            NoteScale.F -> num_flats = 1
            NoteScale.GFlat -> num_flats = 6
            NoteScale.G -> num_sharps = 1
            NoteScale.AFlat -> num_flats = 4
            else -> throw IllegalArgumentException()
        }
        keymap = arrayOfNulls(160)
        ResetKeyMap()
        CreateSymbols()
    }

    /** The keymap tells what accidental symbol is needed for each
     * note in the scale.  Reset the keymap to the values of the
     * key signature.
     */
    private fun ResetKeyMap() {
        val key: EnumMap<NoteScale, Accidental> = if (num_flats > 0) {
            flatkeys[num_flats]!!
        } else {
            sharpkeys[num_sharps]!!
        }
        for (noteNumber in keymap.indices) {
            keymap[noteNumber] = key[NoteScale.fromNumber(noteNumber)]
        }
    }

    /** Create the Accidental symbols for this key, for
     * the treble and bass clefs.
     */
    private fun CreateSymbols() {
        val count = Math.max(num_sharps, num_flats)
        treble = arrayOfNulls(count)
        bass = arrayOfNulls(count)
        if (count == 0) {
            return
        }
        var treblenotes: Array<WhiteNote>? = null
        var bassnotes: Array<WhiteNote>? = null
        if (num_sharps > 0) {
            treblenotes = arrayOf(
                    WhiteNote(WhiteNote.F, 5),
                    WhiteNote(WhiteNote.C, 5),
                    WhiteNote(WhiteNote.G, 5),
                    WhiteNote(WhiteNote.D, 5),
                    WhiteNote(WhiteNote.A, 6),
                    WhiteNote(WhiteNote.E, 5)
            )
            bassnotes = arrayOf(
                    WhiteNote(WhiteNote.F, 3),
                    WhiteNote(WhiteNote.C, 3),
                    WhiteNote(WhiteNote.G, 3),
                    WhiteNote(WhiteNote.D, 3),
                    WhiteNote(WhiteNote.A, 4),
                    WhiteNote(WhiteNote.E, 3)
            )
        } else if (num_flats > 0) {
            treblenotes = arrayOf(
                    WhiteNote(WhiteNote.B, 5),
                    WhiteNote(WhiteNote.E, 5),
                    WhiteNote(WhiteNote.A, 5),
                    WhiteNote(WhiteNote.D, 5),
                    WhiteNote(WhiteNote.G, 4),
                    WhiteNote(WhiteNote.C, 5)
            )
            bassnotes = arrayOf(
                    WhiteNote(WhiteNote.B, 3),
                    WhiteNote(WhiteNote.E, 3),
                    WhiteNote(WhiteNote.A, 3),
                    WhiteNote(WhiteNote.D, 3),
                    WhiteNote(WhiteNote.G, 2),
                    WhiteNote(WhiteNote.C, 3)
            )
        }
        var a = Accidental.None
        a = if (num_sharps > 0) Accidental.Sharp else Accidental.Flat
        for (i in 0 until count) {
            treble[i] = AccidentalSymbol(a, treblenotes!![i], Clef.Treble)
            bass[i] = AccidentalSymbol(a, bassnotes!![i], Clef.Bass)
        }
    }

    /** Return the Accidental symbols for displaying this key signature
     * for the given clef.
     */
    fun GetSymbols(clef: Clef): Array<AccidentalSymbol?> {
        return if (clef == Clef.Treble) treble else bass
    }

    /** Given a midi note number, return the accidental (if any)
     * that should be used when displaying the note in this key signature.
     *
     * The current measure is also required.  Once we return an
     * accidental for a measure, the accidental remains for the
     * rest of the measure. So we must update the current keymap
     * with any new accidentals that we return.  When we move to another
     * measure, we reset the keymap back to the key signature.
     */
    fun GetAccidental(notenumber: Int, measure: Int): Accidental? {
        if (measure != prevmeasure) {
            ResetKeyMap()
            prevmeasure = measure
        }
        if (notenumber <= 1 || notenumber >= 127) {
            return Accidental.None
        }
        val result = keymap[notenumber]
        if (result == Accidental.Sharp) {
            keymap[notenumber] = Accidental.None
            keymap[notenumber - 1] = Accidental.Natural
        } else if (result == Accidental.Flat) {
            keymap[notenumber] = Accidental.None
            keymap[notenumber + 1] = Accidental.Natural
        } else if (result == Accidental.Natural) {
            keymap[notenumber] = Accidental.None
            val nextkey = NoteScale.fromNumber(notenumber + 1)
            val prevkey = NoteScale.fromNumber(notenumber - 1)
            /* If we insert a natural, then either:
             * - the next key must go back to sharp,
             * - the previous key must go back to flat.
             */
            if (keymap[notenumber - 1] == Accidental.None && keymap[notenumber + 1] == Accidental.None &&
                    nextkey.isBlackKey && prevkey.isBlackKey) {
                if (num_flats == 0) {
                    keymap[notenumber + 1] = Accidental.Sharp
                } else {
                    keymap[notenumber - 1] = Accidental.Flat
                }
            } else if (keymap[notenumber - 1] == Accidental.None && prevkey.isBlackKey) {
                keymap[notenumber - 1] = Accidental.Flat
            } else if (keymap[notenumber + 1] == Accidental.None && nextkey.isBlackKey) {
                keymap[notenumber + 1] = Accidental.Sharp
            } else { /* Shouldn't get here */
            }
        }
        return result
    }

    /** Given a midi note number, return the white note (the
     * non-sharp/non-flat note) that should be used when displaying
     * this note in this key signature.  This should be called
     * before calling GetAccidental().
     */
    fun GetWhiteNote(notenumber: Int): WhiteNote {
        val notescale = NoteScale.fromNumber(notenumber)
        var octave = (notenumber + 3) / 12 - 1
        var letter = 0
        val whole_sharps = intArrayOf(
                WhiteNote.A, WhiteNote.A,
                WhiteNote.B,
                WhiteNote.C, WhiteNote.C,
                WhiteNote.D, WhiteNote.D,
                WhiteNote.E,
                WhiteNote.F, WhiteNote.F,
                WhiteNote.G, WhiteNote.G
        )
        val whole_flats = intArrayOf(
                WhiteNote.A,
                WhiteNote.B, WhiteNote.B,
                WhiteNote.C,
                WhiteNote.D, WhiteNote.D,
                WhiteNote.E, WhiteNote.E,
                WhiteNote.F,
                WhiteNote.G, WhiteNote.G,
                WhiteNote.A
        )
        val accidental = keymap[notenumber]
        if (accidental == Accidental.Flat) {
            letter = whole_flats[notescale.value]
        } else if (accidental == Accidental.Sharp) {
            letter = whole_sharps[notescale.value]
        } else if (accidental == Accidental.Natural) {
            letter = whole_sharps[notescale.value]
        } else if (accidental == Accidental.None) {
            letter = whole_sharps[notescale.value]
            /* If the note number is a sharp/flat, and there's no accidental,
             * determine the white note by seeing whether the previous or next note
             * is a natural.
             */
            if (notescale.isBlackKey) {
                if (keymap[notenumber - 1] == Accidental.Natural &&
                        keymap[notenumber + 1] == Accidental.Natural) {
                    letter = if (num_flats > 0) {
                        whole_flats[notescale.value]
                    } else {
                        whole_sharps[notescale.value]
                    }
                } else if (keymap[notenumber - 1] == Accidental.Natural) {
                    letter = whole_sharps[notescale.value]
                } else if (keymap[notenumber + 1] == Accidental.Natural) {
                    letter = whole_flats[notescale.value]
                }
            }
        }
        /* The above algorithm doesn't quite work for G-flat major.
         * Handle it here.
         */
        if (num_flats == Gflat && notescale == NoteScale.B) {
            letter = WhiteNote.C
        }
        if (num_flats == Gflat && notescale == NoteScale.BFlat) {
            letter = WhiteNote.B
        }
        if (num_flats > 0 && notescale == NoteScale.AFlat) {
            octave++
        }
        return WhiteNote(letter, octave)
    }

    /** Return true if this key signature is equal to key signature k  */
    fun equals(k: KeySignature): Boolean {
        return k.num_sharps == num_sharps && k.num_flats == num_flats
    }

    /* Return the Major Key of this Key Signature */
    fun Notescale(): NoteScale {
        val flatmajor = arrayOf(
                NoteScale.C, NoteScale.F, NoteScale.BFlat, NoteScale.EFlat,
                NoteScale.AFlat, NoteScale.DFlat, NoteScale.GFlat, NoteScale.B
        )
        val sharpmajor = arrayOf(
                NoteScale.C, NoteScale.G, NoteScale.D, NoteScale.A, NoteScale.E,
                NoteScale.B, NoteScale.FSharp, NoteScale.CSharp, NoteScale.GSharp,
                NoteScale.DSharp
        )
        return if (num_flats > 0) flatmajor[num_flats] else sharpmajor[num_sharps]
    }

    /* Return a string representation of this key signature.
     * We only return the major key signature, not the minor one.
     */
    override fun toString(): String {
        return Notescale().keyToString()
    }

    companion object {
        /** The number of sharps in each key signature  */
        const val C = 0
        const val G = 1
        const val D = 2
        const val A = 3
        const val E = 4
        const val B = 5

        /** The number of flats in each key signature  */
        const val F = 1
        const val Bflat = 2
        const val Eflat = 3
        const val Aflat = 4
        const val Dflat = 5
        const val Gflat = 6

        /** The two arrays below are key maps.  They take a major key
         * (like G major, B-flat major) and a note in the scale, and
         * return the Accidental required to display that note in the
         * given key.  In a nutshel, the map is
         *
         * map[Key][NoteScale] -> Accidental
         */
        private val sharpkeys: Map<Int, EnumMap<NoteScale, Accidental>> by lazy {
            mapOf(
                    C to EnumMap<NoteScale, Accidental>(NoteScale::class.java).also {
                        it[NoteScale.A] = Accidental.None
                        it[NoteScale.ASharp] = Accidental.Flat
                        it[NoteScale.B] = Accidental.None
                        it[NoteScale.C] = Accidental.None
                        it[NoteScale.CSharp] = Accidental.Sharp
                        it[NoteScale.D] = Accidental.None
                        it[NoteScale.DSharp] = Accidental.Sharp
                        it[NoteScale.E] = Accidental.None
                        it[NoteScale.F] = Accidental.None
                        it[NoteScale.FSharp] = Accidental.Sharp
                        it[NoteScale.G] = Accidental.None
                        it[NoteScale.GSharp] = Accidental.Sharp
                    },
                    G to EnumMap<NoteScale, Accidental>(NoteScale::class.java).also {
                        it[NoteScale.A] = Accidental.None
                        it[NoteScale.ASharp] = Accidental.Flat
                        it[NoteScale.B] = Accidental.None
                        it[NoteScale.C] = Accidental.None
                        it[NoteScale.CSharp] = Accidental.Sharp
                        it[NoteScale.D] = Accidental.None
                        it[NoteScale.DSharp] = Accidental.Sharp
                        it[NoteScale.E] = Accidental.None
                        it[NoteScale.F] = Accidental.Natural
                        it[NoteScale.FSharp] = Accidental.None
                        it[NoteScale.G] = Accidental.None
                        it[NoteScale.GSharp] = Accidental.Sharp
                    },
                    D to EnumMap<NoteScale, Accidental>(NoteScale::class.java).also {
                        it[NoteScale.A] = Accidental.None
                        it[NoteScale.ASharp] = Accidental.Flat
                        it[NoteScale.B] = Accidental.None
                        it[NoteScale.C] = Accidental.Natural
                        it[NoteScale.CSharp] = Accidental.None
                        it[NoteScale.D] = Accidental.None
                        it[NoteScale.DSharp] = Accidental.Sharp
                        it[NoteScale.E] = Accidental.None
                        it[NoteScale.F] = Accidental.Natural
                        it[NoteScale.FSharp] = Accidental.None
                        it[NoteScale.G] = Accidental.None
                        it[NoteScale.GSharp] = Accidental.Sharp
                    },
                    A to EnumMap<NoteScale, Accidental>(NoteScale::class.java).also {
                        it[NoteScale.A] = Accidental.None
                        it[NoteScale.ASharp] = Accidental.Flat
                        it[NoteScale.B] = Accidental.None
                        it[NoteScale.C] = Accidental.Natural
                        it[NoteScale.CSharp] = Accidental.None
                        it[NoteScale.D] = Accidental.None
                        it[NoteScale.DSharp] = Accidental.Sharp
                        it[NoteScale.E] = Accidental.None
                        it[NoteScale.F] = Accidental.Natural
                        it[NoteScale.FSharp] = Accidental.None
                        it[NoteScale.G] = Accidental.Natural
                        it[NoteScale.GSharp] = Accidental.None
                    },
                    E to EnumMap<NoteScale, Accidental>(NoteScale::class.java).also {
                        it[NoteScale.A] = Accidental.None
                        it[NoteScale.ASharp] = Accidental.Flat
                        it[NoteScale.B] = Accidental.None
                        it[NoteScale.C] = Accidental.Natural
                        it[NoteScale.CSharp] = Accidental.None
                        it[NoteScale.D] = Accidental.Natural
                        it[NoteScale.DSharp] = Accidental.None
                        it[NoteScale.E] = Accidental.None
                        it[NoteScale.F] = Accidental.Natural
                        it[NoteScale.FSharp] = Accidental.None
                        it[NoteScale.G] = Accidental.Natural
                        it[NoteScale.GSharp] = Accidental.None
                    },
                    B to EnumMap<NoteScale, Accidental>(NoteScale::class.java).also {
                        it[NoteScale.A] = Accidental.Natural
                        it[NoteScale.ASharp] = Accidental.None
                        it[NoteScale.B] = Accidental.None
                        it[NoteScale.C] = Accidental.Natural
                        it[NoteScale.CSharp] = Accidental.None
                        it[NoteScale.D] = Accidental.Natural
                        it[NoteScale.DSharp] = Accidental.None
                        it[NoteScale.E] = Accidental.None
                        it[NoteScale.F] = Accidental.Natural
                        it[NoteScale.FSharp] = Accidental.None
                        it[NoteScale.G] = Accidental.Natural
                        it[NoteScale.GSharp] = Accidental.None
                    }
            )
        }
        private val flatkeys: Map<Int, EnumMap<NoteScale, Accidental>> by lazy {
            mapOf(
                    C to EnumMap<NoteScale, Accidental>(NoteScale::class.java).also {
                        it[NoteScale.A] = Accidental.None
                        it[NoteScale.ASharp] = Accidental.Flat
                        it[NoteScale.B] = Accidental.None
                        it[NoteScale.C] = Accidental.None
                        it[NoteScale.CSharp] = Accidental.Sharp
                        it[NoteScale.D] = Accidental.None
                        it[NoteScale.DSharp] = Accidental.Sharp
                        it[NoteScale.E] = Accidental.None
                        it[NoteScale.F] = Accidental.None
                        it[NoteScale.FSharp] = Accidental.Sharp
                        it[NoteScale.G] = Accidental.None
                        it[NoteScale.GSharp] = Accidental.Sharp
                    },
                    F to EnumMap<NoteScale, Accidental>(NoteScale::class.java).also {
                        it[NoteScale.A] = Accidental.None
                        it[NoteScale.BFlat] = Accidental.None
                        it[NoteScale.B] = Accidental.Natural
                        it[NoteScale.C] = Accidental.None
                        it[NoteScale.CSharp] = Accidental.Sharp
                        it[NoteScale.D] = Accidental.None
                        it[NoteScale.EFlat] = Accidental.Flat
                        it[NoteScale.E] = Accidental.None
                        it[NoteScale.F] = Accidental.None
                        it[NoteScale.FSharp] = Accidental.Sharp
                        it[NoteScale.G] = Accidental.None
                        it[NoteScale.AFlat] = Accidental.Flat
                    },
                    Bflat to EnumMap<NoteScale, Accidental>(NoteScale::class.java).also {
                        it[NoteScale.A] = Accidental.None
                        it[NoteScale.BFlat] = Accidental.None
                        it[NoteScale.B] = Accidental.Natural
                        it[NoteScale.C] = Accidental.None
                        it[NoteScale.CSharp] = Accidental.Sharp
                        it[NoteScale.D] = Accidental.None
                        it[NoteScale.EFlat] = Accidental.None
                        it[NoteScale.E] = Accidental.Natural
                        it[NoteScale.F] = Accidental.None
                        it[NoteScale.FSharp] = Accidental.Sharp
                        it[NoteScale.G] = Accidental.None
                        it[NoteScale.AFlat] = Accidental.Flat
                    },
                    Eflat to EnumMap<NoteScale, Accidental>(NoteScale::class.java).also {
                        it[NoteScale.A] = Accidental.Natural
                        it[NoteScale.BFlat] = Accidental.None
                        it[NoteScale.B] = Accidental.Natural
                        it[NoteScale.C] = Accidental.None
                        it[NoteScale.DFlat] = Accidental.Flat
                        it[NoteScale.D] = Accidental.None
                        it[NoteScale.EFlat] = Accidental.None
                        it[NoteScale.E] = Accidental.Natural
                        it[NoteScale.F] = Accidental.None
                        it[NoteScale.FSharp] = Accidental.Sharp
                        it[NoteScale.G] = Accidental.None
                        it[NoteScale.AFlat] = Accidental.None
                    },
                    Aflat to EnumMap<NoteScale, Accidental>(NoteScale::class.java).also {
                        it[NoteScale.A] = Accidental.Natural
                        it[NoteScale.BFlat] = Accidental.None
                        it[NoteScale.B] = Accidental.Natural
                        it[NoteScale.C] = Accidental.None
                        it[NoteScale.DFlat] = Accidental.None
                        it[NoteScale.D] = Accidental.Natural
                        it[NoteScale.EFlat] = Accidental.None
                        it[NoteScale.E] = Accidental.Natural
                        it[NoteScale.F] = Accidental.None
                        it[NoteScale.FSharp] = Accidental.Sharp
                        it[NoteScale.G] = Accidental.None
                        it[NoteScale.AFlat] = Accidental.None
                    },
                    Dflat to EnumMap<NoteScale, Accidental>(NoteScale::class.java).also {
                        it[NoteScale.A] = Accidental.Natural
                        it[NoteScale.BFlat] = Accidental.None
                        it[NoteScale.B] = Accidental.Natural
                        it[NoteScale.C] = Accidental.None
                        it[NoteScale.DFlat] = Accidental.None
                        it[NoteScale.D] = Accidental.Natural
                        it[NoteScale.EFlat] = Accidental.None
                        it[NoteScale.E] = Accidental.Natural
                        it[NoteScale.F] = Accidental.None
                        it[NoteScale.GFlat] = Accidental.None
                        it[NoteScale.G] = Accidental.Natural
                        it[NoteScale.AFlat] = Accidental.None
                    },
                    Gflat to EnumMap<NoteScale, Accidental>(NoteScale::class.java).also {
                        it[NoteScale.A] = Accidental.Natural
                        it[NoteScale.BFlat] = Accidental.None
                        it[NoteScale.B] = Accidental.None
                        it[NoteScale.C] = Accidental.Natural
                        it[NoteScale.DFlat] = Accidental.None
                        it[NoteScale.D] = Accidental.Natural
                        it[NoteScale.EFlat] = Accidental.None
                        it[NoteScale.E] = Accidental.Natural
                        it[NoteScale.F] = Accidental.None
                        it[NoteScale.GFlat] = Accidental.None
                        it[NoteScale.G] = Accidental.Natural
                        it[NoteScale.AFlat] = Accidental.None
                    }
            )
        }

        /** Guess the key signature, given the midi note numbers used in
         * the song.
         */
        fun Guess(notes: List<Int>): KeySignature {
            /* Get the frequency count of each note in the 12-note scale */
            val notecount = IntArray(12)
            for (noteNumber in notes) {
                val notescale = (noteNumber + 3) % 12
                notecount[notescale] += 1
            }
            /* For each key signature, count the total number of accidentals
         * needed to display all the notes.  Choose the key signature
         * with the fewest accidentals.
         */
            var bestkey = 0
            var is_best_sharp = true
            var smallestAccidentalCount = notes.size
            var key: Int
            key = 0
            while (key < 6) {
                var accidentalCount = 0
                for (n in NoteScale.values()) {
                    if (sharpkeys[key]?.get(n) != Accidental.None) {
                        accidentalCount += notecount[n.value]
                    }
                }
                if (accidentalCount < smallestAccidentalCount) {
                    smallestAccidentalCount = accidentalCount
                    bestkey = key
                    is_best_sharp = true
                }
                key++
            }
            key = 0
            while (key < 7) {
                var accidentalCount = 0
                for (n in NoteScale.values()) {
                    if (flatkeys[key]?.get(n) != Accidental.None) {
                        accidentalCount += notecount[n.value]
                    }
                }
                if (accidentalCount < smallestAccidentalCount) {
                    smallestAccidentalCount = accidentalCount
                    bestkey = key
                    is_best_sharp = false
                }
                key++
            }
            return if (is_best_sharp) {
                KeySignature(bestkey, 0)
            } else {
                KeySignature(0, bestkey)
            }
        }

        /* Convert a Major Key into a String */
        fun NoteScale.keyToString(): String  {
             return when (this) {
                NoteScale.A -> "A major"
                NoteScale.BFlat -> "B-flat major"
                NoteScale.B -> "B major"
                NoteScale.C -> "C major"
                NoteScale.DFlat -> "D-flat major"
                NoteScale.D -> "D major"
                NoteScale.EFlat -> "E-flat major"
                NoteScale.E -> "E major"
                NoteScale.F -> "F major"
                NoteScale.GFlat -> "G-flat major"
                NoteScale.G -> "G major"
                NoteScale.AFlat -> "A-flat major"
                else -> ""
            }
        }
    }
}