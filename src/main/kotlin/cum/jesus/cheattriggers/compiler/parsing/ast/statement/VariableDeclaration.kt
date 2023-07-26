package cum.jesus.cheattriggers.compiler.parsing.ast.statement

import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNodeType

data class VariableDeclaration(val name: String, val value: AstNode?) : AstNode(AstNodeType.VARIABLE_DECLARATION) {
    override fun toString(): String {
        return "(var $name = $value)"
    }
}