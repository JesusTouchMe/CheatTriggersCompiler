package cum.jesus.cts.lexing;

import java.io.PrintStream;
import java.util.Objects;

public final class Token {
    private final TokenType type;
    private final String text;

    public Token(final TokenType type, final String text) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(text);

        this.type = type;
        this.text = text;
    }

    public TokenType getType() {
        return type;
    }

    public String getText() {
        return text;
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
