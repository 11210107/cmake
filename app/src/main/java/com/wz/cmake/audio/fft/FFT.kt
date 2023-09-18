package com.wz.cmake.audio.fft

import android.util.Log

class FFT {
    // compute the FFT of x[], assuming its length is a power of 2
    companion object{
        // compute the inverse FFT of x[], assuming its length is a power of 2
        @JvmStatic
        fun fft(x: Array<Complex?>): Array<Complex?> {
            val n = x.size

            // base case
            if (n == 1) return arrayOf<Complex?>(x[0])

            // radix 2 Cooley-Tukey FFT
            require(n % 2 == 0) { "n is not a power of 2" }

            // fft of even terms
            val even: Array<Complex?> = arrayOfNulls<Complex>(n / 2)
            for (k in 0 until n / 2) {
                even[k] = x[2 * k]
            }
            val q: Array<Complex?> = fft(even)

            // fft of odd terms
            for (k in 0 until n / 2) {
                even[k] = x[2 * k + 1]
            }
            val r: Array<Complex?> = fft(even)

            // combine
            val y: Array<Complex?> = arrayOfNulls<Complex>(n)
            for (k in 0 until n / 2) {
                val kth = -2 * k * Math.PI / n
                val wk = Complex(Math.cos(kth), Math.sin(kth))
                y[k] = q[k]?.plus(wk.times(r[k]))
                y[k + n / 2] = q[k]?.minus(wk.times(r[k]))
            }
            return y
        }
        // compute the circular convolution of x and y
        @JvmStatic
        fun fft(x: DoubleArray, sc: Int): DoubleArray {
            val len = x.size
            if (len == 1) {
                return x
            }
            val cs: Array<Complex?> = arrayOfNulls<Complex>(len)
            val ds = DoubleArray(len / 2)
            for (i in 0 until len) {
                cs[i] = Complex(x[i], 0.0)
            }
            val ffts: Array<Complex?> = fft(cs)
            for (i in ds.indices) {
                ds[i] = Math.sqrt(Math.pow(ffts[i]?.re()?:0.0, 2.0) + Math.pow(ffts[i]?.im()?:0.0, 2.0)) / x.size
            }
            return ds
        }
        @JvmStatic
        fun ifft(x: Array<Complex?>): Array<Complex?>? {
            val n = x.size
            var y: Array<Complex?> = arrayOfNulls<Complex>(n)

            // take conjugate
            for (i in 0 until n) {
                y[i] = x[i]?.conjugate()
            }

            // compute forward FFT
            y = fft(y)

            // take conjugate again
            for (i in 0 until n) {
                y[i] = y[i]?.conjugate()
            }

            // divide by n
            for (i in 0 until n) {
                y[i] = y[i]?.scale(1.0 / n)
            }
            return y
        }

        @JvmStatic
        fun cconvolve(x: Array<Complex?>, y: Array<Complex?>): Array<Complex?>? {

            // should probably pad x and y with 0s so that they have same length
            // and are powers of 2
            require(x.size == y.size) { "Dimensions don't agree" }
            val n = x.size

            // compute FFT of each sequence
            val a: Array<Complex?> = fft(x)
            val b: Array<Complex?> = fft(y)

            // point-wise multiply
            val c: Array<Complex?> = arrayOfNulls<Complex>(n)
            for (i in 0 until n) {
                c[i] = a[i]?.times(b[i])
            }

            // compute inverse FFT
            return ifft(c)
        }

        // compute the linear convolution of x and y
        @JvmStatic
        fun convolve(x: Array<Complex?>, y: Array<Complex?>): Array<Complex?>? {
            val ZERO = Complex(0.0, 0.0)
            val a: Array<Complex?> = arrayOfNulls<Complex>(2 * x.size)
            for (i in x.indices) a[i] = x[i]
            for (i in x.size until 2 * x.size) a[i] = ZERO
            val b: Array<Complex?> = arrayOfNulls<Complex>(2 * y.size)
            for (i in y.indices) b[i] = y[i]
            for (i in y.size until 2 * y.size) b[i] = ZERO
            return cconvolve(a, b)
        }

        // display an array of Complex numbers to standard output
        @JvmStatic
        fun show(x: Array<Complex?>, title: String?) {
            println(title)
            println("-------------------")
            for (i in 0 until SIZE) {
                System.out.println(x[i])
            }
            println()
        }

        private val SIZE = 16384 / 4
        @JvmStatic
        fun `fun`(x: Int): Double {
            return Math.sin((15f * x).toDouble()) //f= 3.142
        }
        @JvmStatic
        fun getY(d: DoubleArray): Double {
            var y = 0.0
            var x = 0
            for (i in d.indices) {
                if (d[i] > y) {
                    y = d[i]
                    x = i
                }
            }
            x++
            log(String.format("x： %s ， y: %s", x, y))
            log(String.format("频率： %sHz", x.toFloat() / SIZE))
            log(String.format("频率2： %sHz", (SIZE - x).toFloat() / SIZE))
            log(String.format("振幅： %s", y))
            return y
        }
        @JvmStatic
        fun log(s: String?) {
            Log.d("FFT", "$s")
        }
    }

}