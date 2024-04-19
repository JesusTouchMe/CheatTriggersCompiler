package cum.jesus.cts.lexing;

import java.io.PrintStream;
import java.util.Objects;

public final class Token {
    private TokenType type;
    private final String text;
    private final SourceLocation sourceLocation;

    public Token(final SourceLocation sourceLocation, final TokenType type, final String text) {
        this.sourceLocation = Objects.requireNonNull(sourceLocation);
        this.type = Objects.requireNonNull(type);
        this.text = Objects.requireNonNull(text);
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public SourceLocation getSourceLocation() {
        return sourceLocation;
    }

    public void printFull(PrintStream stream) {
        stream.print(type);
        if (!text.isEmpty()) {
            stream.print(":" + text);
        }
        stream.print("\n");
    }

    public void print(PrintStream stream) {
        if (!text.isEmpty()) {
            stream.println(text);
        } else {
            stream.println(type);
        }
    }
}
