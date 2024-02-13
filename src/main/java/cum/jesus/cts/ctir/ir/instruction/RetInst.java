package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.nooperandinstruction.RetInstruction;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.singleoperandinstruction.PopInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.MovInstruction;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.type.Type;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

public final class RetInst extends Instruction {
    private int returnValue;

    public RetInst(Block parent, int id, Value returnValue) {
        super(parent.getParent().getModule(), parent, id);
        this.returnValue = returnValue != null ? returnValue.getId() : -1;
    }

    public Value getReturnValue() {
        return parent.getParent().getValue(returnValue);
    }

    public Type getReturnType() {
        return parent.getParent().getValue(returnValue).getType();
    }

    @Override
    public boolean requiresRegister() {
        return false;
    }

    @Override
    public List<Integer> getOperands() {
        return Collections.singletonList(returnValue);
    }

    @Override
    public void print(PrintStream stream) {
        if (returnValue != -1) {
            stream.printf("ret %s", parent.getParent().getValue(returnValue).ident());
        } else {
            stream.print("ret void");
        }
    }

    @Override
    public String ident() {
        return "%undef";
    }

    @Override
    public void emit(List<AsmValue> values) {
        if (returnValue != -1) {
            Operand returnValue = parent.getEmittedValue(this.returnValue);
            if (returnValue != null) {
                Operand regE = new Register(Register.regE);
                if (returnValue instanceof Register) {
                    if (((Register) returnValue).getId() != Register.regE) {
                        values.add(new MovInstruction(regE, returnValue));
                    }
                } else {
                    values.add(new MovInstruction(regE, returnValue));
                }
            }
        }

        values.add(new MovInstruction(new Register(Register.regStackTop), new Register(Register.regStackBase)));
        values.add(new PopInstruction(new Register(Register.regStackBase)));
        values.add(new RetInstruction());
    }
}
