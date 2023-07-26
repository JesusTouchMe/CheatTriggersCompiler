package cum.jesus.cheattriggers.compiler.bytecode

import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.expression.BinaryExpression
import cum.jesus.cheattriggers.compiler.parsing.ast.expression.FunctionCall
import cum.jesus.cheattriggers.compiler.parsing.ast.expression.Variable
import cum.jesus.cheattriggers.compiler.std

class ByteCodeGenerator(val sig: FunctionSignature, val optimise: Boolean) {
    fun generate(): ByteArray {
        val bytes = ArrayList<UByte>()



        val byteArray = ByteArray(bytes.size)
        for (i in byteArray.indices) {
            byteArray[i] = bytes[i].toByte()
        }
        return byteArray
    }

    private fun isFunctionStandard(function: FunctionCall) = (function.callee as Variable).name in std
}