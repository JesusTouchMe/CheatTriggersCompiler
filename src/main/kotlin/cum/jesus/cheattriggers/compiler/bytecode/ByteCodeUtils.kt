package cum.jesus.cheattriggers.compiler.bytecode

/**
 * Utility class for generating bytecode instructions
 */
object ByteCodeUtils {
    var optimise: Boolean = true;

    fun makeByteCode(sig: FunctionSignature): ByteArray {
        return ByteCodeGenerator(sig, optimise).generate()
    }

    class FunctionSpecifics(val sig: FunctionSignature) {

    }
}