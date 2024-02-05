package cum.jesus.cts.asm.instruction.operand;

import cum.jesus.cts.asm.instruction.Operand;

public final class Register extends Operand {
    public static final int prefixBuf = 0;
    public static final int regA = 1;
    public static final int regB = 2;
    public static final int regC = 3;
    public static final int regD = 4;
    public static final int regE = 5;
    public static final int regF = 6;
    public static final int regG = 7;
    public static final int regH = 8;
    public static final int regStackBase = 9;
    public static final int regStackTop = 10;

    public static final String[] registerNames = {
            "pb",
            "regA",
            "regB",
            "regC",
            "regD",
            "regE",
            "regF",
            "regG",
            "regH",
            "regSB",
            "regST"
    };

    private final int id;

    public Register(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public Operand clone() {
        return new Register(id);
    }

    public static Register get(final String name) {
        for (int i = 0; i < registerNames.length; i++) {
            if (registerNames[i].equals(name)) {
                return new Register(i);
            }
        }
        return new Register(-1);
    }
}
