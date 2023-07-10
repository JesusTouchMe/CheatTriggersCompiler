package cum.jesus.cheattriggers.compiler.parsing.ast.expression

import cum.jesus.cheattriggers.compiler.lexing.Token
import cum.jesus.cheattriggers.compiler.lexing.TokenType
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNodeType

enum class BinaryOperator(val tokenType: TokenType) {
    ADD(TokenType.PLUS), SUB(TokenType.MINUS),
    MUL(TokenType.STAR), DIV(TokenType.SLASH),

    AND(TokenType.DOUBLE_AMPERSAND), OR(TokenType.DOUBLE_PIPE),

    EQUAL(TokenType.DOUBLE_EQUALS), NOT_EQUAL(TokenType.BANG_EQUALS),
    LESS_THAN(TokenType.LEFT_ANGLE_BRACKET), GREATER_THAN(TokenType.RIGHT_ANGLE_BRACKET),
    LESS_OR_EQUAL(TokenType.LEFT_ANGLE_BRACKET_EQUALS), GREATER_OR_EQUAL(TokenType.RIGHT_ANGLE_BRACKET_EQUALS),

    ASSIGN(TokenType.EQUALS),
    ADD_ASSIGN(TokenType.PLUS_EQUALS), SUB_ASSIGN(TokenType.MINUS_EQUALS),
    MUL_ASSIGN(TokenType.STAR_EQUALS), DIV_ASSIGN(TokenType.SLASH_EQUALS),
}

data class BinaryExpression(val left: AstNode, val operatorToken: Token, val right: AstNode) : AstNode(AstNodeType.BINARY_EXPRESSION) {
    val operator = BinaryOperator.values().filter { it.tokenType == operatorToken.tokenType }[0];

    override fun toString(): String {
        return "($left, $operator, $right)"
    }
}