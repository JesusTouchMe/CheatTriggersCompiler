package cum.jesus.cheattriggers.compiler.preprocessor

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.concurrent.ConcurrentHashMap

object Preprocessor {
    val definitions = ConcurrentHashMap<String, String?>()

    fun process(file: File, outputFile: File) { // TODO: make the preprocessor better
        if (!outputFile.exists()) outputFile.createNewFile()
        val fileWriter = FileWriter(outputFile, true)
        val outputWriter = BufferedWriter(fileWriter)

        file.useLines { lines ->
            for (_line in lines) {
                var line = _line

                for (definition in definitions) {
                    if (line.contains(definition.key)) {
                        line = line.replace(definition.key, definition.value ?: "")
                    }
                }

                if (line.startsWith("#define")) {
                    val statement = line.drop("#define ".length)
                    if (statement.trim() == "") throw PreprocessorException("#define needs at least a name")
                    val name = statement.split(" ")[0]
                    val value = if (statement == name) null else statement.drop(name.length + 1)

                    definitions[name] = value
                } else {
                    outputWriter.write(line)
                    outputWriter.newLine()
                }
            }
        }

        outputWriter.close()
    }
}