package cum.jesus.cts.asm.instruction.fakes;

import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;

import java.io.PrintStream;

/**
 * Fake instruction to add an operand to constant pool
 */
public final class ConstantPoolFake extends AsmValue {
    private final Operand op;

    public ConstantPoolFake(Operand op) {
        this.op = op;
    }

    @Override
    public void print(PrintStream stream) {

    }

    @Override
    public void emit(OpcodeBuilder builder) {
        builder.addNewConstant(op);
    }
}
