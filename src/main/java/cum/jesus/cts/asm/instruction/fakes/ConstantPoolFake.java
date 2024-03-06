package cum.jesus.cts.asm.instruction.fakes;

import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.StringOperand;

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
        if (op instanceof StringOperand) {
            stream.printf("    string %s\n", op.ident());
        } else if (op instanceof Immediate) {
            stream.printf("    number %s\n", op.ident());
        }
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        builder.addNewConstant(op);
    }
}
