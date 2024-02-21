package cum.jesus.cts.asm.lexing;

public final class SourceLocation {
    public final int line;
    public final int column;

    public SourceLocation(int line, int column) {
        this.line = line;
        this.column = column;
    }
}
