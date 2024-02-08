package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class CmpInstruction extends LogicalInstruction {
    public static final int eq = 0;
    public static final int ne = 1;
    public static final int lt = 2;
    public static final int gt = 3;
    public static final int lte = 4;
    public static final int gte = 5;

    private static final String[] cmpNames = { "eq", "ne", "lt", "gt", "lte", "gte" };

    public CmpInstruction(int compType, Operand dest, Operand left, Operand right) {
        super(Opcodes.values()[Opcodes.CMPEQ.ordinal() + compType], "cmp" + cmpNames[compType], dest, left, right);
    }
}
