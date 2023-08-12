package cum.jesus.cheattriggers.compiler.preprocessor

import cum.jesus.cheattriggers.compiler.bytecode.BYTECODE_VERSION
import cum.jesus.cheattriggers.compiler.lexing.LETTERS
import cum.jesus.cheattriggers.compiler.lexing.LETTERS_NUMBERS
import cum.jesus.cheattriggers.compiler.lexing.Lexer
import cum.jesus.cheattriggers.compiler.lexing.TokenType
import cum.jesus.cheattriggers.compiler.util.Diagnostics
import cum.jesus.cheattriggers.compiler.util.Diagnostics.PreprocessorError
import cum.jesus.cheattriggers.compiler.util.toBoolean
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import kotlin.math.pow

class Preprocessor(private val file: File, private val outputFile: File) {
    private val text = file.readText()
    private val lines = text.lines()

    private val fileWriter = FileWriter(outputFile, true)
    private val outputWriter = BufferedWriter(fileWriter)

    private var lineNumber = 0

    fun process() { // TODO: make the preprocessor better
        while (lineNumber < lines.size) {
            var line = current()
            line = processDefinedVariables(line)

            if (line.trim().startsWith('#')) {
                processMacro(line.substring(1))
            } else {
                write(line)
                newLine()
            }

            consume()
        }

        outputWriter.close()
    }

    private fun current() = lines[lineNumber]
    private fun consume() = lines[lineNumber++]
    private fun peek(offset: Int) = lines[lineNumber + offset]

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

        if (line.trim().startsWith("#ifdef") || line.trim().startsWith("#ifndef")) return processedString

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
                val included = pattern.find(line.substring(macroName.length + 1))?.groupValues?.get(1) ?: run {
                    Diagnostics.error(
                        PreprocessorError("#$macroName has to actually contain a file to $macroName", lineNumber), true
                    )
                    error("to make kotlin shut the fuck up (this will not be reached)")
                }

                require(fileRoot.isDirectory) { "This error should be physically impossible to reach, but somehow your file is being contained by another file" }

                val fileToInclude = File(fileRoot, included)
                if (!fileToInclude.exists()) Diagnostics.error(PreprocessorError("The file $included does not exist", lineNumber), true)

                Preprocessor(fileToInclude, outputFile).process()
            }

            "define", "def" -> {
                if (macro.size < 2) Diagnostics.error(PreprocessorError("'#$macroName' used without defining a name", lineNumber), true)
                if (macro[1][0] !in LETTERS + "_") Diagnostics.error(PreprocessorError("The name for '#$macroName' has to begin with an alphabetic character or _", lineNumber), true)
                for (c in macro[1]) {
                    if (c == '(') Diagnostics.error(PreprocessorError("define macros are not implemented yet", lineNumber), true)
                    if (c !in LETTERS_NUMBERS + "_") Diagnostics.error(PreprocessorError("The name for '#$macroName' can only contain alphanumeric characters and _", lineNumber), true)
                }

                val name = macro[1]

                if (isStandard(name)) Diagnostics.error(PreprocessorError("Standard definitions cannot be redefined or undefined", lineNumber), true)

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

            "undefine", "undef" -> {
                if (macro.size < 2) Diagnostics.error(PreprocessorError("'#$macroName' used without defining a name", lineNumber), true)
                if (macro[1][0] !in LETTERS + "_") Diagnostics.error(PreprocessorError("The name for '#$macroName' has to begin with an alphabetic character or _", lineNumber), true)
                for (c in macro[1]) {
                    if (c !in LETTERS_NUMBERS + "_") Diagnostics.error(PreprocessorError("The name for '#$macroName' can only contain alphanumeric characters and _", lineNumber), true)
                }

                val name = macro[1]

                if (isStandard(name)) Diagnostics.error(PreprocessorError("Standard definitions cannot be redefined or undefined", lineNumber), true)

                definitions.remove(name)
            }

            "if", "ifn" -> {
                val not = macroName == "ifn"

                if (macro.size < 2) Diagnostics.error(PreprocessorError("'#$macroName' needs a statement", lineNumber), true)

                val statementArray = macro.drop(1)
                var statement = ""

                for (str in statementArray) {
                    statement += "$str "
                }
                statement = statement.dropLast(1)

                val statementResult = if (not) !isStatementTrue(statement) else isStatementTrue(statement)

                val ifBlock = ArrayList<String>()
                val elseBlock = ArrayList<String>()
                while (!peek(1).trim().startsWith("#else") && !peek(1).trim().startsWith("#end")) {
                    consume()

                    var line = current()
                    line = processDefinedVariables(line)

                    if (line.startsWith('#'))
                        processMacro(line.substring(1))
                    else
                        ifBlock.add(line)
                }
                consume()

                if (current().trim().startsWith("#else")) {
                    while (!peek(1).startsWith("#end")) {
                        consume()

                        var line = current()
                        line = processDefinedVariables(line)

                        if (line.startsWith('#'))
                            processMacro(line.substring(1))
                        else
                            elseBlock.add(line)
                    }
                }

                if (statementResult) {
                    ifBlock.forEach {
                        write(it)
                        newLine()
                    }
                } else {
                    elseBlock.forEach {
                        write(it)
                        newLine()
                    }
                }

                consume()
            }

            "ifdef", "ifndef" -> {
                val not = macroName == "ifndef"

                if (macro.size < 2) Diagnostics.error(PreprocessorError("'#$macroName' used without defining a name", lineNumber), true)
                if (macro[1][0] !in LETTERS + "_") Diagnostics.error(PreprocessorError("The name for '#$macroName' has to begin with an alphabetic character or _", lineNumber), true)
                for (c in macro[1]) {
                    if (c !in LETTERS_NUMBERS + "_") Diagnostics.error(PreprocessorError("The name for '#$macroName' can only contain alphanumeric characters and _", lineNumber), true)
                }

                val name = macro[1]
                val statementResult = if (not) name !in definitions else name in definitions

                val ifBlock = ArrayList<String>()
                val elseBlock = ArrayList<String>()
                while (!peek(1).trim().startsWith("#else") && !peek(1).trim().startsWith("#end")) {
                    consume()

                    var line = current()
                    line = processDefinedVariables(line)

                    if (line.startsWith('#'))
                        processMacro(line.substring(1))
                    else
                        ifBlock.add(line)
                }
                consume()

                if (current().trim().startsWith("#else")) {
                    while (!peek(1).startsWith("#end")) {
                        consume()

                        var line = current()
                        line = processDefinedVariables(line)

                        if (line.startsWith('#'))
                            processMacro(line.substring(1))
                        else
                            elseBlock.add(line)
                    }
                }

                if (statementResult) {
                    ifBlock.forEach {
                        write(it)
                        newLine()
                    }
                } else {
                    elseBlock.forEach {
                        write(it)
                        newLine()
                    }
                }

                consume()
            }

            "repeat" -> {
                if (macro.size < 2) Diagnostics.error(PreprocessorError("'#$macroName' needs a statement", lineNumber), true)

                val statementArray = macro.drop(1)
                var statement = ""

                for (str in statementArray) {
                    statement += "$str "
                }
                statement = statement.dropLast(1)

                val amountOfRepeats = simpleMathImpl(statement) ?: 0.0

                val block = ArrayList<String>()
                while (!peek(1).trim().startsWith("#else") && !peek(1).trim().startsWith("#end")) {
                    consume()

                    var line = current()
                    line = processDefinedVariables(line)

                    if (line.startsWith('#'))
                        processMacro(line.substring(1))
                    else
                        block.add(line)
                }
                consume()

                var finalBlock = ""

                block.forEach {
                    finalBlock += it + "\n"
                }

                repeat(amountOfRepeats.toInt()) {
                    write(finalBlock)
                }
            }

            "else", "end" -> error("Unreachable")

            else -> Diagnostics.error(PreprocessorError("'#$macroName' is not a valid macro", lineNumber))
        }
    }

    companion object {
        private val definitions = LinkedHashMap<String, String?>()
        private val standardDefinitions = LinkedHashMap<String, String>()

        init {
            standardDefinitions["true"] = "1"
            standardDefinitions["false"] = "-1"
            standardDefinitions["maybe"] = "0"
            standardDefinitions["__BYTECODE_VERSION"] = BYTECODE_VERSION

            definitions.putAll(standardDefinitions)
        }

        private fun isStandard(name: String) = name in standardDefinitions

        private fun getParentDirPath(path: String): String {
            val endsWithSlash = path.endsWith(File.separator)
            return path.substring(
                0, path.lastIndexOf(
                    File.separatorChar,
                    if (endsWithSlash) path.length - 2 else path.length - 1
                )
            )
        }

        private fun isStatementTrue(statement: String): Boolean {
            if (statement.toDoubleOrNull() != null) return statement.toDouble().toBoolean()

            val math = simpleMathImpl(statement)
            return math?.toBoolean() ?: (statement != "")
        }

        // I found this on GitHub a while ago and I've just been using it since. I have no clue who to credit :skull:
        private fun simpleMathImpl(str: String): Double? {
            try {
                return object {
                    var pos = -1
                    var ch = 0
                    fun nextChar() {
                        ch = if (++pos < str.length) str[pos].code else -1
                    }

                    fun eat(charToEat: Int): Boolean {
                        while (ch == ' '.code) nextChar()
                        if (ch == charToEat) {
                            nextChar()
                            return true
                        }
                        return false
                    }

                    fun parse(): Double {
                        nextChar()
                        val x = parseExpression()
                        if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                        return x
                    }

                    // Grammar:
                    // expression = term | expression `+` term | expression `-` term
                    // term = factor | term `*` factor | term `/` factor
                    // factor = `+` factor | `-` factor | `(` expression `)` | number
                    //        | functionName `(` expression `)` | functionName factor
                    //        | factor `^` factor
                    fun parseExpression(): Double {
                        var x = parseTerm()
                        while (true) {
                            if (eat('+'.code)) x += parseTerm() // addition
                            else if (eat('-'.code)) x -= parseTerm() // subtraction
                            else return x
                        }
                    }

                    fun parseTerm(): Double {
                        var x = parseFactor()
                        while (true) {
                            if (eat('*'.code)) x *= parseFactor() // multiplication
                            else if (eat('/'.code)) x /= parseFactor() // division
                            else return x
                        }
                    }

                    fun parseFactor(): Double {
                        if (eat('+'.code)) return +parseFactor() // unary plus
                        if (eat('-'.code)) return -parseFactor() // unary minus
                        var x: Double
                        val startPos = pos
                        if (eat('('.code)) { // parentheses
                            x = parseExpression()
                            if (!eat(')'.code)) throw RuntimeException("Missing ')'")
                        } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) { // numbers
                            while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                            x = str.substring(startPos, pos).toDouble()
                        } else {
                            throw RuntimeException("Unexpected: " + ch.toChar())
                        }
                        if (eat('^'.code)) x = x.pow(parseFactor()) // exponentiation
                        return x
                    }
                }.parse()
            } catch (e: Exception) {
                return null
            }
        }
    }
}
