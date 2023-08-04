package cum.jesus.cheattriggers.compiler.util

inline fun Double.toBoolean(): Boolean = this != 0.0

inline fun String.toBoolean(): Boolean = this.isNotEmpty()

fun ArrayList<UByte>.addAllBytes(elements: ByteArray) {
    for (byte in elements) {
        this.add(byte.toUByte())
    }
}
