package cum.jesus.cheattriggers.compiler

import cum.jesus.cheattriggers.compiler.bytecode.ByteCode
import cum.jesus.cheattriggers.compiler.bytecode.SignatureBytes
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNodeType
import cum.jesus.cheattriggers.compiler.parsing.ast.statement.Function
import cum.jesus.cheattriggers.compiler.util.MultiThreadedRunner
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

class Compiler(private val ast: ArrayList<AstNode>, private val outputFile: File, private val globalScope: Scope) {
    private val fileWriter = FileWriter(outputFile, true)
    private val outputWriter = BufferedWriter(fileWriter)

    init {
        if (!outputFile.exists()) outputFile.createNewFile()
    }

    private val writeQueue: BlockingQueue<String> = LinkedBlockingQueue()
    private val isWriting = AtomicBoolean(false)
    private val writer = Thread {
        while (true) {
            val content = writeQueue.take()

            outputWriter.write(content)
            outputWriter.newLine()
            outputWriter.flush()

            if (writeQueue.isEmpty()) {
                isWriting.set(false)
                break
            }
        }
    }

    private fun write(content: String) {
        writeQueue.put(content)
        if (!isWriting.getAndSet(true)) {
            writer.start()
        }
    }

    private fun writeSingleByte(byte: UByte) {
        write(String(byteArrayOf(byte.toByte())))
    }

    fun compile(optimise: Boolean = true) {
        write(ByteCode.BYTECODE_VERSION + "\n\n")
        makeFunctionSignatures()
    }

    private fun makeFunctionSignatures() {
        MultiThreadedRunner.new(CompilerTasks.options.valueOf("threads") as Int)

        writeSingleByte(SignatureBytes.FUNCTIONS)

        for (node in ast) {
            if (node.nodeType == AstNodeType.FUNCTION) {
                MultiThreadedRunner.addTask {
                    val function = node as Function
                    val name = function.name
                    val scope = function.scope
                    val body = function.body
                    val args = function.args
                    val isGlobal = scope.parent == globalScope
                }
            }
        }

        MultiThreadedRunner.destroy()
    }
}