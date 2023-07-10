package cum.jesus.cheattriggers.compiler

import cum.jesus.cheattriggers.compiler.lexing.Lexer
import cum.jesus.cheattriggers.compiler.parsing.Parser
import cum.jesus.cheattriggers.compiler.preprocessor.Preprocessor
import cum.jesus.cheattriggers.compiler.util.MultiThreadedRunner
import joptsimple.NonOptionArgumentSpec
import joptsimple.OptionParser
import joptsimple.OptionSet
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.file.Files

// TODO: preprocessor

fun main(args: Array<String>) {
    val mode = args[0]

    if (mode == "devConsoleMode") {
        while (true) {
            print("> ")
            var input = readlnOrNull() ?: continue
            if (input.lowercase() == "quit") break

            input += "\n"

            val lexer = Lexer(input)
            val tokens = lexer.lex()

            println(tokens)

            val globalScope = Scope(null)

            val parser = Parser(tokens, globalScope)
            val ast = parser.parse()

            println(ast)
        }

        return
    }
    val programArgs = args.copyOfRange(1, args.size)
    val optionParser = OptionParser();

    optionParser.accepts("output").withRequiredArg().ofType(File::class.java).describedAs("The output file").defaultsTo(File(".", "out.ctc"))
    optionParser.accepts("threads").withRequiredArg().ofType(Int::class.java).describedAs("The amounts of threads to use (don't set it higher than the amount of cpu threads in your system)").defaultsTo(Runtime.getRuntime().availableProcessors())
    val files = optionParser.nonOptions().ofType(File::class.java)

    val options = optionParser.parse(*programArgs)

    CompilerTasks.options = options
    CompilerTasks.files = files

    when (mode) {
        "preprocess" -> CompilerTasks.preprocess()

        "compile" -> CompilerTasks.compile()
    }
}

object CompilerTasks {
    lateinit var options: OptionSet
    lateinit var files: NonOptionArgumentSpec<File>

    fun preprocess() {

    }

    fun compile() {
        val tmpDir = File(".", "ctcompiler")
        if (!tmpDir.exists()) tmpDir.mkdirs()

        val filesToCompile = arrayListOf<File>()

        // preprocessing
        MultiThreadedRunner.new(options.valueOf("threads") as Int)

        for (file in options.valuesOf(files)) {
            val processedFile = File(tmpDir, "${filesToCompile.size}.cts")
            filesToCompile.add(processedFile)
            MultiThreadedRunner.addTask {
                Preprocessor.process(file, processedFile)
            }
        }

        MultiThreadedRunner.destroy()

        // combining out files
        val combinedFiles = File(tmpDir, "a.cts")
        if (!combinedFiles.exists()) combinedFiles.createNewFile()

        val fileWriter = FileWriter(combinedFiles, true)
        val outputWriter = BufferedWriter(fileWriter)

        for (file in filesToCompile) {
            val content = String(Files.readAllBytes(file.toPath()))
            outputWriter.write(content)
            outputWriter.newLine()

            file.delete()
        }

        outputWriter.close()

        // lex
        val lexer = Lexer(String(Files.readAllBytes(combinedFiles.toPath())))
        val tokens = lexer.lex()

        // generate ast
        val globalScope = Scope(null)
        val parser = Parser(tokens, globalScope)
        val ast = parser.parse()

        println(ast)

        // compile
        val compiler = Compiler(ast, options.valueOf("output") as File, globalScope)
        compiler.compile()

        // cleanup
        combinedFiles.delete()
        if (tmpDir.listFiles()!!.isNotEmpty()) tmpDir.listFiles()!!.forEach { it.delete() }
        tmpDir.delete()
    }
}