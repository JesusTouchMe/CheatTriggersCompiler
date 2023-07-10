package cum.jesus.cheattriggers.compiler.parsing.ast.expression

import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNodeType

data class Variable(val name: String) : AstNode(AstNodeType.VARIABLE) {
    override fun toString(): String {
        return "($name)"
    }
}
