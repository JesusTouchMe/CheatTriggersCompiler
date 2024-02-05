package cum.jesus.cts.ctir.ir.constant;

import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.type.Type;

import java.io.PrintStream;

public final class ConstantInt extends Constant {
    private long value;
    private String name;

    public ConstantInt(Block parent, int id, long value, Type type, String name) {
        super(parent.getParent().getModule(), id);

        this.value = value;
        this.name = name;

        super.type = type;
    }

    public long getValue() {
        return value;
    }

    @Override
    public boolean requiresRegister() {
        return register != -1;
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("%%%s = %s %d", name, type.getName(), value);
    }

    @Override
    public String ident() {
        return String.format("%s %%%s", type.getName(), name);
    }
}
