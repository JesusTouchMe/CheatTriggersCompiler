package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;

import java.io.PrintStream;

public final class UnOpInst extends Instruction {
    private int operand;
    private Operator op;
    private String name;

    public UnOpInst(Block parent, int id, Value operand, Operator op, String name) {
        super(parent.getParent().getModule(), parent, id);

        this.operand = operand.getId();
        this.op = op;
        this.name = name;

        switch (op) {
            case POS:
            case NEG:
                super.type = operand.getType();
                break;
        }
    }

    @Override
    public boolean requiresRegister() {
        return register != -1;
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("%%%s = %s %s", name, op.str, parent.getParent().getValue(operand).ident());
    }

    @Override
    public String ident() {
        return "%" + name;
    }

    public enum Operator {
        POS("pos"), NEG("neg");

        private String str;

        Operator(String str) {
            this.str = str;
        }
    }
}
