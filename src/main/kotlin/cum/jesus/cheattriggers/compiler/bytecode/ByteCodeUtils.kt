package cum.jesus.cheattriggers.compiler.bytecode

import cum.jesus.cheattriggers.compiler.util.Diagnostics
import cum.jesus.cheattriggers.compiler.util.addAllBytes

/**
 * Utility class for generating bytecode instructions
 */
object ByteCodeUtils {
    val comparisons = linkedMapOf<String, Byte>(
        "==" to 0,
        "!=" to 1,
        "<" to 2,
        ">" to 3,
        "<=" to 4,
        ">=" to 5
    )

    var optimise: Boolean = true;

    fun makeByteCode(sig: FunctionSignature): ByteArray {
        return ByteCodeGenerator(sig, optimise).generate()
    }

    fun compEqual(): ByteArray {
        return byteArrayOf(
            COMPARE.toByte(), comparisons["=="]!!
        )
    }

    fun compNotEqual(): ByteArray {
        return byteArrayOf(
            COMPARE.toByte(), comparisons["!="]!!
        )
    }

    fun compLT(): ByteArray {
        return byteArrayOf(
            COMPARE.toByte(), comparisons["<"]!!
        )
    }

    fun compGT(): ByteArray {
        return byteArrayOf(
            COMPARE.toByte(), comparisons[">"]!!
        )
    }

    fun compLTE(): ByteArray {
        return byteArrayOf(
            COMPARE.toByte(), comparisons["<="]!!
        )
    }

    fun compGTE(): ByteArray {
        return byteArrayOf(
            COMPARE.toByte(), comparisons[">="]!!
        )
    }

    fun loadNull(): ByteArray {
        return byteArrayOf(
            LOAD_CONST.toByte(), 0
        )
    }

    class FunctionSpecifics(private val sig: FunctionSignature) {
        fun assignVariable(name: String, value: ArrayList<UByte>?): ArrayList<UByte> {
            val bytes = ArrayList<UByte>()
            val (byte, offset) = sig.lookup(name) ?: run {
                Diagnostics.error(Diagnostics.CompilerError("Unknown variable: $name"), true)
                error("to get kotlin to shut the fuck up (it will never be reached)")
            }
            val correctStoreByte = when (byte) {
                LOAD_GLOBAL -> STORE_GLOBAL
                LOAD_NAME -> STORE_NAME
                LOAD_FAST -> STORE_FAST

                LOAD_CONST -> {
                    Diagnostics.error(Diagnostics.CompilerError("Cannot assign literal"), true)
                    NUL
                }
                else -> error("Unreachable")
            }

            if (value != null)
                bytes.addAll(value)
            else
                bytes.addAllBytes(loadNull())

            bytes.add(correctStoreByte)
            bytes.add(offset.toUByte())

            return bytes
        }

        fun binaryAssignVariable(name: String, value: ArrayList<UByte>, binaryByte: UByte): ArrayList<UByte> {
            val bytes = ArrayList<UByte>()
            val (byte, offset) = sig.lookup(name) ?: run {
                Diagnostics.error(Diagnostics.CompilerError("Unknown variable: $name"), true)
                error("to get kotlin to shut the fuck up (it will never be reached)")
            }
            val correctStoreByte = when (byte) {
                LOAD_GLOBAL -> STORE_GLOBAL
                LOAD_NAME -> STORE_NAME
                LOAD_FAST -> STORE_FAST

                LOAD_CONST -> {
                    Diagnostics.error(Diagnostics.CompilerError("Cannot assign literal"), true)
                    NUL
                }
                else -> error("Unreachable")
            }

            bytes.add(byte)
            bytes.add(offset.toUByte())
            bytes.addAll(value)

            bytes.add(binaryByte)

            // top of stack should now be the value of (value of "name") "binaryByte" ("value")

            bytes.add(correctStoreByte)
            bytes.add(offset.toUByte())

            return bytes
        }

        fun loadVariable(name: String): ArrayList<UByte> {
            val bytes = ArrayList<UByte>()

            val (byte, offset) = sig.lookup(name) ?: run {
                Diagnostics.error(Diagnostics.CompilerError("Unknown variable: $name"), true)
                error("to get kotlin to shut the fuck up (it will never be reached)")
            }
            bytes.add(byte)
            bytes.add(offset.toUByte())

            return bytes
        }
    }
}