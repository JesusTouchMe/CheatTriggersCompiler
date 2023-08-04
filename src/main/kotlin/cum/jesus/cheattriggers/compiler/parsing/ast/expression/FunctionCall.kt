package cum.jesus.cheattriggers.compiler.parsing.ast.expression

import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNodeType

/*
callee is always Variable
 */
data class FunctionCall(val callee: AstNode, val args: ArrayList<AstNode>) : AstNode(AstNodeType.CALL) {
    override fun toString(): String {
        return "($callee($args))"
    }
}