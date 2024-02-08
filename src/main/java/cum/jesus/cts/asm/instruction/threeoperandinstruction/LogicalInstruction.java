package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.ThreeOperandInstruction;

import java.io.PrintStream;

public abstract class LogicalInstruction extends ThreeOperandInstruction {
    private Opcodes opcode;
    private String ident;

    protected LogicalInstruction(Opcodes opcode, String ident, Operand dest, Operand left, Operand right) {
        super(dest, left, right);

        this.opcode = opcode;
        this.ident = ident;
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("    %s %s, %s, %s\n", ident, first.ident(), second.ident(), third.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {

    }
}
