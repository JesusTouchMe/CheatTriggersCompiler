package cum.jesus.cts.asm.instruction;

public abstract class Operand {
    public abstract String ident();

    public abstract Operand clone();
}
