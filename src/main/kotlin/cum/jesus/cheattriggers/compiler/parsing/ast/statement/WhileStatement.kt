package cum.jesus.cheattriggers.compiler.parsing.ast.statement

import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNodeType

data class WhileStatement(val condition: AstNode, val body: AstNode) : AstNode(AstNodeType.WHILE) {
    override fun toString(): String {
        return "(while ($condition) $body)"
    }
}
