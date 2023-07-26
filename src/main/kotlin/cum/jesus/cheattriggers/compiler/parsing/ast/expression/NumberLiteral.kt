package cum.jesus.cheattriggers.compiler.parsing.ast.expression

import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNodeType
import cum.jesus.cheattriggers.compiler.parsing.ast.IPrimitive

interface INumberLiteral : IPrimitive {
    override val value: Number;
}

data class IntLiteral(override val value: Int) : AstNode(AstNodeType.INTEGER), INumberLiteral {
    override fun toString(): String {
        return "$value";
    }
}

data class FloatLiteral(override val value: Float) : AstNode(AstNodeType.FLOAT), INumberLiteral {
    override fun toString(): String {
        return "$value"
    }
}