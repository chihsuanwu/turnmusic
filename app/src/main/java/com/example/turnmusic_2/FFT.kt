package com.nclab.audiorecognition

import kotlin.math.cos
import kotlin.math.sin

class  FFT(private var fftSize: Int, private var fftPow: Int) {

    private var cosTable = DoubleArray(fftSize / 2)
    private var sinTable = DoubleArray(fftSize / 2)

    init {
        if (fftSize != 1 shl fftPow) throw RuntimeException()

        for (i in 0 until fftSize / 2) {
            val deg = -2 * Math.PI * i / fftSize
            cosTable[i] = cos(deg)
            sinTable[i] = sin(deg)
        }
    }

    fun fft(real: DoubleArray, imaginary: DoubleArray) {
        bitReverse(real, imaginary)

        var n2 = 1
        for (i in 0 until fftPow) {
            val n1 = n2
            n2 += n2
            var a = 0

            for (j in 0 until n1) {
                val cos = cosTable[a]
                val sin = sinTable[a]
                a += 1 shl fftPow - i - 1

                for (k in j until fftSize step n2) {
                    val temp1 = cos * real[k + n1] - sin * imaginary[k + n1]
                    val temp2 = sin * real[k + n1] + cos * imaginary[k + n1]
                    real[k + n1] = real[k] - temp1
                    imaginary[k + n1] = imaginary[k] - temp2
                    real[k] = real[k] + temp1
                    imaginary[k] = imaginary[k] + temp2
                }
            }
        }
    }

    private fun bitReverse(real: DoubleArray, imaginary: DoubleArray) {
        var j = 0
        val halfSize = fftSize / 2

        for (i in 1 until fftSize - 1) {
            var n = halfSize
            while (j >= n) {
                j -= n
                n /= 2
            }
            j += n
            if (i < j) {
                var temp = real[i]
                real[i] = real[j]
                real[j] = temp
                temp = imaginary[i]
                imaginary[i] = imaginary[j]
                imaginary[j] = temp
            }
        }
    }
}