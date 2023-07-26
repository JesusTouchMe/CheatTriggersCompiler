package cum.jesus.cheattriggers.compiler.lexing

const val NUMBERS = "0123456789"
const val LETTERS = "abcdefghijklmnopqrstuvwxyzæøåABCDEFGHIJKLMNOPQRSTUVWXYZÆØÅ"
const val LETTERS_NUMBERS = LETTERS + NUMBERS

class Lexer(private val text: String) {
    private var pos = 0;

    fun lex(): ArrayList<Token> {
        val tokens = arrayListOf<Token>()

        while (pos < text.length) {
            val token = nextToken()

            if (token != null)
                tokens.add(token)

            consume()
        }

        tokens.add(Token(TokenType.EOF))

        return tokens
    }

    private inline fun current() = text[pos]

    private inline fun consume() = text[pos++]

    private inline fun peek(offset: Int) = text[pos+offset]

    private fun nextToken(): Token? {
        if (current() in LETTERS + "_") { // keyword/identifier
            val start = pos
            var value = current().toString()

            while (peek(1) in LETTERS_NUMBERS + "_") {
                consume()
                value += current()
            }

            if (keywords.containsKey(value))
                return Token(keywords[value]!!)

            return Token(TokenType.IDENTIFIER, value, start, pos)
        }

        if (current() in NUMBERS) {
            var value = current().toString()
            var commas = 0

            while (peek(1) in NUMBERS + "_.") {
                consume()

                if (current() == '.') {
                    commas++
                    if (commas > 1) {
                        return Token(TokenType.ERROR, "More than 1 dot in number")
                    }
                }

                value += current()
            }

            return if (commas > 0) Token(TokenType.FLOAT, value.replace("_", "")) else Token(TokenType.INTEGER, value.replace("_", ""))
        }

        return when (current()) {
            '\n', '\r', ' ' -> null

            '(' -> Token(TokenType.LEFT_PAREN)
            ')' -> Token(TokenType.RIGHT_PAREN)

            '[' -> Token(TokenType.LEFT_SQUARE_BRACKET)
            ']' -> Token(TokenType.RIGHT_SQUARE_BRACKET)

            '{' -> Token(TokenType.LEFT_BRACKET)
            '}' -> Token(TokenType.RIGHT_BRACKET)

            '<' -> {
                if (peek(1) == '=') {
                    consume()
                    return Token(TokenType.LEFT_ANGLE_BRACKET_EQUALS)
                }

                return Token(TokenType.LEFT_ANGLE_BRACKET)
            }
            '>' -> {
                if (peek(1) == '=') {
                    consume()
                    return Token(TokenType.RIGHT_ANGLE_BRACKET_EQUALS)
                }

                return Token(TokenType.RIGHT_ANGLE_BRACKET)
            }

            '+' -> {
                if (peek(1) == '=') {
                    consume()
                    return Token(TokenType.PLUS_EQUALS)
                }

                return Token(TokenType.PLUS)
            }

            '-' -> {
                if (peek(1) == '=') {
                    consume()
                    return Token(TokenType.MINUS_EQUALS)
                }

                return Token(TokenType.MINUS)
            }

            '*' -> {
                if (peek(1) == '=') {
                    consume()
                    return Token(TokenType.STAR_EQUALS)
                }

                return Token(TokenType.STAR)
            }

            '/' -> {
                if (peek(1) == '/') {
                    while (current() != '\n')
                        consume()

                    return null
                } else if (peek(1) == '*') {
                    pos += 2

                    while (current() != '*' && peek(1) != '/')
                        consume()
                    consume()

                    return null
                } else if (peek(1) == '=') {
                    consume()
                    return Token(TokenType.SLASH_EQUALS)
                }

                return Token(TokenType.SLASH)
            }

            '=' -> {
                if (peek(1) == '=') {
                    consume()
                    return Token(TokenType.DOUBLE_EQUALS)
                }

                return Token(TokenType.EQUALS)
            }

            '!' -> {
                if (peek(1) == '=') {
                    consume()
                    return Token(TokenType.BANG_EQUALS)
                }

                return Token(TokenType.BANG)
            }

            '^' -> {
                if (peek(1) == '^') {
                    consume()
                    return Token(TokenType.DOUBLE_CARET)
                } else if (peek(1) == '=') {
                    consume()
                    return Token(TokenType.CARET_EQUALS)
                }

                return Token(TokenType.CARET)
            }

            '&' -> {
                if (peek(1) == '&') {
                    consume()
                    return Token(TokenType.DOUBLE_AMPERSAND)
                }

                return Token(TokenType.AMPERSAND)
            }

            '|' -> {
                if (peek(1) == '|') {
                    consume()
                    return Token(TokenType.DOUBLE_PIPE)
                }

                return Token(TokenType.PIPE)
            }

            '.' -> Token(TokenType.DOT)
            ',' -> Token(TokenType.COMMA)

            ':' -> Token(TokenType.COLON)
            ';' -> Token(TokenType.SEMICOLON)

            else -> Token(TokenType.UNKNOWN)
        }
    }
}