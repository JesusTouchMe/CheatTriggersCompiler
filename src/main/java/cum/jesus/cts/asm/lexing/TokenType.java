package cum.jesus.cts.asm.lexing;

public enum TokenType {
    ERROR,

    DOLLAR,

    PLUS, MINUS, STAR, SLASH,
    LEFT_PAREN, RIGHT_PAREN, // ()
    LEFT_BRACKET, RIGHT_BRACKET, // []
    LEFT_BRACE, RIGHT_BRACE, // {}, only for special operands

    COMMA, COLON,
    HASH,

    SIZE,

    CONSTANT,

    IDENTIFIER,
    IMMEDIATE, STRING,

    REGISTER,

    INSTRUCTION,

    END,
}
