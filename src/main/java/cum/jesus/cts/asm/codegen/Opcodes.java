package cum.jesus.cts.asm.codegen;

/**
 * <a href="https://docs.google.com/spreadsheets/d/1hRenVVeyh3f27tRenfae8wAnFxkpI1cn3Jle5Rtt5TA/edit?usp=sharing">Documentation</a>
 */
public enum Opcodes {
    NOP(0x00),
    NEWL(0x0A),

    PUSH(0x01),
    POP(0x02),
    DUP(0x03),
    ALCA(0x04),
    FREA(0x05),

    MOV(0x07),
    MOVZ(0x08),

    ALC(0x09),
    FRE(0x0B),
    LOD(0x0C),
    STR(0x0D),

    ADD(0x10),
    SUB(0x11),
    MUL(0x12),
    DIV(0x13),
    AND(0x14),
    OR(0x15),
    XOR(0x16),
    SHL(0x17),
    SHR(0x18),
    LAND(0x19),
    LOR(0x1A),
    LXOR(0x1B),

    INC(0x1C),
    DEC(0x1D),

    NOT(0x1E),
    NEG(0x1F),
    LNOT(0x20),

    CMPEQ(0x21),
    CMPNE(0x22),
    CMPLT(0x23),
    CMPGT(0x24),
    CMPLTE(0x25),
    CMPGTE(0x26),

    JMP(0x27),
    JMV(0x28),
    JIT(0x29),
    JVT(0x2A),
    JIZ(0x2B),
    JVZ(0x2C),

    CALL(0x2D),
    RET(0x2E),
    INT(0x2F),

    CLD(0x30),
    CST(0x31),

    IMM8(0x80, 3),
    IMM16(0x81, 4),
    IMM32(0x82, 6),
    IMM64(0x83, 10),
    IMMS(0x84, -1),
    PPOP(0x85, 1),
    PMEMI(0x86, 5),
    PMEM(0x87, 4),

    ;

     public static final int DEFAULT_CODE_LENGTH = 5;

    /**
     * A byte opcode, but java is retarded and doesn't support unsigned types
     */
    private final int opcode;

    /**
     * All instructions are 5 bytes long as it is the standard
     * except prefixes which don't follow this standard
     * <p>
     * -1 means unknown/restful
     */
    private final int codeLength;

    Opcodes(int opcode, int codeLength) {
        this.opcode = opcode;
        this.codeLength = codeLength;
    }

    Opcodes(int opcode) {
        this(opcode, DEFAULT_CODE_LENGTH);
    }

    public byte getOpcode() {
        return (byte) opcode;
    }

    public int getCodeLength() {
        return codeLength;
    }
}
