package cum.jesus.cheattriggers.compiler

import cum.jesus.cheattriggers.compiler.bytecode.*
import java.io.File

/**
 * debug purposes :D
 */
class Disassembler(private val file: File) {
    private val supportedBytecodeVersions = arrayOf(
        "1"
    )

    private val text = file.readText()
    @OptIn(ExperimentalUnsignedTypes::class)
    private val bytes = text.toByteArray().toUByteArray()

    private var pos = 0

    @OptIn(ExperimentalUnsignedTypes::class)
    fun disassemble(): String {
        val fileVersion = text.lines()[0]
        if (fileVersion !in supportedBytecodeVersions) println("WARNING: Bytecode version $fileVersion used in ${file.name} is not supported by this disassembler, however it will still try")

        pos += text.lines()[0].length + 2

        var string = ""
        while (pos < bytes.size) {
            if (current() == SignatureUtils.FUNCTION_SIGNATURE) {
                consume()
                string += "${genName()}:\n"
            }
            if (current() == SignatureUtils.ENTRY) {
                string += "_start:\n"
            }
            if (current() == SignatureUtils.BYTECODE) {
                consume()
                string += genBytecode()
            }

            consume()
        }

        return string
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun current() = bytes[pos]

    @OptIn(ExperimentalUnsignedTypes::class)
    fun consume() = bytes[pos++]

    @OptIn(ExperimentalUnsignedTypes::class)
    fun peek(offset: Int) = bytes[pos + offset]

    private fun genName(): String {
        var string = ""

        while (current() != NUL) {
            string += String(byteArrayOf(current().toByte()))
            consume()
        }

        return string
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun genBytecode(): String {
        var string = ""
        var otherPos = 0
        while (pos < bytes.size) {
            val (name, argc) = opcodeMap[current()] ?: error("bad bytecode")
            string += "$otherPos: $name"
            if (argc > 0) {
                string += if (name.length < 12) "\t\t\t"
                else "\t\t"
            }

            otherPos += 1 + argc

            for (i in 1..argc) {
                consume()
                string += current().toInt().toString() + ", "
            }
            if (argc > 0) string = string.dropLast(2)

            string += "\n"

            if (peek(1) == NUL && peek(2) == NUL) break

            consume()
        }
        string += "\n"
        return string
    }
}