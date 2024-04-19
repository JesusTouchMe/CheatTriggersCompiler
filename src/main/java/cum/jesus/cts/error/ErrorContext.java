package cum.jesus.cts.error;

import cum.jesus.cts.lexing.SourceLocation;
import cum.jesus.cts.lexing.Token;

public final class ErrorContext {
    public String file;
    public String message;
    public SourceLocation location;

    public ErrorContext(String file, String message, SourceLocation location) {
        this.file = file;
        this.message = message;
        this.location = location;
    }

    public ErrorContext(String file, String message, Token token) {
        this(file, message, token.getSourceLocation());
    }

    public ErrorContext(String file, String message, cum.jesus.cts.asm.lexing.Token token) {
        this(file, message, token.getSourceLocation());
    }
}
