package cum.jesus.cheattriggers.compiler.preprocessor

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class PreprocessorTest {
    @Test
    fun process() {
        Preprocessor("""
            #define NWORD nigger farter
            i hate NWORD
            and i hope they die
        """.trimIndent()).process()
    }
}