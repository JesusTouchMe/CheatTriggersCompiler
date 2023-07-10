package cum.jesus.cheattriggers.compiler.parsing.ast.statement

import cum.jesus.cheattriggers.compiler.Scope
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNodeType

data class Function(val name: String, val scope: Scope, val body: AstNode, val args: ArrayList<String>) : AstNode(AstNodeType.FUNCTION) {
    override fun toString(): String {
        return "(fun $name($args) $body)"
    }
}