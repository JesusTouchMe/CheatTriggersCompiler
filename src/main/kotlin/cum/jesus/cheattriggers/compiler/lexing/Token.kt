package cum.jesus.cheattriggers.compiler.lexing

enum class TokenType {
    IDENTIFIER,
    UNKNOWN,
    ERROR,
    EOF,

    INTEGER,
    FLOAT,

    PLUS, PLUS_EQUALS,
    MINUS, MINUS_EQUALS,
    STAR, STAR_EQUALS,
    SLASH, SLASH_EQUALS,

    EQUALS, DOUBLE_EQUALS,
    LEFT_ANGLE_BRACKET_EQUALS, RIGHT_ANGLE_BRACKET_EQUALS,

    BANG, BANG_EQUALS,

    CARET, DOUBLE_CARET, CARET_EQUALS,
    AMPERSAND, DOUBLE_AMPERSAND,
    PIPE, DOUBLE_PIPE,

    LEFT_PAREN, RIGHT_PAREN, // ()
    LEFT_SQUARE_BRACKET, RIGHT_SQUARE_BRACKET, // []
    LEFT_BRACKET, RIGHT_BRACKET, // {}
    LEFT_ANGLE_BRACKET, RIGHT_ANGLE_BRACKET, // <>

    DOT, COMMA,
    COLON, SEMICOLON,

    NULL,

    IF, ELSE,
    WHILE, FOR,
    BREAK,

    VAR,

    FUN, BYTECODE
}

val keywords = mapOf(
    "if" to TokenType.IF,
    "else" to TokenType.ELSE,
    "while" to TokenType.WHILE,
    "for" to TokenType.FOR,
    "fun" to TokenType.FUN,
    "bytecode" to TokenType.BYTECODE,
    "var" to TokenType.VAR,
    "break" to TokenType.BREAK,
    "null" to TokenType.NULL,
)

data class Token(val tokenType: TokenType, val text: String = "", val startPos: Int = -1, val endPos: Int = -1) {
    fun indices() = startPos..endPos

    override fun toString(): String {
        return if (text != "") "$tokenType:$text" else "$tokenType"
    }
}