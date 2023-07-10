package cum.jesus.cheattriggers.compiler.parsing.ast.statement

import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNodeType

data class ForStatement(val init: AstNode, val condition: AstNode, val afterThought: AstNode, val body: AstNode) : AstNode(AstNodeType.FOR) {
    override fun toString(): String {
        return "(for ($init; $condition; $afterThought) $body)"
    }
}
