package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.singleoperandinstruction.JmpInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.JitInstruction;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public final class BranchInst extends Instruction {
    private int condition;
    private Block trueBranch;
    private Block falseBranch;

    public BranchInst(Block parent, int id, Value condition, Block trueBranch, Block falseBranch) {
        super(parent.getParent().getModule(), parent, id);
        this.condition = condition.getId();
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    public BranchInst(Block parent, int id, Block destination) {
        super(parent.getParent().getModule(), parent, id);
        this.condition = -1;
        this.trueBranch = destination;
        this.falseBranch = null;
    }

    @Override
    public boolean requiresRegister() {
        return false;
    }

    @Override
    public List<Integer> getOperands() {
        return new ArrayList<>();
    }

    @Override
    public void print(PrintStream stream) {
        if (falseBranch == null) {
            stream.printf("br %s", trueBranch.ident());
        } else {
            stream.printf("br if %s, %s, %s", parent.getParent().getValue(condition).ident(), trueBranch.ident(), falseBranch.ident());
        }
    }

    @Override
    public String ident() {
        return "%undef";
    }

    @Override
    public void emit(List<AsmValue> values) {
        if (falseBranch == null) { // if there's no false block this bitch a jumper
            values.add(new JmpInstruction(parent.getParent().getBlockEmittedValue(trueBranch)));
        } else {
            Operand conditionOperand = parent.getEmittedValue(condition);
            values.add(new JitInstruction(conditionOperand, parent.getParent().getBlockEmittedValue(trueBranch)));
            values.add(new JmpInstruction(parent.getParent().getBlockEmittedValue(falseBranch)));
        }
    }
}
