package cum.jesus.cheattriggers.compiler.util

import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import kotlin.system.exitProcess

object Diagnostics {
    val errors = ArrayList<Error>()
    val warnings = ArrayList<Warning>()

    fun info(message: String) {
        println("INFO: $message")
    }

    fun warn(warning: Warning) {
        if (warning.severity == Severity.EXTREME) println("\u001b[1;91mIMPORTANT WARNING: ${warning.message}")
        else println("WARNING: ${warning.message}")
    }

    fun warn(message: String, severity: Severity = Severity.MEDIUM) {
        warn(Warning(message, severity))
    }

    fun error(err: Error, fatal: Boolean = false) {
        errors.add(err)

        when (err) {
            is PreprocessorError -> {
                println("${if (fatal) "FATAL " else ""}PREPROCESSING ERROR: ${err.message}\nAt line: ${err.line}")
            }

            is LexerError -> {
                println("${if (fatal) "FATAL " else ""}LEXING ERROR: ${err.message}\nAt line: ${err.line}")
            }

            is ParserError -> {
                println("${if (fatal) "FATAL " else ""}PARSING ERROR: ${err.message}\nCaused by: ${err.cause}\nNote that this is a node and might not represent your actual code")
            }

            is CompilerError -> {
                println("${if (fatal) "FATAL " else ""}COMPILER ERROR: ${err.message}")
            }

            else -> {
                println("${if (fatal) "FATAL " else ""}ERROR: $err")
            }
        }

        if (fatal) {
            println("Compilation failed with ${warnings.size} warnings and ${errors.size} errors")

            exitProcess(err.exitCode)
        }
    }

    fun error(message: String) {
        error(Error(message, 1))
    }

    open class Warning(val message: String, val severity: Severity)

    enum class Severity {
        VERY_LOW,
        LOW,
        MEDIUM,
        HIGH,
        VERY_HIGH,
        EXTREME
    }

    open class Error(private val msg: String, val exitCode: Int) {
        override fun toString() = msg
    }
    class PreprocessorError(val message: String, val line: Int) : Error(message, 2)
    class LexerError(val message: String, val line: Int) : Error(message, 3)
    class ParserError(val message: String, val cause: AstNode) : Error(message, 4)
    class CompilerError(val message: String) : Error(message, 5)
}