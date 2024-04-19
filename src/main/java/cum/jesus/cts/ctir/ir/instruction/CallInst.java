package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.operand.ConstPoolEntryOperand;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.singleoperandinstruction.PushInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.CallInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.MovInstruction;
import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.ctir.type.FunctionType;
import cum.jesus.cts.ctir.type.VoidType;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class CallInst extends Instruction {
    private String name;
    private Module module;
    private FunctionType functionType;
    private int callee;
    private List<Integer> parameters;
    private List<Integer> stackParameters;

    public CallInst(Block parent, int id, String name, Module module, FunctionType functionType, Value callee, List<Value> parameters) {
        super(parent.getParent().getModule(), parent, id);

        this.name = name;
        this.module = module;
        this.functionType = functionType;
        this.callee = callee.getId();
        this.parameters = new ArrayList<>();
        stackParameters = new ArrayList<>();


        int[] paramRegisters = { Register.regC, Register.regD, Register.regF, Register.regG };
        int i = 0;
        int stackParams = -1;
        for (Value param : parameters) {
            if (!param.getType().equals(functionType.getArgument(i))) {
                throw new RuntimeException("Expected " + functionType.getArgument(i).getName() + ", got " + param.getType().getName());
            }
            if (i < paramRegisters.length) {
                param.color = paramRegisters[i++];
            } else {
                stackParameters.add(param.getId());
            }
            this.parameters.add(param.getId());
        }

        super.type = functionType.getReturnType();

        Collections.reverse(stackParameters);
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

        for (int param : stackParameters) {
            values.add(new PushInstruction(parent.getEmittedValue(param)));
        }

        values.add(new CallInstruction(new ConstPoolEntryOperand(super.module.getImport(this.module.getName())), callee));

        if (color == -1) {
            color = Register.regE;
            register = "regE";
        }

        if (color != Register.regE) {
            values.add(new MovInstruction(Register.get(register), Register.get("regE")));
        }

        emittedValue = Register.get(register);
    }
}
