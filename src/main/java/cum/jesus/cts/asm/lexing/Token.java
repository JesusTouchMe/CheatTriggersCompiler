package cum.jesus.cts.asm.lexing;

import cum.jesus.cts.lexing.SourceLocation;

import java.util.Objects;

public final class Token {
    private TokenType type;
    private String text;
    private SourceLocation sourceLocation;

    public Token(SourceLocation sourceLocation, TokenType type, String text) {
        this.type = type;
        this.text = text;
        this.sourceLocation = sourceLocation;
    }

    public Token(SourceLocation sourceLocation, TokenType type) {
        this(sourceLocation, type, "");
    }

    public TokenType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public SourceLocation getSourceLocation() {
        return sourceLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        if (type != token.type) return false;
        return Objects.equals(text, token.text);
    }
}
