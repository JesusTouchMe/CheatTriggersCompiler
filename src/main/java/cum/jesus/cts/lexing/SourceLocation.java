package cum.jesus.cts.lexing;

public final class SourceLocation {
    public final int line;
    public final int column;

    public SourceLocation(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public SourceLocation(SourceLocation other) {
        this.line = other.line;
        this.column = other.column;
    }
}
