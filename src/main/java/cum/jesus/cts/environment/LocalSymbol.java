package cum.jesus.cts.environment;

import cum.jesus.cts.ctir.ir.Argument;
import cum.jesus.cts.ctir.ir.instruction.AllocaInst;
import cum.jesus.cts.type.Type;
import cum.jesus.cts.util.Variant2;

/**
 * Either holds a variable on stack or in a register for arguments
 */
public final class LocalSymbol {
    public Variant2<AllocaInst, Argument> alloca;
    public Type type;

    public LocalSymbol(AllocaInst alloca, Type type) {
        this.alloca = new Variant2<>();
        this.type = type;

        this.alloca.setA(alloca);
    }

    public LocalSymbol(Argument argument, Type type) {
        this.alloca = new Variant2<>();
        this.type = type;

        this.alloca.setB(argument);
    }
}
