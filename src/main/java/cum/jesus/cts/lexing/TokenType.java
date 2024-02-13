package cum.jesus.cts.lexing;

public enum TokenType {
    ERROR,
    EOF,

    IDENTIFIER,
    TYPE,
    INTEGER_LITERAL,

    LEFT_PAREN, RIGHT_PAREN, // ()
    LEFT_BRACKET, RIGHT_BRACKET, // []
    LEFT_BRACE, RIGHT_BRACE, // {}
    LEFT_ANGLE_BRACKET, RIGHT_ANGLE_BRACKET,

    SEMICOLON, COMMA,

    PLUS, MINUS,
    STAR, SLASH,

    EQUALS,

    KEYWORD_FUNC,
    KEYWORD_RETURN,
}
