package cum.jesus.cheattriggers.compiler.parsing.ast.expression

import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNodeType

interface INumberLiteral;

data class IntLiteral(val value: Int) : AstNode(AstNodeType.INTEGER), INumberLiteral {
    override fun toString(): String {
        return "$value";
    }
}

data class FloatLiteral(val value: Float) : AstNode(AstNodeType.FLOAT), INumberLiteral {
    override fun toString(): String {
        return "$value"
    }
}