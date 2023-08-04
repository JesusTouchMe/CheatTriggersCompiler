package cum.jesus.cheattriggers.compiler

import cum.jesus.cheattriggers.compiler.bytecode.*
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.statement.Function
import cum.jesus.cheattriggers.compiler.util.Diagnostics
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

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun write(vararg bytes: UByte) {
        write(bytes.toByteArray())
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun compile(optimise: Boolean = true) {
        ByteCodeUtils.optimise = optimise

        write(BYTECODE_VERSION + "\n\n")
        write(SignatureUtils.makeGlobals(globalScope))

        var entryFunction: Function? = null

        // adding bytecode + signatures for everything in the AST
        for (node in ast) {
            if (node is Function) {
                if (node.name == "_start") {
                    entryFunction = node
                    continue
                }

                val (signature, bytes) = SignatureUtils.makeFunctionSignature(node)
                write(bytes)
                write(SignatureUtils.BYTECODE)
                write(ByteCodeUtils.makeByteCode(signature.setStatements(node.body.statements)))
                write(LOAD_CONST, 0u, RETURN_VALUE)
                write(NUL, NUL) // 2 NUL bytes to be 100% sure the bytecode has ended
            }
        }

        // taking inspiration from real asm by making the real program entry "_start" and making that execute main. this also means that a person can declare their own program entry
        if (globalScope.hasFunSymbol("_start")) { // the programmer made their own entry
            if (entryFunction == null) error("Impossible to reach, but still needed lol")
            if (entryFunction.args.isNotEmpty()) { // will just remove the args at compile time
                Diagnostics.error(Diagnostics.CompilerError("Program entry should not contain arguments"))
                entryFunction.args.clear()
            }

            Diagnostics.warn("Making a custom entry point is highly not recommended as the compiler may create important bytecode for it by default", Diagnostics.Severity.HIGH)

            val (signature, bytes) = SignatureUtils.makeFunctionSignature(entryFunction)
            write(bytes)
            write(SignatureUtils.BYTECODE)
            write(ByteCodeUtils.makeByteCode(signature))
            val (code, offset) = signature.lookup("0")!!
            write(code, offset.toUByte(), EXIT)
        } else { // compiler generated entry point
            val mainNumber = SignatureUtils.globalTable["main"] ?: run {
                Diagnostics.error(Diagnostics.CompilerError("Could not find main function to invoke"), true)
                error("to get kotlin to shut the fuck up (this will never be reached)")
            }

            write(SignatureUtils.ENTRY)
            write("_start")
            write(SignatureUtils.CONST_TABLE)
            write("null") // 0
            write("0") // 1
            write(SignatureUtils.END_TABLE)
            write(SignatureUtils.BYTECODE)
            write(
                LOAD_GLOBAL, mainNumber.toUByte(),
                CALL_FUNCTION, 0u,
                POP,
                LOAD_CONST, 1u,
                EXIT
            )
            write(NUL, NUL)
        }

        outputWriter.close()
    }
}