package cum.jesus.cts.ctir.ir;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.type.Type;

import java.io.PrintStream;

public final class Argument extends Value {
    private String name;

    Argument(Module module, int id, Type type, String name) {
        super(module, id);
        super.type = type;

        this.name = name;
    }

    @Override
    public boolean requiresRegister() {
        return true;
    }

    @Override
    public void print(PrintStream stream) {

    }

    @Override
    public String ident() {
        return String.format("%s %%%s", type.getName(), name);
    }
}
