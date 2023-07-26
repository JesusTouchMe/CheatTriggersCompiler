package cum.jesus.cheattriggers.compiler

import cum.jesus.cheattriggers.compiler.bytecode.*
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.statement.Function
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class Compiler(private val ast: ArrayList<AstNode>, private val outputFile: File, private val globalScope: Scope) {
    private val fileWriter = FileWriter(outputFile, true)
    private val outputWriter = BufferedWriter(fileWriter)

    private fun write(content: String) {
        synchronized (outputFile) {
            outputWriter.write(content)
            outputWriter.flush()
        }
    }

    private fun write(content: ByteArray) {
        write(String(content))
    }

    private fun writeSingleByte(byte: UByte) {
        write(byteArrayOf(byte.toByte()))
    }

    fun compile(optimise: Boolean = true) {
        ByteCodeUtils.optimise = optimise

        write(BYTECODE_VERSION + "\n\n")
        write(SignatureUtils.makeGlobals(globalScope))

        for (node in ast) {
            if (node is Function) {
                val (signature, bytes) = SignatureUtils.makeFunctionSignature(node)
                write(bytes)
                writeSingleByte(SignatureUtils.BYTECODE)
                write(ByteCodeUtils.makeByteCode(signature.setStatements(node.body.statements)))
                writeSingleByte(NUL)
            }
        }

        outputWriter.close()
    }
}