package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.operand.ConstPoolEntryOperand;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.Memory;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.threeoperandinstruction.AddInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.CldInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.LodInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.MovInstruction;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

public final class LoadInst extends Instruction {
    private int ptr;
    private String name;

    public LoadInst(Block parent, int id, Value ptr, String name) {
        super(parent.getParent().getModule(), parent, id);

        this.ptr = ptr.getId();
        this.name = name;

        super.type = ptr.getType().getPointerElementType();
    }

    @Override
    public boolean requiresRegister() {
        return true;
    }

    @Override
    public List<Integer> getOperands() {
        return Collections.singletonList(ptr);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("%%%s = load %s", name, parent.getParent().getValue(ptr).ident());
    }

    @Override
    public String ident() {
        return String.format("%s %%%s", type.getName(), name);
    }

    @Override
    public void emit(List<AsmValue> values) {
        Value ptrValue = parent.getParent().getValue(ptr);
        Operand ptrOperand = parent.getEmittedValue(ptr);

        if (ptrValue instanceof AllocaInst && ((AllocaInst) ptrValue).getAllocatedType().isArrayType()) {
            AllocaInst alloca = (AllocaInst) ptrValue;
            if (ptrOperand instanceof Memory) {
                values.add(new AddInstruction(Register.get(register), ((Memory) ptrOperand).getReg(), new Immediate(((Memory) ptrOperand).getOffset())));
            } else {
                throw new RuntimeException("todo");
            }
        } else if (ptrValue instanceof AllocaInst && ((AllocaInst) ptrValue).getAllocatedType().isStructType()) {
            values.add(new AddInstruction(Register.get(register), ((Memory) ptrOperand).getReg(), new Immediate(((Memory) ptrOperand).getOffset())));
        } else {
            if (ptrOperand instanceof Memory) {
                values.add(new LodInstruction(Register.get(register), ptrOperand));
            } else if (ptrOperand instanceof ConstPoolEntryOperand) {
                values.add(new CldInstruction(Register.get(register), ptrOperand));
            } else {
                values.add(new MovInstruction(Register.get(register), ptrOperand));
            }
        }

        emittedValue = Register.get(register);
    }

    public Value getPointer() {
        return parent.getParent().getValue(ptr);
    }
}
