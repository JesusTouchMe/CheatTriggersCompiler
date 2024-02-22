package cum.jesus.cts.ctir;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.fakes.FunctionInstructionFake;
import cum.jesus.cts.asm.instruction.operand.ConstPoolEntryOperand;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.operand.StringOperand;
import cum.jesus.cts.asm.instruction.twooperandinstruction.CallInstruction;
import cum.jesus.cts.asm.instruction.singleoperandinstruction.IntInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.MovInstruction;

import java.io.PrintStream;
import java.util.List;

/**
 * Represents the default ".start" function symbol which the vm will attempt to call otherwise it defaults to 0 which is usually the first function
 */
public final class StartFunction {
    public static void print(PrintStream stream) {
        stream.println("\n\ndefine private void @.start() {");
        stream.println("    %0 = i32 ctir.vm.getters.argc");
        stream.println("    %1 = i8** ctir.vm.getters.argv");
        stream.println("    %2 = call i32 @main(i32 %0, i8** %1)");
        stream.println("    ctir.vm.signal.exit(i32 %2)");
        stream.print("}");
    }

    public static void emit(List<AsmValue> values, Module module) {
        int mainFunction = module.getFunctionByName("main");

        values.add(new FunctionInstructionFake(".start"));

        if (mainFunction != -1) {
            values.add(new CallInstruction(new ConstPoolEntryOperand(0), module.getFunctionEmittedValue(mainFunction)));
        } else {
            StringOperand failMsg = new StringOperand("No main function was located in this module\n");
            values.add(new MovInstruction(Register.get("regC"), failMsg));
            values.add(new IntInstruction(0x04)); // 0x04 = write, https://docs.google.com/spreadsheets/d/1hRenVVeyh3f27tRenfae8wAnFxkpI1cn3Jle5Rtt5TA/edit?usp=sharing
            values.add(new MovInstruction(Register.get("regE"), new Immediate(-1)));
        }

        values.add(new IntInstruction(0x01)); // 0x01 = exit, https://docs.google.com/spreadsheets/d/1hRenVVeyh3f27tRenfae8wAnFxkpI1cn3Jle5Rtt5TA/edit?usp=sharing
    }
}
