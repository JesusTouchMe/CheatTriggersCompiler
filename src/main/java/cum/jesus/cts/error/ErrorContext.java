package cum.jesus.cts.error;

import cum.jesus.cts.asm.lexing.Token;

public final class ErrorContext {
    public String file;
    public String message;
    public Token token;

    public ErrorContext(String file, String message, Token token) {
        this.file = file;
        this.message = message;
        this.token = token;
    }
}
