package cum.jesus.cheattriggers.compiler.parsing.ast.expression

import cum.jesus.cheattriggers.compiler.lexing.Token
import cum.jesus.cheattriggers.compiler.lexing.TokenType
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNodeType

enum class UnaryOperator(val tokenType: TokenType) {
    NUMBER_SUBSTANTIATION(TokenType.PLUS),
    NUMBER_NEGATION(TokenType.MINUS),
    LOGICAL_NEGATION(TokenType.BANG),
}

data class UnaryExpression(val operand: AstNode, val operatorToken: Token) : AstNode(AstNodeType.UNARY_EXPRESSION) {
    val operator = UnaryOperator.values().filter { it.tokenType == operatorToken.tokenType }[0]

    override fun toString(): String {
        return "($operator, $operand)"
    }
}