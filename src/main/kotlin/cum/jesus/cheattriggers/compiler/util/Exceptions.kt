package cum.jesus.cheattriggers.compiler.util

class PreProcessorException(override val message: String) : Exception(message) {

}

class ParseException(override val message: String) : Exception(message) {

}

class CompilerException(override val message: String) : Exception(message) {

}