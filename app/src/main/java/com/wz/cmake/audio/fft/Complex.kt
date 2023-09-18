package com.wz.cmake.audio.fft

import java.util.Objects

/**
 * 复数
 *
 * @author test
 */
class Complex(
    /**
     * 实数部分
     */
    private val real: Double,
    /**
     * 虚数部分 imaginary
     */
    private val im: Double
) {
    override fun toString(): String {
        return String.format("hypot: %s, complex: %s+%si", hypot(), real, im)
    }

    fun hypot(): Double {
        return Math.hypot(real, im)
    }

    fun phase(): Double {
        return Math.atan2(im, real)
    }

    /**
     * 复数求和
     */
    operator fun plus(b: Complex): Complex {
        val real = real + b.real
        val imag = im + b.im
        return Complex(real, imag)
    }

    // return a new Complex object whose value is (this - b)
    operator fun minus(b: Complex): Complex {
        val real = real - b.real
        val imag = im - b.im
        return Complex(real, imag)
    }

    // return a new Complex object whose value is (this * b)
//    operator fun times(b: Complex): Complex {
//        val a = this
//        val real = a.real * b.real - a.im * b.im
//        val imag = a.real * b.im + a.im * b.real
//        return Complex(real, imag)
//    }

    operator fun times(b: Complex?): Complex {
        val a = this
        val real = a.real * (b?.real?:0.0) - a.im * (b?.im?:0.0)
        val imag = a.real * (b?.im?:0.0) + a.im * (b?.real?:0.0)
        return Complex(real, imag)
    }

    // return a new object whose value is (this * alpha)
    fun scale(alpha: Double): Complex {
        return Complex(alpha * real, alpha * im)
    }

    // return a new Complex object whose value is the conjugate of this
    fun conjugate(): Complex {
        return Complex(real, -im)
    }

    // return a new Complex object whose value is the reciprocal of this
    fun reciprocal(): Complex {
        val scale = real * real + im * im
        return Complex(real / scale, -im / scale)
    }

    // return the real or imaginary part
    fun re(): Double {
        return real
    }

    fun im(): Double {
        return im
    }

    // return a / b
    fun divides(b: Complex): Complex {
        val a = this
        return a.times(b.reciprocal())
    }

    // return a new Complex object whose value is the complex exponential of this
    fun exp(): Complex {
        return Complex(Math.exp(real) * Math.cos(im), Math.exp(real) * Math.sin(im))
    }

    // return a new Complex object whose value is the complex sine of this
    fun sin(): Complex {
        return Complex(Math.sin(real) * Math.cosh(im), Math.cos(real) * Math.sinh(im))
    }

    // return a new Complex object whose value is the complex cosine of this
    fun cos(): Complex {
        return Complex(Math.cos(real) * Math.cosh(im), -Math.sin(real) * Math.sinh(im))
    }

    // return a new Complex object whose value is the complex tangent of this
    fun tan(): Complex {
        return sin().divides(cos())
    }

    // See Section 3.3.
    override fun equals(x: Any?): Boolean {
        if (x == null) return false
        if (this.javaClass != x.javaClass) return false
        val that = x as Complex
        return real == that.real && im == that.im
    }

    // See Section 3.3.
    override fun hashCode(): Int {
        return Objects.hash(real, im)
    }

    companion object {
        // a static version of plus
        fun plus(a: Complex, b: Complex): Complex {
            val real = a.real + b.real
            val imag = a.im + b.im
            return Complex(real, imag)
        }
    }
}