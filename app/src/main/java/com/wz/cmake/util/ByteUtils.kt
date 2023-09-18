package com.wz.cmake.util

class ByteUtils {
    companion object{
        /**
         * ShortArray 转 ByteArray
         */
        @JvmStatic
        fun toBytes(src: ShortArray):ByteArray {
            val count = src.size
            val dest = ByteArray(count shl 1)
            for (i in 0 until count) {
                dest[i * 2] = src[i].toByte()
                dest[i * 2 + 1] = (src[i].toInt() shr 8).toByte()
            }
            return dest
        }
        /**
         * ByteArray 转 ShortArray
         * ShortArray 2字节
         */
        @JvmStatic
        fun toShorts(src:ByteArray):ShortArray{
            val count = src.size shr 1
            val dest = ShortArray(count)
            for (i in 0 until count) {
                dest[i] = (src[i * 2].toInt() and 0xff or (src[2 * i + 1].toInt() and 0xff shl 8)).toShort()
            }
            return dest
        }

        /**
         * String to ByteArray
         */
        @JvmStatic
        fun toBytes(src: String): ByteArray {
            return src.toByteArray(Charsets.UTF_8)
        }

        /**
         * Int to ByteArray
         */
        @JvmStatic
        fun toBytes(i: Int): ByteArray {
            val dest = ByteArray(4)
            dest[0] = (i and 0xff).toByte()
            dest[1] = ((i shr 8) and 0xff).toByte()
            dest[2] = ((i shr 16) and 0xff).toByte()
            dest[3] = ((i shr 24) and 0xff).toByte()
            return dest
        }

        /**
         * Short to ByteArray
         */
        @JvmStatic
        fun toBytes(s: Short): ByteArray {
            val dest = ByteArray(2)
            dest[0] = s.toByte()
            dest[1] = (s.toInt() shr 8).toByte()
            return dest
        }

        @JvmStatic
        fun toHardDouble(shorts: ShortArray):DoubleArray {
            val length = 512
            val ds = DoubleArray(length)
            for (i in 0 until length) {
                ds[i] = shorts[i].toDouble()
            }
            return ds
        }

        @JvmStatic
        fun toSoftBytes(doubles: DoubleArray): ByteArray {
            val max = getMax(doubles)
            var sc = 1.0
            if (max > 127) {
                sc = max / 128.0
            }
            val bytes = ByteArray(doubles.size)
            for (i in doubles.indices) {
                val item = doubles[i] / sc
                bytes[i] = (if (item > 127) 127 else item).toByte()
            }
            return bytes
        }
        @JvmStatic
        fun toHardBytes(doubles: DoubleArray): ByteArray {
            val bytes = ByteArray(doubles.size)
            for (i in doubles.indices) {
                val item = doubles[i]
                bytes[i] = (if (item > 127) 127 else item).toByte()
            }
            return bytes
        }

        @JvmStatic
        fun getMax(data: DoubleArray): Double {
            var max = 0.0
            for (i in data.indices) {
                if (data[i] > max) max = data[i]
            }
            return max
        }

        @JvmStatic
        fun getAve(data: ByteArray): Int {
            var sum = 0.0
            val ave: Double
            val length = Math.min(data.size, 128)
            val offsetStart = 0
            for (i in offsetStart until length) {
                sum += (data[i] * data[i]).toDouble()
            }
            ave = sum / (length - offsetStart)
            return (Math.log10(ave) * 20).toInt()
        }

        @JvmStatic
        fun merger(bt1: ByteArray, bt2: ByteArray):ByteArray {
            val dest = ByteArray(bt1.size + bt2.size)
            System.arraycopy(bt1, 0, dest, 0, bt1.size)
            System.arraycopy(bt2, 0, dest, bt1.size, bt2.size)
            return dest
        }
    }
}