package cum.jesus.cheattriggers.compiler.parsing.ast.statement

import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNodeType

data class IfStatement(val condition: AstNode, val body: AstNode, val elseBody: AstNode?) : AstNode(AstNodeType.IF) {
    override fun toString(): String {
        var res = "(if $condition) $body"
        if (elseBody != null) {
            res += elseBody.toString()
        }
        res += ")"
        return res
    }
}
