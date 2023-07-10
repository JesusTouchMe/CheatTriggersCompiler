package cum.jesus.cheattriggers.compiler.parsing

import cum.jesus.cheattriggers.compiler.Scope
import cum.jesus.cheattriggers.compiler.lexing.Token
import cum.jesus.cheattriggers.compiler.lexing.TokenType
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNodeType
import cum.jesus.cheattriggers.compiler.parsing.ast.expression.*
import cum.jesus.cheattriggers.compiler.parsing.ast.statement.*
import cum.jesus.cheattriggers.compiler.parsing.ast.statement.Function
import cum.jesus.cheattriggers.compiler.symbol.FunSymbol
import cum.jesus.cheattriggers.compiler.symbol.VarSymbol
import java.text.ParseException
import java.util.WeakHashMap
import java.util.function.BiFunction
import kotlin.math.exp

class Parser(private val tokens: ArrayList<Token>, var currentScope: Scope) {
    private var pos = 0

    fun parse(): ArrayList<AstNode> {
        val nodes = arrayListOf<AstNode>()

        while (pos < tokens.size) {
            if (current().tokenType == TokenType.EOF) break

            val expr = parseExpr()
            if (current().tokenType == TokenType.SEMICOLON) consume()

            if (expr.nodeType == AstNodeType.FUNCTION || expr.nodeType == AstNodeType.VARIABLE_DECLARATION)
                nodes.add(expr)
        }

        return nodes
    }

    private fun current() = tokens[pos]

    private fun consume() = tokens[pos++]

    private fun peek(offset: Int) = tokens[pos + offset]

    private fun getBinOpPrecedence(tokenType: TokenType) = when (tokenType) {
        TokenType.LEFT_SQUARE_BRACKET, TokenType.LEFT_PAREN -> 55

        TokenType.DOT -> 50

        TokenType.STAR, TokenType.SLASH -> 45
        TokenType.PLUS, TokenType.MINUS -> 40

        TokenType.LEFT_ANGLE_BRACKET, TokenType.RIGHT_ANGLE_BRACKET -> 35

        TokenType.DOUBLE_EQUALS, TokenType.BANG_EQUALS -> 30

        TokenType.DOUBLE_AMPERSAND -> 25
        TokenType.DOUBLE_PIPE -> 20

        TokenType.EQUALS, TokenType.PLUS_EQUALS, TokenType.MINUS_EQUALS, TokenType.STAR_EQUALS, TokenType.SLASH_EQUALS -> 15

        else -> 0
    }

    private fun getUnOpPrecedence(tokenType: TokenType) = when (tokenType) {
        TokenType.PLUS, TokenType.MINUS, TokenType.BANG -> 50

        else -> 0
    }

    private fun expect(tokenType: TokenType) {
        if (current().tokenType != tokenType) {
            throw ParseException("Expected $tokenType, but got ${current()}", pos)
        }
    }

    private fun parseExpr(precedence: Int = 1): AstNode {
        var lhs: AstNode
        val unOpPrecedence = getUnOpPrecedence(current().tokenType)

        if (unOpPrecedence != 0 && unOpPrecedence >= precedence) {
            val operator = consume()
            lhs = UnaryExpression(parseExpr(unOpPrecedence), operator)
        } else {
            lhs = parsePrimary()
        }

        while (true) {
            val binOpPrecedence = getBinOpPrecedence(current().tokenType)
            if (binOpPrecedence < precedence) break

            val operator = consume()
            var rhs: AstNode? = null

            if (operator.tokenType == TokenType.DOT) {
                rhs = Variable(consume().text)
            } else if (operator.tokenType == TokenType.LEFT_PAREN) {
                lhs = parseCall(lhs)
            } else {
                rhs = parseExpr(binOpPrecedence)
            }

            if (operator.tokenType != TokenType.LEFT_PAREN) {
                lhs = BinaryExpression(lhs, operator, rhs!!)
            }
        }

        return lhs
    }

    private fun parsePrimary() = when (current().tokenType) {
        TokenType.LET -> parseVariableDeclaration()
        TokenType.FUN -> parseFunctionDeclaration()
        TokenType.INTEGER, TokenType.FLOAT -> parseNumberLiteral()
        TokenType.IDENTIFIER -> parseVariable()
        TokenType.LEFT_PAREN -> parseParenExpr()
        TokenType.LEFT_BRACKET -> parseCompoundExpr()
        TokenType.IF -> parseIf()
        TokenType.FOR -> parseFor()
        TokenType.WHILE -> parseWhile()

        else -> throw ParseException("Expected primary expression but got ${current()}", pos)
    }

    private fun parseVariableDeclaration(): AstNode {
        consume()

        expect(TokenType.IDENTIFIER)
        val name = current().text

        currentScope.varSymbols.add(VarSymbol(name))

        if (current().tokenType != TokenType.EQUALS)
            return VariableDeclaration(name, null)

        consume()
        val value = parseExpr()

        return VariableDeclaration(name, value)
    }

    private fun parseVariable(): AstNode {
        val name = consume().text
        return Variable(name)
    }

    private fun parseFunctionDeclaration(): AstNode {
        consume()

        expect(TokenType.IDENTIFIER)
        val name = current().text
        consume()

        currentScope.funSymbols.add(FunSymbol(name))

        val args = arrayListOf<String>()
        if (current().tokenType != TokenType.LEFT_PAREN) throw ParseException("Expected '(' after function declaration", pos)
        consume()

        val scope = Scope(currentScope)
        currentScope = scope

        while (current().tokenType != TokenType.RIGHT_PAREN) {
            expect(TokenType.IDENTIFIER)
            val name = current().text

            args.add(name)
            currentScope.varSymbols.add(VarSymbol(name))
            if (current().tokenType == TokenType.RIGHT_PAREN) break;

            expect(TokenType.COMMA)
            consume()
        }
        consume()

        val body = parseExpr()

        currentScope = currentScope.parent!!
        return Function(name, scope, body, args)
    }

    private fun parseCall(callee: AstNode): AstNode {
        val args = arrayListOf<AstNode>()

        while (current().tokenType != TokenType.RIGHT_PAREN) {
            args.add(parseExpr())
            if (current().tokenType == TokenType.RIGHT_PAREN) break;

            expect(TokenType.COMMA)
            consume()
        }
        consume()

        return FunctionCall(callee, args)
    }

    private fun parseNumberLiteral(): AstNode {
        return when (current().tokenType) {
            TokenType.INTEGER -> {
                val value = current().text.toInt()
                consume()
                IntLiteral(value)
            }

            TokenType.FLOAT -> {
                val value = current().text.toFloat()
                consume()
                FloatLiteral(value)
            }

            else -> throw Exception("(Should be) impossible to reach")
        }
    }

    private fun parseParenExpr(): AstNode {
        consume()

        val expr = parseExpr()
        expect(TokenType.RIGHT_PAREN)
        consume()

        return expr
    }

    private fun parseCompoundExpr(): AstNode {
        consume()

        val scope = Scope(currentScope)
        currentScope = scope

        val exprs = arrayListOf<AstNode>()

        while (current().tokenType != TokenType.RIGHT_BRACKET) {
            exprs.add(parseExpr())
        }
        consume()

        currentScope = currentScope.parent!!
        return Compound(exprs, scope)
    }

    private fun parseIf(): AstNode {
        consume()

        expect(TokenType.LEFT_PAREN)
        consume()

        val condition = parseExpr()

        expect(TokenType.RIGHT_PAREN)
        consume()

        val body = parseExpr()

        if (peek(1).tokenType == TokenType.ELSE) {
            consume()
            return IfStatement(condition, body, parseExpr())
        }

        return IfStatement(condition, body, null)
    }

    private fun parseFor(): AstNode {
        consume()

        expect(TokenType.LEFT_PAREN)
        consume()

        val init = parseExpr()

        expect(TokenType.SEMICOLON)
        consume()

        val condition = parseExpr()

        expect(TokenType.SEMICOLON)
        consume()

        val afterThought = parseExpr()

        expect(TokenType.RIGHT_PAREN)
        consume()

        val body = parseExpr()

        return ForStatement(init, condition, afterThought, body)
    }

    private fun parseWhile(): AstNode {
        consume()

        expect(TokenType.LEFT_PAREN)
        consume()

        val condition = parseExpr()

        expect(TokenType.RIGHT_PAREN)
        consume()

        val body = parseExpr()

        return WhileStatement(condition, body)
    }
}