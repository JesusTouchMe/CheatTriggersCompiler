package cum.jesus.cts.environment;

import cum.jesus.cts.ctir.ir.instruction.AllocaInst;
import cum.jesus.cts.type.Type;

public final class LocalSymbol {
    public AllocaInst alloca;
    public Type type;

    public LocalSymbol(AllocaInst alloca, Type type) {
        this.alloca = alloca;
        this.type = type;
    }
}
