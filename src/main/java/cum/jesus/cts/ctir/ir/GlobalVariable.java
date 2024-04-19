package cum.jesus.cts.ctir.ir;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.fakes.ConstantPoolFake;
import cum.jesus.cts.asm.instruction.operand.ConstPoolEntryOperand;
import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.OptimizationLevel;
import cum.jesus.cts.ctir.ir.constant.Constant;
import cum.jesus.cts.ctir.type.Type;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

public final class GlobalVariable extends Global {
    private String name;
    private Constant initializer = null;

    private boolean isConstant;

    public GlobalVariable(Module module, Type type, boolean isConstant, String name) {
        super(module);
        super.type = type;

        this.name = name;
        this.isConstant = isConstant;
    }

    public Constant getInitializer() {
        return initializer;
    }

    public void setInitializer(Constant initializer) {
        if (!initializer.type.equals(type)) {
            throw new RuntimeException("Initializer type must match variable type.");
        }
        this.initializer = initializer;
        initializer.eraseFromParent();
    }

    @Override
    public boolean requiresRegister() {
        return false;
    }

    @Override
    public List<Integer> getOperands() {
        return Collections.singletonList(initializer.getId());
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("%%%s = %s", name, initializer.ident());
    }

    @Override
    public String ident() {
        return String.format("%s %%%s", type.getName(), name);
    }

    @Override
    public void emit(List<AsmValue> values) {
        if (initializer == null) {
            throw new RuntimeException("No global initializer for %" + name);
        }

        initializer.emit(values);
        Operand initializerOperand = initializer.getEmittedValue();

        if (isConstant) {
            emittedValue = initializer.getEmittedValue();
            return;
        }

        values.add(new ConstantPoolFake(initializerOperand));
        emittedValue = new ConstPoolEntryOperand(module.constPoolOffset++);
    }

    @Override
    public void optimize(OptimizationLevel optimizationLevel) {

    }
}
