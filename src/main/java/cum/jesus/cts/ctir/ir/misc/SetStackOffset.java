package cum.jesus.cts.ctir.ir.misc;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility value to help functions optimize stack usage
 */
public final class SetStackOffset extends Value {
    private Block parent;

    public SetStackOffset(Block parent, int id) {
        super(parent.getParent().getModule(), id);
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
        prints = false;
    }

    @Override
    public String ident() {
        return "%undef";
    }

    @Override
    public void emit(List<AsmValue> values) {

    }
}
