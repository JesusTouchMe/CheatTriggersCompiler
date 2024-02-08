package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.singleoperandinstruction.CallInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.MovInstruction;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Function;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.type.VoidType;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class CallInst extends Instruction {
    private String name;
    private int callee;
    private List<Integer> parameters;

    public CallInst(Block parent, int id, String name, Value callee, final List<Value> parameters) {
        super(parent.getParent().getModule(), parent, id);

        this.name = name;
        this.callee = callee.getId();
        this.parameters = new ArrayList<>();

        Function function = module.getFunctions().get(this.callee);

        int[] paramRegisters = { Register.regC, Register.regD, Register.regF, Register.regG };
        int i = 0;
        for (Value param : parameters) {
            assert param.getType().equals(function.getArgument(i).getType());
            if (i < paramRegisters.length) {
                param.color = paramRegisters[i++];
            } else {
                param.color = -1;
            }
            this.parameters.add(param.getId());
        }

        super.type = function.getReturnType();
    }

    @Override
    public boolean requiresRegister() {
        return !(type instanceof VoidType);
    }

    @Override
    public List<Integer> getOperands() {
        List<Integer> operands = new ArrayList<>();
        operands.add(callee);

        for (int param : parameters) {
            List<Integer> paramOperands = parent.getParent().getValue(param).getOperands();
            operands.addAll(paramOperands);
            operands.add(param);
        }

        return operands;
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("%%%s = call %s %s(", name, type.getName(), module.getFunctions().get(callee).ident());
        Iterator<Integer> it = parameters.iterator();
        while (it.hasNext()) {
            stream.print(parent.getParent().getValue(it.next()).ident());
            if (it.hasNext()) {
                stream.print(", ");
            }
        }
        stream.print(")");
    }

    @Override
    public String ident() {
        return String.format("%s %%%s", type.getName(), name);
    }

    @Override
    public void emit(List<AsmValue> values) {
        Operand callee = module.getFunctionEmittedValue(this.callee);

        values.add(new CallInstruction(callee));

        if (color != Register.regE) {
            values.add(new MovInstruction(Register.get(register), Register.get("regE")));
        }

        emittedValue = Register.get(register);
    }
}
