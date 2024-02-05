package cum.jesus.cts.asm.instruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;

public abstract class NoOperandInstruction extends Instruction {
    protected Opcodes opcode;

    protected NoOperandInstruction(Opcodes opcode) {
        this.opcode = opcode;
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        builder.createInstruction()
                .opcode(opcode)
                .emit();
    }
}
