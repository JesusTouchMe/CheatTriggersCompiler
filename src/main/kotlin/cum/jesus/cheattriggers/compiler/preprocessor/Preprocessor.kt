package cum.jesus.cheattriggers.compiler.preprocessor

import cum.jesus.cheattriggers.compiler.lexing.LETTERS
import cum.jesus.cheattriggers.compiler.lexing.LETTERS_NUMBERS
import cum.jesus.cheattriggers.compiler.lexing.Lexer
import cum.jesus.cheattriggers.compiler.lexing.TokenType
import cum.jesus.cheattriggers.compiler.util.PreProcessorException
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class Preprocessor(private val file: File, private val outputFile: File) {
    private val text = file.readText()
    private val lines = text.lines()

    private val fileWriter = FileWriter(outputFile, true)
    private val outputWriter = BufferedWriter(fileWriter)

    private var lineNumber = 0

    fun process() { // TODO: make the preprocessor better
        for (s in lines) {
            var line = s
            line = processDefinedVariables(line)

            if (line.startsWith('#')) {
                processMacro(line.substring(1))
            } else {
                write(line)
                newLine()
            }

            lineNumber++
        }

        outputWriter.close()
    }

    private fun write(content: String) {
        synchronized (outputFile) {
            outputWriter.write(content)
            outputWriter.flush()
        }
    }

    private fun newLine() {
        synchronized (outputFile) {
            outputWriter.newLine()
            outputWriter.flush()
        }
    }

    private fun processDefinedVariables(line: String): String {
        var processedString = line

        for ((key, value) in definitions) {
            if (line.contains(key)) processedString = parseName(line, key, value  ?: "")
        }

        return processedString
    }

    private fun parseName(line: String, name: String, replaceWith: String): String {
        val identifiers = Lexer(line).lex().filter { it.tokenType == TokenType.IDENTIFIER && it.text == name }
        var newLine = line

        for (ident in identifiers) {
            newLine = newLine.replaceRange(ident.indices(), replaceWith)
        }

        return newLine
    }

    private fun processMacro(line: String) {
        val macro = line.split(' ')

        when (val macroName = macro[0]) {
            "include", "import" -> {
                val fileRoot = File(getParentDirPath(file.absolutePath))
                val pattern = "\"(.*?)\"".toRegex()
                val included = pattern.find(line.substring(macroName.length + 1))?.groupValues?.get(1) ?: throw PreProcessorException("#$macroName has to actually contain a file to $macroName")

                require(fileRoot.isDirectory) { "This error should be physically impossible to reach, but somehow your file is being contained by another file" }

                val fileToInclude = File(fileRoot, included)
                if (!fileToInclude.exists()) throw PreProcessorException("The file $included does not exist")

                val contents = fileToInclude.readText()

                write(contents)
            }

            "define", "def" -> {
                if (macro.size < 2) throw PreProcessorException("'#$macroName' used without defining a name")
                if (macro[1][0] !in LETTERS + "_") throw PreProcessorException("The name for '#$macroName' has to begin with an alphabetic character or _")
                for (c in macro[1]) {
                    if (c !in LETTERS_NUMBERS + "_") throw PreProcessorException("The name for '#$macroName' can only contain alphanumeric characters and _")
                }

                val name = macro[1]

                if (macro.size == 2) definitions[name] = null
                else {
                    val valueArray = macro.drop(2)
                    var value = ""

                    for (str in valueArray) {
                        value += "$str "
                    }
                    value = value.dropLast(1)

                    definitions[name] = value
                }
            }

            else -> throw PreProcessorException("'#$macroName' is not a valid macro")
        }
    }

    private fun getParentDirPath(path: String): String {
        val endsWithSlash = path.endsWith(File.separator)
        return path.substring(
            0, path.lastIndexOf(
                File.separatorChar,
                if (endsWithSlash) path.length - 2 else path.length - 1
            )
        )
    }

    companion object {
        private val definitions = LinkedHashMap<String, String?>()

        init {
            definitions["true"] = "1"
            definitions["false"] = "0"
        }
    }
}