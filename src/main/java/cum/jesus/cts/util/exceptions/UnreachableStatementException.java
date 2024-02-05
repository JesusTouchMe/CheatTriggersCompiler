package cum.jesus.cts.util.exceptions;

public final class UnreachableStatementException extends RuntimeException {
    public static final UnreachableStatementException INSTANCE = new UnreachableStatementException();

    public UnreachableStatementException() {
        super("unreachable");
    }
}
