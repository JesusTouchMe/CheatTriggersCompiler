package cum.jesus.cts.ctir.ir;

import cum.jesus.cts.ctir.OptimizationLevel;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Block extends Value {
    private String name;
    private Function parent;
    private List<Integer> values;

    private Block(String name, Function parent) {
        super(parent.module, parent.instructionCount++);

        this.name = name;
        this.parent = parent;
        this.values = new ArrayList<>();
    }

    public static Block create(String name, Function parent) {
        if (name.isEmpty()) {
            name = String.valueOf(parent.instructionCount++);
        }

        Block block = new Block(name, parent);
        parent.insertBlock(block);

        return block;
    }

    public String getName() {
        return name;
    }

    public Function getParent() {
        return parent;
    }

    public List<Integer> getInstructions() {
        return values;
    }

    public void insertValue(Value value) {
        values.add(value.id);
    }

    public void eraseValue(int id) {
        Iterator<Integer> it = values.iterator();
        while (it.hasNext()) {
            int current = it.next();
            if (current == id) {
                it.remove();
                break;
            }
        }
    }

    @Override
    public boolean requiresRegister() {
        return false;
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("  %s:\n", name);
        for (int instruction : values) {
            stream.print("    ");
            parent.getValue(instruction).print(stream);
            stream.println();
        }
    }

    @Override
    public String ident() {
        return "label " + name;
    }

    public void optimize(OptimizationLevel level) {
        if (level == OptimizationLevel.NONE) {
            return;
        }
    }
}
