package cum.jesus.cts.error;

public final class DefaultErrorReporter implements ErrorReporter {
    @Override
    public void reportError(ErrorContext context) {
        System.err.printf("%s: %s:%s: %s\n", context.file, context.token.getSourceLocation().line, context.token.getSourceLocation().column, context.message);
        System.exit(1);
    }
}
