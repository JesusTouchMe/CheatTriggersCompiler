package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;

import java.io.PrintStream;
import java.util.List;

public final class BinOpInst extends Instruction {
    private int left;
    private Operator op;
    private int right;
    private String name;

    public BinOpInst(Block parent, int id, Value left, Operator op, Value right, String name) {
        super(parent.getParent().getModule(), parent, id);

        this.left = left.getId();
        this.op = op;
        this.right = right.getId();
        this.name = name;

        assert left.getType().equals(right.getType());

        switch (op) {
            case ADD:
            case SUB:
            case MUL:
            case DIV:
                super.type = left.getType();
                break;
        }
    }

    @Override
    public boolean requiresRegister() {
        return register != -1;
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("%%%s = %s %s, %s", name, op.str, parent.getParent().getValue(left).ident(), parent.getParent().getValue(right).ident());
    }

    @Override
    public String ident() {
        return "%" + name;
    }

    @Override
    public void emit(List<AsmValue> values) {

    }

    public enum Operator {
        ADD("add"), SUB("sub"),
        MUL("mul"), DIV("div");

        private final String str;

        Operator(String str) {
            this.str = str;
        }
    }
}
